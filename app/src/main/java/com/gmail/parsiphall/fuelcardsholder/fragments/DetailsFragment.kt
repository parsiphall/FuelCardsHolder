package com.gmail.parsiphall.fuelcardsholder.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card
import com.gmail.parsiphall.fuelcardsholder.data.Note
import com.gmail.parsiphall.fuelcardsholder.interfaces.MainView
import com.gmail.parsiphall.fuelcardsholder.recycler.DetailsViewAdapter
import com.google.android.material.snackbar.Snackbar
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_details.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailsFragment : MvpAppCompatFragment() {

    private lateinit var callbackActivity: MainView
    private var items: List<Note> = ArrayList()
    private lateinit var adapter: DetailsViewAdapter
    private lateinit var ad: AlertDialog.Builder
    private lateinit var adD: AlertDialog.Builder
    private lateinit var note: Note
    private lateinit var card: Card
    private var cardBalance = 0f
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackActivity = context as MainView
        ad = AlertDialog.Builder(context)
        adD = AlertDialog.Builder(context)
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
        details_recycler.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedRight(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onSwipedLeft(position: Int) {
                adD
                    .setTitle(resources.getString(R.string.adDeleteMessage))
                    .setMessage("Дата - ${items[position].date}\nСумма  ${items[position].difference}")
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.adYes)) { _, _ ->
                        val delete = GlobalScope.async { DB.getDao().deleteNote(items[position]) }
                        MainScope().launch {
                            delete.await()
                            adapter.notifyItemRemoved(position)
                            getData()
                        }
                    }
                    .setNegativeButton(resources.getString(R.string.adNo)) { dialog, _ ->
                        dialog.cancel()
                        getData()
                    }
                    .show()
            }
        })
        details_add_fab.setOnClickListener {
            note = Note()
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
                .setPositiveButton(btn1) { dialog, _ ->
                    if (date.text == resources.getString(R.string.date) || difference.text.isEmpty()) {
                        dialog.cancel()
                        Snackbar.make(view, getString(R.string.wrongData), Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        note.cardId = card.id
                        note.date = date.text.toString()
                        note.difference = -difference.text.toString().toFloat()
                        saveNotes()
                    }
                }
                .setNegativeButton(btn2) { dialog, _ ->
                    if (date.text == resources.getString(R.string.date) || difference.text.isEmpty()) {
                        dialog.cancel()
                        Snackbar.make(view, getString(R.string.wrongData), Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        note.cardId = card.id
                        note.date = date.text.toString()
                        note.difference = difference.text.toString().toFloat()
                        saveNotes()
                    }
                }
                .show()
        }
        details_back_fab.setOnClickListener {
            callbackActivity.fragmentPlace(MainFragment())
        }
        details_share_fab.setOnClickListener {
            shareData()
        }
        details_fuelType.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent!!.getChildAt(0) as TextView).textSize = 20f
                (parent.getChildAt(0) as TextView).setTextColor(Color.GRAY)
                card.fuelType = details_fuelType.selectedItemPosition
                GlobalScope.launch {
                    DB.getDao().updateCard(card)
                }

            }
        })
    }

    private fun shareData() = GlobalScope.launch {
        var textToSend = "${resources.getString(R.string.cardNumber)} ${card.number}\n" +
                "${card.name}\n" +
                "${("%.2f".format(card.balance))} ${resources.getString(R.string.lit)}"
        if (adapter.itemCount != 0) {
            textToSend += "\n" +
                    getString(R.string.lastChanges)
        }
        if (adapter.itemCount >= 3) {
            for (i in 0..2) {
                val diff = if (items[i].difference > 0) {
                    "+${("%.2f".format(items[i].difference))}"
                } else {
                    ("%.2f".format(items[i].difference))
                }
                textToSend += "\n" +
                        "${items[i].date}   $diff"
            }
        } else if (adapter.itemCount != 0) {
            for (i in 0 until adapter.itemCount) {
                val diff = if (items[i].difference > 0) {
                    "+${("%.2f".format(items[i].difference))}"
                } else {
                    ("%.2f".format(items[i].difference))
                }
                textToSend += "\n" +
                        "${items[i].date}   $diff"
            }
        }
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share)))
    }

    private fun saveNotes() {
        val dataSave = GlobalScope.async {
            DB.getDao().addNote(note)
        }
        MainScope().launch {
            dataSave.await()
            getData()
        }
    }

    private fun updateCard() {
        card.balance = cardBalance
        card.fuelType = details_fuelType.selectedItemPosition
        GlobalScope.launch {
            DB.getDao().updateCard(card)
        }
    }

    private fun getData() {
        val data = GlobalScope.async {
            items = (DB.getDao().getNotesForCard(card.id)).sortedByDescending { sdf.parse(it.date) }
        }
        MainScope().launch {
            data.await()
            setData()
        }
    }

    private fun setData() {
        calculateCardBalance()
        details_balance_textView.text = ("%.2f".format(cardBalance))
        details_card_name.text = card.name
        details_card_number.text = card.number
        details_fuelType.setSelection(card.fuelType)
        adapter.dataChanged(items)
        updateCard()
    }

    private fun calculateCardBalance() {
        cardBalance = 0f
        items.forEach { cardBalance += it.difference }
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