package com.paguelofacil.posfacil.util

fun networkErrorConverter(
    code: String
): String{
    return when(code){
        "400"->{
            "Error en el servidor\nintente nuevamente"
        }
        "408"->{
            "No se ha podido conectar\nverifique su conexion a internet e intente nuevamente"
        }
        "500"->{
            "Error en el servidor\nintente nuevamente"
        }
        "502"->{
            "Error en el servidor\nintente nuevamente"
        }
        else->{
            "Ha ocurrido un error inesperado\nintente nuevamente"
        }
    }
}