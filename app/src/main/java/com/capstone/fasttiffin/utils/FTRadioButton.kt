package com.capstone.fasttiffin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class FTRadioButton(context: Context, attrs: AttributeSet) : AppCompatRadioButton(context, attrs) {

    init{
        applyFont()
    }
    private fun applyFont(){
        val regularTypeFace: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = regularTypeFace
    }
}