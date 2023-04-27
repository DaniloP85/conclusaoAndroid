package br.com.conclusaoandroid

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
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
            val valueProduct = binding.valueProduct.text.toString()
            val descriptionText = binding.nameProduct.text.toString()
            val amountText = binding.valueAmount.text.toString()

            if (valueProduct.isBlank() || descriptionText.isBlank()) {
                CustomToast.warning(this, getString(R.string.fill_in_all_fields))
                return@setOnClickListener
            }

            val amount = Utils.validAmount(amountText)

            addProduct(valueProduct.toDouble(), descriptionText, amount)
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

    private fun adapterOnClick(productCurrent: Product) {
        val dialog = AddEditListShoppingDialogFragment()
        dialog.receiveData(
            productCurrent.description,
            productCurrent.value.toString(),
            productCurrent.amount,
            productCurrent
        )
        dialog.show(supportFragmentManager, dialog.tag)
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

    @SuppressLint("LongLogTag")
    private fun addProduct(value: Double, description: String, amount: Int) {

        val purchaseValue = Utils.calcPurchaseValue(amount, value)

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
                binding.recyclerShoppingList.requestFocus()
                hideKeyboard(this)
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view: View? = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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