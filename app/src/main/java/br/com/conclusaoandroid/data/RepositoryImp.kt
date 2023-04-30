package br.com.conclusaoandroid.data

import com.google.firebase.auth.FirebaseAuth

class RepositoryImp: LoginRepository {
    override suspend fun firebaseLogin() = FirebaseAuth.getInstance()
}