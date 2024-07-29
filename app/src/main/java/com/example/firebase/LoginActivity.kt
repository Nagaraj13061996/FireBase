package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
// View Binding

        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)

        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()


        // Check if the user is logged in
        val currentUser = auth.currentUser
        Log.i("LoginActivity", "currentUser:$currentUser ")

        if (currentUser!=null){
            val intent = Intent(this, AddData::class.java)
            startActivity(intent)
            finish()
        }
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()
            if (email.isNotEmpty()&&pass.isNotEmpty()){
                login()
            }else{
                Toast.makeText(this, "Enter Login Details", Toast.LENGTH_SHORT).show()
            }

        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }
    }
    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast

        // Check if the user is logged in
        val currentUser = auth.currentUser
        Log.i("LoginActivity", "currentUser123:$currentUser ")
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Log.i("Login", "loginSuccess:$it ")
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, AddData::class.java)
                startActivity(intent)
                // using finish() to end the activity
                finish()
            } else{
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
                Log.i("Login", "loginFailure:$it ")

            }
        }.addOnFailureListener{
            Log.i("Login", "loginError:$it ")

        }
    }
}