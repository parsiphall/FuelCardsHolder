package com.gmail.parsiphall.fuelcardsholder.recycler

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Collections.reverse

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
        holder.cardBalance.text = items[position].balance.toString()
        holder.delete.setOnClickListener {
            val btn1 = context.getString(R.string.adYes)
            val btn2 = context.getString(R.string.adNo)
            ad
                .setTitle(items[position].name)
                .setMessage(context.getString(R.string.adDeleteMessage))
                .setPositiveButton(btn1) { _, _ ->
                    delete(position)
                }
                .setNegativeButton(btn2) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
    }

    fun dataChanged(newItems: List<Card>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun delete(position: Int) = GlobalScope.launch {
        DB.getDao().deleteCard(items[position])
        items = DB.getDao().getAllCards()
        MainScope().launch {
            notifyDataSetChanged()
        }
    }

}