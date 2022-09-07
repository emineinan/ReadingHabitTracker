package com.hms.readinghabittracker.data.repository

import android.util.Log
import com.hms.readinghabittracker.data.model.Collection
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.utils.Resource
import com.hms.readinghabittracker.utils.helper.ObjectTypeInfoHelper
import com.huawei.agconnect.cloud.database.*
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudDbRepository @Inject constructor(
    private val cloudDB: AGConnectCloudDB
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
                    queryAllCollections()
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

    fun getCollectionItems(): Flow<Resource<Boolean>> =
        callbackFlow {
            if (cloudDBZone == null) {
                Log.d(TAG, "Cloud DB Zone is null, try re-open it")
                send(Resource.Error(Exception(("Something went wrong please try again later"))))
                close()
                return@callbackFlow
            }

            trySend(Resource.Loading)

            val query = CloudDBZoneQuery.where(Collection::class.java)
            val queryTask = cloudDBZone?.executeQuery(
                query,
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
            )

            queryTask?.addOnSuccessListener {
                val collectionInfoCursor = it.snapshotObjects
                val collectionInfoList: MutableList<Collection> = ArrayList()
                try {
                    while (collectionInfoCursor.hasNext()) {
                        val collectionInfo = collectionInfoCursor.next()
                        collectionInfoList.add(collectionInfo)
                    }
                } catch (e: AGConnectCloudDBException) {
                    Log.w(TAG, "processQueryResult: " + e.message)
                } finally {
                    it.release()
                }
                _cloudDbCollectionResponse.value = collectionInfoList


                val isCollectionExist = it.snapshotObjects.size() > 0
                Log.e("COLLECTION", it.snapshotObjects.size().toString())
                Log.e("COLLECTION", it.snapshotObjects[0].name)
                Log.e("COLLECTION", it.snapshotObjects[1].name)
                if (isCollectionExist) {
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


    fun queryAllCollections() {
        Log.e("COLLECTION REPO", "Query fonksiyonu cagrildi.")
        if (cloudDBZone == null) {
            Log.w("COLLECTION", "CloudDBZone is null, try re-open it")
            return
        }

        val query = CloudDBZoneQuery.where(Collection::class.java)
        val queryTask = cloudDBZone?.executeQuery(
            query,
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
        )

        queryTask?.addOnSuccessListener { snapshot ->
            processQueryResult(snapshot)
            Log.e("COLLECTION REPO", "Query is success.")
        }
            ?.addOnFailureListener {
                Log.e("COLLECTION REPO", "Query failed.")
            }
    }

    private fun processQueryResult(snapshot: CloudDBZoneSnapshot<Collection>) {
        Log.e("COLLECTION REPO", "processQueryResult fonksiyonu cagrildi.")
        val collectionInfoCursor = snapshot.snapshotObjects
        val collectionInfoList: MutableList<Collection> = ArrayList()
        try {
            while (collectionInfoCursor.hasNext()) {
                val collectionInfo = collectionInfoCursor.next()
                collectionInfoList.add(collectionInfo)
            }
            Log.e("COLLECTION REPO", "collectionInfoList Size: " + collectionInfoList.size.toString())
        } catch (e: AGConnectCloudDBException) {
            Log.w("COLLECTION REPO", "processQueryResult: " + e.message)
        } finally {
            snapshot.release()
        }
        _cloudDbCollectionResponse.value = collectionInfoList
    }

    private fun isDbOpen(): Boolean = cloudDBZone != null

    companion object {
        private const val DB_NAME = "ReadingDB"
        private const val TAG = "CloudDB"
    }
}