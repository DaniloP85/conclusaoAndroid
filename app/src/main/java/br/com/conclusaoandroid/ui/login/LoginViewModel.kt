package br.com.conclusaoandroid.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.conclusaoandroid.domain.UseCaseRegister
import br.com.conclusaoandroid.domain.model.UseCaseLogin
import kotlinx.coroutines.launch

abstract class LoginViewModel : ViewModel() {
    abstract val loginStatus: MutableLiveData<Boolean>
    abstract val registerStatus: MutableLiveData<Boolean>
    abstract fun loginFirebase(email: String, password: String)
    abstract fun registerFirebase(email: String, password: String)
}

class LoginViewModelImp (
    private val useCaseLogin: UseCaseLogin,
    private val useCaseRegister: UseCaseRegister
) : LoginViewModel(){

    override val loginStatus: MutableLiveData<Boolean> = MutableLiveData()
    override val registerStatus: MutableLiveData<Boolean> = MutableLiveData()


    override fun loginFirebase(email: String, password: String) {
        viewModelScope.launch {
            val result = useCaseLogin.loginFirebase(email, password)
            loginStatus.postValue(result)
        }
    }

    override fun registerFirebase(email: String, password: String) {
        viewModelScope.launch {
            val result = useCaseRegister.registerFirebase(email, password)
            registerStatus.postValue(result)
        }
    }
}