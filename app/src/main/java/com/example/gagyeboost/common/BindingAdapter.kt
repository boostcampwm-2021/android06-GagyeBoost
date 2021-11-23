package com.example.gagyeboost.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

object BindingAdapter {

    @BindingAdapter("intToWon")
    @JvmStatic
    fun intToWon(textView: TextView, money: Int) {
        val str = formatter.format(money) + "원"
        textView.text = str
    }

    @BindingAdapter("intToString")
    @JvmStatic
    fun intToString(text: TextInputEditText, money: Int) {
        val new = formatter.format(money)
        val old = text.text.toString()
        if (old == new) return
        if (old != new) {
            text.setText(formatter.format(money))
        }
    }

    @JvmStatic
    @BindingAdapter("textChanged")
    fun setInverseBindingListener(text: TextInputEditText, listener: InverseBindingListener?) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text.setSelection(s?.length ?: 0)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                listener?.onChange()
                text.setSelection(s?.length ?: 0)
            }
        }
        text.addTextChangedListener(watcher)
    }

    @InverseBindingAdapter(attribute = "intToString", event = "textChanged")
    @JvmStatic
    fun TextInputEditText.stringToInt(): Int {
        return text.toString().replace(",", "").replace(" ", "").toIntOrNull() ?: 0
    }

    @BindingAdapter("year", "month", "date")
    @JvmStatic
    fun intToDate(textView: TextView, year: Int, month: Int, date: Int) {
        val str = "$year.$month.${String.format("%02d", date)}"
        textView.text = str
    }
}
