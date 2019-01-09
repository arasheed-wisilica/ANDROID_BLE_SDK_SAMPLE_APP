package com.wisilica.ble.sample

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.wisilica.wiseconnect.WiSeConnect
import com.wisilica.wiseconnect.WiSeMeshDevice
import com.wisilica.wiseconnect.WiseNetworkInfo
import com.wisilica.wiseconnect.commissioning.WiSeDeviceCommissioningCallback
import com.wisilica.wiseconnect.commissioning.WiSeDeviceCommissioningInterface
import com.wisilica.wiseconnect.commissioning.WiSeDeviceScanCallBack
import com.wisilica.wiseconnect.commissioning.WiSeScanResult
import com.wisilica.wiseconnect.utility.*
import java.util.*

class DeviceCommissioningActivity : BaseActivity(), WiSeDeviceCommissioningCallback, WiSeDeviceScanCallBack, OnItemClickListener {


    var mSnackbar: Snackbar? = null
    var mProgressDialog: ProgressDialogUtility? = null;
    val mScanResult: ArrayList<WiSeScanResult> = arrayListOf()
    var mWiSeDeviceCommissioningInterface: WiSeDeviceCommissioningInterface<*>? = null
    var mMenu: Menu? = null;
    val rv_deviceList: RecyclerView by bind<RecyclerView>(R.id.rv_deviceList)
    val mAdapter: DeviceListAdapter<WiSeScanResult> = DeviceListAdapter(mScanResult)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setToolbar(true)
        mProgressDialog = ProgressDialogUtility()
        mWiSeDeviceCommissioningInterface = WiSeConnect.getDeviceCommissioningService(this)
        setDeviceList()
    }

    override fun onItemClick(v: View, obj: Object, pos: Int) {

        if (obj is WiSeScanResult) {
            displayMessage(obj.deviceName)
            connectDevice(obj)
        }
    }

    private fun displayMessage(msg: String) {
        if (mSnackbar != null) {
            mSnackbar?.dismiss()
        }

        mSnackbar=Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
        mSnackbar?.show()


    }

    private fun connectDevice(obj: WiSeScanResult) {
        obj.networkInfo = createNetworkInfo()
        val status = obj.connectToDevice(this, this)
        if (status.statusCode != ErrorHandler.CALL_SUCCESS) {
            displayMessage(status.statusMessage)
        }else{
            mProgressDialog?.show(this,"Connecting to device")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_scan, menu)
        mMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return when (id) {
            R.id.action_scan -> {
                if (setupPermissions()) {
                    if (mWiSeDeviceCommissioningInterface?.isScanning == true) {
                        stopDeviceScan()
                    } else {
                        startDeviceScan()
                    }
                }
                setupMenu()

                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun startDeviceScan() {
        mScanResult.clear()
        mAdapter.notifyDataSetChanged()
        mWiSeDeviceCommissioningInterface?.setAvoidDuplicatePacket(true)
        mWiSeDeviceCommissioningInterface?.startWiseDeviceScan(this)
    }

    @SuppressLint("MissingPermission")
    private fun stopDeviceScan() {
        mWiSeDeviceCommissioningInterface?.stopWiSeDeviceScanning(this)
        setupMenu()

    }

    override fun scanResult(scanResult: WiSeScanResult) {
        mScanResult.add(scanResult)
        mAdapter.items = mScanResult
        mAdapter.notifyDataSetChanged()

    }

    private fun setDeviceList() {
        mAdapter.onItemClickListener = this;
        rv_deviceList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_deviceList.adapter = mAdapter


    }

    override fun onFailure(p0: Int) {
    }

    override fun onFailure(p0: Int, p1: ByteArray?) {
    }

    override fun onFailure(p0: WiSeMeshError?) {
    }

    override fun onFailure(p0: WiSeMeshError?, p1: ByteArray?) {
    }

    override fun scanFinished(p0: Long) {
    }

    override fun onDeviceCapabilityRead(p0: WiSeScanResult?, p1: Int, p2: ByteArray?) {
    }

    override fun pairingSuccess(wiseScanResult: WiSeScanResult?, wiseMeshDevice: WiSeMeshDevice?) {
        runOnUiThread {
            mProgressDialog?.dismiss()
           displayMessage( wiseMeshDevice?.deviceName + " Paired.")
            val intent = Intent()
            intent.putExtra("device", wiseMeshDevice)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onTestSuccess(p0: WiSeScanResult?) {
    }

    override fun onTestFailure(p0: WiSeScanResult?, p1: WiSeMeshError?) {
    }

    override fun deviceConnected(scanResult: WiSeScanResult?) {
        runOnUiThread {
            stopDeviceScan()
             Thread.sleep(1 * 1000) //just for a safe connection
            displayMessage( scanResult?.deviceName + " Connected.")

            mProgressDialog?.setMessage("Pairing...")
            //scanResult?.networkInfo = createNetworkInfo()
            scanResult?.pairWiSeDevice(this, getRandomMeshId(), getDevicePairingKey(), this)
        }
    }

    private fun createNetworkInfo(): WiseNetworkInfo? {

        return WiseNetworkInfo(getNetworkId(), getNetworkKey(), getPhoneMeshId())
    }

    private fun getPhoneMeshId(): Int {

        return getRandomMeshId()
    }

    private fun getNetworkKey(): ByteArray? {

        return byteArrayOf(0x00, 0x01, 0x02, 0x03,
                0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x0a, 0x0b,
                0x0c, 0x0d, 0x0e, 0x0f)
    }


    private fun getNetworkId(): Long {

        return 0x1234
    }

    private fun getDevicePairingKey(): ByteArray? {
        //for secure pairing you should provide device pairing key which can be obtained from server
        //here we are doing non-secure pairing and hence returns null
        return null

    }

    private fun getRandomMeshId(): Int {

        val random = Random()
        val meshId: Int = random.nextInt(4094) + 1
        if (!MeshValidator.isValidDeviceId(meshId)) {
            return getRandomMeshId()
        }
        return meshId

    }

    override fun onChildDeviceFound(p0: WiSeScanResult?, p1: Int) {
    }

    override fun deviceDisconnected(scanResult: WiSeScanResult?, error: WiSeMeshError?) {
        runOnUiThread {
            mProgressDialog?.dismiss()
            Logger.e(TAG, "deviceDisconnected() : Error Code : " + error?.errorCode + " Msg : " + error?.errorMessage)
            displayMessage(" Disconnected, Error Code: " + error?.errorCode)
        }
    }

    override fun pairingFailed(scanResult: WiSeScanResult?, error: WiSeMeshError?) {
        runOnUiThread {
            Logger.e(TAG, "pairingFailed() : Error Code : " + error?.errorCode + " Msg : " + error?.errorMessage)
            mProgressDialog?.dismiss()
            displayMessage(scanResult?.deviceName + " Pairing failed, Error Code: " + error?.errorCode)
        }
    }

    override fun onChildDevicePaired(p0: WiSeScanResult?, p1: WiSeMeshDevice?) {
    }


    fun setupMenu() {

        val menuItem: MenuItem? = mMenu?.findItem(R.id.action_scan);
        if (mWiSeDeviceCommissioningInterface?.isScanning == true) {
            menuItem?.title = getString(R.string.action_stop);
        } else {
            menuItem?.title = getString(R.string.action_scan);
        }
        onPrepareOptionsMenu(mMenu)
    }


    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, DeviceCommissioningActivity::class.java)
            return intent
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDeviceScan()
    }
}