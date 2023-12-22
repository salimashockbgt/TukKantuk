package com.example.tukkantuk

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class EditTextEmail : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.enter_email)

        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pattern = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,7}\\b"
                if (Regex(pattern).matches(s.toString())) {
                    error = null
                } else {
                    setError(context.getString(R.string.invalid_email_address), null)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

}