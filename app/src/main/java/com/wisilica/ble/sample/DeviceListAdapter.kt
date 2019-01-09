package com.wisilica.ble.sample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.wisilica.wiseconnect.WiSeMeshDevice
import com.wisilica.wiseconnect.commissioning.WiSeScanResult
import com.wisilica.wiseconnect.devices.WiSeMeshOperatableDevice
import java.util.*

class DeviceListAdapter<T>() : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {
    internal var items: ArrayList<T> = arrayListOf()
     var onItemClickListener: OnItemClickListener? = null;

    constructor(items1: ArrayList<T>) : this() {
        items = items1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {

        val inflater = LayoutInflater.from(viewGroup.context)
        val view = inflater.inflate(R.layout.rv_device_list, viewGroup, false)
        return ViewHolder(view)
            //TODO do other stuff here


       // return ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_device_list, viewGroup, false)).l
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val objects = items.get(position) as Object;
        viewHolder.bind(objects,onItemClickListener)
        viewHolder.view_parent

    }

     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

         val tv_deviceName = itemView.findViewById(R.id.tv_deviceName) as TextView
         val view_parent = itemView as View
        val tv_deviceUUID = itemView.findViewById(R.id.tv_deviceUUID) as TextView
         val rg_control = itemView.findViewById(R.id.rg_control) as RadioGroup
         val rb_on = itemView.findViewById(R.id.rb_on) as RadioButton
         val rb_off = itemView.findViewById(R.id.rb_off) as RadioButton

        init {

        }

        fun bind(data: Object, onItemClickListener: OnItemClickListener?) {
            view_parent.setOnClickListener {onItemClickListener?.onItemClick(view_parent,data,adapterPosition)}
            rb_on.setOnClickListener {onItemClickListener?.onItemClick(rb_on,data,adapterPosition)}
            rb_off.setOnClickListener {onItemClickListener?.onItemClick(rb_off,data,adapterPosition)}

            if (data is WiSeMeshOperatableDevice) {
                val control=if(data is WiSeMeshOperatableDevice)View.VISIBLE else View.GONE
                rg_control.visibility=control
            }

            if (data is WiSeMeshDevice) {
                tv_deviceName.text = data.deviceName
                tv_deviceUUID.text = data.deviceUUID
                return
            } else if (data is WiSeScanResult) {
                tv_deviceName.text = data.deviceName
                tv_deviceUUID.text = data.deviceUUID
                return
            }
        }

    }
}