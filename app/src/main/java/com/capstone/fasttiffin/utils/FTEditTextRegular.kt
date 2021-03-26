package com.capstone.fasttiffin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class FTEditTextRegular(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs){
    init{
        applyFont()
    }

    private fun applyFont(){
        val regularTypeFace: Typeface = Typeface.createFromAsset(context.assets,"Montserrat-Regular.ttf")
        typeface = regularTypeFace
    }
}