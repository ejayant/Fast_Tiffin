package com.capstone.fasttiffin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginTop

class FTTextViewBold(context: Context, attrs: AttributeSet): AppCompatTextView(context,attrs){
    init{
        applyFont()
    }
    private fun applyFont(){
        val boldTypeFace: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = boldTypeFace
    }
}