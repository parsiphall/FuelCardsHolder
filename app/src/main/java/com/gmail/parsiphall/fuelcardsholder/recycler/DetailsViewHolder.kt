package com.gmail.parsiphall.fuelcardsholder.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_details.view.*

class DetailsViewHolder(view:View): RecyclerView.ViewHolder(view) {
    val detailsDate = view.item_details_date!!
    val detailsDifference = view.item_details_change!!
    val detailsDelete = view.item_details_delete!!
}