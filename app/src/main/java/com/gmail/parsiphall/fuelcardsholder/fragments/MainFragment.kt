package com.gmail.parsiphall.fuelcardsholder.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.parsiphall.fuelcardsholder.DB
import com.gmail.parsiphall.fuelcardsholder.R
import com.gmail.parsiphall.fuelcardsholder.data.Card
import com.gmail.parsiphall.fuelcardsholder.interfaces.MainView
import com.gmail.parsiphall.fuelcardsholder.recycler.CardViewAdapter
import com.gmail.parsiphall.fuelcardsholder.recycler.OnItemClickListener
import com.gmail.parsiphall.fuelcardsholder.recycler.addOnItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.tsuryo.swipeablerv.SwipeLeftRightCallback
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import kotlin.collections.ArrayList

class MainFragment : MvpAppCompatFragment() {

    private lateinit var callbackActivity: MainView
    private var items: List<Card> = ArrayList()
    private lateinit var adapter: CardViewAdapter
    private lateinit var ad: AlertDialog.Builder
    private lateinit var adD: AlertDialog.Builder
    private lateinit var card: Card

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbackActivity = context as MainView
        ad = AlertDialog.Builder(context)
        adD = AlertDialog.Builder(context)
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
                    .setMessage(items[position].name)
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.adYes)) { _, _ ->
                        val delete = GlobalScope.async { DB.getDao().deleteCard(items[position]) }
                        MainScope().launch {
                            delete.await()
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
                    if (name.text.isNotEmpty() && number.text.isNotEmpty()) {
                        card.name = name.text.toString()
                        card.number = number.text.toString()
                        val addCard = GlobalScope.async {
                            DB.getDao().addCard(card)
                        }
                        MainScope().launch {
                            addCard.await()
                            getData()
                        }
                    } else {
                        dialog.cancel()
                        Snackbar.make(view, getString(R.string.wrongData), Snackbar.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton(btn2) { dialog, _ ->
                    dialog.cancel()
                }
                .show()
        }
    }

    private fun getData() {
        val data = GlobalScope.async {
            items = DB.getDao().getAllCards()
        }
        MainScope().launch {
            data.await()
            adapter.dataChanged(items)
        }
    }

}