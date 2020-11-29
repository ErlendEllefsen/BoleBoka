package com.example.boleboka

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_main.*

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


    override fun onStart() {
        super.onStart()
        /*
            Robin
            Om brukeren har logget it før, vil brukeren automatisk bli logget inn
            neste gang brukeren tar i bruk appen.
        */
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val name: String = firebaseAuth.currentUser!!.displayName.toString()
            Toast.makeText(this, "Welcome back $name", Toast.LENGTH_LONG).show()
            startActivity(MainActivity.getLaunchIntent(this))
        }
    }


    private fun configureGoogleSignIn() {
        //Requester ID token og email fra brukeren.
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
        mGoogleSignInClient.signOut() //Gir brukeren mulighet til å velge å logge inn med samme account eller legge til en ny account.
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*
            Metoden sender brukeren som skal bli logget inn til firebaseAuthWithGoogle funksjonen
            for å bli autentisert.
        */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }catch (error: ApiException) {
                Toast.makeText(this, "Sign in with Google failed $error", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        /*
            Om brukeren sitt forsøk på signin er gyldig, vil brukeren få en ID token fra
            googlesigninaccount. ID token blir byttet med en firebase attest. Denne attesten vil bli
            brukt til å autentisere med firebase.
        */
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        val personName: String = acct.displayName.toString()
        
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(MainActivity.getLaunchIntent(this))
                Toast.makeText(this, "Welcome $personName", Toast.LENGTH_LONG).show()
            }else {
                Toast.makeText(this, "Sign in with Google Failed", Toast.LENGTH_LONG).show()
            }
        }
    }

}
