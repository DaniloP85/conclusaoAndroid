package br.com.conclusaoandroid.data

import com.google.firebase.auth.FirebaseAuth

interface LoginRepository {
    suspend fun firebaseLogin() : FirebaseAuth
}