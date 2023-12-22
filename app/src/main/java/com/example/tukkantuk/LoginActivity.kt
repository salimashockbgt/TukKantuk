package com.example.tukkantuk

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tukkantuk.ButtonCustomView
import com.example.tukkantuk.EditTextEmail
import com.example.tukkantuk.EditTextPassword
import com.example.tukkantuk.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.ApiException
//
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<ViewModelLogin> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var passwordEditText: EditTextPassword
    private lateinit var usernameEditText: EditTextEmail
    private lateinit var button: ButtonCustomView
    private var checkEmail: Boolean = false
    private var checkPassword: Boolean = false
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Configure Google Sign In
//        val gso = GoogleSignInOptions
//            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//        // Initialize Firebase Auth
//        auth = Firebase.auth
//
//        binding.signInButton.setOnClickListener {
//            signIn()
//        }

        setupView()
        setupAction()
        playAnimation()
        checkButton()
    }
//
//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        resultLauncher.launch(signInIntent)
//    }
//
//    private var resultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = auth.currentUser
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
//                }
//            }
//    }
//    private fun updateUI(currentUser: FirebaseUser?) {
//        if (currentUser != null){
//            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
//            finish()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        // Check if user is signed in (non-null) and update UI accordingly.
//        val currentUser = auth.currentUser
//        updateUI(currentUser)
//    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
//        val message =
//            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
//                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener(){
            val username = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.login(username, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            showToast(getString(R.string.login_berhasil))
                            val token = result.data.loginResult?.token.toString()
                            viewModel.saveSession(UserModel(username, token))
                            viewModel.getSession().observe(this) { user ->
                                if (user.token.isNotEmpty() && user.token.isNotBlank()) {
                                    AlertDialog.Builder(this).apply {
                                        setTitle("Yeah!")
                                        setMessage("Anda berhasil login. Sudah tidak sabar untuk berkendara ya?")
                                        setPositiveButton("Lanjut") { _, _ ->
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.flags =
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(intent)
                                            finish()
                                        }
                                        create()
                                        show()
                                    }

                                }
                            }
                        }

                        is Result.Error -> {
                            showLoading(false)
                            showToast(getString(R.string.login_gagal))
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



    private fun checkButton() {
        passwordEditText = binding.passwordEditText
        usernameEditText = binding.emailEditText
        button = binding.loginButton


        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,7}\\b"
                checkEmail = !s.isNullOrEmpty() && Regex(pattern).matches(s.toString())
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkPassword = !s.isNullOrEmpty() && s.toString().length > 7
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun setButtonEnabled() {
        button.isEnabled = checkEmail && checkPassword
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
