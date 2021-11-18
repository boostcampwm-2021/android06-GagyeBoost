package com.example.gagyeboost.ui.statstics

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

private const val OVERLAP_LIMIT = 5.0

class CustomPieNumberFormatter(var pieChart: PieChart?) : PercentFormatter(pieChart) {

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        return if (pieChart != null && pieChart!!.isUsePercentValuesEnabled) {
            if (value < OVERLAP_LIMIT)
                ""
            else
                getFormattedValue(value)
        } else {
            // raw value, skip percent sign
            mFormat.format(value.toDouble())
        }
    }
}
