package com.capstone.fasttiffin.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityCartListBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.CartItem
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.ui.adapters.CartItemsListAdapter

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    private lateinit var binding: ActivityCartListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

    }

    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarCartListActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        binding.toolbarCartListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()
        for(product in mProductsList){
            for(cartItem in cartList){
                if(product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if(product.stock_quantity.toInt()==0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0){
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this)
            binding.rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this, cartList)
            binding.rvCartItemsList.adapter = cartListAdapter
            var subTotal: Double = 0.0
            for(item in mCartListItems){
                val availableQuantity = item.stock_quantity.toInt()
                if(availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }

            }
            binding.tvSubTotal.text = "₹$subTotal"
            binding.tvShippingCharge.text = "₹0.0"

            if(subTotal > 0){
                binding.llCheckout.visibility = View.VISIBLE
                val total = subTotal
                binding.tvTotalAmount.text = "₹$total"
            }
            else{
                binding.llCheckout.visibility = View.GONE
            }
        }
        else{
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun getCartItemsList(){
        FirestoreClass().getCartList(this)
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductsList()
    }

    fun successProductsListFromFirestore(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()

    }

    fun getProductsList(){
        showProgressDialog()
        FirestoreClass().getAllProductsList(this)
    }

    fun itemRemovedSuccess(){
        hideProgressDialog()
        Toast.makeText(this,
                resources.getString(R.string.success_msg_item_remove_successfully),
                Toast.LENGTH_SHORT).show()

        getCartItemsList()
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }
}