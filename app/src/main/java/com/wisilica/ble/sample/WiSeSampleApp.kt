package com.wisilica.ble.sample

import android.app.Application
import com.wisilica.wiseconnect.WiSeConnect
import com.wisilica.wiseconnect.utility.Logger

class WiSeSampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Logger.enable()
        WiSeConnect.initialise();
    }
}