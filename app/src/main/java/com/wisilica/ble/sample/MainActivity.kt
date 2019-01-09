package com.wisilica.ble.sample

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.wisilica.wiseconnect.WiSeMeshDevice
import com.wisilica.wiseconnect.devices.WiSeDeviceOperationTypes
import com.wisilica.wiseconnect.devices.WiSeOperationListener
import com.wisilica.wiseconnect.utility.ErrorHandler
import com.wisilica.wiseconnect.utility.WiSeMeshError
import com.wisilica.wiseconnect.utility.WiSeMeshStatus
import java.util.ArrayList

class MainActivity : BaseActivity(), OnItemClickListener {


    val REQUEST_CODE_ADD_NEW_DEVICE = 100
    val mListOfDevices: ArrayList<WiSeMeshDevice> = arrayListOf()
    var mDeviceListAdapter: DeviceListAdapter<WiSeMeshDevice>? = DeviceListAdapter(mListOfDevices)
    val rv_deviceList by bind<RecyclerView>(R.id.rv_deviceList);


    val fab by bind<FloatingActionButton>(R.id.fab)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar(false)



        fab.setOnClickListener(this)
        setDeviceList();


    }

    private fun setDeviceList() {
        mDeviceListAdapter?.onItemClickListener = this
        rv_deviceList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_deviceList.adapter = mDeviceListAdapter
    }


    override fun onItemClick(v: View, obj: Object, pos: Int) {
        var wiSeMeshDevice: WiSeMeshDevice? = null
        if (obj is WiSeMeshDevice) {
            wiSeMeshDevice = obj
        }
        when (v.id) {
            R.id.rb_off -> {
                operateDevice(wiSeMeshDevice, WiSeDeviceOperationTypes.MESH_DEVICE_OFF_V2)
                return
            }
            R.id.rb_on -> {
                operateDevice(wiSeMeshDevice, WiSeDeviceOperationTypes.MESH_DEVICE_ON_V2)
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun operateDevice(wiSeMeshDevice: WiSeMeshDevice?, operation: Int) {
        var sequenceNumber: Long = if (wiSeMeshDevice != null) wiSeMeshDevice.sequenceNumber + 1 else 0
        wiSeMeshDevice?.sequenceNumber = sequenceNumber
        val listener = object : WiSeOperationListener {
            override fun onSuccess(wiSeMeshDevice: WiSeMeshDevice, i: Int, l: Long) {

            }

            override fun onSuccess(wiSeMeshDevice: WiSeMeshDevice, o: Any, i: Int, l: Long) {

            }

            override fun onFailure(wiSeMeshDevice: WiSeMeshDevice, wiSeMeshError: WiSeMeshError, i: Int) {

            }

            override fun onSuccess(wiSeMeshDevice: WiSeMeshDevice, l: Long) {

            }

            override fun onFailure(wiSeMeshDevice: WiSeMeshDevice, i: Int) {

            }

            override fun gotDeviceToConnect(bluetoothDevice: BluetoothDevice, l: Long, i: Int): ByteArray {
                return ByteArray(0)
            }
        }
        val status = wiSeMeshDevice?.doOperation(this, operation, listener) as WiSeMeshStatus
        if (status.statusCode != ErrorHandler.CALL_SUCCESS) {
            Toast.makeText(this, status.statusMessage, Toast.LENGTH_LONG).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_ADD_NEW_DEVICE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val wiSeMeshDevice = data?.getParcelableExtra("device") as WiSeMeshDevice
                    if (wiSeMeshDevice != null && wiSeMeshDevice is WiSeMeshDevice) {
                        mListOfDevices.add(wiSeMeshDevice)
                        mDeviceListAdapter?.items = mListOfDevices
                        mDeviceListAdapter?.notifyDataSetChanged()
                    }
                }

                return
            }
        }
    }

    override fun onClick(v: View) {
        val id = v.id;
        when (id) {
            R.id.fab -> startActivityForResult(DeviceCommissioningActivity.newIntent(this), REQUEST_CODE_ADD_NEW_DEVICE)
        }

    }


}
