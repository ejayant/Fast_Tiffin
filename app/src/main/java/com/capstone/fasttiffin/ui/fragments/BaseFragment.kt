package com.capstone.fasttiffin.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.FragmentBaseBinding

open class BaseFragment : Fragment() {
    private lateinit var binding: FragmentBaseBinding
    private lateinit var mProgressDialog: Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun showProgressDialog(){
        mProgressDialog = Dialog(requireActivity())
        /* Set a screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen. */
        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }


}