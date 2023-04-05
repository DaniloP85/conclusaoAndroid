package br.com.conclusaoandroid.common

import android.util.Patterns

object Utils {

    fun emailValidator(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}