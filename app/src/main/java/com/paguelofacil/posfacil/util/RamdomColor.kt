package com.paguelofacil.posfacil.util

import android.graphics.Color
import java.util.*

fun getRamdomColor(): Int{
    val rnd = Random()
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
}