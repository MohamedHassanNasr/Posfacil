package com.paguelofacil.posfacil.pax

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.databinding.ActivityDetectedCardBinding
import com.paguelofacil.posfacil.pax.DeviceImplNeptune.hexStringToByteArray
import com.paguelofacil.posfacil.pax.jemv.EMVCAPKSCollection.VisaMC_PROD
import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.model.EntryOutParam
import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.trans.ClssEntryPoint
import com.paguelofacil.posfacil.pax.jemv.clsspaypass.trans.ClssPayPass
import com.paguelofacil.posfacil.pax.jemv.clsspaywave.trans.ClssPayWave
import com.paguelofacil.posfacil.repository.UserRepo
import com.paguelofacil.posfacil.tools.ModelTrack
import com.paguelofacil.posfacil.tools.TransactionRequest
import com.paguelofacil.posfacil.tools.getInfoByTrackNumber
import com.paguelofacil.posfacil.tools.getNames
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.ComprobanteCobroFragment.Companion.OPTION_CARD_SELECTED
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.networkErrorConverter
import com.pax.dal.*
import com.pax.dal.entity.*
import com.pax.dal.exceptions.IccDevException
import com.pax.jemv.clcommon.*
import com.pax.jemv.device.DeviceManager
import com.pax.jemv.emv.api.EMVCallback
import com.pax.jemv.emv.model.EmvEXTMParam
import com.pax.jemv.emv.model.EmvMCKParam
import com.pax.jemv.emv.model.EmvParam
import com.pax.jemv.paypass.api.ClssPassApi
import com.pax.jemv.paywave.api.ClssWaveApi
import com.pax.neptunelite.api.NeptuneLiteUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.ByteArray

@AndroidEntryPoint
class DetectedCardActivity : BaseActivity() {
    private var dialog: CancelBottomSheet? = null
    private val viewModel: CobroViewModel by viewModels()

    private lateinit var binding: ActivityDetectedCardBinding

    private val error = CoroutineExceptionHandler { _, exception ->
        Timber.e("Error ${exception.message.toString()}")
    }

    private var dalProxyClients: NeptuneLiteUser? = null

    private var Dals: IDAL? = null

    private var Mags: IMag? = null
    private var Peds: IPed? = null
    private var ICCs: IIcc? = null
    private var Piccs: IPicc? = null
    private var Syss: ISys? = null
    private var Scanners: IScanner? = null

    var tmAidLists: Array<ClssTmAidList?>? = null
    var preProcInfos: Array<Clss_PreProcInfo?>? = null
    var mcAidParams: Array<Clss_MCAidParam?>? = null
    var vsAidParams: Array<Clss_VisaAidParam?>? = null

    var serial: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val montoTx = (intent.getStringExtra("AMOUNT")?.replace(',', '.')?.toDouble())
        val taxTx = intent.getStringExtra("TAXES")?.replace(',', '.')?.toDouble()
        val tip = intent.getStringExtra("TIP")?.replace(',', '.')?.toDouble()

