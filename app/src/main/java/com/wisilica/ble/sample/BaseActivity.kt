package com.wisilica.ble.sample

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.wisilica.wiseconnect.utility.Logger

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener  {
    val REQUEST_CODE_ACCESS_COARSE_LOCATION = 100
    val REQUEST_CODE_BLUETOOTH_ADMIN = 101
    val TAG : String=javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

     fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
         @Suppress("UNCHECKED_CAST")
         return unsafeLazy { findViewById(idRes) as T }
     }

     fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
         @Suppress("UNCHECKED_CAST")
         return unsafeLazy { findViewById(idRes) as T }
     }

    override fun onClick(v: View) {
        Logger.enable()
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
     private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)


     fun setupPermissions() : Boolean{
        var permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_ADMIN)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                    REQUEST_CODE_BLUETOOTH_ADMIN)
           return false
        }
        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_CODE_ACCESS_COARSE_LOCATION)
            return false
        }
        return true
    }

     fun setToolbar(showBackArrow:Boolean){
         val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
         toolbar.setNavigationOnClickListener { onBackPressed() }
         setSupportActionBar(toolbar)
         if(showBackArrow){
             supportActionBar?.setDisplayHomeAsUpEnabled(true)
             supportActionBar?.setDisplayShowHomeEnabled(true)
         }

     }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}