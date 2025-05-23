package gov.rr.iteraima.ui.admin.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gov.rr.iteraima.data.repository.ScheduleRepository
import gov.rr.iteraima.network.AuthResponse
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AdminLoginViewModel(
    private val repository: ScheduleRepository = ScheduleRepository.getInstance()
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        if (!validateInput(email, password)) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = repository.login(email, password)
                result.fold(
                    onSuccess = { authResponse ->
                        _loginState.value = LoginState.Success(authResponse)
                    },
                    onFailure = { exception ->
                        _loginState.value = LoginState.Error(exception.message ?: "Erro ao fazer login")
                    }
                )
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Erro de conexão. Tente novamente.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Todos os campos são obrigatórios")
            return false
        }

        if (!isValidEmail(email)) {
            _loginState.value = LoginState.Error("E-mail inválido")
            return false
        }

        if (password.length < 6) {
            _loginState.value = LoginState.Error("A senha deve ter pelo menos 6 caracteres")
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
            "@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
        )
        return pattern.matcher(email).matches()
    }

    sealed class LoginState {
        data class Success(val authResponse: AuthResponse) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
