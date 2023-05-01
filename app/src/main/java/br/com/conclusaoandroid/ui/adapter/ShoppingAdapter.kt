package br.com.conclusaoandroid.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.databinding.ShoppingItemBinding
import br.com.conclusaoandroid.domain.model.Shopping
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

open class ShoppingAdapter(
    query: Query,
    private val onClick: (Shopping) -> Unit,
    private val onClickEditShopping: (Shopping) -> Unit) : FirestoreAdapter<ShoppingAdapter.ViewHolder>(query) {

    class ViewHolder(
        private val binding: ShoppingItemBinding,
        val onClick: (Shopping) -> Unit,
        val onClickEditShopping: (Shopping) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        private var currentShopping: Shopping? = null

        init {
            itemView.setOnClickListener {
                currentShopping?.let {
                    onClick(it)
                }
            }
        }

        @SuppressLint("LongLogTag", "SimpleDateFormat")
        fun bind(shopping: Shopping, snapshotId: String) {

            currentShopping = shopping
            currentShopping?.documentId = snapshotId

            binding.marketplace.text = shopping.marketplace

            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 2
            format.setCurrency(Currency.getInstance("BRL")).toString()
            binding.value.text = format.format(shopping.total)

            //binding.marketplace.text = "${shopping.marketplace} | ${format.format(shopping.total)}"

            val pattern = "dd/MM/yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date = shopping.date?.toDate()?.let { simpleDateFormat.format(it) }

            binding.date.text = date



            binding.removeShopping.setOnClickListener {
                removeShopping(shopping, snapshotId)
            }

            binding.editShopping.setOnClickListener {
                currentShopping?.let {
                    onClickEditShopping(shopping)
                }
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
        val view = ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view, onClick, onClickEditShopping)
    }
}