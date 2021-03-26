package com.capstone.fasttiffin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.utils.GlideLoader

open class DashboardItemsListAdapter(
        private val context: Context,
        private val list: ArrayList<Product>
): RecyclerView.Adapter<DashboardItemsListAdapter.DashboardViewHolder>(){

    private var onClickListener: OnClickListener? = null

    class DashboardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val productImage: ImageView = itemView.findViewById(R.id.iv_dashboard_item_image)
        val productTitle: TextView = itemView.findViewById(R.id.tv_dashboard_item_title)
        val productPrice: TextView = itemView.findViewById(R.id.tv_dashboard_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        return DashboardViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.item_dashboard_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadProfilePicture(model.image, holder.productImage)
        holder.productTitle.text = model.title
        holder.productPrice.text = "₹${model.price}"

        holder.itemView.setOnClickListener {
            if(onClickListener!=null){
                onClickListener!!.onClick(position, model)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener{
        fun onClick(position: Int, product: Product)
    }

}