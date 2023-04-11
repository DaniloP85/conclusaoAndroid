package br.com.conclusaoandroid.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.databinding.ShoppingItemBinding
import br.com.conclusaoandroid.model.Shopping
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

open class ShoppingAdapter(query: Query) : FirestoreAdapter<ShoppingAdapter.ViewHolder>(query) {

    class ViewHolder(val binding: ShoppingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("LongLogTag")
        fun bind(shopping: Shopping, snapshotId: String) {
            if (shopping == null) {
                return
            }

            binding.marketplace.text = shopping.marketplace

            var pattern = "dd/MM/yyyy";
            var simpleDateFormat = SimpleDateFormat(pattern);
            var date = shopping.date?.toDate()?.let { simpleDateFormat.format(it) };

            binding.date.text = date

            val total = listOf(shopping.items?.map { x -> x.value })[0]?.reduce { acc, d -> acc?.plus(d ?: 0.0)}
            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 2
            format.setCurrency(Currency.getInstance("BRL")).toString()
            binding.value.text = format.format(total)

            binding.removeShopping.setOnClickListener {
                removeShopping(shopping, snapshotId)
            }

            binding.editShopping.setOnClickListener {
                println("abrir edição")
                Log.d(TAG, "idDocument ${snapshotId} shopping: ${shopping}")
            }
        }

        @SuppressLint("LongLogTag")
        private fun removeShopping(shopping: Shopping, snapshotId: String){

            Firebase
                .firestore
                .collection("shopping")
                .document(snapshotId)
                .update("userId", "${shopping.userId}_99999")
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                }.addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }

        companion object {
            private const val TAG = "ShoppingAdapter.ViewHolder.bind"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snapshot = getSnapshot(position)
        snapshot.toObject<Shopping>()?.let {
            holder.bind(it, snapshot.id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}