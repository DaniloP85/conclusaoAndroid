package br.com.conclusaoandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.R
import br.com.conclusaoandroid.databinding.ShoppingItemBinding
import br.com.conclusaoandroid.databinding.ShoppingListItemBinding
import br.com.conclusaoandroid.model.Products
import br.com.conclusaoandroid.model.Shopping
import br.com.conclusaoandroid.model.ShoppingItem
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

open class ShoppingListAdapter(query: Query, private val onClick: (Products) -> Unit) : FirestoreAdapter<ShoppingListAdapter.ShoppingListViewHolder>(query) {

    // Describes an item view and its place within the RecyclerView
    class ShoppingListViewHolder(val binding: ShoppingListItemBinding, val onClick: (Products) -> Unit) : RecyclerView.ViewHolder(binding.root) {

        private var currentShopping: Products? = null

        init {
            itemView.setOnClickListener {
                currentShopping?.let {
                    onClick(it)
                }
            }
        }


        fun bind(word: Products, snapshotId: String) {
            binding.nameItem.text = word.description
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
        return ShoppingListViewHolder(view, onClick)
    }
}