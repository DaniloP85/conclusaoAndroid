package br.com.conclusaoandroid

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingAdapter
import br.com.conclusaoandroid.databinding.ActivityMainBinding
import br.com.conclusaoandroid.model.Shopping
import com.example.mobcompoents.cusomtoast.CustomToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var auth: FirebaseAuth;
    private lateinit var shoppingAdapter: ShoppingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        window.statusBarColor = Color.parseColor("#0075FF")

        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            gotoLogin()
        }

        binding.logMenu.logout.setOnClickListener {
            auth.signOut()
            gotoLogin()
        }

        var shoppingQuery = Firebase.firestore
            .collection("shopping")
            .whereEqualTo("userId", auth.uid)
            .limit(50)

        shoppingAdapter = object : ShoppingAdapter(shoppingQuery, { shopping -> adapterOnClick(shopping) }) {
            override fun onDataChanged() {

                if (itemCount == 0) {
                    binding.rltHome.visibility = View.GONE
                    binding.rltEmptyState.visibility = View.VISIBLE
                } else {
                    binding.rltHome.visibility = View.VISIBLE
                    binding.rltEmptyState.visibility = View.GONE
                    binding.recyclerShopping.adapter = shoppingAdapter
                }
            }
        }

        setupListener(auth.uid.toString())
    }

    private fun adapterOnClick(shopping: Shopping) {
        val intent = Intent(this, AddEditListShopping::class.java)
        intent.putExtra("documentId", "${shopping.documentId}")
        intent.putExtra("marketPlace", "${shopping.marketplace}")

        var pattern = "dd/MM/yyyy";
        var simpleDateFormat = SimpleDateFormat(pattern);
        var date = shopping.date?.toDate()?.let { simpleDateFormat.format(it) };
        intent.putExtra("marketDate", "$date")

        startActivity(intent)
        finish()
    }

    private fun setupListener(userId:String) {
        binding.addShopping.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle(getString(R.string.enter_market_name))
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton(R.string.add_new) {
                    _, _ -> if (editText.text.isNotBlank()) {
                        addShopping(editText.text.toString(), userId)
                    }
            }

            builder.setNegativeButton(R.string.cancel) {
                    dialog, _ -> dialog.cancel()
            }

            builder.show()
        }
    }

    private fun addShopping(marketplace:String, userId:String){

        val shopping = hashMapOf(
            "userId" to userId,
            "marketplace" to marketplace,
            "date" to Timestamp.now(),
            "total" to 0
        )

        Firebase
            .firestore
            .collection("shopping")
            .add(shopping)
            .addOnSuccessListener { documentReference ->
                CustomToast.success( this, getString(R.string.registered_successfully) )
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    private fun gotoLogin() {
        val intent = Intent(this, Login::class.java);
        startActivity(intent);
        finish();
    }

    public override fun onStart() {
        super.onStart()
        shoppingAdapter.startListening()
    }

    public override fun onStop() {
        super.onStop()
        shoppingAdapter.stopListening()
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}