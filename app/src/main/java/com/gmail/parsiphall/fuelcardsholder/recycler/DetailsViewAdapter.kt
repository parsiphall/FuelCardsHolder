package com.gmail.parsiphall.fuelcardsholder.recycler

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Note
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Collections.reverse

class DetailsViewAdapter(private var items: List<Note>, private val context: Context) :
    RecyclerView.Adapter<DetailsViewHolder>() {

    private val ad = AlertDialog.Builder(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DetailsViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item_details, parent, false)
    )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.detailsDate.text = items[position].date
        holder.detailsDifference.text = ("%.2f".format(items[position].difference))
    }

    fun dataChanged(newItems: List<Note>) {
        items = newItems
        notifyDataSetChanged()
    }
}