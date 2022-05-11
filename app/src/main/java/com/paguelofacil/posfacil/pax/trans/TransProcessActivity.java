/*
 *  ===========================================================================================
 *  = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *     This software is supplied under the terms of a license agreement or nondisclosure
 *     agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *     disclosed except in accordance with the terms in that agreement.
 *          Copyright (C) 2020 -? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 *  Description: // Detail description about the function of this module,
 *               // interfaces with the other modules, and dependencies.
 *  Revision History:
 *  Date	               Author	                   Action
 *  2020/06/01 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.trans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.paguelofacil.posfacil.ApplicationClass;
import com.paguelofacil.posfacil.R;
import com.paguelofacil.posfacil.pax.entity.DetectCardResult;
import com.paguelofacil.posfacil.pax.entity.EnterPinResult;
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessContract;
import com.paguelofacil.posfacil.pax.trans.mvp.cardprocess.TransProcessPresenter;
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.DetectCardContract;
import com.paguelofacil.posfacil.pax.trans.mvp.detectcard.NeptunePollingPresenter;
import com.paguelofacil.posfacil.pax.util.AppDataUtils;
import com.paguelofacil.posfacil.pax.util.CardInfoUtils;
import com.paguelofacil.posfacil.pax.util.CurrencyConverter;
import com.paguelofacil.posfacil.pax.util.ScreenUtils;
import com.paguelofacil.posfacil.pax.util.TickTimer;
import com.paguelofacil.posfacil.pax.util.TimeRecordUtils;
import com.paguelofacil.posfacil.pax.view.widget.ClssLight;
import com.paguelofacil.posfacil.pax.view.widget.ClssLightsView;
import com.pax.commonlib.utils.LogUtils;
import com.pax.commonlib.utils.ToastUtils;
import com.pax.commonlib.utils.convert.ConvertHelper;
import com.pax.commonlib.utils.convert.IConvert;
import com.pax.dal.entity.EReaderType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.RetCode;
import com.paxsz.module.emv.param.EmvTransParam;
import com.paxsz.module.emv.process.contact.EmvProcess;
import com.paxsz.module.emv.process.contactless.ClssProcess;
import com.paxsz.module.emv.process.entity.EOnlineResult;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;
import com.paxsz.module.emv.process.enums.CvmResultEnum;
import com.paxsz.module.emv.process.enums.TransResultEnum;

import java.util.Arrays;


public class TransProcessActivity extends Activity implements DetectCardContract.View, TransProcessContract.View {
    private static final String TAG = "TransProcessActivity";
    private static final int PROCESSING_TYPE_ONLINE = 1;
    private static final int PROCESSING_TYPE_SIGNATURE = 2;
    private static final int PROMPT_TYPE_FAILED = 1;
    private static final int PROMPT_TYPE_SUCCESS = 2;

    public static final int TXN_TYPE_MAG = 0x100;
    public static final int TXN_TYPE_ICC = 0x101;
    public static final int TXN_TYPE_PICC = 0x102;

    public static final String EXTRA_TRANS_TYPE = "trans_type";
    public static final String EXTRA_TRANS_AMOUNT = "trans_amount";
    public static final String EXTRA_OTHER_AMOUNT = "other_amount";
    public static final String EXTRA_TRANS_RESULT = "trans_result";
    public static final String EXTRA_TRANS_RESULT_CODE = "trans_result_code";
    public static final String EXTRA_CVM_RESULT = "cvm_result";
    public static final String EXTRA_1STGAC_TVR = "first_gac_tvr";
    public static final String EXTRA_1STGAC_TSI = "first_gac_tsi";
    public static final String EXTRA_1STGAC_CID = "first_gac_cid";
    public static final String EXTRA_CURRENT_TXN_TYPE = "current_txn_type";
    public static final String EXTRA_IS_ONLINE_APPROVE_WITHOUT_2GAC = "is_online_approved_without_2GAC";

    private TextView panText;
    private TextView expiryDateText;
    private TextView useCardPromptText;
    private ClssLightsView clssLightView;
    private View bottomView;
    private TextView transAmtText;
    private TextView otherAmtText;
    //    private DetectCardPresenter detectPresenter;
    private NeptunePollingPresenter detectPresenter;
    private TransProcessPresenter transProcessPresenter;
    private PopupWindow mEnterPinPopWindow;
    private TextView pinText;
    private AlertDialog processingDlg;
    private AlertDialog selectOnlineResultDlg;
    private AlertDialog transPromptDlg;
    private int onlineResultCode = EOnlineResult.FAILED.ordinal(); //online approve,online deny,connect host failed
    private byte[] responseCode; //issuer auth response code
    private int currOnlineResultIndex = 0;
    private int currentTxnType = TXN_TYPE_ICC;
    private boolean hasDetectedCard = false;
    private boolean isOnlineApprovedNo2ndGAC = false;
    private TransResultEnum currTransResultEnum;
    private int currTransResultCode = RetCode.EMV_OK;
    private CvmResultEnum currentTxnCVMResult;
    private byte transType;
    private long transAmt;
    private long otherAmt;
    private int pinResult;

    private String firstGacTVR = "";
    private String firstGacTSI = "";
    private String firstGacCID = "";
    private boolean isSecondTap = false;
    private IssuerRspData issuerRspData = new IssuerRspData();

    @Override
    public void onRemoveCard() {
        clssLighteErr();
        useCardPromptText.setText("Please remove card");
        useCardPromptText.setTextColor(Color.RED);
    }

    @Override
    public void onReadCardOK() {
        clssLightReadCardOk();
    }

    /**
     * when clss transaction error occur, need to show red light,
     * the detail please refer to Book_A
     */
    private void clssLighteErr() {
        clssLightView.setLights(0, ClssLight.OFF);
        clssLightView.setLights(1, ClssLight.OFF);
        clssLightView.setLights(2, ClssLight.OFF);
        clssLightView.setLights(3, ClssLight.ON);
    }

    /**
     * when clss transaction Start detect card,the blue light need to blink
     * the detail please refer to Book_A
     */
    private void clssLightDetectCard() {
        clssLightView.setLights(0, ClssLight.BLINK);
        clssLightView.setLights(1, ClssLight.OFF);
        clssLightView.setLights(2, ClssLight.OFF);
        clssLightView.setLights(3, ClssLight.OFF);
    }

    /**
     * when clss transaction detected card,show the blue light and yellow light
     * the detail please refer to Book_A
     */
    private void clssLightProcessing() {
        clssLightView.setLights(0, ClssLight.ON);
        clssLightView.setLights(1, ClssLight.ON);
        clssLightView.setLights(2, ClssLight.OFF);
        clssLightView.setLights(3, ClssLight.OFF);
    }

    private void clssLightCloseAll() {
        clssLightView.setLights(0, ClssLight.OFF);
        clssLightView.setLights(1, ClssLight.OFF);
        clssLightView.setLights(2, ClssLight.OFF);
        clssLightView.setLights(3, ClssLight.OFF);
    }

    /**
     * when clss transaction read card ok,show the blue , yellow and green light at least for 750ms
     * the detail please refer to Book_A
     */
    private void clssLightReadCardOk() {
        clssLightView.setLights(0, ClssLight.ON);
        clssLightView.setLights(1, ClssLight.ON);
        clssLightView.setLights(2, ClssLight.ON);
        clssLightView.setLights(3, ClssLight.OFF);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans_process);
        initView();
        initTransProcessPresenter();
        transPreProcess(true);
        startDetectCard(EReaderType.ICC_PICC);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void initView() {
        panText = findViewById(R.id.tv_pan);
        expiryDateText = findViewById(R.id.tv_expiry_date);
        clssLightView = findViewById(R.id.layout_clssLight);
        useCardPromptText = findViewById(R.id.tv_use_card_prompt);
        clssLightView.setClssLigthStd(0);//0:standard 1:europe
        clssLightView.setVisibility(View.VISIBLE);
        clssLightView.setLights(0, ClssLight.BLINK);
        bottomView = findViewById(R.id.view_bottom);
        transType = getIntent().getByteExtra(EXTRA_TRANS_TYPE, (byte) 0x00);
        transAmt = getIntent().getLongExtra(EXTRA_TRANS_AMOUNT, 0);
        otherAmt = getIntent().getLongExtra(EXTRA_OTHER_AMOUNT, 0);
        transAmtText = ((TextView) findViewById(R.id.tv_trans_amt));
        if (transType != 0x30) {
            transAmtText.setText(CurrencyConverter.convert(transAmt));
        }
        otherAmtText = ((TextView) findViewById(R.id.tv_other_amt));
        otherAmtText.setText(CurrencyConverter.convert(otherAmt));
    }

    private void startDetectCard(EReaderType readType) {
        hasDetectedCard = false;
        TimeRecordUtils.clearTimeRecordList();
        if (detectPresenter != null) {
            detectPresenter.stopDetectCard();
            detectPresenter.detachView();
            detectPresenter.closeReader();
            detectPresenter = null;
        }


          /* ============NOTE==============
             Detect card with API getPicc()/getIcc/getMag ==> DetectCardPresenter(PiccDetectModel,IccDetectModel,MagDetectModel),
             DetectCardPresenter has resolve the detect card conflict problem("when swipe card, some terminals may detect picc ,as a result of terminal's mag reader and picc reader are very close"),
             but it may increase the time of detecting card process
           */
//        detectPresenter = new DetectCardPresenter();

        // detect card with polling() ==> NeptunePollingPresenter
        detectPresenter = new NeptunePollingPresenter();
        detectPresenter.attachView(this);
        detectPresenter.startDetectCard(readType);
    }

    private void initTransProcessPresenter() {
        if (transProcessPresenter == null) {
            transProcessPresenter = new TransProcessPresenter();
            transProcessPresenter.attachView(this);
        }
    }

    //before detected card.
    private void transPreProcess(boolean isNeedContact) {
        try {

            EmvTransParam transParam = new EmvTransParam();
            LogUtils.i(TAG, "transType:" + ConvertHelper.getConvert().bcdToStr(new byte[]{transType}) + ",int val:" + transType);
            transParam.setTransType(transType);
            transParam.setAmount(Long.toString(transAmt));
            transParam.setAmountOther(Long.toString(otherAmt));
            transParam.setTerminalID(AppDataUtils.getSN());
            transParam.setTransCurrencyCode(CurrencyConverter.getCurrencyCode());
            transParam.setTransCurrencyExponent((byte) CurrencyConverter.getCurrencyFraction());
            transParam.setTransDate(AppDataUtils.getCurrDate());
            transParam.setTransTime(AppDataUtils.getCurrTime());
            transParam.setTransTraceNo("0001");

            transProcessPresenter.preTrans(transParam, isNeedContact);

        } catch (IllegalArgumentException e) {
            LogUtils.e(TAG, e);
        }
    }

    @Override
    public void onMagDetectOK(String pan, String expiryDate) {
        // magstripe Fallback(terminal fallback to a magstripe transaction when chip cannot be read)
        currentTxnType = TXN_TYPE_MAG;
        hasDetectedCard = true;
        panText.setVisibility(View.VISIBLE);
        expiryDateText.setVisibility(View.VISIBLE);

        panText.setText(pan);
        expiryDateText.setText(expiryDate);
        //add CVM process, such as enter pin or signature and so on.
        displayTransPromptDlg(PROMPT_TYPE_SUCCESS, "MSR");
    }

    @Override
    public void onIccDetectOK() {
        currentTxnType = TXN_TYPE_ICC;
        hasDetectedCard = true;
        ToastUtils.showToast(TransProcessActivity.this, "ICC detect succ");
        if (transProcessPresenter != null) {
            transProcessPresenter.startEmvTrans();
        }
    }

    @Override
    public void onPiccDetectOK() {
        currentTxnType = TXN_TYPE_PICC;
        hasDetectedCard = true;
        clssLightProcessing();
        ToastUtils.showToast(TransProcessActivity.this, "PICC detect succ");
        if (transProcessPresenter != null) {
            if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE) {

            } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) {

            } else if (isSecondTap) {//visa card and other card(not contain master card) 2nd detect card
                isSecondTap = false;
                transProcessPresenter.completeClssTrans(issuerRspData);
            } else {
                transProcessPresenter.startClssTrans(); // first time detect card finish
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d(TAG, "onKeyDown:" + event.getKeyCode());
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_APP_SWITCH:
                if (hasDetectedCard) {
                    return true;
                }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDetectError(DetectCardResult.ERetCode errorCode) {
        if (errorCode == DetectCardResult.ERetCode.FALLBACK) {
            ToastUtils.showToast(TransProcessActivity.this, "Fallback,Please insert card");
        } else {
            displayTransPromptDlg(PROMPT_TYPE_FAILED, errorCode.name());
        }
    }

    @Override
    public void onBackPressed() {
        stopDetectCard();
        displayTransPromptDlg(PROMPT_TYPE_FAILED, DetectCardResult.ERetCode.CANCEL.name());
    }

    private void stopDetectCard() {
        if (detectPresenter != null) {
            detectPresenter.stopDetectCard();
            detectPresenter.detachView();
            detectPresenter.closeReader();
            detectPresenter = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDetectCard();
        if (transProcessPresenter != null) {
            transProcessPresenter.detachView();
            transProcessPresenter = null;
        }
    }

    @Override
    public void onUpdatePinLen(String pin) {
        ApplicationClass.getApp().runOnUiThread(() -> {
            if (pinText != null) {
                pinText.setText(pin);
            }
        });
    }

    @Override
    public String getEnteredPin() {
        return pinText == null ? "" : pinText.getText().toString();
    }

    @Override
    public void onEnterPinFinish(int pinResult) {
        this.pinResult = pinResult;
        ApplicationClass.getApp().runOnUiThread(() -> {
            if (mEnterPinPopWindow != null && mEnterPinPopWindow.isShowing()) {
                mEnterPinPopWindow.dismiss();
            }
            if (pinResult == EnterPinResult.RET_SUCC
                    || pinResult == EnterPinResult.RET_CANCEL
                    || pinResult == EnterPinResult.RET_TIMEOUT
                    || pinResult == EnterPinResult.RET_PIN_BY_PASS
                    || pinResult == EnterPinResult.RET_OFFLINE_PIN_READY
                    || pinResult == EnterPinResult.RET_NO_KEY) {
                LogUtils.d(TAG, "to do nothing");
            } else {
                displayTransPromptDlg(PROMPT_TYPE_FAILED, pinResult + "");
            }
        });
    }

    @Override
    public void onStartEnterPin(String prompt) {
        LogUtils.w(TAG, "onStartEnterPin, current thread " + Thread.currentThread().getName() + ", id:" + Thread.currentThread().getId());
        ApplicationClass.getApp().runOnUiThread(() -> displayEnterPinDlg(prompt));
    }

    private void getFirstGACTag() {
        ByteArray byteArray = new ByteArray();

        int ret = EmvProcess.getInstance().getTlv(0x95, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacTVR = ConvertHelper.getConvert().bcdToStr(dataArr);
        }

        byteArray = new ByteArray();
        ret = EmvProcess.getInstance().getTlv(0x9B, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacTSI = ConvertHelper.getConvert().bcdToStr(dataArr);
        }

        byteArray = new ByteArray();
        ret = EmvProcess.getInstance().getTlv(0x9F27, byteArray);
        if (ret == RetCode.EMV_OK) {
            byte[] dataArr = new byte[byteArray.length];
            System.arraycopy(byteArray.data, 0, dataArr, 0, byteArray.length);
            firstGacCID = ConvertHelper.getConvert().bcdToStr(dataArr);
        }
    }

    private void startClssTransAgain(String msg) {
        detectPresenter.closeReader();
        transPreProcess(false);
        isSecondTap = false;
        useCardPromptText.setTextColor(Color.RED);
        useCardPromptText.setText(msg);
        clssLightDetectCard();
        startDetectCard(EReaderType.PICC);
    }

    private void processCvm() {
        //get TransResult
        if (currentTxnCVMResult == CvmResultEnum.CVM_NO_CVM) {
            //1.check trans result
            checkTransResult();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_SIG) {
            //1.signature process 2.check trans result
            signatureProcess();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN) {
            if (currentTxnType == TXN_TYPE_PICC) {
                //1.online pin process 2.check trans result
                transProcessPresenter.startOnlinePin();
            } else if (currentTxnType == TXN_TYPE_ICC) {
                //check result
                checkTransResult();
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_ONLINE_PIN_SIG) {
            if (currentTxnType == TXN_TYPE_PICC) {
                //picc no this cvm
            } else if (currentTxnType == TXN_TYPE_ICC) {
                //1.signature process 2.check trans result
                signatureProcess();
            }
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_OFFLINE_PIN) {//contact trans
            //1.check trans result
            checkTransResult();
        } else if (currentTxnCVMResult == CvmResultEnum.CVM_CONSUMER_DEVICE) {//contactless trans
            //1.restart detect(tap) card and transaction
            startClssTransAgain("See phone, Please tap phone");
        }
    }

    //the Scenes to show red light, refer to Book A
    private void showClssErrLight(TransResult transResult) {
        //if show clss light first
        if (currentTxnType == TXN_TYPE_PICC) {
            if (transResult.getResultCode() != RetCode.EMV_OK) {
                clssLighteErr();
            }
        }
    }

    private void processTransResult(TransResult transResult) {
        //check if need to show rea light first
        showClssErrLight(transResult);

        if (currTransResultEnum == TransResultEnum.RESULT_FALLBACK) { //contact
            useCardPromptText.setTextColor(Color.RED);
            useCardPromptText.setText(" Fallback, Please swipe card");
            ToastUtils.showToast(TransProcessActivity.this, "Fallback, Please swipe card");
            startDetectCard(EReaderType.MAG); // onMagDetectOk will callback
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_SEE_PHONE) { //contactless
            //PICC return  USE_CONTACT 1.restart detect(insert/swipe) card and transaction
            startClssTransAgain("See phone, Please tap phone");
        } else if (currTransResultEnum == TransResultEnum.RESULT_CLSS_TRY_ANOTHER_INTERFACE
                || transResult.getResultCode() == RetCode.CLSS_USE_CONTACT) {//contactless
            useCardPromptText.setTextColor(Color.RED);
            useCardPromptText.setText("Try other interface, Please Insert card");
            clssLightCloseAll();
            startDetectCard(EReaderType.ICC);
        } else if (currTransResultEnum == TransResultEnum.RESULT_TRY_AGAIN) {//contactless
            //PICC return  USE_CONTACT 1.restart detect card and transaction
            startClssTransAgain("Try again, Please tap card again");
        } else if (transResult.getResultCode() == RetCode.EMV_DENIAL
                || transResult.getResultCode() == RetCode.CLSS_DECLINE) {
            //to result page to get tag95 and tag 9b to find the reason of deciline
            toTransResultPage();
        } else {
            displayTransPromptDlg(PROMPT_TYPE_FAILED, transResult.getResultCode() + "");
        }
    }

    @Override
    public void onTransFinish(TransResult transResult) {
        currTransResultEnum = transResult.getTransResult();
        currentTxnCVMResult = transResult.getCvmResult();
        currTransResultCode = transResult.getResultCode();
        LogUtils.d(TAG, "onTransFinish,retCode:" + currTransResultCode + ", transResult:" + currTransResultEnum + ", cvm result:" + transResult.getCvmResult());
        getFirstGACTag();
        if (transResult.getResultCode() == RetCode.EMV_OK) {
            processCvm();
        } else {
            processTransResult(transResult);
        }
    }

    private void onlineProcess() {
        //====online process =====
        //1.get TAG value with getTlv API
        //2.pack message, such as ISO8583
        //3.send message to acquirer host
        //4.get response of acquirer host
        //5.set value of acquirer result code and script, such as TAG 71(Issuer Script Data 1),72(Issuer Script Data 2),91(Issuer Authentication Data),8A(Response Code),89(Authorization Code) and so on.
        //6.call completeTransProcess API

        //There is a time-consuming wait dialog to simulate the online process
        displayProcessDlg(PROCESSING_TYPE_ONLINE, "Online Processing...");
    }

    private void signatureProcess() {
        //There is a time-consuming wait dialog to simulate the signature process
        displayProcessDlg(PROCESSING_TYPE_SIGNATURE, "Signature Processing...");
    }

    private void checkTransResult() {
        LogUtils.w(TAG, "checkTransResult:" + currTransResultEnum);
        if (currTransResultEnum == TransResultEnum.RESULT_REQ_ONLINE) {
            // 1.online process 2.to result page
            onlineProcess();
        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_APPROVED) {
            //1.to result page
            toTransResultPage();
        } else if (currTransResultEnum == TransResultEnum.RESULT_OFFLINE_DENIED) {
            // 1.to result page
            toTransResultPage();
        } else {
            LogUtils.e(TAG, "unexpected result," + currTransResultEnum);
        }
    }

    @Override
    public void onCompleteTrans(TransResult transResult) {
        currTransResultEnum = transResult.getTransResult();
        currTransResultCode = transResult.getResultCode();
        LogUtils.d(TAG, "onCompleteTrans,retCode:" + transResult.getResultCode() + ", transResult:" + currTransResultEnum);
        if (transResult.getResultCode() == RetCode.EMV_OK) {
            //1.to Trans result page
        }
        toTransResultPage();
    }

    private void toTransResultPage() {
        Intent toResultPage = new Intent(TransProcessActivity.this, TransResultActivity.class);
        toResultPage.putExtra(EXTRA_TRANS_AMOUNT, transAmtText.getText().toString());
        toResultPage.putExtra(EXTRA_OTHER_AMOUNT, otherAmtText.getText().toString());
        toResultPage.putExtra(EXTRA_TRANS_RESULT, currTransResultEnum.name());
        toResultPage.putExtra(EXTRA_TRANS_RESULT_CODE, currTransResultCode);
        toResultPage.putExtra(EXTRA_CVM_RESULT, currentTxnCVMResult.name());
        toResultPage.putExtra(EXTRA_1STGAC_TVR, firstGacTVR);
        toResultPage.putExtra(EXTRA_1STGAC_TSI, firstGacTSI);
        toResultPage.putExtra(EXTRA_1STGAC_CID, firstGacCID);
        toResultPage.putExtra(EXTRA_CURRENT_TXN_TYPE, currentTxnType);
        toResultPage.putExtra(EXTRA_IS_ONLINE_APPROVE_WITHOUT_2GAC, isOnlineApprovedNo2ndGAC);
        startActivity(toResultPage);
        TransProcessActivity.this.finish();
    }

    private void displayEnterPinDlg(String title) {
        if (isFinishing()) {
            return;
        }
        if (mEnterPinPopWindow != null) {
            if (mEnterPinPopWindow.isShowing()) {
                mEnterPinPopWindow.dismiss();
            }
            mEnterPinPopWindow = null;
        }

        View popView = getLayoutInflater().inflate(R.layout.dlg_enter_pin, null);
        mEnterPinPopWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pinText = popView.findViewById(R.id.tv_pin);
        TextView titleTxt = popView.findViewById(R.id.tv_title);
        titleTxt.setText(title);
        mEnterPinPopWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        mEnterPinPopWindow.setFocusable(true);
        mEnterPinPopWindow.setOutsideTouchable(false);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(200);
        mEnterPinPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenUtils.lightOn(TransProcessActivity.this);
                if (currentTxnType == TXN_TYPE_PICC) {
                    if (pinResult != 0) {
                        displayTransPromptDlg(PROMPT_TYPE_FAILED, "getString pinblock err: " + pinResult);
                    } else {
                        checkTransResult();
                    }
                }
            }
        });

        mEnterPinPopWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEnterPinPopWindow.showAtLocation(bottomView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        popView.startAnimation(animation);
        ScreenUtils.lightOff(TransProcessActivity.this);
    }

    private void displayProcessDlg(int type, String msg) {
        if (isFinishing()) {
            return;
        }
        if (processingDlg != null) {
            if (processingDlg.isShowing()) {
                processingDlg.dismiss();
            }

            processingDlg = null;
        }


        AlertDialog.Builder mProgressDlgBuilder = new AlertDialog.Builder(TransProcessActivity.this, R.style.AlertDialog);
        View view = LayoutInflater.from(TransProcessActivity.this).inflate(R.layout.dlg_processing, null);
        ((TextView) view.findViewById(R.id.tv_msg)).setText(msg);
        mProgressDlgBuilder.setCancelable(false);
        mProgressDlgBuilder.setView(view);
        processingDlg = mProgressDlgBuilder.create();
        processingDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (type == PROCESSING_TYPE_ONLINE) {
                    //show dialog to select online approve or online decline simulate online result
                    displaySelectOnlineResultDlg();
                } else if (type == PROCESSING_TYPE_SIGNATURE) {
                    checkTransResult();
                }
            }
        });

        processingDlg.show();
        new TickTimer().start(3, () -> {
            if (processingDlg != null && processingDlg.isShowing()) {
                processingDlg.dismiss();
            }
        });

        final WindowManager.LayoutParams params = processingDlg.getWindow().getAttributes();
        params.width = 600;
        params.height = 400;
        processingDlg.getWindow().setAttributes(params);
        processingDlg.getWindow().setBackgroundDrawableResource(android.R.color.background_light);
    }

    private void displaySelectOnlineResultDlg() {
        if (selectOnlineResultDlg != null && selectOnlineResultDlg.isShowing()) {
            selectOnlineResultDlg.dismiss();
            selectOnlineResultDlg = null;
        }

        byte[] script1 = {0x71, 0x0F, (byte) 0x9F, 0x18, 0x04, 0x11, 0x22, 0x33, 0x44, (byte) 0x86, 0x06, (byte) 0x84, 0x24, 0x00, 0x00, 0x00, 0X00};
        byte[] script2 = ConvertHelper.getConvert().strToBcd("72289F1804AABBCCDD86098424000004ABBBCCDD86098418000004BBBBCCDD86098416000004CCBBCCDD", IConvert.EPaddingPosition.PADDING_RIGHT);
        byte[] authCode = ConvertHelper.getConvert().strToBcd("313233343536", IConvert.EPaddingPosition.PADDING_RIGHT);
        byte[] authData = ConvertHelper.getConvert().strToBcd("e344ee82e00ca8763030", IConvert.EPaddingPosition.PADDING_RIGHT);
        String[] onlineResultArr = new String[]{"Online Approved\n(With Scripts)", "Online Approved\n(No Scripts)", "Online Approved\n(No 2nd GAC)", "Online Decline", "Online Failed"};

        onlineResultCode = EOnlineResult.APPROVE.ordinal();
        responseCode = ConvertHelper.getConvert().strToBcd("3030", IConvert.EPaddingPosition.PADDING_RIGHT);
        currOnlineResultIndex = 0;
        currTransResultEnum = TransResultEnum.RESULT_ONLINE_APPROVED;
        selectOnlineResultDlg = new AlertDialog.Builder(TransProcessActivity.this).setSingleChoiceItems(onlineResultArr, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                currOnlineResultIndex = i;
                if (i == 0 || i == 1) {//online approve
                    onlineResultCode = EOnlineResult.APPROVE.ordinal();
                    responseCode = ConvertHelper.getConvert().strToBcd("3030", IConvert.EPaddingPosition.PADDING_RIGHT);
                } else if (i == 2) {// online approve but no 2nd GAC
                    isOnlineApprovedNo2ndGAC = true;
                    currTransResultEnum = TransResultEnum.RESULT_ONLINE_APPROVED;
                } else if (i == 3) {//online decline
                    currTransResultEnum = TransResultEnum.RESULT_ONLINE_DENIED;
                    onlineResultCode = EOnlineResult.DENIAL.ordinal();
                    responseCode = ConvertHelper.getConvert().strToBcd("3035", IConvert.EPaddingPosition.PADDING_RIGHT);
                } else if (i == 4) {//online failed
                    onlineResultCode = EOnlineResult.FAILED.ordinal();
                    currTransResultEnum = TransResultEnum.RESULT_ONLINE_CARD_DENIED;
                }
            }
        }).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (currOnlineResultIndex == 2) {//no 2nd GAC process
                    toTransResultPage();
                } else {
                    if (onlineResultCode != EOnlineResult.FAILED.ordinal()) {
                        issuerRspData.setRespCode(responseCode);//TAG:8A
                        issuerRspData.setAuthCode(authCode);//TAG:89
                        issuerRspData.setAuthData(authData);//TAG:91
                        if (currOnlineResultIndex == 0) {//online approve with scripts
//                            issuerRspData.setScript(script2);
                            issuerRspData.setScript(CardInfoUtils.combine7172(Arrays.copyOfRange(script1, 2, script1.length), Arrays.copyOfRange(script2, 2, script2.length)));
                        } else if (currOnlineResultIndex == 3) {//online decline
//                            issuerRspData.setScript(script1); with script 71
                        }
                    }

                    issuerRspData.setOnlineResult((byte) onlineResultCode);
                    if (transProcessPresenter != null) {
                        if (currentTxnType == TXN_TYPE_ICC) {
                            transProcessPresenter.completeEmvTrans(issuerRspData);
                        } else if (currentTxnType == TXN_TYPE_PICC) {
                            //check if need second tap or not.
                            if (ClssProcess.getInstance().isNeedSecondTap(issuerRspData)) {
                                isSecondTap = true;
                                useCardPromptText.setText("Second tap, Pls tap card to execute script or issuer auth");
                                startDetectCard(EReaderType.PICC);
                            } else {
                                isOnlineApprovedNo2ndGAC = true;
                                toTransResultPage();
                            }
                        }
                    }
                }
            }
        }).setCancelable(false).setTitle("Please Select Online Result").create();
        if (!isFinishing()) {
            selectOnlineResultDlg.show();
        }
    }

    private void displayTransPromptDlg(int type, String msg) {

        if (transPromptDlg != null) {
            if (transPromptDlg.isShowing()) {
                transPromptDlg.dismiss();
            }

            transPromptDlg = null;
        }
        if (type == PROMPT_TYPE_SUCCESS) {
            transPromptDlg = new AlertDialog.Builder(TransProcessActivity.this).setCancelable(false).setIcon(R.mipmap.ic_dialog_alert_holo_light).setTitle("Transaction Prompt").setMessage(msg).create();
        } else {
            transPromptDlg = new AlertDialog.Builder(TransProcessActivity.this).setCancelable(false).setIcon(R.mipmap.indicator_input_error).setTitle("Transaction Failed").setMessage("errCode:" + msg).create();
        }

        transPromptDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                TransProcessActivity.this.finish();
            }
        });
        LogUtils.d(TAG, "is Act Finish?" + isFinishing());

        if (!isFinishing()) {
            transPromptDlg.show();
            new TickTimer().start(3, () -> {
                if (transPromptDlg != null && transPromptDlg.isShowing()) {
                    transPromptDlg.dismiss();
                }
            });
        }

    }

}
