package com.kozen.support.x.config

enum class DukptAesKeyBit(val type: String) {
    BIT_128("128 bit"),
    BIT_192("192 bit"),
    BIT_256("256 bit")
}

interface GroupedFunction {
    val group: Int?
    val functionName: Int
    val functionSummary: Int?
}
