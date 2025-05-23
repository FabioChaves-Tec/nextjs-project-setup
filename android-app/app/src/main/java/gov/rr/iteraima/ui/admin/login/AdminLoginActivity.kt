package gov.rr.iteraima.ui.admin.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import gov.rr.iteraima.R
import gov.rr.iteraima.databinding.ActivityAdminLoginBinding
import gov.rr.iteraima.ui.admin.dashboard.AdminDashboardActivity

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private val viewModel: AdminLoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        with(binding) {
            // Email input handling
            etEmail.addTextChangedListener { 
                tilEmail.error = null
            }

            // Password input handling
            etPassword.addTextChangedListener {
                tilPassword.error = null
            }

            // Login button click handler
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                // Validate inputs
                var hasError = false
                
                if (email.isEmpty()) {
                    tilEmail.error = getString(R.string.required_field)
                    hasError = true
                }
                
                if (password.isEmpty()) {
                    tilPassword.error = getString(R.string.required_field)
                    hasError = true
                }

                if (!hasError) {
                    viewModel.login(email, password)
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
            binding.etEmail.isEnabled = !isLoading
            binding.etPassword.isEnabled = !isLoading
        }

        // Observe login state
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is AdminLoginViewModel.LoginState.Success -> {
                    // Navigate to Dashboard
                    startActivity(Intent(this, AdminDashboardActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
                is AdminLoginViewModel.LoginState.Error -> {
                    showError(state.message)
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setAction("OK") { dismiss() }
            show()
        }
    }

    companion object {
        private const val TAG = "AdminLoginActivity"
    }
}
