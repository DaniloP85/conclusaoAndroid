package br.com.conclusaoandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.Shopping
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class AddEditListShopping : AppCompatActivity() {
    private lateinit var binding: ActivityAddEditListShoppingBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditListShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        val documentId = bundle?.get("documentId")

        Firebase.firestore
            .collection("shopping")
            .document("${documentId.toString()}")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val shopping = documentSnapshot.toObject<Shopping>()
                binding.nameMarketplace.text = shopping?.marketplace
            }
    }
}