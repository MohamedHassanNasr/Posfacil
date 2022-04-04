package com.paguelofacil.posfacil.data.network.api

/**
 * Api error class. Base representation of an Error response from an API
 *
 * @constructor Create empty Api error
 */
open class ApiError {
    var status: Boolean = false
    var message: String? = null
    var data: Any? = null
    var code: Int? = null
    var statusCode:Int?=null
}
