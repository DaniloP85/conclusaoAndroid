package br.com.conclusaoandroid.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import br.com.conclusaoandroid.R
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.ActivityLoginBinding
import br.com.conclusaoandroid.ui.MainActivity
import br.com.conclusaoandroid.ui.Register
import com.samuelribeiro.mycomponents.CustomToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class Login : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        val view = binding.root
        setContentView(view)

        setUpListener()
        observer()
    }

    private fun setUpListener() {
        binding.registerNow.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            binding.progressBarLogin.visibility = View.VISIBLE
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.progressBarLogin.visibility = View.GONE
                CustomToast.warning(this, getString(R.string.login_validate_fields))
                return@setOnClickListener
            }

            if (!Utils.emailValidator(email)) {
                binding.progressBarLogin.visibility = View.GONE
                CustomToast.error(this, getString(R.string.email_validate))
                return@setOnClickListener
            }

            viewModel.loginFirebase(email, password)
        }
    }

    private fun observer() {
        viewModel.loginStatus.observe(this, Observer {
            binding.progressBarLogin.visibility = View.GONE
            if (it) {
                CustomToast.success(this, "Successful Authentication :)")
                startMainPage()
            } else {
                CustomToast.error(this, "Authentication failed :(")
            }
        })
    }

    private fun startMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}