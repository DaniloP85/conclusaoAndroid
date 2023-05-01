package br.com.conclusaoandroid.domain.model

import android.content.ContentValues
import android.util.Log
import br.com.conclusaoandroid.data.RepositoryImp
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.tasks.await

class UseCaseLogin (private val repositoryImp: RepositoryImp) {
    suspend fun loginFirebase(email: String, password: String) : Boolean {
        return try {
            val task: AuthResult = repositoryImp.firebaseLogin().signInWithEmailAndPassword(email, password).await()
            Log.d(ContentValues.TAG, "signInWithEmail:success")
            task.user != null
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, "signInWithEmail:failure", e)
            false
        }
    }
}