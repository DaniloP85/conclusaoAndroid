package br.com.conclusaoandroid

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout

class CustomToolbar(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private var imgIcon: AppCompatButton = AppCompatButton(context)
    private var txtTitle: AppCompatTextView = AppCompatTextView(context)

    init {
        inflate(context, R.layout.toolbar_custom_add, this)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)

        imgIcon = findViewById(R.id.img_icon)
        txtTitle = findViewById(R.id.txt_title)

        attributes.recycle()
    }

    fun actionToBack(onClick: () -> Unit) {
        imgIcon.setOnClickListener { onClick() }
    }

    fun getTitleSetup(receivedText: String) {
        setTextTitle(receivedText)
    }

    private fun setTextTitle(receivedText: String) {
        txtTitle.text = receivedText
    }
}