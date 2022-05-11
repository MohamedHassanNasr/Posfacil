package com.paguelofacil.posfacil.ui.view.transactions.payment.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.paguelofacil.posfacil.ApplicationClass
import com.paguelofacil.posfacil.R
import com.paguelofacil.posfacil.databinding.FragmentDetectCreditCardBinding
import com.paguelofacil.posfacil.pax.entity.DetectCardResult
import com.paguelofacil.posfacil.pax.entity.EnterPinResult
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessContract
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessPresenter
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.DetectCardContract
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.NeptunePollingPresenter
import com.paguelofacil.posfacil.pax.util.AppDataUtils
import com.paguelofacil.posfacil.pax.util.CurrencyConverter
import com.paguelofacil.posfacil.pax.util.TimeRecordUtils
import com.paguelofacil.posfacil.ui.view.adapters.ListItemSolucionAdapter
import com.paguelofacil.posfacil.ui.view.custom_view.CancelBottomSheet
import com.paguelofacil.posfacil.ui.view.home.activities.HomeActivity
import com.paguelofacil.posfacil.ui.view.transactions.payment.viewmodel.CobroViewModel
import com.paguelofacil.posfacil.util.networkErrorConverter
import com.pax.dal.*
import com.pax.dal.entity.EPedType
import com.pax.dal.entity.EPiccType
import com.pax.dal.entity.EReaderType
import com.pax.dal.entity.EScannerType
import com.pax.jemv.clcommon.RetCode
import com.pax.neptunelite.api.NeptuneLiteUser
import kotlinx.android.synthetic.main.bottom_sheet_dialog.view.*
import timber.log.Timber
import java.util.*
import com.pax.jemv.clcommon.ByteArray as ByteArray


