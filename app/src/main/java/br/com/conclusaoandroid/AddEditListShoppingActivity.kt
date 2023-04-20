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
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Product
import com.example.mobcompoents.cusomtoast.CustomToast
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*
import kotlin.math.roundToInt

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

    private fun addProduct() {
        binding.addProduct.setOnClickListener {
            val valeText = binding.valueProduct.text.toString()
            val descriptionText = binding.nameProduct.text.toString()
            val amountText = binding.valueAmount.text.toString()

            if (valeText.isBlank() || descriptionText.isBlank()) {
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
                return@setOnClickListener
            }

            val amount = validAmount(amountText)

            addProduct(valeText.toDouble(), descriptionText, amount)
        }
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
        documentId = bundle?.get("documentId").toString()
        marketplace = bundle?.get("marketPlace").toString()
        marketDate = bundle?.get("marketDate").toString()
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
                val amount = validAmount(textAmount)
                val purchaseValue = calcPurchaseValue(amount, value.toDouble())

                Firebase.firestore
                    .collection("shopping")
                    .document(documentId)
                    .collection("products")
                    .document(productCurrent.documentId.toString())
                    .update(
                        "description", editTextDescription.text.toString(),
                        "value", editTextValue.text.toString().toDouble(), "amount", amount, "purchaseValue", purchaseValue )
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
        val ok = ((value * 100.0).roundToInt() / 100.0)

        Firebase
            .firestore
            .collection("shopping")
            .document(documentId)
            .update("total", ok)
            .addOnSuccessListener {
                Log.d(TAG,":)")
            }.addOnFailureListener { e -> Log.d(TAG, ":( :: $e") }

    }

    @SuppressLint("LongLogTag")
    private fun addProduct(value: Double, description: String, amount: Int) {

        val purchaseValue = calcPurchaseValue(amount, value)

        val product = hashMapOf(
            "description" to description,
            "value" to value,
            "amount" to amount,
            "purchaseValue" to purchaseValue
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
                binding.valueAmount.setText("")
                binding.nameProduct.requestFocus()
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun calcPurchaseValue(amount:Int, value: Double): Double {
        val purchaseValue = amount * value
        return (purchaseValue * 100.0).roundToInt() / 100.0
    }

    private fun validAmount (textAmount:String): Int {

        if (textAmount.isBlank()){
            return 1
        }

        if (textAmount.toInt() == 0){
            return 1
        }

        return textAmount.toInt()
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