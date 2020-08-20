package com.gmail.parsiphall.fuelcardsholder.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*

class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardName = view.item_card_name!!
    val cardNumber = view.item_card_number!!
    val cardBalance = view.item_card_balance!!
    val delete = view.item_card_delete!!
}