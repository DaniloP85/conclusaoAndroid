package br.com.conclusaoandroid.common

import android.util.Patterns
import kotlin.math.roundToInt

object Utils {

    fun emailValidator(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun calcPurchaseValue(amount:Int, value: Double): Double {
        val purchaseValue = amount * value
        return rounding(purchaseValue)
    }

    fun rounding(value: Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }

    fun validAmount (textAmount:String): Int {

        if (textAmount.isBlank()){
            return 1
        }

        if (textAmount.toInt() == 0){
            return 1
        }

        return textAmount.toInt()
    }
}