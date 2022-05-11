package com.paguelofacil.posfacil.pax

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.ApplicationClass.Companion.getApp
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.ActivityMain2Binding
import com.paguelofacil.posfacil.pax.entity.DetectCardResult
import com.paguelofacil.posfacil.pax.entity.EnterPinResult
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessContract
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessPresenter
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.DetectCardContract
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.NeptunePollingPresenter
import com.paguelofacil.posfacil.pax.util.*
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.showToast
import com.pax.commonlib.utils.LogUtils
import com.pax.commonlib.utils.ToastUtils
import com.pax.commonlib.utils.convert.ConvertHelper
import com.pax.commonlib.utils.convert.IConvert
import com.pax.dal.entity.EReaderType
import com.pax.jemv.clcommon.RetCode
import com.paxsz.module.emv.param.EmvTransParam
import com.paxsz.module.emv.process.contact.EmvProcess
import com.paxsz.module.emv.process.contactless.ClssProcess
import com.paxsz.module.emv.process.entity.EOnlineResult
import com.paxsz.module.emv.process.entity.IssuerRspData
import com.paxsz.module.emv.process.entity.TransResult
import com.paxsz.module.emv.process.enums.CvmResultEnum
import com.paxsz.module.emv.process.enums.TransResultEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DetectCardContract.View, TransProcessContract.View {
    private var dialog: CancelBottomSheet? = null
    private var mapFlags: Map<String, Boolean>? = null

    private val viewModel: CobroViewModel by viewModels()
    private lateinit var binding: ActivityMain2Binding

    private val importeCobro = "1.00"
    private var mEnterPinPopWindow: PopupWindow? = null

    private var pinText: TextView? = null
    private var pinResult = 0

    private var currTransResultEnum: TransResultEnum? = null
    private var currentTxnCVMResult: CvmResultEnum? = null
    private var currTransResultCode = RetCode.EMV_OK

    private var currentTxnType = TXN_TYPE_ICC
    private var hasDetectedCard = false
    private var isSecondTap = false
    private val issuerRspData = IssuerRspData()

    private var currOnlineResultIndex = 0
    private var transAmt: Long = 0
    private var otherAmt: Long = 0
    private var transType: Byte = 0
    private var isOnlineApprovedNo2ndGAC = false

    private var selectOnlineResultDlg: AlertDialog? = null
    private var processingDlg: AlertDialog? = null
    private var transPromptDlg: AlertDialog? = null

    private var firstGacTVR = ""
    private var firstGacTSI = ""
    private var firstGacCID = ""

    private var paxUtil = PaxUtil()

    private var responseCode: ByteArray? = null

    private var onlineResultCode =
        EOnlineResult.FAILED.ordinal //online approve,online deny,connect host failed


    private var transProcessPresenter: TransProcessPresenter? = null
    private var detectPresenter: NeptunePollingPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        initTransProcessPresenter()
        loadListeners()
        loadLanguage()
        initObservers()
        viewModel.getFlagsDeteccionTarjetaFirestore()
        //validateCard()
    }

    private fun loadListeners() {
        /*binding.lnArrowBack.setOnClickListener { onBackPressed() }

        binding.lnCloseBack.setOnClickListener { showBottomSheet() }*/
    }

    private fun showBottomSheet(mensaje: String, origen: Int) {
        val dialog = baseContext?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)
        view.tv_mensaje_dialog.text = mensaje
