package com.kozen.support.x.model

import java.io.Serializable
import java.util.Calendar

class TransactionData : Serializable {
    var transId: String = Calendar.getInstance().timeInMillis.toString()
    var cardType: Int = 0
    var transType: Int = 0
    var transResult: Int = 0
    var transAmount: Long = 0L
    var transAmountOther: Long = 0L
    var transDate: Calendar = Calendar.getInstance()
    var transData: ByteArray? = null
    var cardNumber: String? = null
    var appleVasResult: Int = 0
    var appleVasData: ByteArray? = null

    override fun toString(): String = transId

    override fun equals(other: Any?): Boolean {
        return other is TransactionData && transId == other.transId
    }

    override fun hashCode(): Int = transId.hashCode()
}
