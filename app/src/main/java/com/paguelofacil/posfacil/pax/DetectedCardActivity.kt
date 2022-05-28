package com.paguelofacil.posfacil.pax

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.base.BaseActivity
import com.paguelofacil.posfacil.data.network.api.ApiError
import com.paguelofacil.posfacil.databinding.ActivityDetectedCardBinding
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.fragments.ComprobanteCobroFragment.Companion.OPTION_CARD_SELECTED
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.pax.dal.*
import com.pax.dal.entity.EBeepMode
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPiccType
import com.pax.dal.entity.EScannerType
import com.pax.dal.exceptions.IccDevException
import com.pax.jemv.clcommon.EMV_APPLIST
import com.pax.jemv.clcommon.RetCode
import com.pax.jemv.device.DeviceManager
import com.pax.jemv.emv.api.EMVCallback
import com.pax.jemv.emv.model.EmvEXTMParam
import com.pax.jemv.emv.model.EmvMCKParam
import com.pax.jemv.emv.model.EmvParam
import com.pax.neptunelite.api.NeptuneLiteUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetectedCardActivity : BaseActivity() {
    private var dialog: CancelBottomSheet? = null
    private val viewModel: CobroViewModel by viewModels()

    private lateinit var binding: ActivityDetectedCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetectedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch {
            withContext(Dispatchers.IO){
                intent?.run {
                    val optionSelected = extras?.getInt(OPTION_CARD_SELECTED) ?: 0
                    if (optionSelected == 0) {
                        accionDetectCardChip()
                    } else if (optionSelected == 1){
                        accionLeerBanda()
                    }
                }
            }
        }
        initNeptune()
    }

    private fun initNeptune() {
        try {
            dalProxyClient = NeptuneLiteUser.getInstance();
            Dal = dalProxyClient?.getDal(this);
            Ped = Dal?.getPed(EPedType.INTERNAL);
            Mag = Dal?.getMag();
            ICC = Dal?.getIcc();
            Picc = Dal?.getPicc(EPiccType.INTERNAL);
            Sys = Dal?.getSys();
            Scanner = Dal?.getScanner(EScannerType.REAR);

            loadLibrary();
        } catch (e: Exception) {
        }
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
            } while (ICC.detect(0.toByte()) == false)
            Sys.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)

            val map = HashMap<String, Any?>()
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    map["track"] = ""
                    viewModel.saveDataCardFirestore(map)
                    toTransResultPage()
                }
            }
            /*while (!ICC!!.detect(0.toByte()))*/
            val emvParam: EmvParam
            val mckParam: EmvMCKParam
            emvParam = EmvParam()
            mckParam = EmvMCKParam()
            mckParam.extmParam = EmvEXTMParam()
            DeviceManager.getInstance().setIDevice(DeviceImplNeptune.getInstance(Dal))
            var ret = EMVCallback.EMVCoreInit()
            if (ret != RetCode.EMV_OK) {
                return
            }
            EMVCallback.EMVSetCallback()
            EMVCallback.EMVGetParameter(emvParam)
            Sys?.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)
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

                val map = HashMap<String, Any?>()
                map["track1"] = hex(track1Final.data)
                map["track2"] = hex(track2.data)
                map["track3"] = hex(track2Final.data)
                map["cardHolder"] = str
                viewModel.saveDataCardFirestore(map)
                toTransResultPage()

            } catch (e: java.lang.Exception) {
                return
            }
        } catch (e: IccDevException) {
            e.printStackTrace()
        }
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

    private fun accionLeerBanda() {
        try {
            Mag.open()
            Mag.reset()
            do {
            } while (Mag.isSwiped == false)
            val tracks = Mag.read()
            Sys.beep(EBeepMode.FREQUENCE_LEVEL_1, 500)

            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val map = HashMap<String, Any?>()
                    map["track1"] = tracks?.track1
                    map["track2"] = tracks?.track2
                    map["track3"] = tracks?.track3
                    map["track4"] = tracks?.track4
                    viewModel.saveDataCardFirestore(map)
                    toTransResultPage()
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

    private fun showBottomSheet(mensaje: String, origen: Int) {
        val dialog = baseContext?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)
        view.tv_mensaje_dialog.text = mensaje
        btnClose.setOnClickListener {
            dialog?.dismiss()
            if (origen == 1)
                finish()
            else
                goViewFirma()
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


    companion object {
        const val EXTRA_RESULT_PAY_IS_SUCCESS = "EXTRA_RESULT_PAY_SUCCESS";
        const val EXTRA_RESULT_CARD_NUMBER = "EXTRA_RESULT_CARD_NUMBER";

        const val TXN_TYPE_ICC = 0x101
    }

}