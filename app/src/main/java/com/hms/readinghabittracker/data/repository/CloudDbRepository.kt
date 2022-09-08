package com.hms.readinghabittracker.data.repository

import android.util.Log
import com.hms.readinghabittracker.data.model.Book
import com.hms.readinghabittracker.data.model.Collection
import com.hms.readinghabittracker.data.model.CollectionUIModel
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.utils.Resource
import com.hms.readinghabittracker.utils.helper.ObjectTypeInfoHelper
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig
import com.huawei.agconnect.cloud.database.CloudDBZoneObject
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudDbRepository @Inject constructor(
    private val cloudDB: AGConnectCloudDB,
    private val agConnectAuth: AGConnectAuth,
) {

    private var cloudDBZone: CloudDBZone? = null

    private val initialCloudDbZone: CloudDBZone? = null
    private val _cloudDbZoneFlow = MutableStateFlow(initialCloudDbZone)
    val cloudDbZoneFlow: StateFlow<CloudDBZone?> get() = _cloudDbZoneFlow.asStateFlow()

    private val initialCloudDbUserResponseList: MutableList<User> = mutableListOf()
    private val _cloudDbUserResponse = MutableStateFlow(initialCloudDbUserResponseList)
    val cloudDbUserResponse: StateFlow<MutableList<User>> get() = _cloudDbUserResponse.asStateFlow()

    private val initialCloudDbCollectionResponseList: MutableList<Collection> = mutableListOf()
    private val _cloudDbCollectionResponse = MutableStateFlow(initialCloudDbCollectionResponseList)
    val cloudDbCollectionResponse: StateFlow<MutableList<Collection>> get() = _cloudDbCollectionResponse.asStateFlow()

    private val initialCloudDbBookResponseList: MutableList<Book> = mutableListOf()
    private val _cloudDbBookResponse = MutableStateFlow(initialCloudDbBookResponseList)
    val cloudDbBookResponse: StateFlow<MutableList<Book>> get() = _cloudDbBookResponse.asStateFlow()

    init {
        openDb()
    }

    private fun openDb() {
        if (cloudDBZone == null) {
            val mConfig = CloudDBZoneConfig(
                DB_NAME,
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC
            )
            createObjectType()
            mConfig.persistenceEnabled = true
            cloudDB.openCloudDBZone2(mConfig, true)
                .addOnSuccessListener {
                    Log.i(TAG, "Open cloudDBZone success")
                    cloudDBZone = it
                    _cloudDbZoneFlow.value = it
                    if (cloudDBZone != null && agConnectAuth.currentUser?.uid != null) {
                        queryAllCollectionsForCurrentUser(agConnectAuth.currentUser.uid.toLong())
                        queryAllCollectionsForCurrentUser(agConnectAuth.currentUser.uid.toLong())
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG, "Open cloudDBZone failed for " + it.message)
                }
        }
    }

    private fun createObjectType() {
        cloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo())
    }

    fun saveToCloudDb(cloudDBZoneObject: CloudDBZoneObject): Flow<Resource<Boolean>> =
        callbackFlow {
            trySend(Resource.Loading)
            if (isDbOpen().not()) {
                trySend(Resource.Error(Exception("Something went wrong")))
                close()
                return@callbackFlow
            }
            val upsertTask = cloudDBZone?.executeUpsert(cloudDBZoneObject)
            upsertTask?.addOnSuccessListener { cloudDBZoneResult ->
                Log.i(TAG, "Upsert $cloudDBZoneResult records")
                trySend(Resource.Success(true))
            }?.addOnFailureListener { exception ->
                trySend(Resource.Error(exception))
                Log.i(TAG, exception.localizedMessage.orEmpty())
            }?.addOnCompleteListener {
                close()
            }
            awaitClose {
                upsertTask?.addOnSuccessListener(null)
                upsertTask?.addOnFailureListener(null)
                upsertTask?.addOnCompleteListener(null)
            }

        }.flowOn(Dispatchers.IO)

    fun checkUserById(uid: Long): Flow<Resource<Boolean>> =
        callbackFlow {
            if (cloudDBZone == null) {
                Log.d(TAG, "Cloud DB Zone is null, try re-open it")
                send(Resource.Error(Exception(("Something went wrong please try again later"))))
                close()
                return@callbackFlow
            }

            trySend(Resource.Loading)

            val query = CloudDBZoneQuery.where(User::class.java).equalTo("id", uid)
            val queryTask = cloudDBZone?.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
            )

            queryTask?.addOnSuccessListener {
                val isUserExist = it.snapshotObjects.size() > 0
                if (isUserExist) {
                    trySend(Resource.Success(true))
                } else {
                    trySend(Resource.Success(false))
                }
            }?.addOnFailureListener {
                trySend(Resource.Error(Exception(("Something went wrong please try again later"))))
            }
            awaitClose {
                queryTask?.addOnSuccessListener(null)
                queryTask?.addOnFailureListener(null)
                channel.close()
            }
        }

    fun queryAllCollectionsForCurrentUser(uid: Long): Flow<Resource<List<Collection>>> =
        callbackFlow {
            if (cloudDBZone == null || agConnectAuth.currentUser.uid == null) {
                Log.w("COLLECTION", "CloudDBZone is null, try re-open it")
                send(Resource.Error(Exception(("Something went wrong please try again later"))))
                close()
                return@callbackFlow
            }
            trySend(Resource.Loading)

            val query = CloudDBZoneQuery.where(Collection::class.java).equalTo("userId", uid)
            val queryTask = cloudDBZone?.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
            )

            queryTask?.addOnSuccessListener { snapshot ->
                val collectionInfoCursor = snapshot.snapshotObjects
                val collectionInfoList: MutableList<Collection> = ArrayList()
                try {
                    while (collectionInfoCursor.hasNext()) {
                        val collectionInfo = collectionInfoCursor.next()
                        collectionInfoList.add(collectionInfo)
                    }
                } catch (e: AGConnectCloudDBException) {
                    Log.w(TAG, "processQueryResult: " + e.message)
                } finally {
                    snapshot.release()
                }
                _cloudDbCollectionResponse.value = collectionInfoList
                trySend(Resource.Success(collectionInfoList))

                Log.e(TAG, "Query is success.")
            }
                ?.addOnFailureListener {
                    trySend(Resource.Error(Exception(("Something went wrong please try again later"))))
                }
            awaitClose {
                queryTask?.addOnSuccessListener(null)
                queryTask?.addOnFailureListener(null)
                channel.close()
            }
        }

    fun queryAllBooksForCurrentUser(uid: Long): Flow<Resource<List<Book>>> =
        callbackFlow {
            if (cloudDBZone == null || agConnectAuth.currentUser.uid == null) {
                Log.w("COLLECTION", "CloudDBZone is null, try re-open it")
                send(Resource.Error(Exception(("Something went wrong please try again later"))))
                close()
                return@callbackFlow
            }
            trySend(Resource.Loading)

            val query = CloudDBZoneQuery.where(Book::class.java).equalTo("userId", uid)
            val queryTask = cloudDBZone?.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
            )

            queryTask?.addOnSuccessListener { snapshot ->
                val bookInfoCursor = snapshot.snapshotObjects
                val bookInfoList: MutableList<Book> = ArrayList()
                try {
                    while (bookInfoCursor.hasNext()) {
                        val bookInfo = bookInfoCursor.next()
                        bookInfoList.add(bookInfo)
                    }
                } catch (e: AGConnectCloudDBException) {
                    Log.w(TAG, "processQueryResult: " + e.message)
                } finally {
                    snapshot.release()
                }
                _cloudDbBookResponse.value = bookInfoList
                trySend(Resource.Success(bookInfoList))
                Log.e(TAG, "Query is success.")
            }
                ?.addOnFailureListener {
                    trySend(Resource.Error(Exception(("Something went wrong please try again later"))))
                }
            awaitClose {
                queryTask?.addOnSuccessListener(null)
                queryTask?.addOnFailureListener(null)
                channel.close()
            }
        }


    fun getCollectionsForCurrentUser(uid: Long): Flow<Resource<List<CollectionUIModel>>> = flow {
        val collectionsList = mutableListOf<CollectionUIModel>()

        val currentUserBooks = queryAllBooksForCurrentUser(uid)
        val currentUserCollections = queryAllCollectionsForCurrentUser(uid)

        currentUserCollections.zip(currentUserBooks) { collectionsResult, booksResult ->
            when (collectionsResult) {
                is Resource.Loading -> emit(Resource.Loading)
                is Resource.Error -> emit(Resource.Error(collectionsResult.exception))
                is Resource.Success -> {

                    when (booksResult) {
                        is Resource.Loading -> emit(Resource.Loading)
                        is Resource.Error -> emit(Resource.Error(booksResult.exception))
                        is Resource.Success -> {

                            val collections = collectionsResult.data
                            val books = booksResult.data

                            collections.forEach { collection ->

                                val allBooksInTheCurrentCollection =
                                    books.filter { it.collectionId == collection.id }
                                val collectionUIModel =
                                    CollectionUIModel(
                                        collection.id,
                                        collection.name,
                                        allBooksInTheCurrentCollection
                                    )
                                collectionsList.add(collectionUIModel)
                            }
                            emit(Resource.Success(collectionsList))
                        }
                    }
                }
            }
        }
    }

    private fun isDbOpen(): Boolean = cloudDBZone != null

    companion object {
        private const val DB_NAME = "ReadingDB"
        private const val TAG = "CloudDB"
    }
}