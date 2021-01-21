package com.gmail.parsiphall.fuelcardsholder.interfaces

import android.os.Bundle
import com.arellomobile.mvp.MvpView
import moxy.MvpAppCompatFragment

interface MainView : MvpView {
    fun fragmentPlaceMain(fragment: MvpAppCompatFragment)
    fun fragmentPlace(fragment: MvpAppCompatFragment, bundle: Bundle? = null)
}