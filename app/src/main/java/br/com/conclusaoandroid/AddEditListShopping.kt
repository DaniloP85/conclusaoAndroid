package br.com.conclusaoandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.adapter.ShoppingAdapter
import br.com.conclusaoandroid.adapter.ShoppingListAdapter
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Shopping
import br.com.conclusaoandroid.model.ShoppingItem
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AddEditListShopping : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditListShoppingBinding;

    private lateinit var shoppingAdapter: ShoppingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditListShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        val documentId = bundle?.get("documentId")
        val queryShopping = Firebase.firestore
            .collection("shopping")
            .document(documentId.toString())
            .collection("products").limit(100)

        shoppingAdapter = object : ShoppingListAdapter(queryShopping, { }) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                } else {
                    binding.recyclerShoppingList.adapter = shoppingAdapter
                }
            }
        }
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