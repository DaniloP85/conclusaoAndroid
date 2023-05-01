package br.com.conclusaoandroid.domain

import android.content.ContentValues
import android.util.Log
import br.com.conclusaoandroid.data.RepositoryImp
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class UseCaseRegister (private val repositoryImp: RepositoryImp) {

    suspend fun registerFirebase(email: String, password: String): Boolean {

        return try {
            val task: AuthResult =
                repositoryImp.firebaseLogin().createUserWithEmailAndPassword(email, password)
                    .await()
            Log.d(ContentValues.TAG, "createUserWithEmail:success")
            task.user != null
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "createUserWithEmail:failure", e)
            false
        }
    }
}