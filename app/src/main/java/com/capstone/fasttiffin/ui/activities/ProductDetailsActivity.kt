package com.capstone.fasttiffin.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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

class ProductDetailsActivity : BaseActivity() {

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private lateinit var binding: ActivityProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        var productOwnerId: String = ""
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }
        if(FirestoreClass().getCurrentUserID() == productOwnerId){
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        }
        else{
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

        binding.btnGoToCart.setOnClickListener{
            startActivity(Intent(this,CartListActivity::class.java))
        }
        getProductDetails()
    }

    private fun addToCart(){
        val cartItem = CartItem(
                FirestoreClass().getCurrentUserID(),
                mProductId,
                mProductDetails.title,
                mProductDetails.price,
                mProductDetails.image,
                Constants.DEFAULT_CART_QUANTITY
        )
        showProgressDialog()
        FirestoreClass().addCartItems(this, cartItem)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this,
                resources.getString(R.string.success_msg_item_added_to_cart),
                Toast.LENGTH_SHORT)
                .show()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE

    }

    // Back Button Code In Toolbar
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarProductDetailsActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails(){
        showProgressDialog()
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product){
        mProductDetails = product
//        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProfilePicture(
                product.image, binding.ivProductDetailImage
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
                    ContextCompat.getColor(this, R.color.colorSnackBarError)
            )
        }
        else{
            if(FirestoreClass().getCurrentUserID() == product.user_id){
                hideProgressDialog()
            }
            else{
                FirestoreClass().checkIfItemExistInCart(this, mProductId)
            }
        }

        if(FirestoreClass().getCurrentUserID() == product.user_id){
            hideProgressDialog()
        }
        else{
            FirestoreClass().checkIfItemExistInCart(this, mProductId)
        }
    }

    fun productExistInCart(){
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}