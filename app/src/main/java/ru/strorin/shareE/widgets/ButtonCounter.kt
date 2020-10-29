package ru.strorin.shareE.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import ru.strorin.shareE.R

class ButtonCounter: LinearLayout {

    private val titleView: TextView
    private val counterView: TextView
    private var number: Int = 0

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.counter_button, this)
        titleView = findViewById(R.id.button_title)
        counterView = findViewById(R.id.counter_view)
        background = context.getDrawable(R.drawable.rounded_button)
        gravity = Gravity.CENTER
        orientation = HORIZONTAL
    }

    fun setTitle(text: String){
        titleView.text = text
    }

    fun setNumber(num: Int){
        number = num
        counterView.visibility = if (number == 0) View.GONE else View.VISIBLE
        counterView.text = number.toString()
    }

    fun getNumber() = number
}