package com.passthru.android.util.models

import java.io.Serializable

class InputReport : Serializable {
    var messageTimestamp: Long = 0
    val buttonReport: ButtonReport = ButtonReport()
    val axisReport: AxisReport = AxisReport()

    fun areEqual(other: InputReport): Boolean{
        return buttonReport.areEqual(other.buttonReport)
                && axisReport.areEqual(other.axisReport)
    }

    fun copy(other: InputReport): InputReport{
        buttonReport.copy(other.buttonReport)
        axisReport.copy(other.axisReport)
        return this
    }
}

