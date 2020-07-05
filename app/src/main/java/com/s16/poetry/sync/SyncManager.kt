package com.s16.poetry.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.core.os.bundleOf

@SuppressLint("MissingPermission")
class SyncManager(context: Context) : ContextWrapper(context) {

    private val accountManager = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

    private val syncAccount : Account
        get() = Account(ACCOUNT_NAME, ACCOUNT_TYPE)

    /**
     * Create a new dummy account for the sync adapter
     */
    fun createAccount(email: String): Account {
        return syncAccount.also { account ->
            val userData = bundleOf(ACCOUNT_EMAIL to email)

            // Add the account. If successful, return the Account object, otherwise report an error.
            if (accountManager.addAccountExplicitly(account, null, userData)) {
                // Inform the system that this account supports sync
                ContentResolver.setIsSyncable(account, AUTHORITY, 1)

                // Inform the system that this account is eligible for auto sync when the network is up
                ContentResolver.setSyncAutomatically(account, AUTHORITY, true)

                // Recommend a schedule for automatic synchronization. The system may modify this based
                // on other scheduled syncs and network utilization.
                ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY, SYNC_FREQUENCY)

                // Force a sync if the account was just created
                // ContentResolver.requestSync(account, AUTHORITY, null)
            } else {
                accountManager.setUserData(account, ACCOUNT_EMAIL, email)
            }
        }
    }

    fun signOutAccount() {
        val account = syncAccount
        accountManager.setUserData(account, ACCOUNT_EMAIL, "")
    }

    fun requestSync() {
        // Pass the settings flags by inserting them in a bundle
        val settingsBundle = Bundle().apply {
            putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
        }
        // Request the sync for the active account, authority, and manual sync settings
        ContentResolver.requestSync(syncAccount, AUTHORITY, settingsBundle)
    }

    companion object {
        // Constants
        // The authority for the sync adapter's content provider
        const val AUTHORITY = SyncProvider.AUTHORITY
        // An account type, in the form of a domain name
        const val ACCOUNT_TYPE = "poetry.s16.com"
        // The account name
        const val ACCOUNT_NAME = "Sync Account"

        const val ACCOUNT_EMAIL = "email"

        const val SYNC_FREQUENCY = 24L * 60L * 60L // 1 day in seconds
    }
}