package br.com.conclusaoandroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.databinding.ShoppingItemBinding
import br.com.conclusaoandroid.model.Shopping
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

open class ShoppingAdapter(query: Query) : FirestoreAdapter<ShoppingAdapter.ViewHolder>(query) {

    class ViewHolder(val binding: ShoppingItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(shopping: Shopping) {
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