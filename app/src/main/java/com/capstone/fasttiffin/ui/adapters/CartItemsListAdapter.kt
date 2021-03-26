package com.capstone.fasttiffin.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.fasttiffin.R
import com.capstone.fasttiffin.firestore.FirestoreClass
import com.capstone.fasttiffin.models.CartItem
import com.capstone.fasttiffin.ui.activities.CartListActivity
import com.capstone.fasttiffin.utils.Constants
import com.capstone.fasttiffin.utils.GlideLoader

class CartItemsListAdapter(
    private val context: Context,
    private val list: ArrayList<CartItem>) : RecyclerView.Adapter<CartItemsListAdapter.CartViewHolder>(){

    class CartViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cartImage: ImageView = itemView.findViewById(R.id.iv_cart_item_image)
        val cartQuantity: TextView = itemView.findViewById(R.id.tv_cart_quantity)
        val cartTitle: TextView = itemView.findViewById(R.id.tv_cart_item_title)
        val cartPrice: TextView = itemView.findViewById(R.id.tv_cart_item_price)
        val cartRemoveButton: ImageButton = itemView.findViewById(R.id.ib_remove_cart_item)
        val cartAddButton: ImageButton = itemView.findViewById(R.id.ib_add_cart_item)
        val cartDeleteButton: ImageButton = itemView.findViewById(R.id.ib_delete_cart_item)
        val spinner: Spinner = itemView.findViewById(R.id.mySpinner)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.item_cart_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val model = list[position]
        GlideLoader(context).loadProfilePicture(model.image, holder.cartImage)
        holder.cartTitle.text = model.title
        holder.cartPrice.text = "₹${model.price}"
        holder.cartQuantity.text = model.cart_quantity

        if(model.cart_quantity == "0"){
            holder.cartRemoveButton.visibility = View.GONE
            holder.cartAddButton.visibility = View.GONE

            holder.cartQuantity.text = context.resources.getString(R.string.lbl_out_of_stock)
            holder.cartQuantity.setTextColor(
                    ContextCompat.getColor(context,R.color.colorSnackBarError)
            )
        }
        else{
            holder.cartRemoveButton.visibility = View.VISIBLE
            holder.cartAddButton.visibility = View.VISIBLE

            holder.cartQuantity.setTextColor(
                    ContextCompat.getColor(context,R.color.colorSecondaryText)
            )
        }

        holder.cartDeleteButton.setOnClickListener {
            when(context){
                is CartListActivity->{
                    context.showProgressDialog()
                    FirestoreClass().removeItemFromCart(context, model.id)
                }
            }
        }

        holder.cartRemoveButton.setOnClickListener{
            if(model.cart_quantity == "1"){
                FirestoreClass().removeItemFromCart(context,model.id)
            }
            else{
                val cartQuantity: Int = model.cart_quantity.toInt()
                val itemHashMap = HashMap<String,Any>()
                itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()
                //Show the progress dialog
                if(context is CartListActivity){
                    context.showProgressDialog()
                }

                FirestoreClass().updateMyCart(context, model.id, itemHashMap)
            }
        }

        holder.cartAddButton.setOnClickListener{
            val cartQuantity: Int = model.cart_quantity.toInt()
            if(cartQuantity < model.stock_quantity.toInt()){
                val itemHashMap = HashMap<String,Any>()
                itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()
                if(context is CartListActivity){
                    context.showProgressDialog()
                }
                FirestoreClass().updateMyCart(context, model.id, itemHashMap)
            }
            else{
                if(context is CartListActivity){
                    context.showErrorSnackBar(
                            context.resources.getString(R.string.msg_for_available_stock,model.stock_quantity),
                    true)
                }
            }

        }

        // Spinner Code
        val adapter = ArrayAdapter.createFromResource(
                context,
                R.array.quality,
                android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner.adapter = adapter

        holder.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent!!.getItemAtPosition(position)
                if(selectedItem.toString()=="Full"){
                    val a = (model.price.toInt()*2)-20
                    holder.cartPrice.text = "₹${a}"
                }
                else if(selectedItem.toString()=="Half"){
                    holder.cartPrice.text = "₹${model.price}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}