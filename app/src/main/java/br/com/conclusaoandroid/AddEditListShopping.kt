package br.com.conclusaoandroid

import android.R
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.Adapter
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.conclusaoandroid.adapter.AddEditAdapter
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Shopping
import br.com.conclusaoandroid.model.ShoppingItem
import br.com.conclusaoandroid.model.ShoppingItemFormatted
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddEditListShopping : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditListShoppingBinding;
    var listAux: MutableList<ShoppingItemFormatted> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditListShoppingBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)

        val bundle: Bundle? = intent.extras
        val documentId = bundle?.get("documentId")
        val marketName = bundle?.get("marketName")
        val marketDate =  bundle?.get("marketDate")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0075FF")))
        supportActionBar?.setTitleColor(Color.WHITE, "${marketName.toString()} (${marketDate})")

        var title: String = ""
        var list: List<ShoppingItem>

        Firebase.firestore
            .collection("shopping")
            .document("${documentId.toString()}")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val shopping = documentSnapshot.toObject<Shopping>()

                title = shopping?.marketplace.toString()
                list = shopping?.items!!

                for (item in list) {
                    val format: NumberFormat = NumberFormat.getCurrencyInstance()
                    format.maximumFractionDigits = 2
                    format.setCurrency(Currency.getInstance("BRL")).toString()
                    val price = format.format(item.value)

                    listAux.add(ShoppingItemFormatted(description = item.description, value = price))
                }
                binding.recyclerShoppingAddEdit.layoutManager = LinearLayoutManager(this)
                binding.recyclerShoppingAddEdit.adapter = AddEditAdapter(listAux)
            }
    }

    private fun ActionBar.setTitleColor(color: Int, t: String) {
        val text = SpannableString(t)
        text.setSpan(ForegroundColorSpan(color),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
                finishAffinity()
            }
            else -> {}
        }
        return true
    }


}