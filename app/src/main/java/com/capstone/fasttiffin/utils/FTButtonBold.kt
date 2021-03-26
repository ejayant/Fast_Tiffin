package com.capstone.fasttiffin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class FTButtonBold(context: Context, attrs: AttributeSet): AppCompatButton(context, attrs) {

    init{
        applyFont()
    }

    private fun applyFont(){
        val regularTypeFace: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        typeface = regularTypeFace
    }
}