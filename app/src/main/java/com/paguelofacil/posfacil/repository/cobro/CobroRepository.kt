package com.paguelofacil.posfacil.repository.cobro

import android.os.Build
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.base.BaseRepo
import com.paguelofacil.posfacil.data.network.api.ApiEndpoints
import com.paguelofacil.posfacil.data.network.api.ApiRequestCode
import com.paguelofacil.posfacil.model.Others
import com.paguelofacil.posfacil.model.QrInfo
import com.paguelofacil.posfacil.model.RequestQr
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.util.Constantes.ApiParams
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class CobroRepository @Inject constructor(val posService: CobroService): BaseRepo() {
    var db = FirebaseFirestore.getInstance()

    fun saveDataCardFirestore(data: HashMap<String, Any>) {
        try {
            data["fecha"] = FieldValue.serverTimestamp()
            db.collection("logs-deteccion-tarjeta")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    showToast("Registro Logs Exitoso")
                }
                .addOnFailureListener { e ->
                    showToast("Registro Logs Error")
                }
        } catch (e: Exception) {
            showToast("Registro Logs Error")
        }
    }

    fun getFlagsDeteccionTarjetaFirestore(mutableFlags: MutableLiveData<Map<String, Boolean>?>) {
        try {
            db.collection("flag")
                .document("deteccion-tarjeta")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        mutableFlags.postValue(document.data as Map<String,Boolean>)
                    } else {
                        mutableFlags.postValue(null)
                    }
                }
                .addOnFailureListener { exception ->
                    mutableFlags.postValue(null)
                }
        } catch (e: Exception) {
            showToast("Registro Logs Error")
        }
    }

    suspend fun getSystem(
        amount: String,
        taxes: String,
        tip: String,
        onSuucces: (String, String)-> Unit,
        onFailure: (String)-> Unit
    ){
        val uuid = UUID.randomUUID()
        val map = HashMap<String, Any>()

        map[ApiParams.CONDITIONAL] = "props::POSFACIL"
        val response = remoteDao.getSystem(
            url = "${ApiEndpoints.PARAMS_SYSTEM}", map
        )
        Timber.e("MODEL ${Build.MODEL} $uuid")
        if (response.headerStatus.code == 200){
            getUrl(
                RequestQr(
                    type = "QR_INFO",
                    qrInfo = Gson().toJson(QrInfo(
                        idSearch = Build.MODEL,
                        tx = "${UserRepo.getUser().idMerchant.toString().dropLast(2)}_$uuid",
                        amount = amount,
                        taxes = taxes,
                        description = "TEST-POS",
                        others = Others(
                            idUser = UserRepo.getUser().id.toString(),
                            idMerchant = UserRepo.getUser().idMerchant.toString().dropLast(2),
                            tip = tip
                        )
                    ))
                ),
                onSuucces = {
                    onSuucces(response.data.values._url_qr,it)
                },
                onFailure = {
                    Timber.e("FAILURE SYSTEM qr")
                    onFailure(it)
                }
            )
        }
    }

    suspend fun getUrl(requestQr: RequestQr, onSuucces: (String) -> Unit, onFailure: (String) -> Unit){
        val response = remoteDao.QrProcessInfo(
            url = ApiEndpoints.QR_ENDPOINT,
            requestQr
        )

        if (response.headerStatus.code == 200){
            onSuucces(response.data.code)
        }else{
            onFailure(response.headerStatus.description)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(ApplicationClass.instance.baseContext, message, Toast.LENGTH_LONG).show()
    }

}