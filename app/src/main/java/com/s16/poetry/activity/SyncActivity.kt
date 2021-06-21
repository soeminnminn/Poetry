package com.s16.poetry.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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

class SyncActivity : AppCompatActivity() {

    private lateinit var syncManager : SyncManager
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var rootLayout: ViewGroup

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_CANCELED) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        rootLayout = findViewById(R.id.rootLayout)

        syncManager = SyncManager(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton: SignInButton = findViewById(R.id.signInButton)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener { signIn() }

        val signOutButton: Button = findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener { signOut() }

        val syncManualButton: Button = findViewById(R.id.syncManualButton);
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
        signInLauncher.launch(signInIntent);
    }

    private fun signOut() {
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            syncManager.signOutAccount()
            updateUI(null)
        }
    }

    private fun updateUI(user: GoogleSignInAccount?) {
        val signInContainer : ViewGroup = findViewById(R.id.signInContainer)
        val signOutContainer : ViewGroup = findViewById(R.id.signOutContainer)
        val textEmail : TextView = findViewById(R.id.textEmail)

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
        // private const val RC_SIGN_IN = 9001
    }
}