package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityCheckoutBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.Address
import com.capstone.fasttiffin.models.CartItem
import com.capstone.fasttiffin.models.Order
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.ui.adapters.CartItemsListAdapter
import com.capstone.fasttiffin.utils.Constants

class CheckoutActivity :BaseActivity() {

    private var mAddressDetails: Address? = null

    private lateinit var mProductsList: ArrayList<Product>

    private lateinit var mCartItemsList: ArrayList<CartItem>

    private var mSubTotal: Double = 0.0

    private var mTotalAmount: Double = 0.0

    private  lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                    intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }


        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }


        getProductList()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {

        showProgressDialog()
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }


    fun successProductsListFromFirestore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }


    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }


    fun successCartItemsList(cartList: ArrayList<CartItem>) {


        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter


        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = "₹$mSubTotal"

        binding.tvCheckoutShippingCharge.text = "₹10.0"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            binding.tvCheckoutTotalAmount.text = "₹$mTotalAmount"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }

    }


    private fun placeAnOrder() {


        showProgressDialog()

        if(mAddressDetails != null ) {
            val order = Order(
                    FirestoreClass().getCurrentUserID(),
                    mCartItemsList,
                    mAddressDetails!!,
                    "My order ${System.currentTimeMillis()}",
                    mCartItemsList[0].image,
                    mSubTotal.toString(),
                    "10.0",
                    mTotalAmount.toString(),
                    System.currentTimeMillis()
            )


            FirestoreClass().placeOrder(this@CheckoutActivity, order)
        }
    }


    fun orderPlacedSuccess() {

        FirestoreClass().updateAllDetails(this,mCartItemsList)
    }

    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
                .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
