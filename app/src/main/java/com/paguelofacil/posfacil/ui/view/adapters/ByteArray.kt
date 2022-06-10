package com.paguelofacil.posfacil.ui.view.adapters

import kotlin.ByteArray

class ByteArray {
    lateinit var data: ByteArray
    var length = 0

    constructor() {
        length = 256
        data = ByteArray(256)
    }

    constructor(length: Int) {
        if (length > 0) {
            this.length = length
            data = ByteArray(length)
        }
    }
}