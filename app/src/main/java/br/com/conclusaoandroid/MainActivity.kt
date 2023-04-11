package br.com.conclusaoandroid

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingAdapter
import br.com.conclusaoandroid.databinding.ActivityMainBinding
import com.example.mobcompoents.cusomtoast.CustomToast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

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

        shoppingAdapter = object : ShoppingAdapter(shoppingQuery) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    println("vazio")
                    //TODO: colocar uma tela vazia ou somente mostrar um texto vaizo
                } else {
                    binding.recyclerShopping.adapter = shoppingAdapter
                }
            }
        }

        setupListener(auth.uid.toString())
    }

    private fun setupListener(userId:String) {
        binding.addShopping.setOnClickListener{

            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Entre com o nome do mercado")
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_edittext, null)
            val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
            builder.setView(dialogLayout)
            builder.setPositiveButton("Add") {
                    _, _ -> if (editText.text.isNotBlank()) {
                        addShopping(editText.text.toString(), userId)
                    }
            }

            builder.setNegativeButton("Cancelar") {
                    dialog, _ -> dialog.cancel()
            }

            builder.show()
        }
    }

    private fun addShopping(marketplace:String, userId:String){

        val shoppingItem = hashMapOf(
            "description" to "",
            "value" to 0
        )

        val shopping = hashMapOf(
            "userId" to userId,
            "marketplace" to marketplace,
            "date" to Timestamp(Date()),
            "items" to arrayListOf(shoppingItem)
        )

        Firebase.firestore.collection("shopping")
            .add(shopping)
            .addOnSuccessListener { documentReference ->
                CustomToast.success( this, "Cadastrado com sucesso :)" )
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
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
}