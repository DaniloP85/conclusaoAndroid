package br.com.conclusaoandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.conclusaoandroid.R
import br.com.conclusaoandroid.databinding.ActivityAddEditListShoppingBinding
import br.com.conclusaoandroid.model.ShoppingItem
import br.com.conclusaoandroid.model.ShoppingItemFormatted
import org.w3c.dom.Text

class AddEditAdapter(private val marketList: List<ShoppingItemFormatted>)
    : RecyclerView.Adapter<AddEditAdapter.MarketViewHolder>() {
    class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.marketDescription)
        val price: TextView = itemView.findViewById(R.id.marketValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_market_list, parent, false)

        return MarketViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val market: ShoppingItemFormatted = marketList[position]

        holder.description.text = market.description
        holder.price.text = market.value.toString()
    }

    override fun getItemCount(): Int {
        return marketList.size
    }

}