package com.s16.poetry.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle

/*
 * Implement AbstractAccountAuthenticator and stub out all
 * of its methods
 */
class Authenticator(context: Context) // Simple constructor
    : AbstractAccountAuthenticator(context) {

    // Don't add additional accounts
    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle?  = null

    // Ignore attempts to confirm credentials
    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle
    ): Bundle?  = null

    // Getting an authentication token is not supported
    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle = Bundle.EMPTY

    // Getting a label for the auth token is not supported
    override fun getAuthTokenLabel(authTokenType: String): String = ""

    // Updating user credentials is not supported
    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle = Bundle.EMPTY

    // Editing properties is not supported
    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle = Bundle.EMPTY

    // Checking features for the account is not supported
    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<String>
    ): Bundle = Bundle.EMPTY
}
