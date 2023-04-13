package br.com.conclusaoandroid

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingListAdapter
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Products
import br.com.conclusaoandroid.model.Shopping
import com.example.mobcompoents.cusomtoast.CustomToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class AddEditListShopping : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditListShoppingBinding;
    private lateinit var shoppingListAdapter: ShoppingListAdapter
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditListShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0075FF")))

        val bundle: Bundle? = intent.extras
        documentId = bundle?.get("documentId").toString()

        val marketplace = bundle?.get("marketplace")
        val queryShopping = Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products").limit(100)

        binding.nameMarketplace.text = "Local: ${marketplace}"

        shoppingListAdapter = object :
            ShoppingListAdapter(queryShopping, documentId, { product -> adapterOnClick(product) }) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    println("Zerado? ${documentId}")
                    updateTotalShopping(0.0)
                    binding.valueTotal.text = "0"
                } else {
                    //TODO: Melhorar as variaveis
                    var t = getAllSnapshot()
                    var s = 0.0
                    for (w in t) {
                        var o = w.toObject<Products>()
                        s += o?.value!!
                    }
                    binding.recyclerShoppingList.adapter = shoppingListAdapter
                    val format: NumberFormat = NumberFormat.getCurrencyInstance()
                    format.maximumFractionDigits = 2
                    format.setCurrency(Currency.getInstance("BRL")).toString()
                    binding.valueTotal.text = "Total: ${format.format(s)}"

                    updateTotalShopping(s)
                }
            }
        }

        binding.addProduct.setOnClickListener {
            var valeText = binding.valueProduct.text.toString()
            var descriptionText = binding.nameProduct.text
            addProduct(valeText.toDouble(), descriptionText.toString())
        }
    }

    private fun adapterOnClick(productCurrent: Products) {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Atualize o registro")
        val dialogLayout = inflater.inflate(R.layout.alert_update_items, null)
        val editTextDescription = dialogLayout.findViewById<EditText>(R.id.editTextDescription)
        val editTextValue = dialogLayout.findViewById<EditText>(R.id.editTextValue)

        editTextDescription.setText(productCurrent.description)
        editTextValue.setText(productCurrent.value.toString())

        builder.setView(dialogLayout)
        builder.setPositiveButton("edit") { _, _ ->
            if (editTextDescription.text.isNotBlank() && editTextValue.text.isNotBlank()) {

                Firebase.firestore
                    .collection("shopping")
                    .document(documentId)
                    .collection("products")
                    .document(productCurrent.documentId.toString())
                    .update("description", editTextDescription.text.toString(),"value", editTextValue.text.toString().toDouble() )
                    .addOnSuccessListener {
                        println("Deu bom")
                    }.addOnFailureListener { e -> println("Deu ruim :: ${e}") }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun updateTotalShopping(value: Double) {
        Firebase
            .firestore
            .collection("shopping")
            .document(documentId)
            .update("total", value)
            .addOnSuccessListener {
                println("Deu bom")
            }.addOnFailureListener { e -> println("Deu ruim :: ${e}") }

    }

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
                CustomToast.success(this, "Cadastrado com sucesso :)")
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
}