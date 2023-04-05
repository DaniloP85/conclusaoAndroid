package br.com.conclusaoandroid

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {
    lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#F7E64F")

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser
            if(currentUser != null){
                val intent = Intent(this, MainActivity::class.java);
                startActivity(intent);
                finish();
            } else {
                val intent = Intent(this, Login::class.java);
                startActivity(intent);
                finish();
            }
        }, 3000)
    }
}