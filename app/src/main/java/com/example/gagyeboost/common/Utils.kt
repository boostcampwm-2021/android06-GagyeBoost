package com.example.gagyeboost.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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
    val stringDate = intToStringDate(year, month, day)
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA)

    return sdf.parse(stringDate).time
}

fun intToStringDate(year: Int, month: Int, day: Int): String {
    val m = if (month < 10) "0$month" else "$month"
    val d = if (day < 10) "0$day" else "$day"

    return "$year/$m/$d"
}

fun isValidPosition(lat: Double?, lng: Double?) =
    lat != null && lat > MIN_LAT && lat < MAX_LAT && lng != null && lng > MIN_LNG && lng < MAX_LNG

fun isValidPosition(latLng: LatLng?) =
    latLng != null && isValidPosition(latLng.latitude, latLng.longitude)

fun GoogleMap.setRegionKorea() {
    setMinZoomPreference(MIN_ZOOM_LEVEL_KOREA)
    setLatLngBoundsForCameraTarget(
        LatLngBounds(
            LatLng(MIN_LAT_KOREA, MIN_LNG_KOREA),
            LatLng(MAX_LAT_KOREA, MAX_LNG_KOREA)
        )
    )
}
