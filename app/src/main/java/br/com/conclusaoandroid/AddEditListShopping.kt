package br.com.conclusaoandroid

import android.app.ActionBar
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddEditListShopping : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_list_shopping)

        val bundle: Bundle? = intent.extras
        val string = bundle?.get("shopping")
        println("Chegou ${string}")
    }
}