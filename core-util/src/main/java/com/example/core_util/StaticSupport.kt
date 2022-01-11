package com.example.core_util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

fun getFormatRupiah(data: Int): String {
    val fmt = NumberFormat.getInstance() as DecimalFormat

    val locale = Locale("in", "ID")

    val symbol = Currency.getInstance(locale).getSymbol(locale)
    fmt.isGroupingUsed = true
    fmt.positivePrefix = "$symbol "
    fmt.negativePrefix = "-$symbol "
    fmt.minimumFractionDigits = 0
    return fmt.format(data).replace("(?<=[A-Za-z])(?=[0-9])|(?<=[0-9])(?=[A-Za-z])", " ")
}