package br.com.conclusaoandroid

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AddEditListShopping : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_list_shopping)

        var actionBar = getActionBar();
        actionBar?.setDisplayHomeAsUpEnabled(true);
    }
}