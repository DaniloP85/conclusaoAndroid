package br.com.conclusaoandroid.di

import br.com.conclusaoandroid.data.RepositoryImp
import br.com.conclusaoandroid.domain.UseCaseRegister
import br.com.conclusaoandroid.domain.model.UseCaseLogin
import br.com.conclusaoandroid.ui.login.LoginViewModel
import br.com.conclusaoandroid.ui.login.LoginViewModelImp
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { RepositoryImp() }
    factory { UseCaseRegister(get()) }
    factory { UseCaseLogin(get()) }
    viewModel<LoginViewModel> { LoginViewModelImp(get(),get()) }

}