        Timber.e("LOS MONTOS SON : $montoTx-$taxTx-$tip")
        viewModel.statusResponseTx.observe(this) {
            if (it.status) {
                Timber.e("ES TRUEEEEE")
                toTransResultPage()
            } else {
                Timber.e("ES FALSEEEE")
                showWarningDialog(
                    message = it.message ?: ApplicationClass.language.error,
                    it.track!!
                )
            }
        }
        loadListeners()
        loadLanguage()
        initOnClick()
    }

    private fun initOnClick() {
        binding.buttonDetectCard.setOnClickListener {
            Toast.makeText(this, "Se inicio la deteccion correctamente", Toast.LENGTH_LONG).show()
            binding.buttonDetectCard.visibility = GONE
            intent?.run {
                val optionSelected = extras?.getInt(OPTION_CARD_SELECTED) ?: 0
                if (optionSelected == 0) {
                    accionDetectCardChip()
                } else if (optionSelected == 1) {
                    accionLeerBanda()
                } else if (optionSelected == 2) {
                    btnDetectSinContacto_OnClick()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initNeptunes()
    }

    private fun initNeptunes() {
        try {
            dalProxyClients = NeptuneLiteUser.getInstance();
            Dals = dalProxyClients?.getDal(this);
            Peds = Dals?.getPed(EPedType.INTERNAL);
            Mags = Dals?.getMag();
            ICCs = Dals?.getIcc();
            Piccs = Dals?.getPicc(EPiccType.INTERNAL);
            Syss = Dals?.getSys();
            Scanners = Dals?.getScanner(EScannerType.REAR);

            loadLibrary();
        } catch (e: Exception) {
        }
    }

    private fun showWarningDialog(message: String, transactionRequest: TransactionRequest) {
        val dialog = BottomSheetDialog(this)

        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
        val title = view.findViewById<TextView>(R.id.titleError)
        val description = view.findViewById<TextView>(R.id.descriptionError)
        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)

        title.text = ApplicationClass.language.error
        description.text = if ((message == "400") or (message == "400") or (message == "400")) {
            ApplicationClass.language.errorPaidTryAgainOrContactOurSupportTeam
        } else {
            networkErrorConverter(message)
        }

        val textTryAgain = ApplicationClass.language.try_againg
        btn.text = if (textTryAgain.isNotEmpty()) textTryAgain else "Intentar de nuevo"
        btn.setOnClickListener {
            dialog?.dismiss()
            lifecycleScope.launch(Dispatchers.IO + error) {
                viewModel.setTransaction(
                    transactionRequest = transactionRequest
                )
            }
        }

        dialog?.setCancelable(true)

        dialog?.setContentView(view)

        dialog?.show()
    }

    fun loadLibrary() {
        //load common
        System.loadLibrary("F_DEVICE_LIB_PayDroid")
        System.loadLibrary("F_PUBLIC_LIB_PayDroid")

        //load contact
        System.loadLibrary("F_EMV_LIBC_PayDroid")
        System.loadLibrary("F_EMV_LIB_PayDroid")
        System.loadLibrary("JNI_EMV_v102")

        //load entry
        System.loadLibrary("F_ENTRY_LIB_PayDroid")
        System.loadLibrary("JNI_ENTRY_v103")


        //load paypass MasterCard
        System.loadLibrary("F_MC_LIB_PayDroid")
        System.loadLibrary("JNI_MC_v100_01")

        //load paywave VISA
        System.loadLibrary("F_WAVE_LIB_PayDroid")
        System.loadLibrary("JNI_WAVE_v100")
    }

    private fun addCapkIntoEmvLib(): Int {
        var ret: Int
        val dataList = ByteArray()
        ret = EMVCallback.EMVGetTLVData(0x4F.toShort(), dataList)
        if (ret != 0) {
            ret = EMVCallback.EMVGetTLVData(0x84.toShort(), dataList)
        }
        if (ret == 0) {
            val rid = ByteArray(5)
            System.arraycopy(dataList.data, 0, rid, 0, 5)
            ret = EMVCallback.EMVGetTLVData(0x8F.toShort(), dataList)
            if (ret == 0) {
                val keyId = dataList.data[0]
                for (llave in VisaMC_PROD) {
                    if (compararArrayBytes(llave.getRID(), rid, 4)) {
                        if (llave.getIndex() === keyId) {
                            val emv_capk = EMV_CAPK()
                            System.arraycopy(llave.getRID(), 0, emv_capk.rID, 0, 5)
                            emv_capk.keyID = llave.getIndex()
                            emv_capk.hashInd = 1
                            emv_capk.arithInd = 1
                            System.arraycopy(llave.getKey1(), 0, emv_capk.modul, 0, 32)
                            System.arraycopy(llave.getKey2(), 0, emv_capk.modul, 32, 32)
                            System.arraycopy(llave.getKey3(), 0, emv_capk.modul, 64, 32)
                            System.arraycopy(llave.getKey4(), 0, emv_capk.modul, 96, 32)
                            System.arraycopy(llave.getKey5(), 0, emv_capk.modul, 128, 32)
                            System.arraycopy(llave.getKey6(), 0, emv_capk.modul, 160, 32)
                            System.arraycopy(llave.getKey7(), 0, emv_capk.modul, 192, 32)
                            System.arraycopy(llave.getKey8(), 0, emv_capk.modul, 224, 24)
                            emv_capk.modulLen = llave.getLongitud().toShort()
                            emv_capk.exponent[0] = 0x03
                            emv_capk.exponentLen = 1
                            System.arraycopy(llave.getSecureHash(), 0, emv_capk.checkSum, 0, 20)
                            System.arraycopy(llave.getExpiryDate(), 0, emv_capk.expDate, 0, 3)
                            ret = EMVCallback.EMVAddCAPK(emv_capk)
                            Log.i("log", "EMVAddCAPK ret=$ret")
                        }
                    }
                }
            }
        }
        return ret
    }

    private fun actualizarKernel(serial: String, fecha: String) {
        try {
            val emvParam: EmvParam
            emvParam = EmvParam()
            val dataList = ByteArray()
            val ret = EMVCallback.EMVGetTLVData(0x84.toShort(), dataList)
            var DFName: ByteArray? = null
            if (ret == 0) {
                DFName = ByteArray(dataList.length)
                System.arraycopy(dataList.data, 0, DFName, 0, dataList.length)
            }

            //DataModel.DataWithEncryptionMode DFName = emv.getTLVData(EMVTags.TAG_84_DF_NAME, DataModel.EncryptionMode.CLEAR, (byte) 0);
            if (DFName != null) {
                if (compararArrayBytes(
                        DFName, byteArrayOf(
                            0xA0.toByte(), 0x00, 0x00, 0x00, 0x03.toByte(), 0x10.toByte(), 0x10
                        ), 7
                    )
                ) {
                    val auxBuf = ByteArray(1)
                    auxBuf[0] = 0x22
                    EMVCallback.EMVSetTLVData(0x9F35.toShort(), auxBuf, auxBuf.size)
                    EMVCallback.EMVSetTLVData(0x5F2A.toShort(), byteArrayOf(0x08, 0x40), 2)
                    EMVCallback.EMVSetTLVData(0x9F1A.toShort(), byteArrayOf(0x05, 0x91.toByte()), 2)
                    EMVCallback.EMVSetTLVData(
                        0x9F33.toShort(), byteArrayOf(
                            0xE0.toByte(),
                            0xB0.toByte(), 0xC8.toByte()
                        ), 3
                    )
                    EMVCallback.EMVSetTLVData(
                        0x9F40.toShort(), byteArrayOf(
                            0xE0.toByte(), 0x00,
                            0xF0.toByte(), 0xA0.toByte(), 0x01
                        ), 5
                    )
                    auxBuf[0] = 2
                    EMVCallback.EMVSetTLVData(0x5F36.toShort(), auxBuf, auxBuf.size)
                    EMVCallback.EMVSetTLVData(0x9F1C.toShort(), "12345678".toByteArray(), 8)
                    EMVCallback.EMVSetTLVData(0x9F16.toShort(), "123456789012345".toByteArray(), 15)
                    EMVCallback.EMVSetTLVData(0x9A.toShort(), hexStringToByteArray(fecha), 3)
                    EMVCallback.EMVSetTLVData(0x9F1E.toShort(), serial.toByteArray(), 8)
                    EMVCallback.EMVGetParameter(emvParam)
                    emvParam.capability = byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())
                    emvParam.countryCode = byteArrayOf(0x05, 0x91.toByte())
                    emvParam.exCapability = byteArrayOf(
                        0xE0.toByte(), 0x00,
                        0xF0.toByte(), 0xA0.toByte(), 0x01
                    )
                    emvParam.referCurrCode = byteArrayOf(0x08, 0x40)
                    emvParam.transCurrCode = byteArrayOf(0x08, 0x40)
                    EMVCallback.EMVSetParameter(emvParam)
                } else if (compararArrayBytes(
                        DFName, byteArrayOf(
                            0xA0.toByte(), 0x00, 0x00, 0x00, 0x04.toByte(), 0x10.toByte(), 0x10
                        ), 7
                    )
                ) {
                    val auxBuf = ByteArray(1)
                    auxBuf[0] = 0x22
                    EMVCallback.EMVSetTLVData(0x9F35.toShort(), auxBuf, auxBuf.size)
                    EMVCallback.EMVSetTLVData(0x5F2A.toShort(), byteArrayOf(0x08, 0x40), 2)
                    EMVCallback.EMVSetTLVData(0x9F1A.toShort(), byteArrayOf(0x05, 0x91.toByte()), 2)
                    EMVCallback.EMVSetTLVData(
                        0x9F33.toShort(), byteArrayOf(
                            0xE0.toByte(),
                            0xB0.toByte(), 0xC8.toByte()
                        ), 3
                    )
                    EMVCallback.EMVSetTLVData(
                        0x9F40.toShort(), byteArrayOf(
                            0xE0.toByte(), 0x00,
                            0xF0.toByte(), 0xA0.toByte(), 0x01
                        ), 5
                    )
                    auxBuf[0] = 2
                    EMVCallback.EMVSetTLVData(0x5F36.toShort(), auxBuf, auxBuf.size)
                    EMVCallback.EMVSetTLVData(0x9F1C.toShort(), "12345678".toByteArray(), 8)
                    EMVCallback.EMVSetTLVData(0x9F16.toShort(), "123456789012345".toByteArray(), 15)
                    EMVCallback.EMVSetTLVData(0x9A.toShort(), hexStringToByteArray(fecha), 3)
                    EMVCallback.EMVSetTLVData(0x9F1E.toShort(), serial.toByteArray(), 8)
                    EMVCallback.EMVGetParameter(emvParam)
                    emvParam.capability = byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())
                    emvParam.countryCode = byteArrayOf(0x05, 0x91.toByte())
                    emvParam.exCapability = byteArrayOf(
                        0xE0.toByte(), 0x00,
                        0xF0.toByte(), 0xA0.toByte(), 0x01
                    )
                    emvParam.referCurrCode = byteArrayOf(0x08, 0x40)
                    emvParam.transCurrCode = byteArrayOf(0x08, 0x40)
                    EMVCallback.EMVSetParameter(emvParam)
                }
            }
        } catch (e: java.lang.Exception) {
            e.toString()
        }
    }

    fun compararArrayBytes(array1: ByteArray, array2: ByteArray, len: Int): Boolean {
        for (i in 0 until len) {
            if (array1[i] != array2[i]) {
                return false
            }
        }
        return true
    }

    fun AuthorizedContactEmvTrans(monto: Long, serialPOS: String?, fecha: String?): Int {
        try {
            actualizarKernel(serialPOS!!, fecha!!)
            EMVCallback.EMVSetTLVData(0x9F39.toShort(), byteArrayOf(0x51), 1)
            for (i in VisaMC_PROD) EMVCallback.EMVDelCAPK(i.index, i.rid)
            addCapkIntoEmvLib()
            var ret = EMVCallback.EMVCardAuth()
            if (ret != RetCode.EMV_OK) {
                return ret
            }
            EMVCallback.EMVSetPCIModeParam(
                1.toByte(),
                "0,4,5,6".toByteArray(),
                (1000 * 120).toLong()
            ) //Set no PCI mode. for input PIN
            val acType = ACType()
            ret = EMVCallback.EMVStartTrans(monto, 0L, acType)
            return if (ret != RetCode.EMV_OK) {
                ret
            } else acType.type


            //ACType.AC_TC = 1 Aprobada Offline
            //ACType.AC_AAC = 0 Declinada Offline
            //ACType.AC_ARQC = 2 Debe viajar a procesador
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 0
    }

    var emvCallback: EMVCallback? = null
    private val emvCallbackListener: EMVCallback.EmvCallbackListener = object :
        EMVCallback.EmvCallbackListener {
        override fun emvWaitAppSel(tryCnt: Int, list: Array<EMV_APPLIST>, appNum: Int) {
            emvCallback?.setCallBackResult(0)
        }

        override fun emvInputAmount(amt: LongArray) {
            emvCallback?.setCallBackResult(RetCode.EMV_OK)
        }

        override fun emvGetHolderPwd(tryFlag: Int, remainCnt: Int, pin: ByteArray) {
            emvCallback?.setCallBackResult(0)
        }

        override fun emvAdviceProc() {}
        override fun emvVerifyPINOK() {}
        override fun emvVerifyPINfailed(valor: ByteArray): Int {
            return 0
        }

        override fun emvUnknowTLVData(tag: Short, data: com.pax.jemv.clcommon.ByteArray): Int {
            return RetCode.EMV_OK
        }

        override fun certVerify() {
            emvCallback?.setCallBackResult(RetCode.EMV_OK)
        }

        override fun emvSetParam(): Int {
            return RetCode.EMV_OK
        }

        override fun cRFU2(): Int {
            return 0
        }

        override fun cEMVGetAppPara(appList: EMV_APPLIST): Int {
            return 0
        }
    }

    private fun accionDetectCardChip() {
        try {
            do {
            } while (ICCs!!.detect(0.toByte()) == false)
            val emvParam: EmvParam
            val mckParam: EmvMCKParam
            emvParam = EmvParam()
            mckParam = EmvMCKParam()
            mckParam.extmParam = EmvEXTMParam()
            DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance(Dals))
            var ret = EMVCallback.EMVCoreInit()
            if (ret != RetCode.EMV_OK) {
                return
            }
            EMVCallback.EMVSetCallback()
            EMVCallback.EMVGetParameter(emvParam)

            Syss?.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)
            emvParam.capability = byteArrayOf(0xE0.toByte(), 0xF8.toByte(), 0xC8.toByte())
            emvParam.exCapability = byteArrayOf(
                0xE0.toByte(), 0x00,
                0xF0.toByte(), 0xA0.toByte(), 0x01
            )
            emvParam.countryCode = byteArrayOf(0x05.toByte(), 0x91.toByte())
            emvParam.forceOnline = 0
            emvParam.getDataPIN = 1.toByte()
            emvParam.merchCateCode = byteArrayOf(0x01.toByte(), 0x88.toByte())
            emvParam.referCurrCode = byteArrayOf(0x08.toByte(), 0x40.toByte())
            emvParam.referCurrCon = 1000
            emvParam.referCurrExp = 2.toByte()
            emvParam.surportPSESel = 1.toByte()
            emvParam.terminalType = 0x22.toByte()
            emvParam.transCurrCode = byteArrayOf(0x08.toByte(), 0x40.toByte())
            emvParam.transCurrExp = 2.toByte()
            emvParam.transType = 0x00
            emvParam.termId = "12345678".toByteArray()
            emvParam.merchId = "123456789012345".toByteArray()
            emvParam.merchName = "abcd".toByteArray()
            EMVCallback.EMVSetParameter(emvParam)
            EMVCallback.EMVGetMCKParam(mckParam)
            mckParam.ucBypassPin = 1
            mckParam.ucBatchCapture = 1
            mckParam.extmParam.aucTermAIP = byteArrayOf(0x08.toByte(), 0x00.toByte())
            mckParam.extmParam.ucUseTermAIPFlg = 1
            mckParam.extmParam.ucBypassAllFlg = 0
            EMVCallback.EMVSetMCKParam(mckParam)
            emvCallback = EMVCallback.getInstance()
            emvCallback?.setCallbackListener(emvCallbackListener)
            EMVCallback.EMVSetTLVData(
                0x9f33.toShort(),
                emvParam.capability,
                emvParam.capability.size
            )
            EMVCallback.EMVSetTLVData(
                0x9f40.toShort(),
                emvParam.exCapability,
                emvParam.exCapability.size
            )
            EMVCallback.EMVSetTLVData(0x9f35.toShort(), byteArrayOf(emvParam.terminalType), 1)
            EMVCallback.EMVSetPCIModeParam(
                1.toByte(),
                "0,4,5,6".toByteArray(),
                (1000 * 120).toLong()
            ) //Set no PCI mode. for input PIN
            EMVCallback.EMVDelAllApp()
            var appD180: EMV_APPLIST
            var nombreByte: ByteArray
            appD180 = EMV_APPLIST()
            nombreByte = "A0000000031010".toByteArray() //VISA
            System.arraycopy(nombreByte, 0, appD180.appName, 0, nombreByte.size)
            System.arraycopy(
                byteArrayOf(
                    0xA0.toByte(), 0x00, 0x00, 0x00,
                    0x03.toByte(), 0x10.toByte(), 0x10
                ), 0, appD180.aid, 0, 7
            )
            appD180.aidLen = 7
            appD180.floorLimit = 0
            appD180.threshold = 0
            appD180.targetPer = 0
            appD180.maxTargetPer = 0
            System.arraycopy(byteArrayOf(0x00, 0x10, 0x00, 0x00, 0x00), 0, appD180.tacDenial, 0, 5)
            System.arraycopy(
                byteArrayOf(
                    0xDC.toByte(),
                    0x40.toByte(), 0x04.toByte(), 0xF8.toByte(), 0x00.toByte()
                ), 0, appD180.tacOnline, 0, 5
            )
            System.arraycopy(
                byteArrayOf(
                    0xDC.toByte(),
                    0x40.toByte(), 0x00.toByte(), 0xA8.toByte(), 0x00.toByte()
                ), 0, appD180.tacDefault, 0, 5
            )
            System.arraycopy(
                byteArrayOf(
                    0x9F.toByte(),
                    0x37.toByte(),
                    0x04.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                ), 0, appD180.dDOL, 0, 20
            )
            appD180.selFlag = 0 //1 ;
            appD180.floorLimitCheck = 1
            System.arraycopy(byteArrayOf(0x00, 0x8C.toByte()), 0, appD180.version, 0, 2)
            appD180.riskManData[0] = 0
            ret = EMVCallback.EMVAddApp(appD180)
            appD180 = EMV_APPLIST()
            nombreByte = "A0000000041010".toByteArray() //MC
            System.arraycopy(nombreByte, 0, appD180.appName, 0, nombreByte.size)
            System.arraycopy(
                byteArrayOf(
                    0xA0.toByte(), 0x00, 0x00, 0x00,
                    0x04.toByte(), 0x10.toByte(), 0x10
                ), 0, appD180.aid, 0, 7
            )
            appD180.aidLen = 7
            appD180.floorLimit = 0
            appD180.threshold = 0
            appD180.targetPer = 0
            appD180.maxTargetPer = 0
            System.arraycopy(byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00), 0, appD180.tacDenial, 0, 5)
            System.arraycopy(
                byteArrayOf(
                    0xFE.toByte(),
                    0x50.toByte(), 0xBC.toByte(), 0x80.toByte(), 0x00.toByte()
                ), 0, appD180.tacOnline, 0, 5
            )
            System.arraycopy(
                byteArrayOf(
                    0xFE.toByte(),
                    0x50.toByte(), 0xBC.toByte(), 0x80.toByte(), 0x00.toByte()
                ), 0, appD180.tacDefault, 0, 5
            )
            System.arraycopy(
                byteArrayOf(
                    0x9F.toByte(),
                    0x37.toByte(),
                    0x04.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                ), 0, appD180.dDOL, 0, 20
            )
            appD180.selFlag = 0 //1 ;
            appD180.floorLimitCheck = 1
            System.arraycopy(byteArrayOf(0x00, 0x02.toByte()), 0, appD180.version, 0, 2)
            appD180.riskManData[0] = 0
            ret = EMVCallback.EMVAddApp(appD180)


            EMVCallback.EMVSetTLVData(0x9C.toShort(), byteArrayOf(0), 0x01)
            ret = EMVCallback.EMVAppSelect(0, 1)
            if (ret != RetCode.EMV_OK) {
                return
            }
            ret = EMVCallback.EMVReadAppData()
            if (ret != RetCode.EMV_OK) {
                return
            }
            try {
                val track2 = com.pax.jemv.clcommon.ByteArray()
                ret = EMVCallback.EMVGetTLVData(0x57.toShort(), track2)

                val track1Final = com.pax.jemv.clcommon.ByteArray()
                ret = EMVCallback.EMVGetTLVData(0x9F1F.toShort(), track1Final)

                val track2Final = com.pax.jemv.clcommon.ByteArray()
                ret = EMVCallback.EMVGetTLVData(0x9F20.toShort(), track2Final)


                println("adsadasdasdas 2  ${hex(track2.data)}") //valor final de ascii a string
                println("adsadasdasdas 1final ${hex(track1Final.data)}") //valor final de ascii a string
                println("adsadasdasdas 2final  ${hex(track2Final.data)}") //valor final de ascii a string


                val cardHolder = com.pax.jemv.clcommon.ByteArray()
                ret = EMVCallback.EMVGetTLVData(0x5F20.toShort(), cardHolder)
                var str = ""
                for (i in 0..19) {
                    str += Character.toString(cardHolder.data[i].toChar())
                    print(str)
                }

                /***
                 * New Implementation
                 **/

                var monto: Long = 10000 //serian 100.00 $

                Syss?.termInfo?.let {
                    serial = it[ETermInfoKey.SN] ?: ""
                }

                AuthorizedContactEmvTrans(monto, serial, Syss!!.date)
                val cripto = ByteArray()
                ret = EMVCallback.EMVGetTLVData(0x9f26.toShort(), cripto)

                generatePrintTracks()

                val map = HashMap<String, Any?>()
                map["track1"] = hex(track1Final.data)
                map["track2"] = hex(track2.data)
                map["cardHolder"] = str
                viewModel.saveDataCardFirestore(map)
                val trackInfo = getInfoByTrackNumber(2, hex(track2.data) ?: "")

                if (trackInfo != null) {
                    showModal(selector = 2, trackInfo, str)
                } else {
                    Timber.e("HA OCURRIDO UN ERROR EN LA LECTURA, INTENTE NUEVAMENTE")
                }

            } catch (e: java.lang.Exception) {
                return
            }
        } catch (e: IccDevException) {
            e.printStackTrace()
        }
    }

    private fun generatePrintTracks() {
        val criptoAID = ByteArray()
        EMVCallback.EMVGetTLVData(0x4F.toShort(), criptoAID)

        val criptoAL = ByteArray()
        EMVCallback.EMVGetTLVData(0x50.toShort(), criptoAL)

        val criptoT2 = ByteArray()
        EMVCallback.EMVGetTLVData(0x57.toShort(), criptoT2)

        val criptoPAN = ByteArray()
        EMVCallback.EMVGetTLVData(0x5A.toShort(), criptoPAN)

        val criptoAIP = ByteArray()
        EMVCallback.EMVGetTLVData(0x82.toShort(), criptoAIP)

        val criptoDF = ByteArray()
        EMVCallback.EMVGetTLVData(0x84.toShort(), criptoDF)

        val criptoTVS = ByteArray()
        EMVCallback.EMVGetTLVData(0x95.toShort(), criptoTVS)

        val criptoTD = ByteArray()
        EMVCallback.EMVGetTLVData(0x9A.toShort(), criptoTD)

        val criptoTSI = ByteArray()
        EMVCallback.EMVGetTLVData(0x9B.toShort(), criptoTSI)

        val criptoTT = ByteArray()
        EMVCallback.EMVGetTLVData(0x9C.toShort(), criptoTT)

        val criptoAED = ByteArray()
        EMVCallback.EMVGetTLVData(0x5F24.toShort(), criptoAED)

        val criptoTCC = ByteArray()
        EMVCallback.EMVGetTLVData(0x5F2A.toShort(), criptoTCC)

        val criptoAPAN = ByteArray()
        EMVCallback.EMVGetTLVData(0x5F34.toShort(), criptoAPAN)

        val criptoAAN = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F02.toShort(), criptoAAN)

        val criptoAON = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F03.toShort(), criptoAON)

        val criptoAIT = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F06.toShort(), criptoAIT)

        val criptoAVN = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F09.toShort(), criptoAVN)

        val criptoIAD = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F10.toShort(), criptoIAD)

        val criptoMI = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F16.toShort(), criptoMI)

        val criptoTCCO = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F1A.toShort(), criptoTCCO)

        val criptoTI = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F1C.toShort(), criptoTI)

        val criptoIDSN = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F1E.toShort(), criptoIDSN)

        val criptoTT1 = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F21.toShort(), criptoTT1)

        val criptoACR = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F26.toShort(), criptoACR)

        val criptoCID = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F27.toShort(), criptoCID)

        val criptoTCA = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F33.toShort(), criptoTCA)

        val criptoCVM = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F34.toShort(), criptoCVM)

        val criptoTTY = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F35.toShort(), criptoTTY)

        val criptoATC = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F36.toShort(), criptoATC)

        val criptoUNU = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F37.toShort(), criptoUNU)

        val criptoPOSEM = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F39.toShort(), criptoPOSEM)

        val criptoATCA = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F40.toShort(), criptoATCA)

        val criptoTSC = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F41.toShort(), criptoTSC)

        val criptoMNL = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F4E.toShort(), criptoMNL)

        val cripto53 = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F53.toShort(), cripto53)

        val cripto66 = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F66.toShort(), cripto66)

        val cripto6E = ByteArray()
        EMVCallback.EMVGetTLVData(0x9F6E.toShort(), cripto6E)

        val cripto17 = ByteArray()
        EMVCallback.EMVGetTLVData(0xDF17.toShort(), cripto17)

        val cripto26E = ByteArray()
        EMVCallback.EMVGetTLVData(0x826E.toShort(), cripto26E)

        val mapTracks = HashMap<String, Any?>()
        mapTracks["0x4F"] = hex(criptoAID.data)
        mapTracks["0x50"] = hex(criptoAL.data)
        mapTracks["0x57"] = hex(criptoT2.data)
        mapTracks["0x5A"] = hex(criptoPAN.data)
        mapTracks["0x82"] = hex(criptoAIP.data)
        mapTracks["0x84"] = hex(criptoDF.data)
        mapTracks["0x95"] = hex(criptoTVS.data)
        mapTracks["0x9A"] = hex(criptoTD.data)
        mapTracks["0x9B"] = hex(criptoTSI.data)
        mapTracks["0x9C"] = hex(criptoTT.data)
        mapTracks["0x5F24"] = hex(criptoAED.data)
        mapTracks["0x5F2A"] = hex(criptoTCC.data)
        mapTracks["0x5F34"] = hex(criptoAPAN.data)
        mapTracks["0x9F02"] = hex(criptoAAN.data)
        mapTracks["0x9F03"] = hex(criptoAON.data)
        mapTracks["0x9F06"] = hex(criptoAIT.data)
        mapTracks["0x9F09"] = hex(criptoAVN.data)
        mapTracks["0x9F10"] = hex(criptoIAD.data)
        mapTracks["0x9F16"] = hex(criptoMI.data)
        mapTracks["0x9F1A"] = hex(criptoTCCO.data)
        mapTracks["0x9F1C"] = hex(criptoTI.data)
        mapTracks["0x9F1E"] = hex(criptoIDSN.data)
        mapTracks["0x9F21"] = hex(criptoTT1.data)
        mapTracks["0x9F26"] = hex(criptoACR.data)
        mapTracks["0x9F27"] = hex(criptoCID.data)
        mapTracks["0x9F33"] = hex(criptoTCA.data)
        mapTracks["0x9F34"] = hex(criptoCVM.data)
        mapTracks["0x9F35"] = hex(criptoTTY.data)
        mapTracks["0x9F36"] = hex(criptoATC.data)
        mapTracks["0x9F37"] = hex(criptoUNU.data)
        mapTracks["0x9F39"] = hex(criptoPOSEM.data)
        mapTracks["0x9F40"] = hex(criptoATCA.data)
        mapTracks["0x9F41"] = hex(criptoTSC.data)
        mapTracks["0x9F4E"] = hex(criptoMNL.data)
        mapTracks["0x9F53"] = hex(cripto53.data)
        mapTracks["0x9F66"] = hex(cripto66.data)
        mapTracks["0x9F6E"] = hex(cripto6E.data)
        mapTracks["0xDF17"] = hex(cripto17.data)
        mapTracks["0x826E"] = hex(cripto26E.data)

        //viewModel.saveDataTracksFirestore(mapTracks)

        val tlvFormat = "4f${hex(criptoAID.data)}50${hex(criptoAL.data)}57${hex(criptoT2.data)}5A${hex(criptoPAN.data)}82${hex(criptoAIP.data)}84${hex(criptoDF.data)}95${hex(criptoTVS.data)}9A${hex(criptoTD.data)}9B${hex(criptoTSI.data)}9C${hex(criptoTT.data)}5F24${hex(criptoAED.data)}5F2A${hex(criptoTCC.data)}5F34${hex(criptoAPAN.data)}9F02${hex(criptoAAN.data)}9F03${hex(criptoAON.data)}9F06${hex(criptoAIT.data)}9F09${hex(criptoAVN.data)}9F10${hex(criptoIAD.data)}9F16${hex(criptoMI.data)}9F1A${hex(criptoTCCO.data)}9F1C${hex(criptoTI.data)}9F1E${hex(criptoIDSN.data)}9F21${hex(criptoTT1.data)}9F26${hex(criptoACR.data)}9F27${hex(criptoCID.data)}9F33${hex(criptoTCA.data)}9F34${hex(criptoCVM.data)}9F35${hex(criptoTTY.data)}9F36${hex(criptoATC.data)}9F37${hex(criptoUNU.data)}9F39${hex(criptoPOSEM.data)}9F40${hex(criptoATCA.data)}9F41${hex(criptoTSC.data)}9F4E${hex(criptoMNL.data)}9F53${hex(cripto53.data)}9F66${hex(cripto66.data)}9F6E${hex(cripto6E.data)}DF17${hex(cripto17.data)}DF826E${hex(cripto26E.data)}"
        val tlv = "4f${(criptoAID.data)}50${(criptoAL.data)}57${(criptoT2.data)}5A${(criptoPAN.data)}82${(criptoAIP.data)}84${(criptoDF.data)}95${(criptoTVS.data)}9A${(criptoTD.data)}9B${(criptoTSI.data)}9C${(criptoTT.data)}5F24${(criptoAED.data)}5F2A${(criptoTCC.data)}5F34${(criptoAPAN.data)}9F02${(criptoAAN.data)}9F03${(criptoAON.data)}9F06${(criptoAIT.data)}9F09${(criptoAVN.data)}9F10${(criptoIAD.data)}9F16${(criptoMI.data)}9F1A${(criptoTCCO.data)}9F1C${(criptoTI.data)}9F1E${hex(criptoIDSN.data)}9F21${(criptoTT1.data)}9F26${(criptoACR.data)}9F27${(criptoCID.data)}9F33${(criptoTCA.data)}9F34${(criptoCVM.data)}9F35${(criptoTTY.data)}9F36${(criptoATC.data)}9F37${(criptoUNU.data)}9F39${(criptoPOSEM.data)}9F40${(criptoATCA.data)}9F41${(criptoTSC.data)}9F4E${(criptoMNL.data)}9F53${(cripto53.data)}9F66${(cripto66.data)}9F6E${(cripto6E.data)}DF17${(cripto17.data)}DF826E${(cripto26E.data)}"
        mapTracks["tlv"] = tlvFormat
        mapTracks["tlvNotHex"] = tlv
        viewModel.saveDataTracksFirestore(mapTracks)
    }

    fun hex(bytes: ByteArray): String? {
        val result = StringBuilder()
        for (aByte in bytes) {
            result.append(String.format("%02x", aByte))
            // upper case
            // result.append(String.format("%02X", aByte));
        }
        return result.toString()
    }

    private fun showModal(selector: Int, trackInfo: ModelTrack, str: String?) {
        showBottomSheetCvv(trackInfo, str)
    }

    private fun accionLeerBanda() {
        try {
            Mags?.open()
            Mags?.reset()
            do {
            } while (Mags?.isSwiped == false)
            val tracks = Mags?.read()
            Syss?.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val map = HashMap<String, Any?>()
                    map["track1"] = tracks?.track1
                    map["track2"] = tracks?.track2
                    map["track3"] = tracks?.track3
                    map["track4"] = tracks?.track4
                    viewModel.saveDataCardFirestore(map)

                    val trackInfo = getInfoByTrackNumber(1, tracks?.track1 ?: "")

                    if (trackInfo != null) {
                        showModal(1, trackInfo, trackInfo.cardHolder)
                    } else {
                        Timber.e("HA OCURRIDO UN ERROR EN LA LECTURA, INTENTE NUEVAMENTE")
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun loadListeners() {
        binding.lnArrowBack.setOnClickListener { onBackPressed() }

        binding.lnCloseBack.setOnClickListener { showBottomSheet() }
    }

    private fun showBottomSheetCvv(trackInfo: ModelTrack, str: String?) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)
        val cvv = view.findViewById<AppCompatEditText>(R.id.cvv)
        view.tv_mensaje_dialog.text = ApplicationClass.language.cardReconocida
        btnClose.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch(Dispatchers.IO + error) {
                val user = UserRepo.getUser()
                val montoTx = (intent.getStringExtra("AMOUNT")?.replace(',', '.')?.toDouble() ?: 0.00)
                val taxTx = intent.getStringExtra("TAXES")?.replace(',', '.')?.toDouble() ?: 0.00
                val tip = intent.getStringExtra("TIP")?.replace(',', '.')?.toDouble() ?: 0.00

                Timber.e("LOS MONTOS SON : $montoTx-$taxTx-$tip")
                user.id?.let {
                    val names = getNames(str ?: "")
                    viewModel.setTransaction(
                        transactionRequest = TransactionRequest(
                            amount = montoTx,
                            taxAmount = taxTx,
                            email = user.email ?: "posfacil@mail.com",
                            phone = user.phone ?: "+5491168500188",
                            concept = "pago tarjeta PosFacil",
                            description = "pago tarjeta PosFacil",
                            idMerchant = user.idMerchant.toString().dropLast(2).toInt(),
                            additionalData = TransactionRequest.AdditionalData(
                                pos = TransactionRequest.AdditionalData.Pos(
                                    idMerchant = user.idMerchant.toString().dropLast(2)
                                        .toInt(),
                                    idUser = it.toInt(),
                                    serial = serial
                                )
                            ),
                            requestPay = TransactionRequest.RequestPay(
                                cardInformation = TransactionRequest.RequestPay.CardInformation(
                                    cardNumber = trackInfo.cardNumber,
                                    cardType = if (trackInfo.cardNumber.startsWith("4")) {
                                        "VISA"
                                    } else {
                                        "MC"
                                    },
                                    cvv = cvv.text.toString(),
                                    expMonth = trackInfo.dateExpiry.substring(
                                        IntRange(
                                            2,
                                            3
                                        )
                                    ),
                                    expYear = trackInfo.dateExpiry.substring(
                                        IntRange(
                                            0,
                                            1
                                        )
                                    ),
                                    firstName = names.firstName,
                                    lastName = names.lastName
                                )
                            )
                        )
                    )
                }
            }
        }
        dialog?.setCancelable(false)
        dialog?.setContentView(view)
        dialog?.show()
    }

    private fun showBottomSheet() {
        dialog = CancelBottomSheet(
            callBackClose = { dismissBottomSheetCancel() },
            callbackVolver = { dismissBottomSheetCancel() },
            callbackCancelar = {
                dismissBottomSheetCancel()
                goHome()
            }
        )
        dialog?.show(supportFragmentManager, "")
    }

    private fun dismissBottomSheetCancel() {
        dialog?.dismiss()
    }

    private fun goHome() {
        val intent = Intent(baseContext, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadLanguage() {
        binding.textViewTitle.text = ApplicationClass.language.verificarCobro
        binding.tvMessageValideCard.text = ApplicationClass.language.esperandoTarjeta
    }

    private fun goViewFirma() {
//        val fr = activity?.supportFragmentManager?.beginTransaction()
//        fr?.replace(R.id.container_frag_cobro, FirmaFragment())
//        fr?.addToBackStack(null)?.commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onExceptionData(requestCode: Int, exception: ApiError, data: Any?) {

    }

    private fun toTransResultPage() {
        val intent = Intent()
        intent.putExtra(EXTRA_RESULT_PAY_IS_SUCCESS, true)
//        intent.putExtra(EXTRA_RESULT_CARD_NUMBER, paxUtil.getDataCard())
        setResult(Activity.RESULT_OK, intent)
        this@DetectedCardActivity.finish()
    }


    fun btnDetectSinContacto_OnClick() {
        try {
            Piccs!!.close()
            Piccs!!.open()
            do {
            } while (Piccs!!.detect(EDetectMode.EMV_AB) == null)
            val entryPoint: ClssEntryPoint
            entryPoint = ClssEntryPoint.getInstance()
            DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance(Dals))
            val transParam = Clss_TransParam()
            transParam.ulAmntAuth = 1
            transParam.ulAmntOther = 0
            transParam.ulTransNo = 1
            transParam.ucTransType = 0x00
            val Transdate = Dals!!.sys.date
            System.arraycopy(
                hexStringToByteArray(Transdate.substring(2, 8)),
                0,
                transParam.aucTransDate,
                0,
                3
            )
            val Transtime = Dals!!.sys.date
            System.arraycopy(
                hexStringToByteArray(Transtime.substring(8)),
                0,
                transParam.aucTransTime,
                0,
                3
            )
            entryPoint.coreInit()
            ClssPayWave.getInstance().coreInit()
            ClssPayPass.getInstance().coreInit(1.toByte())
            cargarListasCLSS()
            var ret: Int
            ret = entryPoint.setConfigParam(0x32.toByte(), true, tmAidLists, preProcInfos)
            if (ret != RetCode.EMV_OK) {
                // showErr(ret);
                return
            }
            val entryOut = EntryOutParam()
            ret = entryPoint.entryProcess(transParam, entryOut)
            if (ret != RetCode.EMV_OK) {
                //showErr(ret);
                return
            }
            val track2 = com.pax.jemv.clcommon.ByteArray()
            val pan = com.pax.jemv.clcommon.ByteArray()
            val cardHolder = com.pax.jemv.clcommon.ByteArray()
            when (ClssEntryPoint.getInstance().getOutParam().ucKernType) {
                KernType.KERNTYPE_MC -> {
                    ret = startMC()
                    if (ret == 0) {
                        val retTrack = ClssPassApi.Clss_GetTLVDataList_MC(
                            byteArrayOf(0x57.toByte()),
                            1.toByte(), 30, track2
                        )
                        val retCH = ClssPassApi.Clss_GetTLVDataList_MC(
                            byteArrayOf(0x5F.toByte(), 0x20),
                            2.toByte(), 30, cardHolder
                        )
                    }
                }
                KernType.KERNTYPE_VIS -> {
                    ret = startVISA()
                    if (ret == 0) {
                        val retTrack = ClssWaveApi.Clss_GetTLVData_Wave(0x57.toShort(), track2)
                        val retCH = ClssWaveApi.Clss_GetTLVData_Wave(0x5F20.toShort(), cardHolder)
                    }
                }
                else -> {
                }
            }
            Dals!!.sys.beep(EBeepMode.FREQUENCE_LEVEL_0, 100)
            val map = HashMap<String, Any?>()
            map["track2"] = hex(track2.data)
            viewModel.saveDataCardFirestore(map)

            var str = ""
            for (i in 0..19) {
                str += Character.toString(cardHolder.data[i].toChar())
                print(str)
            }

            val trackInfo = getInfoByTrackNumber(3, hex(track2.data) ?: "")

            if (trackInfo != null) {
                showModal(3, trackInfo, str)
            }
        } catch (e: java.lang.Exception) {
        }
    }

    var clssPW: ClssPayWave? = null
    private fun startVISA(): Int {
        var procInfo: Clss_PreProcInfo? = null
        val clssEP = ClssEntryPoint.getInstance()
        val aucCvmReq = ByteArray(2)
        aucCvmReq[0] = CvmType.RD_CVM_REQ_SIG.toByte()
        // aucCvmReq[1] = CvmType.RD_CVM_REQ_ONLINE_PIN;
        val visaAidParam = Clss_VisaAidParam(
            100000,
            0.toByte(), 1.toByte(), aucCvmReq, 0.toByte()
        )
        procInfo = preProcInfos!!.get(0)
        clssPW = ClssPayWave.getInstance()
        clssPW!!.setConfigParam(visaAidParam, procInfo)
        val ret = clssPW!!.waveProcessStep1()
        if (ret == 0) {
            // successProcess(ClssPayWave.getInstance().getCVMType(), transResult.result);
        }
        return ret
    }

    var ctlssPayPass: ClssPayPass? = null
    private fun startMC(): Int {
        var procInfo: Clss_PreProcInfo? = null
        var aidParam: Clss_MCAidParam? = null
        ctlssPayPass = ClssPayPass.getInstance()
        procInfo = preProcInfos!!.get(1)
        aidParam = mcAidParams!!.get(1)
        ctlssPayPass!!.setConfigParam(aidParam, procInfo)
        val ret = ctlssPayPass!!.passProcessStep1()
        if (ret == 0) {
            //successProcess(ClssPayPass.getInstance().getCVMType(), transResult.result);
            //Log.i(TAG, "cvm = " + ClssPayPass.getInstance(m).getCVMType());
        }
        return ret
    }

    private fun cargarListasCLSS() {
        tmAidLists = arrayOfNulls<ClssTmAidList>(2)
        preProcInfos = arrayOfNulls<Clss_PreProcInfo>(2)
        mcAidParams = arrayOfNulls<Clss_MCAidParam>(2)
        vsAidParams = arrayOfNulls<Clss_VisaAidParam>(2)
        val i = 0


        /*
                ClssTmAidList
                ================
             */
        var tmAid = ClssTmAidList()
        tmAid.ucAidLen = 7
        System.arraycopy(
            byteArrayOf(
                0xA0.toByte(), 0x00, 0x00, 0x00,
                0x03.toByte(), 0x10.toByte(), 0x10
            ), 0, tmAid.aucAID, 0, tmAid.ucAidLen.toInt()
        )
        tmAid.ucSelFlg = 1
        tmAid.ucKernType = KernType.KERNTYPE_VIS.toByte()

        /*
                Clss_PreProcInfo
                ================
             */
        var preProcInfo = Clss_PreProcInfo()
        preProcInfo.ulTermFLmt = 99999999
        preProcInfo.ulRdClssTxnLmt = 99999999
        preProcInfo.ulRdCVMLmt = 0
        preProcInfo.ulRdClssFLmt = 99999999
        System.arraycopy(
            byteArrayOf(
                0xA0.toByte(), 0x00, 0x00, 0x00,
                0x03.toByte(), 0x10.toByte(), 0x10
            ), 0, preProcInfo.aucAID, 0, tmAid.ucAidLen.toInt()
        )
        preProcInfo.ucAidLen = 7
        preProcInfo.ucKernType = tmAid.ucKernType
        preProcInfo.ucCrypto17Flg = 1
        preProcInfo.ucZeroAmtNoAllowed = 0
        preProcInfo.ucStatusCheckFlg = 0
        System.arraycopy(
            byteArrayOf(0xB2.toByte(), 0x00, 0x00, 0x00),
            0,
            preProcInfo.aucReaderTTQ,
            0,
            4
        )
        preProcInfo.ucTermFLmtFlg = 1
        preProcInfo.ucRdClssTxnLmtFlg = 1
        preProcInfo.ucRdCVMLmtFlg = 1
        preProcInfo.ucRdClssFLmtFlg = 1
        preProcInfo.ucTermFLmtFlg = 0
        var mcAidParam = Clss_MCAidParam()
        mcAidParam.floorLimit = 99999999
        mcAidParam.threshold = 0
        mcAidParam.usUDOLLen = 3
        System.arraycopy(
            byteArrayOf(0x9F.toByte(), 0x6A.toByte(), 0x04.toByte()),
            0,
            mcAidParam.uDOL,
            0,
            3
        )
        mcAidParam.targetPer = 0
        mcAidParam.maxTargetPer = 0
        mcAidParam.floorLimitCheck = 1
        mcAidParam.randTransSel = 1
        mcAidParam.velocityCheck = 1
        System.arraycopy(
            byteArrayOf(
                0x00.toByte(),
                0x10.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
            ), 0, mcAidParam.tacDenial, 0, 5
        )
        System.arraycopy(
            byteArrayOf(
                0xDC.toByte(),
                0x40.toByte(), 0x04.toByte(), 0xF8.toByte(), 0x00.toByte()
            ), 0, mcAidParam.tacOnline, 0, 5
        )
        System.arraycopy(
            byteArrayOf(
                0xDC.toByte(),
                0x40.toByte(), 0x00.toByte(), 0xA8.toByte(), 0x00.toByte()
            ), 0, mcAidParam.tacDefault, 0, 5
        )
        System.arraycopy(byteArrayOf(0x00, 0x00, 0x00, 0x00), 0, mcAidParam.acquierId, 0, 4)
        System.arraycopy(
            byteArrayOf(
                0x9F.toByte(),
                0x37,
                0x04,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            ), 0, mcAidParam.dDOL, 0, 20
        )
        System.arraycopy(
            byteArrayOf(
                0x9F.toByte(),
                0x02,
                0x06,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            ), 0, mcAidParam.tDOL, 0, 20
        )
        System.arraycopy(byteArrayOf(0x00, 0x8C.toByte()), 0, mcAidParam.version, 0, 2)
        mcAidParam.forceOnline = 1
        System.arraycopy(byteArrayOf(0x00, 0x00, 0x00), 0, mcAidParam.magAvn, 0, 3)
        mcAidParam.ucMagSupportFlg = 1
        tmAidLists!!.set(0, tmAid)
        preProcInfos!!.set(0, preProcInfo)
        mcAidParams!!.set(0, mcAidParam)
        tmAid = ClssTmAidList()
        tmAid.ucAidLen = 7
        System.arraycopy(
            byteArrayOf(
                0xA0.toByte(), 0x00, 0x00, 0x00,
                0x04.toByte(), 0x10.toByte(), 0x10
            ), 0, tmAid.aucAID, 0, tmAid.ucAidLen.toInt()
        )
        tmAid.ucSelFlg = 1
        tmAid.ucKernType = KernType.KERNTYPE_DEF.toByte()

        /*
                Clss_PreProcInfo
                ================
             */preProcInfo = Clss_PreProcInfo()
        preProcInfo.ulTermFLmt = 99999999
        preProcInfo.ulRdClssTxnLmt = 99999999
        preProcInfo.ulRdCVMLmt = 0
        preProcInfo.ulRdClssFLmt = 99999999
        System.arraycopy(
            byteArrayOf(
                0xA0.toByte(), 0x00, 0x00, 0x00,
                0x04.toByte(), 0x10.toByte(), 0x10
            ), 0, preProcInfo.aucAID, 0, tmAid.ucAidLen.toInt()
        )
        preProcInfo.ucAidLen = 7
        preProcInfo.ucKernType = tmAid.ucKernType
        preProcInfo.ucCrypto17Flg = 1
        preProcInfo.ucZeroAmtNoAllowed = 0
        preProcInfo.ucStatusCheckFlg = 0
        System.arraycopy(
            byteArrayOf(0xB2.toByte(), 0x00, 0x00, 0x00),
            0,
            preProcInfo.aucReaderTTQ,
            0,
            4
        )
        preProcInfo.ucTermFLmtFlg = 1
        preProcInfo.ucRdClssTxnLmtFlg = 1
        preProcInfo.ucRdCVMLmtFlg = 1
        preProcInfo.ucRdClssFLmtFlg = 1
        preProcInfo.ucTermFLmtFlg = 0
        mcAidParam = Clss_MCAidParam()
        mcAidParam.floorLimit = 99999999
        mcAidParam.threshold = 0
        mcAidParam.usUDOLLen = 3
        System.arraycopy(
            byteArrayOf(0x9F.toByte(), 0x6A.toByte(), 0x04.toByte()),
            0,
            mcAidParam.uDOL,
            0,
            3
        )
        mcAidParam.targetPer = 0
        mcAidParam.maxTargetPer = 0
        mcAidParam.floorLimitCheck = 1
        mcAidParam.randTransSel = 1
        mcAidParam.velocityCheck = 1
        System.arraycopy(
            byteArrayOf(
                0x00.toByte(),
                0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
            ), 0, mcAidParam.tacDenial, 0, 5
        )
        System.arraycopy(
            byteArrayOf(
                0xF4.toByte(),
                0x50.toByte(), 0x08.toByte(), 0x48.toByte(), 0x0C.toByte()
            ), 0, mcAidParam.tacOnline, 0, 5
        )
        System.arraycopy(
            byteArrayOf(
                0xF4.toByte(),
                0x50.toByte(), 0x08.toByte(), 0x48.toByte(), 0x0C.toByte()
            ), 0, mcAidParam.tacDefault, 0, 5
        )
        System.arraycopy(byteArrayOf(0x00, 0x00, 0x00, 0x00), 0, mcAidParam.acquierId, 0, 4)
        System.arraycopy(
            byteArrayOf(
                0x9F.toByte(),
                0x37,
                0x04,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            ), 0, mcAidParam.dDOL, 0, 20
        )
        System.arraycopy(
            byteArrayOf(
                0x00.toByte(),
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00,
                0x00
            ), 0, mcAidParam.tDOL, 0, 20
        )
        System.arraycopy(byteArrayOf(0x00, 0x02.toByte()), 0, mcAidParam.version, 0, 2)
        mcAidParam.forceOnline = 1
        System.arraycopy(byteArrayOf(0x00, 0x00, 0x00), 0, mcAidParam.magAvn, 0, 3)
        mcAidParam.ucMagSupportFlg = 1
        tmAidLists!!.set(1, tmAid)
        preProcInfos!!.set(1, preProcInfo)
        mcAidParams!!.set(1, mcAidParam)
    }

    companion object {
        const val EXTRA_RESULT_PAY_IS_SUCCESS = "EXTRA_RESULT_PAY_SUCCESS";
        const val EXTRA_RESULT_CARD_NUMBER = "EXTRA_RESULT_CARD_NUMBER";

        const val TXN_TYPE_ICC = 0x101
    }

}