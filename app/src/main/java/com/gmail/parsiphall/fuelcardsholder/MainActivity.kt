package com.gmail.parsiphall.fuelcardsholder

import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.gmail.parsiphall.fuelcardsholder.fragments.MainFragment
import com.gmail.parsiphall.fuelcardsholder.interfaces.MainView
import moxy.MvpAppCompatActivity
import moxy.MvpAppCompatFragment

class MainActivity : MvpAppCompatActivity(), MainView {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentPlace(MainFragment())
    }

    override fun fragmentPlace(fragment: MvpAppCompatFragment, bundle: Bundle?) {
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}