package com.example.gagyeboost.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

fun TextInputEditText.setEditTextSize(textView: TextView) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textView.text = s.toString()
            val size = textView.textSize / 3
            textSize = size
        }

        override fun afterTextChanged(s: Editable?) {}

    })
}

fun dateToLong(year: Int, month: Int, day: Int): Long {
    val m = if (month < 10) "0$month" else "$month"
    val d = if (day < 10) "0$day" else "$day"

    val stringDate = "$year-$m-$d"
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    return sdf.parse(stringDate).time
}