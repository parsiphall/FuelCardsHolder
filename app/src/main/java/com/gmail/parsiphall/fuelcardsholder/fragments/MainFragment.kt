package com.gmail.parsiphall.fuelcardsholder.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.DB_NAME
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card
import com.gmail.parsiphall.fuelcardsholder.interfaces.MainView
import com.gmail.parsiphall.fuelcardsholder.recycler.CardViewAdapter
import com.gmail.parsiphall.fuelcardsholder.recycler.OnItemClickListener
import com.gmail.parsiphall.fuelcardsholder.recycler.addOnItemClickListener
import com.gmail.parsiphall.importexportdb.ExportDB
import com.gmail.parsiphall.importexportdb.ImportDB
import com.google.android.material.snackbar.Snackbar
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import java.util.*
import kotlin.collections.ArrayList

class MainFragment : MvpAppCompatFragment() {

    private lateinit var callbackActivity: MainView
    private var items: List<Card> = ArrayList()
    private var itemsToShare: MutableList<Card> = ArrayList()
    private lateinit var adapter: CardViewAdapter
    private lateinit var ad: AlertDialog.Builder
    private lateinit var adD: AlertDialog.Builder
    private lateinit var adS: AlertDialog.Builder
    private lateinit var adSet: AlertDialog.Builder
    private lateinit var card: Card

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackActivity = context as MainView
        ad = AlertDialog.Builder(context)
        adD = AlertDialog.Builder(context)
        adS = AlertDialog.Builder(context)
        adSet = AlertDialog.Builder(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        adapter = CardViewAdapter(items, context!!)
        root.main_recycler.layoutManager = LinearLayoutManager(context)
        root.main_recycler.adapter = adapter
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        main_recycler.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                val bundle = Bundle()
                bundle.putSerializable("ITEM", items[position])
                callbackActivity.fragmentPlace(DetailsFragment(), bundle)
            }
        })
        main_recycler.setListener(object : SwipeLeftRightCallback.Listener {
            override fun onSwipedRight(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onSwipedLeft(position: Int) {
                adD
                    .setTitle(resources.getString(R.string.adDeleteMessage))
                    .setMessage("Карта - ${items[position].name}\nНомер - ${items[position].number}")
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.adYes)) { _, _ ->
                        val delete = GlobalScope.async { DB.getDao().deleteCard(items[position]) }
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
        main_add_fab.setOnClickListener {
            card = Card()
            val btn1 = getString(R.string.adAdd)
            val btn2 = getString(R.string.adCancel)
            val dialogView: View = layoutInflater.inflate(R.layout.dialog_add_card, null)
            val name = dialogView.findViewById<EditText>(R.id.dialog_card_name)
            val number = dialogView.findViewById<EditText>(R.id.dialog_card_number)
            ad
                .setView(dialogView)
                .setTitle(getString(R.string.adAddCard))
                .setCancelable(false)
                .setPositiveButton(btn1) { dialog, _ ->
                    if (name.text.isEmpty() || number.text.isEmpty()) {
                        dialog.cancel()
                        Snackbar.make(view, getString(R.string.wrongData), Snackbar.LENGTH_LONG)
                            .show()
                    } else {
                        card.name = name.text.toString()
                        card.number = number.text.toString()
                        val addCard = GlobalScope.async {
                            DB.getDao().addCard(card)
                        }
                        MainScope().launch {
                            addCard.await()
                            getData()
                        }
                    }
                }
                .setNegativeButton(btn2) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
        main_settings_fab.setOnClickListener {
            val btn1 = getString(R.string.adCancel)
            val dialogView: View = layoutInflater.inflate(R.layout.dialog_settings, null)
            val exportButton = dialogView.findViewById<Button>(R.id.dialog_settings_export)
            val importButton = dialogView.findViewById<Button>(R.id.dialog_settings_import)
            adSet
                .setView(dialogView)
                .setTitle(getString(R.string.settings))
                .setCancelable(false)
                .setPositiveButton(btn1) { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = adSet.create()
            exportButton.setOnClickListener {
                GlobalScope.launch { ExportDB.launch(context!!, DB_NAME) }
                dialog.cancel()
            }
            importButton.setOnClickListener {
                val data = GlobalScope.async { ImportDB.launch(context!!, DB_NAME) }
                MainScope().launch {
                    data.await()
                    callbackActivity.fragmentPlaceMain(MainFragment())
                }
                dialog.cancel()
            }
            dialog.show()
        }
        main_share_fab.setOnClickListener {
            shareDataWithOptions()
        }
    }

    private fun shareDataWithOptions() {
        val btn1 = resources.getString(R.string.share)
        val btn2 = resources.getString(R.string.adCancel)
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_share_data, null)
        val root = dialogView.findViewById<LinearLayout>(R.id.dialog_share_data_root)
        items.forEach {
            val cb = CheckBox(activity)
            cb.text = it.name
            cb.id = it.id
            cb.isChecked = false
            root.addView(cb)
        }
        adS
            .setView(dialogView)
            .setTitle(resources.getString(R.string.share))
            .setCancelable(false)
            .setPositiveButton(btn1) { _, _ ->
                items.forEach {
                    val cb = root.findViewById<CheckBox>(it.id)
                    if (cb.isChecked) {
                        itemsToShare.add(it)
                    }
                }
                root.removeAllViews()
                shareData()
            }
            .setNegativeButton(btn2) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun shareData() = GlobalScope.launch {
        val c = Calendar.getInstance()
        var textToSend =
            "${c.get(Calendar.DAY_OF_MONTH)}-${c.get(Calendar.MONTH) + 1}-${c.get(Calendar.YEAR)}\n\n"
        itemsToShare.forEach {
            val balance = ("%.2f".format(it.balance))
            textToSend += "${it.number}(${it.name})   $balance\n"
        }
        itemsToShare.clear()
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToSend)
        sendIntent.type = "text/plain"
        startActivity(Intent.createChooser(sendIntent, resources.getString(R.string.share)))
    }

    private fun getData() {
        val data = GlobalScope.async {
            items = (DB.getDao().getAllCards()).sortedBy { it.name }
        }
        MainScope().launch {
            data.await()
            adapter.dataChanged(items)
        }
    }

}