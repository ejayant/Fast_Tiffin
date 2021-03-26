package com.capstone.fasttiffin.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.capstone.fasttiffin.R
import java.io.IOException

class GlideLoader(val context: Context) {

    fun loadUserPicture(image: Any, imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(image)
                .centerCrop() // Scale type of the image
                .placeholder(R.drawable.ic_user_placeholder) // A default place holder if image is failed to load
                .into(imageView)  // The view in which the image is loaded.
        }
        catch(e: IOException){
            e.printStackTrace()
        }
    }

    fun loadProfilePicture(image: Any, imageView: ImageView) {
        try{
            Glide
                    .with(context)
                    .load(image)
                    .centerCrop() // Scale type of the image
                    .into(imageView)  // The view in which the image is loaded.
        }
        catch(e: IOException){
            e.printStackTrace()
        }
    }
}