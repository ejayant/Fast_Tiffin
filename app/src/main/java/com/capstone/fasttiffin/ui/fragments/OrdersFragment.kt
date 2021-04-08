package com.capstone.fasttiffin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.fasttiffin.databinding.FragmentOrdersBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.Order
import com.capstone.fasttiffin.ui.adapters.MyOrdersListAdapter

class OrdersFragment : BaseFragment() {

    private lateinit var binding: FragmentOrdersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun populateOrdersListInUI(orderList: ArrayList<Order>){
        hideProgressDialog()
        if(orderList.size > 0){
            binding.tvNoOrdersFound.visibility = View.GONE
            binding.rvMyOrderItems.visibility = View.VISIBLE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)

            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), orderList)
            binding.rvMyOrderItems.adapter = myOrdersAdapter
        }
        else{
            binding.tvNoOrdersFound.visibility = View.VISIBLE
            binding.rvMyOrderItems.visibility = View.GONE
        }
    }

    private fun getMyOrdersList(){
        showProgressDialog()
        FirestoreClass().getMyOrdersList(this)
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }
}