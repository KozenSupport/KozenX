package com.kozen.support.x.exception

class TransactionException(error: TransactionExceptionEnum) : Exception(error.message){
    val code: Int = error.type
    val msg: String = error.message
}

enum class TransactionExceptionEnum(val type: Int,val message: String) {
    CARD_READER_NOT_FOUND(9001,"Card Reader Manager Not Found!"),
    CARD_POWER_ON_FAILED(9002,"Power On Card Failed!"),
    NO_DATA_FOUND(9003,"No Data Found From The Card!"),
    EMV_MANAGER_NOT_FOUND(9004,"EMV Manager Not Found!"),
    READ_CARD_ERROR(9005,"Read Card Error!"),
    READ_CARD_TIME_OUT(9006,"Read Card Timeout!"),
    SECURITY_MANAGER_NOT_FOUND(9007,"Security manager not found!")
}
