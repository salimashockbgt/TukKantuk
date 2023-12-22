package com.example.tukkantuk

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tukkantuk.databinding.ActivityRegisterBinding


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var passwordEditText: EditTextPassword
    private lateinit var emailEditText: EditTextEmail
    private lateinit var nameEditText: TextView
    private lateinit var button: ButtonCustomView
    private var checkName: Boolean = false
    private var checkEmail: Boolean = false
    private var checkPassword: Boolean = false


    private val viewModel by viewModels<ViewModelRegister> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        checkButton()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.register(name, email, password).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showLoading(false)
                            showToast(getString(R.string.register_berhasil))
                            finish()
                        }

                        is Result.Error -> {
                            showToast(getString(R.string.register_gagal ))
                            showLoading(false)
                        }
                    }

                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkButton() {
        passwordEditText = binding.passwordEditText
        emailEditText = binding.emailEditText
        nameEditText = binding.nameEditText
        button = binding.signupButton

        emailEditText.addTextChangedListener(object : TextWatcher {
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

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkName = !s.isNullOrEmpty()
                setButtonEnabled()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }
    private fun setButtonEnabled() {
        button.isEnabled = checkEmail && checkPassword && checkName
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}