class DetectCreditCardFragment : Fragment(){
//    , DetectCardContract.View, TransProcessContract.View {
//
//    private val TAG = "TransProcessActivity"
//    private val PROCESSING_TYPE_ONLINE = 1
//    private val PROCESSING_TYPE_SIGNATURE = 2
//    private val PROMPT_TYPE_FAILED = 1
//    private val PROMPT_TYPE_SUCCESS = 2
//
//    val TXN_TYPE_MAG = 0x100
//    val TXN_TYPE_ICC = 0x101
//    val TXN_TYPE_PICC = 0x102
//
//    val EXTRA_TRANS_TYPE = "trans_type"
//    val EXTRA_TRANS_AMOUNT = "trans_amount"
//    val EXTRA_OTHER_AMOUNT = "other_amount"
//    val EXTRA_TRANS_RESULT = "trans_result"
//    val EXTRA_TRANS_RESULT_CODE = "trans_result_code"
//    val EXTRA_CVM_RESULT = "cvm_result"
//    val EXTRA_1STGAC_TVR = "first_gac_tvr"
//    val EXTRA_1STGAC_TSI = "first_gac_tsi"
//    val EXTRA_1STGAC_CID = "first_gac_cid"
//    val EXTRA_CURRENT_TXN_TYPE = "current_txn_type"
//    val EXTRA_IS_ONLINE_APPROVE_WITHOUT_2GAC = "is_online_approved_without_2GAC"
//
//    private var detectPresenter: NeptunePollingPresenter? = null
//    private var transProcessPresenter: TransProcessPresenter? = null
//    private var mEnterPinPopWindow: PopupWindow? = null
//    private var pinText: TextView? = null
//    private var processingDlg: AlertDialog? = null
//    private var selectOnlineResultDlg: AlertDialog? = null
//    private var transPromptDlg: AlertDialog? = null
//
//    private lateinit var responseCode: ByteArray
//    private var currOnlineResultIndex = 0
//    private var currentTxnType: Int = TXN_TYPE_ICC
//    private var hasDetectedCard = false
//    private var isOnlineApprovedNo2ndGAC = false
//    private var currTransResultEnum: TransResultEnum? = null
//    private var currTransResultCode = RetCode.EMV_OK
//    private var currentTxnCVMResult: CvmResultEnum? = null
//    private val transType: Byte = 0
//    private val transAmt: Long = 0
//    private val otherAmt: Long = 0
//    private val pinResult = 0
//
//    private var firstGacTVR = ""
//    private var firstGacTSI = ""
//    private var firstGacCID = ""
//    private var isSecondTap = false
//    private val issuerRspData: IssuerRspData = IssuerRspData()
//
//    private var dalProxyClient: NeptuneLiteUser? = null
//
//    private var Dal: IDAL? = null
//    private var Mag: IMag? = null
//    private var Ped: IPed? = null
//    private val Printer: IPrinter? = null
//    private var ICC: IIcc? = null
//    private var Picc: IPicc? = null
//    private var Sys: ISys? = null
//    private var Scanner: IScanner? = null
//    private var dialog: CancelBottomSheet? = null
//
//    private var mapFlags: Map<String, Boolean>? = null
//
//    lateinit var binding: FragmentDetectCreditCardBinding
//
//    private val viewModel: CobroViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //initNeptune()
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentDetectCreditCardBinding.inflate(inflater, container, false)
//        loadListeners()
//        loadLanguage()
//        initObservers()
//        viewModel.getFlagsDeteccionTarjetaFirestore()
//        validateCard()
//        Timber.e("BUNDLE ${arguments?.getString("EMAIL")} BUNDLE2 ${arguments?.getString("PHONE")}")
//
//        return binding.root
//    }
//
//    private fun loadLanguage() {
//        binding.textViewTitle.text = ApplicationClass.language.verificarCobro
//        binding.tvMessageValideCard.text = ApplicationClass.language.esperandoTarjeta
//    }
//
//    private fun validateCard() {    //TODO VERIFICAR BIEN CUANDO MOSTRAR EL WARNING
//        try {
//            do {
//            } while (ICC?.detect(0.toByte()) == false)
//            writeLogs()
//            showSuccesCard()
//        } catch (e: Exception) {
//            val data = hashMapOf<String, Any>("status" to "Failed  ${e.message}")
//            viewModel.saveDataCardFirestore(data)
//
//            //Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//            showWarningDialog(e.message ?: "Error inesperado"){
//                validateCard()
//            }
//            e.printStackTrace()
//        }
//    }
//
//    private fun showWarningDialog(message: String, onFailure: ()-> Unit){
//        val dialog = context?.let { BottomSheetDialog(it) }
//
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning, null)
//        val title =view.findViewById<TextView>(R.id.titleError)
//        val description =view.findViewById<TextView>(R.id.descriptionError)
//        val btn = view.findViewById<MaterialButton>(R.id.btnAccept)
//
//        title.text = "!Ha ocurrido un error!"
//        description.text = if ((message == "400") or (message == "400") or (message == "400")){
//            "Su contraseña\nno ha podido ser actualizada"
//        }else{
//            networkErrorConverter(message)
//        }
//
//        btn.text = "Intentar nuevamente"
//        btn.setOnClickListener {
//            dialog?.dismiss()
//            onFailure()
//        }
//
//        dialog?.setCancelable(true)
//
//        dialog?.setContentView(view)
//
//        dialog?.show()
//    }
//
//    private fun showWarningDialogList(message: List<String>, onFailure: ()-> Unit){
//        val dialog = context?.let { BottomSheetDialog(it) }
//
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_warning_with_list, null)
//        val title =view.findViewById<TextView>(R.id.titleError)
//        val description =view.findViewById<TextView>(R.id.descriptionError)
//        val btn = view.findViewById<MaterialButton>(R.id.btnTryAgain)
//        val rv = view.findViewById<RecyclerView>(R.id.rvItemSolucion)
//
//        title.text = "!Ha ocurrido un error!"
//        description.text = "Intenta con otro metodo de\nlectura de tarjeta:"
//
//        rv.hasFixedSize()
//        rv.adapter = ListItemSolucionAdapter(message)
//
//        btn.text = "Intentar nuevamente"
//        btn.setOnClickListener {
//            dialog?.dismiss()
//            onFailure()
//        }
//
//        dialog?.setCancelable(true)
//
//        dialog?.setContentView(view)
//
//        dialog?.show()
//    }
//
//    private fun writeLogs() {
//        val data = hashMapOf<String, Any>(
//            "status" to "Success",
//            "ICC.toString()" to if (mapFlags?.get("ICC") == true) ICC.toString() else "",
//            "ICC.cardAT24Cxx.toString()" to if (mapFlags?.get("ICC.cardAT24Cxx") == true) ICC?.cardAT24Cxx.toString() else "",
//            "ICC.cardAT88SC102.toString()" to if (mapFlags?.get("ICC.cardAT88SC102") == true) ICC?.cardAT88SC102.toString() else "",
//            "ICC.cardAT88SC153.toString()" to if (mapFlags?.get("ICC.cardAT88SC153") == true) ICC?.cardAT88SC153.toString() else "",
//            "ICC.cardAT88SC1608.toString()" to if (mapFlags?.get("ICC.cardAT88SC1608") == true) ICC?.cardAT88SC1608.toString() else "",
//            "ICC.cardSle4428.toString()" to if (mapFlags?.get("ICC.cardSle4428") == true) ICC?.cardSle4428.toString() else "",
//            "ICC.cardAT24Cxx.toString()" to if (mapFlags?.get("ICC.cardAT24Cxx") == true) ICC?.cardAT24Cxx.toString() else ""
//        )
//
//        viewModel.saveDataCardFirestore(data)
//    }
//
//    private fun showSuccesCard() {
//        binding.pbWaitingCard.visibility = View.GONE
//        binding.ivSucces.visibility = View.VISIBLE
//        binding.tvMessageValideCard.text = getString(R.string.tarjeta_reconocida_correctamente)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            validatingCard()
//        }, 2000)
//    }
//
//    private fun validatingCard() {
//        binding.ivSucces.visibility = View.GONE
//        binding.ivWaitCard.visibility = View.GONE
//        binding.pbWaitingCard.visibility = View.VISIBLE
//        binding.tvMessageValideCard.text = getString(R.string.verificando_card)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            showSuccesTransaction()
//        }, 2000)
//    }
//
//    private fun showSuccesTransaction() {
//        binding.ivSucces.visibility = View.VISIBLE
//        binding.ivWaitCard.visibility = View.GONE
//        binding.pbWaitingCard.visibility = View.GONE
//        binding.tvMessageValideCard.text = getString(R.string.cobro_succes)
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            goViewFirma()
//        }, 1000)
//
//    }
//
//    private fun initObservers() {
//        viewModel.mutableFlags.observe(viewLifecycleOwner) {
//            mapFlags = it
//            validateCard()
//        }
//    }
//
//    private fun loadListeners() {
//        binding.lnArrowBack.setOnClickListener { goBackFragment() }
//
//        binding.lnCloseBack.setOnClickListener { showBottomSheet() }
//    }
//
//    private fun goBackFragment() {
//        val fr = activity?.supportFragmentManager
//        fr?.popBackStack()
//    }
//
//    private fun showBottomSheet(mensaje: String, origen: Int) {
//        val dialog = context?.let { BottomSheetDialog(it) }
//        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
//        val btnClose = view.findViewById<Button>(R.id.btn_aceptar_dg)
//        view.tv_mensaje_dialog.text = mensaje
//        btnClose.setOnClickListener {
//            dialog?.dismiss()
//            if (origen == 1)
//                performTransaction()
//            else
//                goViewFirma()
//        }
//        dialog?.setCancelable(false)
//        dialog?.setContentView(view)
//        dialog?.show()
//    }
//
//    private fun goViewFirma() {
//        Timber.e("BUNDLE ${arguments?.getString("EMAIL")} BUNDLE2 ${arguments?.getString("PHONE")}")
//        val bundle = Bundle()
//        val email = arguments?.getString("EMAIL")
//        val phone = arguments?.getString("PHONE")
//        val frg = FirmaFragment()
//        bundle.putString("EMAIL", email)
//        bundle.putString("PHONE", phone)
//        val fr = activity?.supportFragmentManager?.beginTransaction()
//        frg.setArguments(bundle)
//        fr?.replace(R.id.container_frag_cobro, frg)
//        fr?.addToBackStack(null)?.commit()
//    }
//
//    private fun performTransaction() {
//        binding.tvMessageValideCard.text = getString(R.string.verificando_card)
//        binding.ivWaitCard.visibility = View.GONE
//        showBottomSheet(getString(R.string.cobro_succes), 2)
//    }
//
//    private fun initNeptune() {
//        try {
//            dalProxyClient = NeptuneLiteUser.getInstance()
//            Dal = dalProxyClient?.getDal(requireContext())
//            Ped = Dal?.getPed(EPedType.INTERNAL)
//            Mag = Dal?.getMag()
//            Sys = Dal?.getSys()
//            ICC = Dal?.getIcc()
//            Picc = Dal?.getPicc(EPiccType.INTERNAL)
//            Scanner = Dal?.getScanner(EScannerType.REAR)
//        } catch (e: Exception) {
//        }
//    }
//
//    private fun showBottomSheet() {
//        dialog = CancelBottomSheet(
//            callBackClose = { dismissBottomSheetCancel() },
//            callbackVolver = { dismissBottomSheetCancel() },
//            callbackCancelar = {
//                dismissBottomSheetCancel()
//                goHome()
//            }
//        )
//        dialog?.show(parentFragmentManager, "")
//    }
//
//    private fun dismissBottomSheetCancel() {
//        dialog?.dismiss()
//    }
//
//    private fun goHome() {
//        val intent = Intent(context, HomeActivity::class.java)
//        startActivity(intent)
//        activity?.finish()
//    }
//
//    private fun startDetectCard(readType: EReaderType) {
//        hasDetectedCard = false
//        TimeRecordUtils.clearTimeRecordList()
//        if (detectPresenter != null) {
//            detectPresenter!!.stopDetectCard()
//            detectPresenter!!.detachView()
//            detectPresenter!!.closeReader()
//            detectPresenter = null
//        }
//
//
//        /* ============NOTE==============
//             Detect card with API getPicc()/getIcc/getMag ==> DetectCardPresenter(PiccDetectModel,IccDetectModel,MagDetectModel),
//             DetectCardPresenter has resolve the detect card conflict problem("when swipe card, some terminals may detect picc ,as a result of terminal's mag reader and picc reader are very close"),
//             but it may increase the time of detecting card process
//           */
////        detectPresenter = new DetectCardPresenter();
//
//        // detect card with polling() ==> NeptunePollingPresenter
//        detectPresenter = NeptunePollingPresenter()
//        detectPresenter!!.attachView(this)
//        detectPresenter!!.startDetectCard(readType)
//    }
//
//    private fun initTransProcessPresenter() {
//        if (transProcessPresenter == null) {
//            transProcessPresenter = TransProcessPresenter()
//            transProcessPresenter!!.attachView(this)
//        }
//    }
//
//    //before detected card.
//    private fun transPreProcess(isNeedContact: Boolean) {
//        try {
//            val transParam = EmvTransParam()
//            transParam.setTransType(transType)
//            transParam.setAmount(java.lang.Long.toString(transAmt))
//            transParam.setAmountOther(java.lang.Long.toString(otherAmt))
//            transParam.setTerminalID(AppDataUtils.getSN())
//            transParam.setTransCurrencyCode(CurrencyConverter.getCurrencyCode())
//            transParam.setTransCurrencyExponent(CurrencyConverter.getCurrencyFraction() as Byte)
//            transParam.setTransDate(AppDataUtils.getCurrDate())
//            transParam.setTransTime(AppDataUtils.getCurrTime())
//            transParam.setTransTraceNo("0001")
//            transProcessPresenter?.preTrans(transParam, isNeedContact)
//        } catch (e: IllegalArgumentException) {
//
//        }
//    }
//
//    override fun onMagDetectOK(pan: String, expiryDate: String) {
//        // magstripe Fallback(terminal fallback to a magstripe transaction when chip cannot be read)
//        currentTxnType = TXN_TYPE_MAG
//        hasDetectedCard = true
//        Toast.makeText(activity, "MAG DETECT " + pan + "expiryDate " + expiryDate, Toast.LENGTH_LONG).show()
//
//        //add CVM process, such as enter pin or signature and so on.
//    }
//
//    override fun onIccDetectOK() {
//        currentTxnType = TXN_TYPE_ICC
//        hasDetectedCard = true
//        transProcessPresenter?.startEmvTrans()
//    }
//
//    override fun onPiccDetectOK() {
//        currentTxnType = TXN_TYPE_PICC
//        hasDetectedCard = true
//        if (transProcessPresenter != null) {
//            if (currTransResultEnum === TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE) {
//            } else if (currTransResultEnum === TransResultEnum.RESULT_TRY_AGAIN) {
//            } else if (isSecondTap) { //visa card and other card(not contain master card) 2nd detect card
//                isSecondTap = false
//                transProcessPresenter!!.completeClssTrans(issuerRspData)
//            } else {
//                transProcessPresenter!!.startClssTrans() // first time detect card finish
//            }
//        }
//    }
//
//    override fun onDetectError(errorCode: DetectCardResult.ERetCode) {
//        if (errorCode === DetectCardResult.ERetCode.FALLBACK) {
//            Timber.e("ERROR DETECT")
//        } else {
//        }
//    }
//
//    fun onBackPressed() {
//        stopDetectCard()
//    }
//
//    private fun stopDetectCard() {
//        if (detectPresenter != null) {
//            detectPresenter!!.stopDetectCard()
//            detectPresenter!!.detachView()
//            detectPresenter!!.closeReader()
//            detectPresenter = null
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopDetectCard()
//        if (transProcessPresenter != null) {
//            transProcessPresenter!!.detachView()
//            transProcessPresenter = null
//        }
//    }
//
//    override fun onUpdatePinLen(pin: String?) {
//        ApplicationClass.Companion.getApp()?.runOnUiThread {
//            pinText?.setText(pin)
//        }
//    }
//
//    override fun getEnteredPin(): String? {
//        return pinText?.getText()?.toString() ?: ""
//    }
//
//    override fun onEnterPinFinish(pinResult: Int) {
//        ApplicationClass.Companion.getApp()?.runOnUiThread {
//            if (mEnterPinPopWindow != null && mEnterPinPopWindow!!.isShowing()) {
//                mEnterPinPopWindow!!.dismiss()
//            }
//            if (pinResult == EnterPinResult.RET_SUCC || pinResult == EnterPinResult.RET_CANCEL || pinResult == EnterPinResult.RET_TIMEOUT || pinResult == EnterPinResult.RET_PIN_BY_PASS || pinResult == EnterPinResult.RET_OFFLINE_PIN_READY || pinResult == EnterPinResult.RET_NO_KEY
//            ) {
//                LogUtils.d(TAG, "to do nothing")
//            } else {
//            }
//        }
//    }
//
//    override fun onStartEnterPin(prompt: String) {
//        LogUtils.w(
//            TAG,
//            "onStartEnterPin, current thread " + Thread.currentThread().name + ", id:" + Thread.currentThread().id
//        )
//    }
//
//    private fun getFirstGACTag() {
//        var byteArray = ByteArray()
//        var ret: Int = EmvProcess.getInstance().getTlv(0x95, byteArray)
//        if (ret == RetCode.EMV_OK) {
//            val dataArr = kotlin.ByteArray(byteArray.length)
//            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
//            firstGacTVR = ConvertHelper.getConvert().bcdToStr(dataArr)
//        }
//        byteArray = ByteArray()
//        ret = EmvProcess.getInstance().getTlv(0x9B, byteArray)
//        if (ret == RetCode.EMV_OK) {
//            val dataArr = kotlin.ByteArray(byteArray.length)
//            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
//            firstGacTSI = ConvertHelper.getConvert().bcdToStr(dataArr)
//        }
//        byteArray = ByteArray()
//        ret = EmvProcess.getInstance().getTlv(0x9F27, byteArray)
//        if (ret == RetCode.EMV_OK) {
//            val dataArr = kotlin.ByteArray(byteArray.length)
//            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length)
//            firstGacCID = ConvertHelper.getConvert().bcdToStr(dataArr)
//        }
//    }
//
//    private fun startClssTransAgain(msg: String) {
//        detectPresenter?.closeReader()
//        transPreProcess(false)
//        isSecondTap = false
//        startDetectCard(EReaderType.PICC)
//    }
//
//    private fun processCvm() {
//        //get TransResult
//        if (currentTxnCVMResult === CvmResultEnum.CVM_NO_CVM) {
//            //1.check trans result
//            checkTransResult()
//        } else if (currentTxnCVMResult === CvmResultEnum.CVM_SIG) {
//            //1.signature process 2.check trans result
//            signatureProcess()
//        } else if (currentTxnCVMResult === CvmResultEnum.CVM_ONLINE_PIN) {
//            if (currentTxnType == TXN_TYPE_PICC) {
//                //1.online pin process 2.check trans result
//                transProcessPresenter?.startOnlinePin()
//            } else if (currentTxnType == TXN_TYPE_ICC) {
//                //check result
//                checkTransResult()
//            }
//        } else if (currentTxnCVMResult === CvmResultEnum.CVM_ONLINE_PIN_SIG) {
//            if (currentTxnType == TXN_TYPE_PICC) {
//                //picc no this cvm
//            } else if (currentTxnType == TXN_TYPE_ICC) {
//                //1.signature process 2.check trans result
//                signatureProcess()
//            }
//        } else if (currentTxnCVMResult === CvmResultEnum.CVM_OFFLINE_PIN) { //contact trans
//            //1.check trans result
//            checkTransResult()
//        } else if (currentTxnCVMResult === CvmResultEnum.CVM_CONSUMER_DEVICE) { //contactless trans
//            //1.restart detect(tap) card and transaction
//            startClssTransAgain("See phone, Please tap phone")
//        }
//    }
//
//    //the Scenes to show red light, refer to Book A
//    private fun showClssErrLight(transResult: TransResult) {
//        //if show clss light first
//        if (currentTxnType == TXN_TYPE_PICC) {
//            if (transResult.getResultCode() !== RetCode.EMV_OK) {
//            }
//        }
//    }
//
//    private fun processTransResult(transResult: TransResult) {
//        //check if need to show rea light first
//        showClssErrLight(transResult)
//        if (currTransResultEnum === TransResultEnum.RESULT_FALLBACK) { //contact
//            startDetectCard(EReaderType.MAG) // onMagDetectOk will callback
//        } else if (currTransResultEnum === TransResultEnum.RESULT_CLSS_SEE_PHONE) { //contactless
//            //PICC return  USE_CONTACT 1.restart detect(insert/swipe) card and transaction
//            startClssTransAgain("See phone, Please tap phone")
//        } else if (currTransResultEnum === TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE
//            || transResult.getResultCode() === RetCode.CLSS_USE_CONTACT
//        ) { //contactless
//            startDetectCard(EReaderType.ICC)
//        } else if (currTransResultEnum === TransResultEnum.RESULT_TRY_AGAIN) { //contactless
//            //PICC return  USE_CONTACT 1.restart detect card and transaction
//            startClssTransAgain("Try again, Please tap card again")
//        } else if (transResult.getResultCode() === RetCode.EMV_DENIAL
//            || transResult.getResultCode() === RetCode.CLSS_DECLINE
//        ) {
//            //to result page to get tag95 and tag 9b to find the reason of deciline
//        } else {
//        }
//    }
//
//    override fun onTransFinish(transResult: TransResult) {
//        currTransResultEnum = transResult.getTransResult()
//        currentTxnCVMResult = transResult.getCvmResult()
//        currTransResultCode = transResult.getResultCode()
//        Toast.makeText(activity, "FINISH " + transResult.getCvmResult().name, Toast.LENGTH_SHORT).show()
//        LogUtils.d(
//            TAG,
//            "onTransFinish,retCode:" + currTransResultCode + ", transResult:" + currTransResultEnum + ", cvm result:" + transResult.getCvmResult()
//        )
//        getFirstGACTag()
//        if (transResult.getResultCode() === RetCode.EMV_OK) {
//            processCvm()
//        } else {
//            processTransResult(transResult)
//        }
//    }
//
//    private fun onlineProcess() {
//        //====online process =====
//        //1.get TAG value with getTlv API
//        //2.pack message, such as ISO8583
//        //3.send message to acquirer host
//        //4.get response of acquirer host
//        //5.set value of acquirer result code and script, such as TAG 71(Issuer Script Data 1),72(Issuer Script Data 2),91(Issuer Authentication Data),8A(Response Code),89(Authorization Code) and so on.
//        //6.call completeTransProcess API
//
//        //There is a time-consuming wait dialog to simulate the online process
//    }
//
//    private fun signatureProcess() {
//        //There is a time-consuming wait dialog to simulate the signature process
//    }
//
//    private fun checkTransResult() {
//        LogUtils.w(
//            TAG,
//            "checkTransResult:$currTransResultEnum"
//        )
//        if (currTransResultEnum === TransResultEnum.RESULT_REQ_ONLINE) {
//            // 1.online process 2.to result page
//            onlineProcess()
//        } else if (currTransResultEnum === TransResultEnum.RESULT_OFFLINE_APPROVED) {
//            //1.to result page
//        } else if (currTransResultEnum === TransResultEnum.RESULT_OFFLINE_DENIED) {
//            // 1.to result page
//        } else {
//            LogUtils.e(
//                TAG,
//                "unexpected result,$currTransResultEnum"
//            )
//        }
//    }
//
//    override fun onCompleteTrans(transResult: TransResult) {
//        currTransResultEnum = transResult.getTransResult()
//        currTransResultCode = transResult.getResultCode()
//        LogUtils.d(
//            TAG,
//            "onCompleteTrans,retCode:" + transResult.getResultCode()
//                .toString() + ", transResult:" + currTransResultEnum
//        )
//        if (transResult.getResultCode() === RetCode.EMV_OK) {
//            //1.to Trans result page
//        }
//    }
//
//    override fun onRemoveCard() {
//
//    }
//
//    override fun onReadCardOK() {
//        TODO("Not yet implemented")
//    }
}