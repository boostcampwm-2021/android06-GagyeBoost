package com.example.gagyeboost.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
    fun intToString(text: EditText, money: Int) {
        val new = formatter.format(money)
        val old = text.text.toString()
        if (old != new) {
            text.setText(formatter.format(money))
        }
    }

    @JvmStatic
    @BindingAdapter("textChanged")
    fun setInverseBindingListener(text: EditText, listener: InverseBindingListener?) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                listener?.onChange()
            }
        }
        text.addTextChangedListener(watcher)
    }

    @InverseBindingAdapter(attribute = "intToString", event = "textChanged")
    @JvmStatic
    fun EditText.stringToInt(): Int {
        return text.toString().replace(",", "").replace(" ", "").toIntOrNull() ?: 0
    }
}