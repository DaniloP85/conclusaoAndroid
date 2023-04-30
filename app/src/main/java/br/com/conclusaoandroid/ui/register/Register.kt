package br.com.conclusaoandroid.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import br.com.conclusaoandroid.R
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.ActivityRegisterBinding
import br.com.conclusaoandroid.ui.login.Login
import br.com.conclusaoandroid.ui.login.LoginViewModel
import com.samuelribeiro.mycomponents.CustomToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class Register : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModel()

    private lateinit var binding: ActivityRegisterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        val view = binding.root
        setContentView(view)


        setUpListener()
        observer()
    }

    private fun setUpListener() {
        binding.btnRegister.setOnClickListener {
            binding.progressBarRegister.visibility = View.VISIBLE
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                binding.progressBarRegister.visibility = View.GONE
                CustomToast.warning( this, getString(R.string.register_validate) )
                return@setOnClickListener
            }

            if(!Utils.emailValidator(email)) {
                binding.progressBarRegister.visibility = View.GONE
                CustomToast.error( this, getString(R.string.email_validate) )
                return@setOnClickListener
            }

            viewModel.registerFirebase(email, password)
        }

        binding.loginNow.setOnClickListener {
            startLoginPage()
        }
    }

    private fun observer() {
        viewModel.registerStatus.observe(this, Observer {
            binding.progressBarRegister.visibility = View.GONE
            if (it) {
                CustomToast.success( this, getString(R.string.account_created) )
                startLoginPage()
            } else {
                CustomToast.error(this, getString(R.string.account_created_error))
            }
        })
    }

    private fun startLoginPage() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}