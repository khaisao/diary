package com.sutech.diary.util

import java.util.*

object StringUtil {
    fun convertNumberToString(number: Float, numberAfterDecimal: Int): String {
        return String.format(Locale.US, "%,.0" + numberAfterDecimal + "f", number)
    }

    fun compactString(number: String, numberAfterDecimal: Int): String {
        return String.format(
            Locale.US,
            "%,.0" + numberAfterDecimal + "f",
            number.toFloat()
        )
    }

//    fun moneyFormat(money: String): String {
//        return String.format(
//            getInstance().getString(R.string.money),
//            compactString(money, 2)
//        )
//    }

//    fun saleFormat(sale: String): String {
//        return String.format(
//            getInstance().getString(R.string.sale_),
//            compactString(sale, 2)
//        )
//    }
//
//    fun noteFormat(note: String?): String {
//        return String.format(getInstance().getString(R.string.note_), note)
//    }
//
//    fun saleOffFormat(sale: String): String {
//        return String.format(
//            getInstance().getString(R.string.sale_off),
//            compactString(sale, 2)
//        )
//    }

//    fun quantityFormat(quantity: String): String {
//        return String.format(
//            getInstance().getString(R.string.quantity_),
//            compactString(quantity, 0)
//        )
//    }
//
//    fun quantityMax(quantity: String): String {
//        return String.format(
//            getInstance().getString(R.string.quantity_),
//            compactString(quantity, 0)
//        )
//    }
//
//    fun amountOrder(amount: String, payment: String?): String {
//        return String.format(
//            getInstance().getString(R.string.payment_amount),
//            compactString(amount, 2), payment
//        )
//    }
}