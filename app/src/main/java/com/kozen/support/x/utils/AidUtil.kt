package com.kozen.support.x.utils

import com.kozen.financial.aidl.emv.EmvAid
import com.kozen.financial.util.emv.tlv.BerTag
import com.kozen.financial.util.emv.tlv.BerTlvBuilder
import com.kozen.financial.util.emv.tlv.HexUtil

/***
 * @description:
 * @projectName:
 * @author:Yao.Zhang
 * @date :2025/2/10 20:04
 * @version 1.0.0
 */
object AidUtil {

    fun createAidList(): MutableList<EmvAid> {
        val aidList = mutableListOf<EmvAid>()
        var aid: EmvAid? = null


        /***
         * VISA
         */
        //示例：默认参数
        aid = buildAidParam("A0000000031010", "008C")
        aidList.add(aid)

        //示例：带acquirerIdentifier参数
        aid = buildAidParam(
            aid = "A0000000032010",
            version = "008C",
            acquirerIdentifier = "000000000001"
        )
        aidList.add(aid)

        //示例：带combinationData数据
        aid = buildAidParam(
            aid = "A0000000033010",
            version = "008C",
            combinationData = getKernel(
                kernel = getLimit(999999999999L, 999999999999L, 0, 999999999999L),
                visa = HexUtil.parseHex("DF0101019F660436204000")
            )
        )
        aidList.add(aid)


        /***
         * Unionpay
         */
        aid = buildAidParam(
            aid = "A000000333010101",
            version = "0020",
            acquirerIdentifier = "000000000001",
            combinationData = getKernel(
                kernel = getLimit(Int.MAX_VALUE.toLong(), 999999999999L, 0, 999999999999L),
                visa = HexUtil.parseHex("DF0101019F660436204000")
            )
        )
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A000000333010102",
            version = "0020",
            combinationData = getKernel(
                kernel = getLimit(Int.MAX_VALUE.toLong(), 999999999999L, 0, 999999999999L),
                visa = HexUtil.parseHex("DF0101019F660426204000")
            )
        )
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A000000333010103",
            version = "0020",
            combinationData = getKernel(
                kernel = getLimit(Int.MAX_VALUE.toLong(), 999999999999L, 0, 999999999999L),
                visa = HexUtil.parseHex("DF0101019F660426204000")
            )
        )
        aidList.add(aid)

        aid = buildAidParam("A000000333010106", "0020")
        aidList.add(aid)

        /**
         * MasterCard
         */
        aid = buildAidParam(
            aid = "A00000000410",
            version = "0002",
            tacDenial = "0400000000",
            tacOnline = "f850acf800",
            tacDefault = "fc50aca000",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "6C00000000000000"
        )
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A00000000430",
            version = "0002",
            dDOL = "9F3704",
            tDOL = "9F3704",
            tacDenial = "0400000000",
            tacOnline = "f850acf800",
            tacDefault = "fc50aca000",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "4C00800000000000"
        )
        aidList.add(aid)

        //接触示例：重载参数
        aid = buildAidParam(
            aid = "A0000000041010",
            version = "0002",
            tacDenial = "0000000000",
            tacOnline = "FC50BCF800",
            tacDefault = "FC50BCA000",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "6C00800000000000"
        )
        aidList.add(aid)
        //非接示例：重载参数，并传入combinationData值
        aid = buildAidParam(
            aid = "A0000000041010",
            version = "0002",
            tacDenial = "0000000000",
            tacOnline = "F45084800C",
            tacDefault = "F45084800C",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "6C00800000000000",
            combinationData = getKernel(
                kernel = getLimit(500000, 1000, 0, 0),
                mastercard = BerTlvBuilder().apply {
                    addBytes(BerTag("DF8118"), HexUtil.parseHex("60"))
                    addBytes(BerTag("DF8119"), HexUtil.parseHex("08"))
                    addBytes(BerTag("DF811E"), HexUtil.parseHex("30"))
                    addBytes(BerTag("DF812C"), HexUtil.parseHex("00"))
                    addBytes(BerTag("DF811B"), HexUtil.parseHex("30"))
                }.buildArray()
            )
        )
        aidList.add(aid)


        aid = buildAidParam(
            aid = "A0000000043060",
            version = "0002",
            dDOL = "9F3704",
            tDOL = "9F3704",
            tacDenial = "0000000000",
            tacOnline = "FC50BCF800",
            tacDefault = "FC50BCA000",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "6C00800000000000"
        )
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A0000000044010",
            version = "0002",
            dDOL = "9F3704",
            tDOL = "9F3704",
            tacDenial = "0000000000",
            tacOnline = "FC50BCF800",
            tacDefault = "FC50BCA000"
        )
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A0000000045010",
            version = "0002",
            dDOL = "9F3704",
            tDOL = "9F3704",
            tacDenial = "0000000000",
            tacOnline = "FC50BCF800",
            tacDefault = "FC50BCA000"
        )
        aidList.add(aid)

        /**
         * AMEX
         */
        aid = buildAidParam(
            aid = "A00000002501",
            version = "0001",
            tacDenial = "DC50FC9800",
            tacOnline = "DE00FC9800",
            tacDefault = "0010000000"
        )
        aidList.add(aid)


        /**
         * Discover
         */
        aid = buildAidParam("A0000001523010", "0001")
        aidList.add(aid)

        aid = buildAidParam(
            aid = "A0000001524010",
            version = "0001",
            terminalCapabilities = "E0F8C8",
            terminalRiskManagementData = "647A800000000000",
            combinationData = getKernel(
                kernel = getLimit(99999999, 100, 0, 0),
                discover = HexUtil.parseHex("DF0101019F660426004000")
            )
        )
        aidList.add(aid)

        /**
         * JCB
         */
        aid = buildAidParam("A0000000651010", "0021")
        aidList.add(aid)

        /**
         * AMEX
         */
        aid = buildAidParam("A00000002501", "0001")
        aidList.add(aid)

        /**
         * AMEX
         */
        aid = buildAidParam("A0000005241010", "0002")
        aidList.add(aid)

        /**
         * MIR
         */
        aid = buildAidParam("A0000006581010", "0100")
        aidList.add(aid)
        aid = buildAidParam("A0000006581099", "0100")
        aidList.add(aid)
        aid = buildAidParam("A0000006582010", "0100")
        aidList.add(aid)


        /**
         * verve Card
         */
        aid = buildAidParam("A0000003710001", "0001")
        aidList.add(aid)

        /**
         * NSICCS
         */
        aid = buildAidParam(
            aid = "A0000006021010",
            version = "0100",
            tacDenial = "0010000000",
            tacOnline = "fc60acf800",
            tacDefault = "fc60242800",
            terminalCountryCode = "0360",
            transCurrencyCode = "0360",
            terminalRiskManagementData = "647A800000000000",
            terminalCapabilities = "E078C8",
            additionalTerminalCapabilities = "F00080F000"
        )
        aidList.add(aid)

        /**
         * NAPAS
         */
        aid = buildAidParam(
            aid = "A0000007271010",
            version = "0100",
            tacDenial = "0010000000",
            tacOnline = "fc60acf800",
            tacDefault = "fc60242800",
            terminalCountryCode = "0360",
            transCurrencyCode = "0360",
            terminalRiskManagementData = "647A800000000000",
            terminalCapabilities = "E078C8",
            additionalTerminalCapabilities = "F00080F000"
        )
        aidList.add(aid)

        /**
         * TROY
         */
        aid = buildAidParam(
            aid = "A0000006723010",
            version = "0001",
            dDOL = "9F3704",
            tDOL = "9F0802",
            tacDenial = "0010000000",
            tacOnline = "FC78FCF800",
            tacDefault = "FC78FCA000",
            terminalCountryCode = "0792",
            transCurrencyCode = "0949",
            transCurrencyExp = "02",
            terminalRiskManagementData = "647A800000000000",
            terminalCapabilities = "E078C8",
            additionalTerminalCapabilities = "F00080F000"
        )
        aidList.add(aid)
        return aidList
    }

    fun buildAidParam(
        //必选参数
        aid: String,
        version: String,
        selectIndicator: Boolean = true,            //默认值：不传该值默认为 true
        dDOL: String = "9F3704",                    //默认值：不传该值默认为 9F0206
        tDOL: String = "9F3704",                    //默认值：不传该值默认为 9F3704
        tacDenial: String = "0010000000",           //默认值：不传该值默认为 0010000000
        tacOnline: String = "FCE09CF800",           //默认值：不传该值默认为 FCE09CF800
        tacDefault: String = "DC00002000",          //默认值：不传该值默认为 DC00002000
        threshold: Int = 0,                         //默认值：不传该值默认为 0
        terminalCapabilities: String = "E0F8C8",    //默认值：不传该值默认为 E0F8C8
        targetPercentage: Int = 10,                 //默认值：不传该值默认为 10
        maxTargetPercentage: Int = 99,              //默认值：不传该值默认为 99
        floorLimit: Int = 0,                        //默认值：不传该值默认为 0
        contactlessTransLimit: Int = 99999999,      //默认值：不传该值默认为 99999999
        contactlessCVMLimit: Int = 40000,           //默认值：不传该值默认为 200000
        contactlessFloorLimit: Int = 0,             //默认值：不传该值默认为 0
        dynamicTransLimit: Int = 200000,            //默认值：不传该值默认为 200000

        //可选参数
        terminalRiskManagementData: String? = null,
        additionalTerminalCapabilities: String? = null,
        merchantCategoryCode: String? = null,
        terminalCountryCode: String? = null,
        terminalType: String? = null,
        transCurrencyCode: String? = null,
        transCurrencyExp: String? = null,
        acquirerIdentifier: String? = null,
        combinationData: ByteArray? = null,
    ): EmvAid {
        return EmvAid().apply {

            /********  以下为必传参数，传值就使用传过来的参数，不传使用默认参数   *********/
            this.AID = ByteUtils.hexStringToBytes(aid)
            this.Version = ByteUtils.hexStringToBytes(version)
            this.SelectIndicator = selectIndicator ?: true
            this.dDOL = ByteUtils.hexStringToBytes(dDOL)
            this.tDOL = ByteUtils.hexStringToBytes(tDOL)
            this.TACDenial = ByteUtils.hexStringToBytes(tacDenial)
            this.TACOnline = ByteUtils.hexStringToBytes(tacOnline)
            this.TACDefault = ByteUtils.hexStringToBytes(tacDefault)
            this.Threshold = threshold
            this.TerminalCapabilities = ByteUtils.hexStringToBytes(terminalCapabilities)
            this.TargetPercentage = targetPercentage
            this.MaxTargetPercentage = maxTargetPercentage
            this.FloorLimit = floorLimit
            this.ContactlessTransLimit = contactlessTransLimit
            this.ContactlessCVMLimit = contactlessCVMLimit
            this.ContactlessFloorLimit = contactlessFloorLimit
            this.DynamicTransLimit = dynamicTransLimit


            /********  以下为可选参数，传值就使用传过来的参数，不传不操作   *********/
            if (terminalRiskManagementData != null) {
                this.TerminalRiskManagementData = ByteUtils.hexStringToBytes(terminalRiskManagementData)
            }
            if (additionalTerminalCapabilities != null) {
                this.AdditionalTerminalCapabilities = ByteUtils.hexStringToBytes(additionalTerminalCapabilities)
            }
            if (merchantCategoryCode != null) {
                this.MerchantCategoryCode = ByteUtils.hexStringToBytes(merchantCategoryCode)
            }
            if (terminalCountryCode != null) {
                this.TerminalCountryCode = ByteUtils.hexStringToBytes(terminalCountryCode)
            }
            if (terminalType != null) {
                this.TerminalType = ByteUtils.hexStringToBytes(terminalType)
            }
            if (transCurrencyCode != null) {
                this.TransCurrencyCode = ByteUtils.hexStringToBytes(transCurrencyCode)
            }
            if (transCurrencyExp != null) {
                this.TransCurrencyExp = ByteUtils.hexStringToBytes(transCurrencyExp)
            }
            if (acquirerIdentifier != null) {
                this.AcquirerIdentifier = ByteUtils.hexStringToBytes(acquirerIdentifier)
            }
            if (combinationData != null) {
                this.CombinationData = combinationData
            }
        }
    }

    private fun getKernel(
        kernel: ByteArray?,
        visa: ByteArray? = null,
        unionpay: ByteArray? = null,
        mastercard: ByteArray? = null,
        discover: ByteArray? = null,
        mir: ByteArray? = null,
    ): ByteArray {
        val tlvBuilder = BerTlvBuilder()

        if (kernel != null) {
            tlvBuilder.addBytes(BerTag("DF10"), kernel)
        }
        if (visa != null) {
            tlvBuilder.addBytes(BerTag("DF11"), visa)
        }
        if (unionpay != null) {
            tlvBuilder.addBytes(BerTag("DF12"), unionpay)
        }
        if (mastercard != null) {
            tlvBuilder.addBytes(BerTag("DF13"), mastercard)
        }
        if (discover != null) {
            tlvBuilder.addBytes(BerTag("DF14"), discover)
        }
        if (mir != null) {
            tlvBuilder.addBytes(BerTag("DF17"), mir)
        }

        return tlvBuilder.buildArray()
    }


    private fun getLimit(
        contactlessTransLimit: Long,
        contactlessCVMLimit: Long,
        contactlessFloorLimit: Long,
        contactlessDynamicLimit: Long,
    ): ByteArray {
        val tlvBuilder = BerTlvBuilder()
        tlvBuilder.addBytes(BerTag("DF01"), getAmount(contactlessTransLimit))
        tlvBuilder.addBytes(BerTag("DF02"), getAmount(contactlessCVMLimit))
        tlvBuilder.addBytes(BerTag("DF03"), getAmount(contactlessFloorLimit))
        tlvBuilder.addBytes(BerTag("DF04"), getAmount(contactlessDynamicLimit))
        return tlvBuilder.buildArray()
    }

    private fun getAmount(value: Long): ByteArray {
        val builder = StringBuilder(12)
        builder.append(value)
        while (builder.length < 12) {
            builder.insert(0, '0')
        }
        return HexUtil.parseHex(builder.toString())
    }
}