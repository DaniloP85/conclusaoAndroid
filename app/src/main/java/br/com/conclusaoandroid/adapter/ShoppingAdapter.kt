package br.com.conclusaoandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.model.Shopping
import br.com.conclusaoandroid.databinding.ShoppingItemBinding
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject


open class ShoppingAdapter(query: Query) : FirestoreAdapter<ShoppingAdapter.ViewHolder>(query) {

    class ViewHolder(val binding: ShoppingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shopping: Shopping) {
            if (shopping == null) {
                return
            }

            binding.marketplace.text = shopping.marketplace
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getSnapshot(position).toObject<Shopping>()?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}