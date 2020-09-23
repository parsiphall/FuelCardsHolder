package com.gmail.parsiphall.fuelcardsholder.recycler

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card

class CardViewAdapter(private var items: List<Card>, private val context: Context) :
    RecyclerView.Adapter<CardViewHolder>() {

    private val ad = AlertDialog.Builder(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.cardName.text = items[position].name
        holder.cardNumber.text = items[position].number
        holder.cardBalance.text = ("%.2f".format(items[position].balance))
        holder.cardFuelType.text =
            context.resources.getStringArray(R.array.fuelType)[items[position].fuelType]
        if (items[position].balance < 50) {
            holder.cardRoot.setCardBackgroundColor(Color.YELLOW)
        }
    }

    fun dataChanged(newItems: List<Card>) {
        items = newItems
        notifyDataSetChanged()
    }
}