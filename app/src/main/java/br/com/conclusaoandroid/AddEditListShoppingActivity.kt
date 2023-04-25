package br.com.conclusaoandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import br.com.conclusaoandroid.adapter.ShoppingListAdapter
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Product
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.samuelribeiro.mycomponents.CustomToast
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

        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(baseContext, R.color.status_bar)

        setContentView(binding.root)

        getValuesFromBundle()
        setupToolbar()
        setupAdapterShopping(queryShoppingFromFirebase())
        addProduct()
    }

    private fun addProduct() {
        binding.addProduct.setOnClickListener {
            val valueProduct = binding.valueProduct.text.toString()
            val descriptionText = binding.nameProduct.text.toString()
            val amountText = binding.valueAmount.text.toString()
            val amount = Utils.validAmount(amountText)

            setupDialog(valueProduct, descriptionText, amount)
        }
    }

    private fun setupDialog(
        valueProduct: String,
        descriptionText: String,
        amountText: Int
    ) {
        when (validateEmptyFields(
            valueProduct,
            descriptionText
        )) {
            true -> {
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
            }
            false -> {
                showDialog(descriptionText, valueProduct, amountText)
            }
        }
    }

    private fun showDialog(descriptionText: String, valueProduct: String, productAmount: Int) {
        val dialog = AddEditListShoppingDialogFragment()
        dialog.receiveData(descriptionText, valueProduct, productAmount)
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun validateEmptyFields(
        valueProduct: String,
        descriptionText: String
    ): Boolean {
        if (valueProduct.isBlank() || descriptionText.isBlank()) {
            return true
        }
        return false
    }

    private fun queryShoppingFromFirebase(): Query {
        return Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products")
            .orderBy("description", Query.Direction.ASCENDING)
            .limit(300)
    }

    private fun getValuesFromBundle() {
        val bundle: Bundle? = intent.extras
        documentId = bundle?.getString("documentId").toString()
        marketplace = bundle?.getString("marketPlace").toString()
        marketDate = bundle?.getString("marketDate").toString()
    }

    private fun setupToolbar() {
        binding.toolbar.getTitleSetup("$marketplace $marketDate")
        binding.toolbar.actionToBack { goToBackHome() }
    }

    private fun goToBackHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @SuppressLint("LongLogTag")
    private fun adapterOnClick(productCurrent: Product) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater

        val dialogLayout = inflater.inflate(R.layout.alert_update_items, null)
        val editTextDescription = dialogLayout.findViewById<EditText>(R.id.editTextDescription)
        val editTextValue = dialogLayout.findViewById<EditText>(R.id.editTextValue)
        val editTextAmount = dialogLayout.findViewById<EditText>(R.id.editTextValueAmount)
        builder.setTitle(getString(R.string.update_register))
        editTextDescription.setText(productCurrent.description)
        editTextValue.setText(productCurrent.value.toString())
        editTextAmount.setText(productCurrent.amount.toString())

        builder.setView(dialogLayout)
        builder.setPositiveButton(getString(R.string.edit)) { _, _ ->
            if (editTextDescription.text.isNotBlank() && editTextValue.text.isNotBlank() && editTextAmount.text.isNotBlank()) {

                val textAmount = editTextAmount.text.toString()
                val value = editTextValue.text.toString()
                val amount = Utils.validAmount(textAmount)
                val purchaseValue = Utils.calcPurchaseValue(amount, value.toDouble())

                Firebase.firestore
                    .collection("shopping")
                    .document(documentId)
                    .collection("products")
                    .document(productCurrent.documentId.toString())
                    .update(
                        "description",
                        editTextDescription.text.toString(),
                        "value",
                        editTextValue.text.toString().toDouble(),
                        "amount",
                        amount,
                        "purchaseValue",
                        purchaseValue
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, ":)")
                        CustomToast.success(this, getString(R.string.registered_successfully))
                    }.addOnFailureListener { e -> Log.d(TAG, ":( :: $e") }
            } else {
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
            }
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun setupAdapterShopping(queryShopping: Query) {
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
                        amount += itemObject?.purchaseValue!!
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
    }

    @SuppressLint("LongLogTag")
    private fun updateTotalShopping(value: Double) {
        val total = Utils.rounding(value)

        Firebase
            .firestore
            .collection("shopping")
            .document(documentId)
            .update("total", total)
            .addOnSuccessListener {
                Log.d(TAG, ":)")
            }.addOnFailureListener { e -> Log.d(TAG, ":( :: $e") }

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