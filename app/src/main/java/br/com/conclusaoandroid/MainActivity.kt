package br.com.conclusaoandroid

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import br.com.conclusaoandroid.databinding.ActivityLoginBinding
import br.com.conclusaoandroid.databinding.ActivityMainBinding
import br.com.conclusaoandroid.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;

    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.parseColor("#0075FF")

        binding = ActivityMainBinding.inflate(layoutInflater);

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            gotoLogin()
        }

        binding.logMenu.logout.setOnClickListener {
            auth.signOut()
            gotoLogin()
        }
    }

    private fun setupListener() {

    }

    private fun gotoLogin() {
        val intent = Intent(this, Login::class.java);
        startActivity(intent);
        finish();
    }
}