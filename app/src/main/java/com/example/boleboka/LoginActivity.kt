package com.example.boleboka

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.google_button

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 1
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mGoogleSignInOptions: GoogleSignInOptions

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        configureGoogleSignIn()
        setLoginBtn()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            startActivity(MainActivity.getLaunchIntent(this))
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient =
            GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setLoginBtn() {
        google_button.setOnClickListener {
            signIn()
        }
    }


    private fun signIn() {
        mGoogleSignInClient.signOut()
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }catch (e: ApiException) {
                Toast.makeText(this, "Sign in with Google failed $e", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        val personName: String = acct.displayName.toString()
        val personGivenName: String = acct.givenName.toString()
        val personFamilyName: String = acct.familyName.toString()
        val personEmail: String = acct.email.toString()
        val personId: String = acct.id.toString()
        val personPhoto: Uri? = acct.photoUrl


        //textView3.text = personEmail
        // TODO: 21.10.2020 Sender ikke informasjon til MainActivity? Blir ikke informasjonen sendt
        // TODO: 21.10.2020 på riktig måte? eller blir den ikke mottatt på riktig måte? 
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("AccName", personGivenName)
        startActivity(intent)

            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(MainActivity.getLaunchIntent(this))
                // Toast.makeText(this, personGivenName, Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "Sign in with Google Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

}
