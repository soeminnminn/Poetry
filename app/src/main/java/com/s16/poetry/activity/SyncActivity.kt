package com.s16.poetry.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.s16.poetry.R
import com.s16.poetry.sync.SyncManager
import com.s16.ktx.gone
import com.s16.ktx.visible
import kotlinx.android.synthetic.main.activity_sync.*

class SyncActivity : AppCompatActivity() {

    private lateinit var syncManager : SyncManager
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        syncManager = SyncManager(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener { signIn() }

        signOutButton.setOnClickListener { signOut() }

        syncManualButton.setOnClickListener {
            syncManager.requestSync()
        }

        checkSignedIn()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                onSignInSuccess(account)
                updateUI(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)

                Snackbar.make(rootLayout, R.string.message_sign_in_failed, Snackbar.LENGTH_LONG).show()
                updateUI(null)
            }
        }
    }

    private fun onSignInSuccess(user: GoogleSignInAccount) {
        if (user.email != null) {
            syncManager.createAccount(user.email!!)
        }
    }

    private fun checkSignedIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            updateUI(account)
        } else {
            updateUI(null)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            syncManager.signOutAccount()
            updateUI(null)
        }
    }

    private fun updateUI(user: GoogleSignInAccount?) {
        if (user != null) {
            signInContainer.gone()
            textEmail.text = user.email
            signOutContainer.visible()
        } else {
            signOutContainer.gone()
            signInContainer.visible()
        }
    }

    companion object {
        private const val TAG = "SyncActivity"
        private const val RC_SIGN_IN = 9001
    }
}