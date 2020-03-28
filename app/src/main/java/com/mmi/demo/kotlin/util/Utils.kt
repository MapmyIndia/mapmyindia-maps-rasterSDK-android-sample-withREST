package com.mmi.demo.kotlin.util

import java.util.*

object Utils {
    fun randInt(min: Int, max:Int): Int {
        val rndom = Random()
        return rndom.nextInt((max - min) + 1) + min
    }
}