package com.capstone.fasttiffin.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.ui.activities.ProductDetailsActivity
import com.capstone.fasttiffin.ui.fragments.ProductsFragment
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader

open class MyProductsListAdapter(
        private val context: Context,
        private val list: ArrayList<Product>,
        private val fragment: ProductsFragment) : RecyclerView.Adapter<MyProductsListAdapter.ProductsViewHolder>(){

    class ProductsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        val productName: TextView = itemView.findViewById(R.id.tv_item_name)
        val productPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        val btnDelete: ImageButton = itemView.findViewById(R.id.ib_delete_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        return ProductsViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_product_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val model = list[position]
            GlideLoader(context).loadProfilePicture(model.image, holder.productImage)
            holder.productName.text = model.title
            holder.productPrice.text = "₹${model.price}"

        holder.btnDelete.setOnClickListener {
            fragment.deleteProduct(model.product_id)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}