package com.example.homepharmacy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class PriceListAdapter(
    private var items: List<Medicine>,
    private val onItemClick: (Medicine) -> Unit
) : RecyclerView.Adapter<PriceListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_price_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val med = items[position]
        holder.bind(med)
        holder.itemView.setOnClickListener { onItemClick(med) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Medicine>) {
        items = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTv: TextView = itemView.findViewById(R.id.tvName)
        private val priceTv: TextView = itemView.findViewById(R.id.tvPrice)
        private val statusTv: TextView = itemView.findViewById(R.id.tvStatus)
        private val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(med: Medicine) {
            nameTv.text = med.name

            val priceText = if (med.price.isNotEmpty()) "${med.price} ₽" else "—"
            priceTv.text = priceText

            if (med.price.isEmpty()) {
                priceTv.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
            } else {
                priceTv.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.black))
            }

            statusTv.text = if (med.status) "Есть" else "Нужно купить"

            val bgColor = if (med.price.isEmpty()) {
                android.graphics.Color.parseColor("#3E465F")  // если нет цены
            } else {
                android.graphics.Color.parseColor("#A6C6FF")  // если есть цена
            }
            cardView.setCardBackgroundColor(bgColor)
        }
    }
}