//        if (origen == 1)
//            view.iv_alert_dialog.setImageDrawable(
//                ContextCompat.getDrawable(
//                    this,
//                    R.drawable.circle_yellow
//                )
//            )
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
        /*binding.textViewTitle.text = ApplicationClass.language.verificarCobro
        binding.tvMessageValideCard.text = ApplicationClass.language.esperandoTarjeta*/
    }

    private fun initObservers() {
        viewModel.mutableFlags.observe(this) {
            mapFlags = it
            validateCard()
            initStartProcess()
        }
    }

    private fun initStartProcess() {
        initTransProcessPresenter()
        transPreProcess(true)
        startDetectCard(EReaderType.ICC_PICC)
    }

    private fun performTransaction() {
        /*binding.tvMessageValideCard.text = getString(R.string.verificando_card)
        binding.ivWaitCard.visibility = View.GONE*/
        showBottomSheet(getString(R.string.cobro_succes), 2)
    }

    private fun goViewFirma() {
//        val fr = activity?.supportFragmentManager?.beginTransaction()
//        fr?.replace(R.id.container_frag_cobro, FirmaFragment())
//        fr?.addToBackStack(null)?.commit()
    }

    private fun validateCard() {
        transType = currTxnTypeCode
        transAmt = CurrencyConverter.parse(importeCobro)
        otherAmt = CurrencyConverter.parse("0.0")
    }

    private val currTxnTypeCode: Byte
        private get() {
            var typeCode: Byte = 0x00
            when (0) {
                0 -> typeCode = 0x00 //sale
                1 -> typeCode = 0x20 //refund
                2 -> typeCode = 0x09 //cashback
                3 -> typeCode = 0x30 //inquiry
            }
            return typeCode
        }

    override fun onUpdatePinLen(pin: String?) {
        getApp().runOnUiThread {
            if (pinText != null) {
                pinText?.text = pin
            }
        }
    }

    override fun getEnteredPin(): String {
        return if (pinText == null) "" else pinText?.text.toString()
    }

    override fun onEnterPinFinish(pinResult: Int) {
        this.pinResult = pinResult
        getApp().runOnUiThread {
            if (mEnterPinPopWindow != null && (mEnterPinPopWindow?.isShowing() == true)) {
                mEnterPinPopWindow?.dismiss()
            }
            if (pinResult == EnterPinResult.RET_SUCC || pinResult == EnterPinResult.RET_CANCEL || pinResult == EnterPinResult.RET_TIMEOUT || pinResult == EnterPinResult.RET_PIN_BY_PASS || pinResult == EnterPinResult.RET_OFFLINE_PIN_READY || pinResult == EnterPinResult.RET_NO_KEY
            ) {
                LogUtils.d(TAG, "to do nothing")
            } else {
                displayTransPromptDlg(
                    PROMPT_TYPE_FAILED,
                    pinResult.toString() + ""
                )
            }
        }
    }

    override fun onStartEnterPin(prompt: String?) {
        LogUtils.w(
            "TAG",
            "onStartEnterPin, current thread " + Thread.currentThread().name + ", id:" + Thread.currentThread().id
        )
        getApp().runOnUiThread { displayEnterPinDlg(prompt ?: "") }
    }

    override fun onTransFinish(transResult: TransResult?) {
        currTransResultEnum = transResult?.transResult
        currentTxnCVMResult = transResult?.cvmResult
        currTransResultCode = transResult?.resultCode ?: 0
        LogUtils.d(
            TAG,
            "onTransFinish,retCode:" + currTransResultCode + ", transResult:" + currTransResultEnum + ", cvm result:" + transResult?.cvmResult
        )
        getFirstGACTag()
        if (transResult?.resultCode == RetCode.EMV_OK) {
            processCvm()
        } else {
            processTransResult(transResult)
        }
    }

    override fun onCompleteTrans(transResult: TransResult?) {
        currTransResultEnum = transResult?.transResult
        currTransResultCode = transResult?.resultCode ?: 0
        LogUtils.d(
            TAG,
            "onCompleteTrans,retCode:" + transResult?.resultCode + ", transResult:" + currTransResultEnum
        )
        if (transResult?.resultCode == RetCode.EMV_OK) {
            //1.to Trans result page
        }
        toTransResultPage()
    }

    override fun onRemoveCard() {
        clssLighteErr()
        showToast("Please remove card")
    }

    override fun onReadCardOK() {
        clssLightReadCardOk()
    }

    override fun onMagDetectOK(pan: String?, expiryDate: String?) {
        // magstripe Fallback(terminal fallback to a magstripe transaction when chip cannot be read)
        currentTxnType = TXN_TYPE_MAG
        hasDetectedCard = true
        /*binding.tvPan.setVisibility(View.VISIBLE)
        binding.tvExpiryDate.setVisibility(View.VISIBLE)

        binding.tvPan.setText(pan)
        binding.tvExpiryDate.setText(expiryDate)*/
        //add CVM process, such as enter pin or signature and so on.
        displayTransPromptDlg(PROMPT_TYPE_SUCCESS, "MSR")
    }

    override fun onIccDetectOK() {
        currentTxnType = TXN_TYPE_ICC
        hasDetectedCard = true
        ToastUtils.showToast(this@MainActivity, "ICC detect succ")
        if (transProcessPresenter != null) {
            transProcessPresenter?.startEmvTrans()
        }
    }

    override fun onPiccDetectOK() {
        currentTxnType = TXN_TYPE_PICC
        hasDetectedCard = true
        clssLightProcessing()
        ToastUtils.showToast(this@MainActivity, "PICC detect succ")
        if (transProcessPresenter != null) {
            if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE) {
            } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) {
            } else if (isSecondTap) { //visa card and other card(not contain master card) 2nd detect card
                isSecondTap = false
                transProcessPresenter?.completeClssTrans(issuerRspData)
            } else {
                transProcessPresenter?.startClssTrans() // first time detect card finish
            }
        }
    }

    override fun onDetectError(errorCode: DetectCardResult.ERetCode?) {
        if (errorCode == DetectCardResult.ERetCode.FALLBACK) {
            ToastUtils.showToast(this@MainActivity, "Fallback,Please insert card")
        } else {
            displayTransPromptDlg(PROMPT_TYPE_FAILED, errorCode?.name ?: "")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDetectCard()
        if (transProcessPresenter != null) {
            transProcessPresenter?.detachView()
            transProcessPresenter = null
        }
    }

    private fun stopDetectCard() {
        if (detectPresenter != null) {
            detectPresenter?.stopDetectCard()
            detectPresenter?.detachView()
            detectPresenter?.closeReader()
            detectPresenter = null
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun displayEnterPinDlg(title: String) {
        if (isFinishing) {
            return
        }
        if (mEnterPinPopWindow != null) {
            if (mEnterPinPopWindow?.isShowing() == true) {
                mEnterPinPopWindow?.dismiss()
            }
            mEnterPinPopWindow = null
        }
        val popView = layoutInflater.inflate(R.layout.dlg_enter_pin, null)
        mEnterPinPopWindow = PopupWindow(
            popView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        pinText = popView.findViewById<TextView>(R.id.tv_pin)
        val titleTxt = popView.findViewById<TextView>(R.id.tv_title)
        titleTxt.text = title
        mEnterPinPopWindow?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
        mEnterPinPopWindow?.setFocusable(true)
        mEnterPinPopWindow?.setOutsideTouchable(false)
        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT,
            0f,
            Animation.RELATIVE_TO_PARENT,
            0f,
            Animation.RELATIVE_TO_PARENT,
            1f,
            Animation.RELATIVE_TO_PARENT,
            0f
        )
        animation.interpolator = AccelerateInterpolator()
        animation.duration = 200
        mEnterPinPopWindow?.setOnDismissListener(PopupWindow.OnDismissListener {
            ScreenUtils.lightOn(this@MainActivity)
            if (currentTxnType == TXN_TYPE_PICC) {
                if (pinResult != 0) {
                    displayTransPromptDlg(
                        PROMPT_TYPE_FAILED,
                        "getString pinblock err: $pinResult"
                    )
                } else {
                    checkTransResult()
                }
            }
        })
        mEnterPinPopWindow?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        /*mEnterPinPopWindow?.showAtLocation(
            binding.viewBottom,
            Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
            0,
            0
        )*/
        popView.startAnimation(animation)
        ScreenUtils.lightOff(this@MainActivity)
    }

    private fun checkTransResult() {
        LogUtils.w(TAG, "checkTransResult:$currTransResultEnum")
        if (currTransResultEnum == TransResultEnum.RESULT_REQ_ONLINE) {
            // 1.online process 2.to result page
            onlineProcess()
        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_APPROVED) {
            //1.to result page
            toTransResultPage()
        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_DENIED) {
            // 1.to result page
            toTransResultPage()
        } else {
            LogUtils.e(TAG, "unexpected result,$currTransResultEnum")
        }
    }

    private fun displaySelectOnlineResultDlg() {
        if (selectOnlineResultDlg != null && (selectOnlineResultDlg?.isShowing() == true)) {
            selectOnlineResultDlg?.dismiss()
            selectOnlineResultDlg = null
        }
        val script1 = byteArrayOf(
            0x71, 0x0F,
            0x9F.toByte(), 0x18, 0x04, 0x11, 0x22, 0x33, 0x44,
            0x86.toByte(), 0x06, 0x84.toByte(), 0x24, 0x00, 0x00, 0x00, 0X00
        )
        val script2 = ConvertHelper.getConvert().strToBcd(
            "72289F1804AABBCCDD86098424000004ABBBCCDD86098418000004BBBBCCDD86098416000004CCBBCCDD",
            IConvert.EPaddingPosition.PADDING_RIGHT
        )
        val authCode = ConvertHelper.getConvert()
            .strToBcd("313233343536", IConvert.EPaddingPosition.PADDING_RIGHT)
        val authData = ConvertHelper.getConvert()
            .strToBcd("e344ee82e00ca8763030", IConvert.EPaddingPosition.PADDING_RIGHT)
        val onlineResultArr = arrayOf(
            "Online Approved\n(With Scripts)",
            "Online Approved\n(No Scripts)",
            "Online Approved\n(No 2nd GAC)",
            "Online Decline",
            "Online Failed"
        )
        onlineResultCode = EOnlineResult.APPROVE.ordinal
        responseCode =
            ConvertHelper.getConvert().strToBcd("3030", IConvert.EPaddingPosition.PADDING_RIGHT)
        currOnlineResultIndex = 0
        currTransResultEnum = TransResultEnum.RESULT_ONLINE_APPROVED
        selectOnlineResultDlg = AlertDialog.Builder(this@MainActivity).setSingleChoiceItems(
            onlineResultArr, 0
        ) { dialogInterface, i ->
            currOnlineResultIndex = i
            if (i == 0 || i == 1) { //online approve
                onlineResultCode = EOnlineResult.APPROVE.ordinal
                responseCode =
                    ConvertHelper.getConvert()
                        .strToBcd("3030", IConvert.EPaddingPosition.PADDING_RIGHT)
            } else if (i == 2) { // online approve but no 2nd GAC
                isOnlineApprovedNo2ndGAC = true
                currTransResultEnum = TransResultEnum.RESULT_ONLINE_APPROVED
            } else if (i == 3) { //online decline
                currTransResultEnum = TransResultEnum.RESULT_ONLINE_DENIED
                onlineResultCode = EOnlineResult.DENIAL.ordinal
                responseCode =
                    ConvertHelper.getConvert()
                        .strToBcd("3035", IConvert.EPaddingPosition.PADDING_RIGHT)
            } else if (i == 4) { //online failed
                onlineResultCode = EOnlineResult.FAILED.ordinal
                currTransResultEnum = TransResultEnum.RESULT_ONLINE_CARD_DENIED
            }
        }.setNegativeButton("OK") { dialogInterface, i ->
            dialogInterface.dismiss()
            if (currOnlineResultIndex == 2) { //no 2nd GAC process
                toTransResultPage()
            } else {
                if (onlineResultCode != EOnlineResult.FAILED.ordinal) {
                    issuerRspData.setRespCode(responseCode) //TAG:8A
                    issuerRspData.setAuthCode(authCode) //TAG:89
                    issuerRspData.setAuthData(authData) //TAG:91
                    if (currOnlineResultIndex == 0) { //online approve with scripts
//                            issuerRspData.setScript(script2);
                        issuerRspData.setScript(
                            CardInfoUtils.combine7172(
                                Arrays.copyOfRange(
                                    script1,
                                    2,
                                    script1.size
                                ), Arrays.copyOfRange(script2, 2, script2.size)
                            )
                        )
                    } else if (currOnlineResultIndex == 3) { //online decline
//                            issuerRspData.setScript(script1); with script 71
                    }
                }
                issuerRspData.setOnlineResult(paxUtil.parseIntToByte(onlineResultCode))
                if (transProcessPresenter != null) {
                    if (currentTxnType == TXN_TYPE_ICC) {
                        transProcessPresenter?.completeEmvTrans(issuerRspData)
                    } else if (currentTxnType == TXN_TYPE_PICC) {
                        //check if need second tap or not.
                        if (ClssProcess.getInstance().isNeedSecondTap(issuerRspData)) {
                            isSecondTap = true
                            showToast("Second tap, Pls tap card to execute script or issuer auth")
                            startDetectCard(EReaderType.PICC)
                        } else {
                            isOnlineApprovedNo2ndGAC = true
                            toTransResultPage()
                        }
                    }
                }
            }
        }.setCancelable(false).setTitle("Please Select Online Result").create()
        if (!isFinishing) {
            selectOnlineResultDlg?.show()
        }
    }

    private fun getFirstGACTag() {
        var byteArray = com.pax.jemv.clcommon.ByteArray()
        var ret = EmvProcess.getInstance().getTlv(0x95, byteArray)
        if (ret == RetCode.EMV_OK) {
            val dataArr = ByteArray(byteArray.length)
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
            firstGacTVR = ConvertHelper.getConvert().bcdToStr(dataArr)
        }
        byteArray = com.pax.jemv.clcommon.ByteArray()
        ret = EmvProcess.getInstance().getTlv(0x9B, byteArray)
        if (ret == RetCode.EMV_OK) {
            val dataArr = ByteArray(byteArray.length)
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
            firstGacTSI = ConvertHelper.getConvert().bcdToStr(dataArr)
        }
        byteArray = com.pax.jemv.clcommon.ByteArray()
        ret = EmvProcess.getInstance().getTlv(0x9F27, byteArray)
        if (ret == RetCode.EMV_OK) {
            val dataArr = ByteArray(byteArray.length)
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
            firstGacCID = ConvertHelper.getConvert().bcdToStr(dataArr)
        }
    }

    private fun displayTransPromptDlg(type: Int, msg: String) {
        if (transPromptDlg != null) {
            if (transPromptDlg?.isShowing() == true) {
                transPromptDlg?.dismiss()
            }
            transPromptDlg = null
        }
        if (type == PROMPT_TYPE_SUCCESS) {
            transPromptDlg = AlertDialog.Builder(this@MainActivity).setCancelable(false)
                .setIcon(R.mipmap.ic_dialog_alert_holo_light).setTitle("Transaction Prompt")
                .setMessage(msg).create()
        } else {
            transPromptDlg = AlertDialog.Builder(this@MainActivity).setCancelable(false)
                .setIcon(R.mipmap.indicator_input_error).setTitle("Transaction Failed").setMessage(
                    "errCode:$msg"
                ).create()
        }
        transPromptDlg?.setOnDismissListener { this@MainActivity.finish() }
        LogUtils.d(TAG, "is Act Finish?$isFinishing")
        if (!isFinishing) {
            transPromptDlg?.show()
            TickTimer().start(3) {
                if (transPromptDlg != null && (transPromptDlg?.isShowing() == true)) {
                    transPromptDlg?.dismiss()
                }
            }
        }
    }

    private fun processCvm() {
        //get TransResult
        if (currentTxnCVMResult == CvmResultEnum.CVM_NO_CVM) {
            //1.check trans result
            checkTransResult()
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_SIG) {
            //1.signature process 2.check trans result
            signatureProcess()
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN) {
            if (currentTxnType == TXN_TYPE_PICC) {
                //1.online pin process 2.check trans result
                transProcessPresenter?.startOnlinePin()
            } else if (currentTxnType == TXN_TYPE_ICC) {
                //check result
                checkTransResult()
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN_SIG) {
            if (currentTxnType == TXN_TYPE_PICC) {
                //picc no this cvm
            } else if (currentTxnType == TXN_TYPE_ICC) {
                //1.signature process 2.check trans result
                signatureProcess()
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_OFFLINE_PIN) { //contact trans
            //1.check trans result
            checkTransResult()
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_CONSUMER_DEVICE) { //contactless trans
            //1.restart detect(tap) card and transaction
            startClssTransAgain("See phone, Please tap phone")
        }
    }

    private fun processTransResult(transResult: TransResult?) {
        //check if need to show rea light first
        showClssErrLight(transResult)
        if (currTransResultEnum == TransResultEnum.RESULT_FALLBACK) { //contact
            showToast(" Fallback, Please swipe card")
            ToastUtils.showToast(this@MainActivity, "Fallback, Please swipe card")
            startDetectCard(EReaderType.MAG) // onMagDetectOk will callback
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_SEE_PHONE) { //contactless
            //PICC return  USE_CONTACT 1.restart detect(insert/swipe) card and transaction
            startClssTransAgain("See phone, Please tap phone")
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE
            || transResult?.resultCode == RetCode.CLSS_USE_CONTACT
        ) { //contactless
            showToast("Try other interface, Please Insert card")
            clssLightCloseAll()
            startDetectCard(EReaderType.ICC)
        } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) { //contactless
            //PICC return  USE_CONTACT 1.restart detect card and transaction
            startClssTransAgain("Try again, Please tap card again")
        } else if (transResult?.resultCode == RetCode.EMV_DENIAL
            || transResult?.resultCode == RetCode.CLSS_DECLINE
        ) {
            //to result page to get tag95 and tag 9b to find the reason of deciline
            toTransResultPage()
        } else {
            displayTransPromptDlg(
                PROMPT_TYPE_FAILED,
                transResult?.resultCode.toString() + ""
            )
        }
    }

    private fun toTransResultPage() {
        val intent = Intent()
        intent.putExtra(EXTRA_RESULT_PAY_IS_SUCCESS, true)
        intent.putExtra(EXTRA_RESULT_CARD_NUMBER, paxUtil.getDataCard())
        setResult(Activity.RESULT_OK, intent)
        this@MainActivity.finish()
//        if (true) {
//            val intent = Intent()
//            intent.putExtra(EXTRA_RESULT_PAY_IS_SUCCESS, true)
//            intent.putExtra(EXTRA_RESULT_CARD_NUMBER, paxUtil.getDataCard())
//            setResult(Activity.RESULT_OK, intent)
//            this@MainActivity.finish()
//        } else {
//            showBottomSheet("¡Ha ocurrido un error!", 1)
//        }
    }

    private fun clssLighteErr() {
//        clssLightView.setLights(0, ClssLight.OFF)
//        clssLightView.setLights(1, ClssLight.OFF)
//        clssLightView.setLights(2, ClssLight.OFF)
//        clssLightView.setLights(3, ClssLight.ON)
    }

    private fun clssLightReadCardOk() {
//        clssLightView.setLights(0, ClssLight.ON)
//        clssLightView.setLights(1, ClssLight.ON)
//        clssLightView.setLights(2, ClssLight.ON)
//        clssLightView.setLights(3, ClssLight.OFF)
    }

    private fun clssLightProcessing() {
//        clssLightView.setLights(0, ClssLight.ON);
//        clssLightView.setLights(1, ClssLight.ON);
//        clssLightView.setLights(2, ClssLight.OFF);
//        clssLightView.setLights(3, ClssLight.OFF);
    }

    private fun clssLightCloseAll() {
//        clssLightView.setLights(0, ClssLight.OFF);
//        clssLightView.setLights(1, ClssLight.OFF);
//        clssLightView.setLights(2, ClssLight.OFF);
//        clssLightView.setLights(3, ClssLight.OFF);
    }

    private fun clssLightDetectCard() {
//        clssLightView.setLights(0, ClssLight.BLINK)
//        ClssLight(1, ClssLight.OFF)
//        ClssLight(2, ClssLight.OFF)
//        clssLightView.setLights(3, ClssLight.OFF)
    }

    private fun initTransProcessPresenter() {
        if (transProcessPresenter == null) {
            transProcessPresenter = TransProcessPresenter()
            transProcessPresenter?.attachView(this)
        }
    }

    private fun onlineProcess() {
        //====online process =====
        //1.get TAG value with getTlv API
        //2.pack message, such as ISO8583
        //3.send message to acquirer host
        //4.get response of acquirer host
        //5.set value of acquirer result code and script, such as TAG 71(Issuer Script Data 1),72(Issuer Script Data 2),91(Issuer Authentication Data),8A(Response Code),89(Authorization Code) and so on.
        //6.call completeTransProcess API

        //There is a time-consuming wait dialog to simulate the online process
        displayProcessDlg(PROCESSING_TYPE_ONLINE, "Online Processing...")
    }

    private fun startDetectCard(readType: EReaderType) {
        hasDetectedCard = false
        TimeRecordUtils.clearTimeRecordList()
        if (detectPresenter != null) {
            detectPresenter?.stopDetectCard()
            detectPresenter?.detachView()
            detectPresenter?.closeReader()
            detectPresenter = null
        }


        /* ============NOTE==============
             Detect card with API getPicc()/getIcc/getMag ==> DetectCardPresenter(PiccDetectModel,IccDetectModel,MagDetectModel),
             DetectCardPresenter has resolve the detect card conflict problem("when swipe card, some terminals may detect picc ,as a result of terminal's mag reader and picc reader are very close"),
             but it may increase the time of detecting card process
           */
//        detectPresenter = new DetectCardPresenter();

        // detect card with polling() ==> NeptunePollingPresenter
        detectPresenter = NeptunePollingPresenter()
        detectPresenter?.attachView(this)
        detectPresenter?.startDetectCard(readType)
    }

    //the Scenes to show red light, refer to Book A
    private fun showClssErrLight(transResult: TransResult?) {
        //if show clss light first
        if (currentTxnType == TXN_TYPE_PICC) {
            if (transResult?.resultCode != RetCode.EMV_OK) {
                clssLighteErr()
            }
        }
    }

    private fun startClssTransAgain(msg: String) {
        detectPresenter?.closeReader()
        transPreProcess(false)
        isSecondTap = false
        /*binding.tvUseCardPrompt.setTextColor(Color.RED)
        binding.tvUseCardPrompt.setText(msg)*/
        clssLightDetectCard()
        startDetectCard(EReaderType.PICC)
    }

    private fun signatureProcess() {
        //There is a time-consuming wait dialog to simulate the signature process
        displayProcessDlg(PROCESSING_TYPE_SIGNATURE, "Signature Processing...")
    }

    private fun displayProcessDlg(type: Int, msg: String) {
        if (isFinishing) {
            return
        }
        if (processingDlg != null) {
            if (processingDlg?.isShowing() == true) {
                processingDlg?.dismiss()
            }
            processingDlg = null
        }
        val mProgressDlgBuilder =
            AlertDialog.Builder(this@MainActivity, R.style.AlertDialog)
        val view =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.dlg_processing, null)
        (view.findViewById<View>(R.id.tv_msg) as TextView).text = msg
        mProgressDlgBuilder.setCancelable(false)
        mProgressDlgBuilder.setView(view)
        processingDlg = mProgressDlgBuilder.create()
        processingDlg?.setOnDismissListener(DialogInterface.OnDismissListener {
            if (type == MainActivity.PROCESSING_TYPE_ONLINE) {
                //show dialog to select online approve or online decline simulate online result
                displaySelectOnlineResultDlg()
            } else if (type == MainActivity.PROCESSING_TYPE_SIGNATURE) {
                checkTransResult()
            }
        })
        processingDlg?.show()
        TickTimer().start(3) {
            if (processingDlg != null && (processingDlg?.isShowing() == true)) {
                processingDlg?.dismiss()
            }
        }
        val params: WindowManager.LayoutParams = processingDlg?.getWindow()?.getAttributes()!!
        params.width = 600
        params.height = 400
        processingDlg?.getWindow()?.setAttributes(params)
        processingDlg?.getWindow()?.setBackgroundDrawableResource(android.R.color.background_light)
    }

    //before detected card.
    private fun transPreProcess(isNeedContact: Boolean) {
        try {
            val transParam = EmvTransParam()
            LogUtils.i(
                TAG,
                "transType:" + ConvertHelper.getConvert()
                    .bcdToStr(byteArrayOf(transType)) + ",int val:" + transType
            )
            transParam.transType = transType
            transParam.amount = java.lang.Long.toString(transAmt)
            transParam.amountOther = java.lang.Long.toString(otherAmt)
            transParam.terminalID = AppDataUtils.getSN()
            transParam.transCurrencyCode = CurrencyConverter.getCurrencyCode()
            transParam.transCurrencyExponent = CurrencyConverter.getCurrencyFraction().toByte()
            transParam.transDate = AppDataUtils.getCurrDate()
            transParam.transTime = AppDataUtils.getCurrTime()
            transParam.transTraceNo = "0001"
            transProcessPresenter?.preTrans(transParam, isNeedContact)
        } catch (e: IllegalArgumentException) {
            LogUtils.e(TAG, e)
        }
    }

    companion object {
        const val EXTRA_RESULT_PAY_IS_SUCCESS = "EXTRA_RESULT_PAY_SUCCESS";
        const val EXTRA_RESULT_CARD_NUMBER = "EXTRA_RESULT_CARD_NUMBER";

        val TAG = "TransProcessActivity"
        val PROCESSING_TYPE_ONLINE = 1
        val PROCESSING_TYPE_SIGNATURE = 2
        val PROMPT_TYPE_FAILED = 1
        val PROMPT_TYPE_SUCCESS = 2

        const val TXN_TYPE_MAG = 0x100
        const val TXN_TYPE_ICC = 0x101
        const val TXN_TYPE_PICC = 0x102

        val EXTRA_TRANS_TYPE = "trans_type"
        val EXTRA_TRANS_AMOUNT = "trans_amount"
        val EXTRA_OTHER_AMOUNT = "other_amount"
        val EXTRA_TRANS_RESULT = "trans_result"
        val EXTRA_TRANS_RESULT_CODE = "trans_result_code"
        val EXTRA_CVM_RESULT = "cvm_result"
        val EXTRA_1STGAC_TVR = "first_gac_tvr"
        val EXTRA_1STGAC_TSI = "first_gac_tsi"
        val EXTRA_1STGAC_CID = "first_gac_cid"
        val EXTRA_CURRENT_TXN_TYPE = "current_txn_type"
        val EXTRA_IS_ONLINE_APPROVE_WITHOUT_2GAC = "is_online_approved_without_2GAC"
    }

}