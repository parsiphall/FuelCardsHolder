package com.gmail.parsiphall.fuelcardsholder.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card
import com.gmail.parsiphall.fuelcardsholder.data.Note
import com.gmail.parsiphall.fuelcardsholder.interfaces.MainView
import com.gmail.parsiphall.fuelcardsholder.recycler.DetailsViewAdapter
import com.gmail.parsiphall.fuelcardsholder.recycler.OnItemClickListener
import com.gmail.parsiphall.fuelcardsholder.recycler.addOnItemClickListener
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import java.util.*
import java.util.Collections.reverse
import kotlin.collections.ArrayList

class DetailsFragment : MvpAppCompatFragment() {

    private lateinit var callbackActivity: MainView
    private var items: List<Note> = ArrayList()
    private lateinit var adapter: DetailsViewAdapter
    private lateinit var ad: AlertDialog.Builder
    private lateinit var note: Note
    private lateinit var card: Card


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackActivity = context as MainView
        ad = AlertDialog.Builder(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        card = bundle?.getSerializable("ITEM") as Card
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_details, container, false)
        adapter = DetailsViewAdapter(items, context!!)
        root.details_recycler.layoutManager = LinearLayoutManager(context)
        root.details_recycler.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        details_add_fab.setOnClickListener {
            note = Note()
            val total = card.balance
            val btn1 = getString(R.string.adOutcome)
            val btn2 = getString(R.string.adIncome)
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
            val date = dialogView.findViewById<TextView>(R.id.dialog_add_note_date)
            val difference = dialogView.findViewById<EditText>(R.id.dialog_add_note_difference)
            date.setOnClickListener {
                datePickerDialog(it)
            }
            ad
                .setView(dialogView)
                .setTitle(getString(R.string.adAddNote))
                .setCancelable(false)
                .setPositiveButton(btn1) { _, _ ->
                    note.cardId = card.id
                    note.date = date.text.toString()
                    note.difference = (difference.text.toString().toFloat() / 100) * 100
                    card.balance = total - note.difference
                    saveData()
                }
                .setNegativeButton(btn2) { _, _ ->
                    note.cardId = card.id
                    note.date = date.text.toString()
                    note.difference = (difference.text.toString().toFloat() / 100) * 100
                    card.balance = total + note.difference
                    saveData()
                }
                .show()
        }
    }

    private fun saveData() {
        val dataSave = GlobalScope.async {
            DB.getDao().addNote(note)
            DB.getDao().updateCard(card)
        }
        MainScope().launch {
            dataSave.await()
            getData()
        }
    }

    private fun getData() {
        val data = GlobalScope.async {
            items = DB.getDao().getNotesForCard(card.id)
            reverse(items)
        }
        MainScope().launch {
            data.await()
            details_balance_textView.text = card.balance.toString()
            adapter.dataChanged(items)
            details_card_name.text = card.name
            details_card_number.text = card.number
        }
    }

    private fun dateListener(v: View): DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            var myMonth = (month + 1).toString()
            var myDay = dayOfMonth.toString()
            if (month < 9) {
                myMonth = "0$myMonth"
            }
            if (dayOfMonth < 10) {
                myDay = "0$myDay"
            }
            val date = "$myDay/$myMonth/$year"
            (v as TextView).text = date
        }

    private fun datePickerDialog(v: View) {
        val cal = Calendar.getInstance()
        val year: Int
        val month: Int
        val dayOfMonth: Int
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(
            context!!,
            dateListener(v),
            year,
            month,
            dayOfMonth
        ).show()
    }
}