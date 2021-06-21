package com.s16.poetry.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
class SyncAdapter @JvmOverloads constructor(
    context: Context,
    autoInitialize: Boolean,
    /**
     * Using a default argument along with @JvmOverloads
     * generates constructor for both method signatures to maintain compatibility
     * with Android 3.0 and later platform versions
     */
    allowParallelSyncs: Boolean = false,
    /*
     * If your app uses a content resolver, get an instance of it
     * from the incoming Context
     */
    resolver: ContentResolver = context.contentResolver
) : AbstractThreadedSyncAdapter(context, autoInitialize, allowParallelSyncs) {

    private val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
    private val fireStore = Firebase.firestore

    override fun onPerformSync(
        account: Account?,
        extras: Bundle?,
        authority: String?,
        provider: ContentProviderClient?,
        syncResult: SyncResult?
    ) {
        // TODO: Put the data transfer code here.
        account?.let { acc ->
            val email =  accountManager.getUserData(acc, SyncManager.ACCOUNT_EMAIL)
            if (TextUtils.isEmpty(email)) {
                syncResult?.stats?.numAuthExceptions = (syncResult?.stats?.numAuthExceptions ?: 0) + 1
                null
            } else {
                email
            }
        }?.let { email ->
            fireStore.collection(email).get().addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.map { documentSnapshot ->
                    Log.i("SyncDocument", documentSnapshot.id)
                }
                // TODO: Sync

            }.addOnFailureListener { ex ->
                ex.printStackTrace()
                syncResult?.stats?.numIoExceptions = (syncResult?.stats?.numIoExceptions ?: 0) + 1
            }
        }
    }

}
