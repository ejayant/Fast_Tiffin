package com.capstone.fasttiffin.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.models.Order
import com.capstone.fasttiffin.ui.activities.MyOrdersDetailsActivity
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader

class MyOrdersListAdapter(
        private val context: Context,
        private val list: ArrayList<Order>
) : RecyclerView.Adapter<MyOrdersListAdapter.MyOrdersViewHolder>(){

    class MyOrdersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val orderName: TextView = itemView.findViewById(R.id.tv_item_name)
        val orderPrice: TextView = itemView.findViewById(R.id.tv_item_price)
        val orderImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        val deleteButton: ImageButton = itemView.findViewById(R.id.ib_delete_product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrdersViewHolder {
        return MyOrdersViewHolder(
                LayoutInflater
                        .from(context)
                        .inflate(R.layout.item_list_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyOrdersViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadProfilePicture(model.image, holder.orderImage)
        holder.orderName.text = model.title
        holder.orderPrice.text = "₹${model.total_amount}"
        holder.deleteButton.visibility = View.GONE

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MyOrdersDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, model)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}