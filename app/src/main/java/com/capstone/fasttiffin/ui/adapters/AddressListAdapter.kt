package com.capstone.fasttiffin.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.models.Address
import com.capstone.fasttiffin.ui.activities.AddEditAddressActivity
import com.capstone.fasttiffin.ui.activities.CheckoutActivity
import com.capstone.fasttiffin.utils.Constants

open class AddressListAdapter(
        private val context: Context,
        private val list: ArrayList<Address>,
        private val selectAddress: Boolean)
: RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullName: TextView = itemView.findViewById(R.id.tv_address_full_name)
        val type: TextView = itemView.findViewById(R.id.tv_address_type)
        val details: TextView = itemView.findViewById(R.id.tv_address_details)
        val mobile: TextView = itemView.findViewById(R.id.tv_address_mobile_number)

    }

    fun notifyEditItem(activity: Activity, position: Int){
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_address_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val model = list[position]
        holder.fullName.text = model.name
        holder.type.text = model.type
        holder.details.text = "${model.address}, ${model.zipCode}"
        holder.mobile.text = model.mobileNumber

        if(selectAddress){
            holder.itemView.setOnClickListener {

                val intent = Intent(context, CheckoutActivity::class.java)
                intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}
