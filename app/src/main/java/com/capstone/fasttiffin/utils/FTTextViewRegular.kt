package com.capstone.fasttiffin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class FTTextViewRegular(context: Context, attrs: AttributeSet): AppCompatTextView(context,attrs){
    init{
        applyFont()
    }
    private fun applyFont(){
        val boldTypeFace: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = boldTypeFace
    }
}