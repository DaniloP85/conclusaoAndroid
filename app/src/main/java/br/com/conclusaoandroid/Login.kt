package br.com.conclusaoandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.com.conclusaoandroid.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater);

        val view = binding.root
        setContentView(view)

        setUpListener()
    }

    private fun setUpListener() {
        binding.registerNow.setOnClickListener {
            val intent = Intent(this, Register::class.java);
            startActivity(intent);
            finish();
        }
    }
}