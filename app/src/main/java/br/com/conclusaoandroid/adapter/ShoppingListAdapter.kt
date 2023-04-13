package br.com.conclusaoandroid.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.databinding.ShoppingListItemBinding
import br.com.conclusaoandroid.model.Products
import br.com.conclusaoandroid.model.Shopping
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

open class ShoppingListAdapter(query: Query, val documentIdFather: String, private val onClick: (Products) -> Unit) : FirestoreAdapter<ShoppingListAdapter.ShoppingListViewHolder>(query) {

    class ShoppingListViewHolder(val binding: ShoppingListItemBinding, val documentIdFather: String, val onClick: (Products) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        private var currentProduct: Products? = null

        init {
            itemView.setOnClickListener {
                currentProduct?.let {
                    onClick(it)
                }
            }
        }

        fun bind(word: Products, snapshotId: String) {

            currentProduct = word
            currentProduct?.documentId = snapshotId
            binding.description.text = word.description
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 2
            format.setCurrency(Currency.getInstance("BRL")).toString()
            binding.valueList.text = format.format(word.value)

            binding.removeProduct.setOnClickListener{
                removeProduct(documentIdFather, snapshotId)
            }
        }


        @SuppressLint("LongLogTag")
        private fun removeProduct(idFather: String, snapshotId: String){

            Firebase
                .firestore
                .collection("shopping")
                .document(idFather)
                .collection("products")
                .document(snapshotId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }.addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

        companion object {
            private const val TAG = "ShoppingAdapter.ViewHolder.bind"
        }
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val snapshot = getSnapshot(position)
        snapshot.toObject<Products>()?.let {
            holder.bind(it, snapshot.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = ShoppingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(view, documentIdFather, onClick)
    }
}