package com.capstone.fasttiffin.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.fasttiffin.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private lateinit var binding: FragmentOrdersBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        binding.textNotifications.text = "This is a Notification Fragment"
        return binding.root
    }
}