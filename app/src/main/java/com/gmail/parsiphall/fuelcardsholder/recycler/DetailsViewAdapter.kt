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
        holder.detailsDifference.text = items[position].difference.toString()
        holder.detailsDelete.setOnClickListener {
            val btn1 = context.getString(R.string.adYes)
            val btn2 = context.getString(R.string.adNo)
            ad
                .setTitle(items[position].date)
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

    fun dataChanged(newItems: List<Note>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun delete(position: Int) = GlobalScope.launch {
        val cardID = items[position].cardId
        DB.getDao().deleteNote(items[position])
        items = DB.getDao().getNotesForCard(cardID)
        reverse(items)
        MainScope().launch {
            notifyDataSetChanged()
        }
    }
}