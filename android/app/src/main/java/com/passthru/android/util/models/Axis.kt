package com.passthru.android.util.models

import java.io.Serializable

class Axis: Serializable {
    var x: Float = 0.0f
    var y: Float = 0.0f
    var z: Float = 0.0f

    fun areEqual(other: Axis): Boolean{
        return x == other.x
                && y == other.y
                && z == other.z
    }

    fun copy(other: Axis){
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }
}