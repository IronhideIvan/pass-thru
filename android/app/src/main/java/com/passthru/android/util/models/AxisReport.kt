package com.passthru.android.util.models

import java.io.Serializable

class AxisReport : Serializable {
    val axis1: Axis = Axis()
    val axis2: Axis = Axis()
    var throttle: Float = 0.0f
    var brake: Float = 0.0f

    fun areEqual(other: AxisReport): Boolean {
        return axis1.areEqual(other.axis1)
                && axis2.areEqual(other.axis2)
                && throttle == other.throttle
                && brake == other.brake
    }

    fun clone(): AxisReport {
        val temp = AxisReport()
        temp.copy(this)
        return temp
    }

    fun copy(other: AxisReport){
        axis1.copy(other.axis1)
        axis2.copy(other.axis2)
        this.throttle = other.throttle
        this.brake = other.brake
    }
}