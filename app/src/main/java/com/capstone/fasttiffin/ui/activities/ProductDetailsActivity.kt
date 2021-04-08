package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.databinding.ActivityProductDetailsBinding
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.CartItem
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(),View.OnClickListener {
    private lateinit var binding: ActivityProductDetailsBinding

    private lateinit var mProductDetails: Product


    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                    intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }
        var productOwnerId : String =""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId= intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == productOwnerId){
            binding.btnAddToCart.visibility = View.GONE
        }
        else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)

        setupActionBar()

        getProductDetails()
    }


    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarProductDetailsActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun getProductDetails() {


        showProgressDialog()

        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))

                }
            }
        }
    }

    private fun addToCart() {

        val addToCart = CartItem(
                FirestoreClass().getCurrentUserID(),
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )

        // Show the progress dialog
        showProgressDialog()

        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)
    }


    fun productDetailsSuccess(product: Product) {

        mProductDetails = product


        GlideLoader(this@ProductDetailsActivity).loadProfilePicture(
                product.image,
                binding.ivProductDetailImage
        )

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "₹${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsStockQuantity.text = product.stock_quantity


        if(product.stock_quantity.toInt() == 0){

            hideProgressDialog()

            binding.btnAddToCart.visibility = View.GONE

            binding.tvProductDetailsStockQuantity.text =
                    resources.getString(R.string.lbl_out_of_stock)

            binding.tvProductDetailsStockQuantity.setTextColor(
                    ContextCompat.getColor(
                            this@ProductDetailsActivity,
                            R.color.colorSnackBarError
                    )
            )
        }else{

            if (FirestoreClass().getCurrentUserID() == product.user_id) {

                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }


    fun productExistInCart() {

        hideProgressDialog()

        binding.btnAddToCart.visibility = View.GONE

        binding.btnGoToCart.visibility = View.VISIBLE
    }


    fun addToCartSuccess() {

        hideProgressDialog()

        Toast.makeText(
                this@ProductDetailsActivity,
                resources.getString(R.string.success_msg_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show()


        binding.btnAddToCart.visibility = View.GONE

        binding.btnGoToCart.visibility = View.VISIBLE
    }

}