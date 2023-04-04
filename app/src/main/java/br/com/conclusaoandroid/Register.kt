package br.com.conclusaoandroid

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import br.com.conclusaoandroid.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding;

    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater);

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        setUpListener()
    }

    private fun setUpListener() {
        binding.btnRegister.setOnClickListener {
            binding.progressBarRegister.visibility = View.VISIBLE;
            val email = binding.emailRegister.text.toString();
            val password = binding.passwordRegister.text.toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.register_validate), Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBarRegister.visibility = View.GONE;

                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        //val user = auth.currentUser

                        Toast.makeText(baseContext, "Account created.",
                            Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.loginNow.setOnClickListener {
            val intent = Intent(this, Login::class.java);
            startActivity(intent);
            finish();
        }
    }

}