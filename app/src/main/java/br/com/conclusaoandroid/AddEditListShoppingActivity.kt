package br.com.conclusaoandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingListAdapter
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Product
import com.example.mobcompoents.cusomtoast.CustomToast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class AddEditListShoppingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditListShoppingBinding
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var documentId: String
    private lateinit var marketplace: String
    private lateinit var marketDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditListShoppingBinding.inflate(layoutInflater)

        window.statusBarColor = Color.parseColor("#0075FF")

        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        documentId = bundle?.get("documentId").toString()
        marketplace = bundle?.get("marketPlace").toString()
        marketDate =  bundle?.get("marketDate").toString()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0075FF")))
        supportActionBar?.setTitleColor(Color.WHITE, "$marketplace (${marketDate})")

        val queryShopping = Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products")
            .orderBy("description", Query.Direction.ASCENDING)
            .limit(300)

        shoppingListAdapter = object :
            ShoppingListAdapter(queryShopping, documentId, { product -> adapterOnClick(product) }) {

            @SuppressLint("SetTextI18n")
            override fun onDataChanged() {
                val total: String = getString(R.string.total)

                if (itemCount == 0) {
                    println("Nothing $documentId")
                    updateTotalShopping(0.0)
                    binding.valueTotal.text = "$total: R$0"
                } else {
                    val allProducts = getAllSnapshot()

                    var amount = 0.0
                    for (item in allProducts) {
                        val itemObject = item.toObject<Product>()
                        amount += itemObject?.value!!
                    }
                    binding.recyclerShoppingList.adapter = shoppingListAdapter
                    val format: NumberFormat = NumberFormat.getCurrencyInstance()
                    format.maximumFractionDigits = 2
                    format.setCurrency(Currency.getInstance("BRL")).toString()

                    binding.valueTotal.text = "$total: ${format.format(amount)}"

                    updateTotalShopping(amount)
                }
            }
        }

        binding.addProduct.setOnClickListener {
            val valeText = binding.valueProduct.text.toString()
            val descriptionText = binding.nameProduct.text.toString()

            if (valeText.isBlank() || descriptionText.isBlank()){
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
                return@setOnClickListener
            }

            addProduct(valeText.toDouble(), descriptionText)
        }
    }

    private fun ActionBar.setTitleColor(color: Int, t: String) {
        val text = SpannableString(t)
        text.setSpan(ForegroundColorSpan(color),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("LongLogTag")
    private fun adapterOnClick(productCurrent: Product) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogLayout = inflater.inflate(R.layout.alert_update_items, null)
        val editTextDescription = dialogLayout.findViewById<EditText>(R.id.editTextDescription)
        val editTextValue = dialogLayout.findViewById<EditText>(R.id.editTextValue)
        builder.setTitle(getString(R.string.update_register))
        editTextDescription.setText(productCurrent.description)
        editTextValue.setText(productCurrent.value.toString())

        builder.setView(dialogLayout)
        builder.setPositiveButton(getString(R.string.edit)) { _, _ ->
            if (editTextDescription.text.isNotBlank() && editTextValue.text.isNotBlank()) {

                Firebase.firestore
                    .collection("shopping")
                    .document(documentId)
                    .collection("products")
                    .document(productCurrent.documentId.toString())
                    .update("description", editTextDescription.text.toString(),"value", editTextValue.text.toString().toDouble() )
                    .addOnSuccessListener {
                        Log.d(TAG,":)")
                        CustomToast.success( this, getString(R.string.registered_successfully) )
                    }.addOnFailureListener { e ->  Log.d(TAG,":( :: $e") }
            } else {
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    @SuppressLint("LongLogTag")
    private fun updateTotalShopping(value: Double) {
        Firebase
            .firestore
            .collection("shopping")
            .document(documentId)
            .update("total", value)
            .addOnSuccessListener {
                Log.d(TAG,":)")
            }.addOnFailureListener { e -> Log.d(TAG, ":( :: $e") }

    }

    @SuppressLint("LongLogTag")
    private fun addProduct(value: Double, description: String) {

        val product = hashMapOf(
            "description" to description,
            "value" to value
        )

        Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products")
            .add(product)
            .addOnSuccessListener { documentReference ->
                CustomToast.success(this, getString(R.string.registered_successfully))
                binding.valueProduct.setText("")
                binding.nameProduct.setText("")
                binding.nameProduct.requestFocus()
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    public override fun onStart() {
        super.onStart()
        shoppingListAdapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        shoppingListAdapter.stopListening()
    }

    companion object {
        private const val TAG = "AddEditListShoppingActivity"
    }
}