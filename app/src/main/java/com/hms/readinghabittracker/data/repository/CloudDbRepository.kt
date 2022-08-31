package com.hms.readinghabittracker.data.repository

import android.util.Log
import com.hms.readinghabittracker.data.model.User
import com.hms.readinghabittracker.utils.Resource
import com.hms.readinghabittracker.utils.helper.ObjectTypeInfoHelper
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig
import com.huawei.agconnect.cloud.database.CloudDBZoneObject
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

    private fun isDbOpen(): Boolean = cloudDBZone != null

    companion object {
        private const val DB_NAME = "ReadingDB"
        private const val TAG = "CloudDB"
    }
}