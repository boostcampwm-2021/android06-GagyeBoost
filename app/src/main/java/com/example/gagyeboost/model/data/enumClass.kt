package com.example.gagyeboost.model.data

enum class DateColor(val color: String) {
    Sunday("#D96D84"),
    Saturday("#6195e6"),
    Weekday("#676d6e")
}

enum class InitMoneyFilter(val money: Int) {
    Start(0),
    End(300000)
}