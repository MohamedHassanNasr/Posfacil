/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2017-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                           Author	                Action
 * 10:53:38 2017-3-8  	           HuangJs           	    Create
 * ============================================================================		
 */	
package com.paguelofacil.posfacil.pax.jemv.clsspaypass.trans;

import android.util.Log;

import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.CLSS_TORN_LOG_RECORD;
import com.pax.jemv.clcommon.Clss_MCAidParam;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.paypass.api.ClssPassApi;
import com.pax.jemv.paypass.listener.ClssPassCBFunApi;

import java.util.Arrays;


public class ClssPayPass {
	
	private static final String TAG = "ClssPayPass";

	private static final int OC_APPROVED                  = 0x10;
	private static final int OC_DECLINED                  = 0x20;
	private static final int OC_ONLINE_REQUEST            = 0x30;
	private static final int OC_END_APPLICATION           = 0x40;
	private static final int OC_SELECT_NEXT               = 0x50;
	private static final int OC_TRY_ANOTHER_INTERFACE     = 0x60;
	private static final int OC_TRY_AGAIN                 = 0x70;
	private static final int OC_NA                        = 0xF0;
	private static final int OC_A                         = 0x00;
	private static final int OC_B                         = 0x10;
	private static final int OC_C                         = 0x20;
	private static final int OC_D                         = 0x30;
	private static final int OC_NO_CVM                    = 0x00;
	private static final int OC_OBTAIN_SIGNATURE          = 0x10;
	private static final int OC_ONLINE_PIN                = 0x20;
	private static final int OC_CONFIRM_CODE_VER          = 0x30;

	private static final int MI_CARD_READ_OK              = 0x17;
	private static final int MI_TRY_AGAIN                 = 0x21;
	private static final int MI_APPROVED                  = 0x03;
	private static final int MI_APPROVED_SIGN             = 0x1A;
	private static final int MI_DECLINED                  = 0x07;
	private static final int MI_ERROR_OTHER_CARD          = 0x1C;
	private static final int MI_INSERT_CARD               = 0x1D;
	private static final int MI_SEE_PHONE                 = 0x20;
	private static final int MI_AUTHORISING_PLS_WAIT      = 0x1B;
	private static final int MI_CLEAR_DISPLAY             = 0x1E;
	private static final int MI_NA                        = 0xFF;
	private static final int MI_NOT_READY                 = 0x00;
	private static final int MI_IDLE                      = 0x01;
	private static final int MI_READY_TO_READ             = 0x02;
	private static final int MI_PROCESSING                = 0x03;
	private static final int MI_CARD_READ_SUCC            = 0x04;
	private static final int MI_PROC_ERROR                = 0x05;




	private int appTornLogNum = 0;//number of Tornlog
	private  CLSS_TORN_LOG_RECORD[] tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
	private CvmType cvmType = new CvmType();
	private TransactionPath transPath = new TransactionPath();
//	private static KernType kernType = new KernType();
//	private static DDAFlag ddaFlag = new DDAFlag();
	private Clss_TransParam transParam;
//	public static Clss_ReaderParam szReaderParam;
	private Clss_MCAidParam aidParam;
	private Clss_PreProcInfo procInfo;
	
	private static ClssPayPass instance;
	private Test_ClssPassCBFunApi clssPassCBFun = new Test_ClssPassCBFunApi();
	//private TransCallback callback;
	private static ClssEntryPoint entryPoint ;
	
	/*public void setCallback(TransCallback callback) {
		this.callback = callback;
	}*/


	
	public static ClssPayPass getInstance() {
		if (instance == null) {
			instance = new ClssPayPass();
		}

		entryPoint = ClssEntryPoint.getInstance();
		return instance;
	}
	
	private ClssPayPass() {
		tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
	}
	
//	public static int getACType() {
//		return acType.type;
//	}

	public  int getCVMType() {
		return cvmType.type;
	}
	
	public  int getTransPath() {
		return transPath.path;
	}
	
//	public static int getKernType() {
//		return kernType.kernType;
//	}
	
//	public static int getDDAFlag() {
//		return ddaFlag.flag;
//	}
	
	public int coreInit(byte deSupportFlag){
		int ret = ClssPassApi.Clss_CoreInit_MC(deSupportFlag);
		ClssPassCBFunApi.getInstance().setICBFun(clssPassCBFun);
		ret = ClssPassApi.Clss_SetCBFun_SendTransDataOutput_MC();
		Log.i(TAG, "Clss_SetCBFun_SendTransDataOutput_MC = " + ret);
		return ret;
	}
	
	public String readVersion() {
		ByteArray version = new ByteArray();
		ClssPassApi.Clss_ReadVerInfo_MC(version);
		String entryVer = Arrays.toString(version.data);
		return entryVer.substring(0, version.length);
	}
	
	public int setConfigParam(Clss_MCAidParam aidParam, Clss_PreProcInfo procInfo) {
		
		int ret = RetCode.EMV_OK;

		transParam = entryPoint.getTransParam();

		this.aidParam = aidParam;
		this.procInfo = procInfo;



	/*	if (callback != null) {
			appTornLogNum = callback.appLoadTornLog(tornLogRecords);
			if (appTornLogNum > 5) {
				appTornLogNum = 5;
			}
		}*/

//		SetTagPresent();
		
		return ret;
	}


byte valorRRP;

	public int passProcessStep1()
	{



			this.valorRRP = 0x30;

		int ret;

		EntryOutParam outParam = entryPoint.getOutParam();
		appCleanTornLog();
		ret = passFlowBegin(outParam);
		return ret;
	}


	private int passFlowBegin(EntryOutParam outParam) {

		Clss_ReaderParam szReaderParam = new Clss_ReaderParam();
		//Gillian 20170511
		szReaderParam.acquierId 	= new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
		szReaderParam.ucTmType 		=  0x22;
		szReaderParam.aucTmCap 		= new byte[]  {(byte)0xE0,(byte)0xB0,(byte)0xC8};
		szReaderParam.aucTmCntrCode = new byte[]  {(byte)0x05,(byte)0x91};
        szReaderParam.aucTmTransCur	=  new byte[]  {(byte)0x08,(byte)0x40};
		szReaderParam.ucTmTransCurExp = 2;
		szReaderParam.aucTmCapAd = new byte[] {(byte) 0xE0, (byte) 0x00, (byte) 0xF0, (byte) 0xA0, (byte) 0x01};
        szReaderParam.aucTmRefCurCode =  new byte[]  {(byte)0x08,(byte)0x40};
		szReaderParam.ucTmRefCurExp = 2;

		//Gillian end
		int ret;
		ByteArray aucOutcomeParamSet_MC = new ByteArray();
		byte[] szBuff = new byte[]{(byte) 0xDF, (byte) 0x81, 0x29};	//Outcome Parameter
		
		ret = ClssPassApi.Clss_SetFinalSelectData_MC(outParam.sDataOut, outParam.iDataLen);

		if (ret != RetCode.EMV_OK) {
			ret = ClssPassApi.Clss_GetTLVDataList_MC(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
			if((aucOutcomeParamSet_MC.data[1] & 0xF0) == 0x20) {//Start : C
				return RetCode.CLSS_RESELECT_APP;
			}
			return ret;
		}

		setTransParamPass(transParam);
		setReaderParam(szReaderParam);
		setParamByAidPass(aidParam, procInfo);


		setDETData(new byte[]{(byte) 0x9F,0x39}, 2, new byte[]{(byte) 0x71}, 1);

		ret = ClssPassApi.Clss_InitiateApp_MC();
        if(ret != RetCode.EMV_OK) {
	    	ret = ClssPassApi.Clss_GetTLVDataList_MC(szBuff, (byte) 3, 24, aucOutcomeParamSet_MC);
			if((aucOutcomeParamSet_MC.data[1] & 0xF0) == 0x20)  {//Start : C
				return RetCode.CLSS_RESELECT_APP;
			}
			return ret;
	    }

	    ret = ClssPassApi.Clss_ReadData_MC(transPath);
	    if (ret != RetCode.EMV_OK) {
	        return ret;
	    }



		//leerTrack2chip(cardData);

	    return ret;
		
	}


	

	
	private int appCleanTornLog() {
		byte[] time = new byte[8];
		int ret=0;
		DeviceManager.getInstance().getTime(time);
		if (appTornLogNum == 0)
		{
			return 0;
		}
		ClssPassApi.Clss_SetTornLog_MC_MChip(tornLogRecords, appTornLogNum);
		ret = ClssPassApi.Clss_CleanTornLog_MC_MChip(time, 6, (byte) 0);
		if (ret != 0)
		{
			return 0;
		}
		tornLogRecords = new CLSS_TORN_LOG_RECORD[5];
		int dataOut[] = new int[2];
		ret = ClssPassApi.Clss_GetTornLog_MC_MChip(tornLogRecords, dataOut);
		if (ret == 0) {
			appTornLogNum = dataOut[0];
		}
		return 0;
	}
	
	private void setTransParamPass(Clss_TransParam transParam) {
		
		byte[] amtAuth = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01,(byte) 0x00} ;
		byte[] amtOther = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00} ;
        byte[] transNo = new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x01} ;

        TransParamTable table[] =
		{
			new TransParamTable(new byte[]{(byte) 0x9A}, 	 1, transParam.aucTransDate, 3),
			new TransParamTable(new byte[]{(byte)0x9F, 0x21}, 2, transParam.aucTransTime, 3),
			new TransParamTable(new byte[]{(byte) 0x9C},     1, new byte[]{transParam.ucTransType}, 1),
			new TransParamTable(new byte[]{(byte)0x9F, 0x02}, 2, amtAuth,   						 6),
			new TransParamTable(new byte[]{(byte)0x9F, 0x03}, 2, amtOther,  						 6),
			new TransParamTable(new byte[]{(byte)0x9F, 0x41}, 2, transNo,    					 4),
		};
		
		for (TransParamTable transParamTable : table) {
			setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
		}
	}
	
	private int setReaderParam(Clss_ReaderParam readerParam) {
		
		int ret = RetCode.EMV_OK;

		TransParamTable table[] = {
				new TransParamTable(new byte[]{(byte) 0x9F,0x4E}, 2, readerParam.aucMchNameLoc, readerParam.usMchLocLen),//00
				new TransParamTable(new byte[]{(byte) 0x9F,0x15}, 2, readerParam.aucMerchCatCode, 2),//00
				new TransParamTable(new byte[]{(byte) 0x9F,0x16}, 2, readerParam.aucMerchantID, 15),//00
				new TransParamTable(new byte[]{(byte) 0x9F,0x01}, 2, readerParam.acquierId, 6),//"\x00\x00\x00\x12\x34\x56"
				new TransParamTable(new byte[]{(byte) 0x9F,0x1C}, 2, readerParam.aucTmID, 8),//0
				new TransParamTable(new byte[]{(byte) 0x9F,0x35}, 2, new byte[]{readerParam.ucTmType}, 1),
				new TransParamTable(new byte[]{(byte) 0x9F,0x33}, 2, readerParam.aucTmCap, 3),//E0F0C8
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x17}, 3, new byte[]{readerParam.aucTmCap[0]}, 1),//\xE0
				//new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x18}, 3, new byte[]{readerParam.aucTmCap[1]}, 1),//60
				// ARTE new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x18}, 3, new byte[]{AID.getTRM()[0]}, 1),//60
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x18}, 3, new byte[]{(byte) 0x20}, 1),//60
				//new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x19}, 3, new byte[] {(byte) 0x08}, 1),//NO CVM Gillian 20170606
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x19}, 3, new byte[] {(byte)0xB0}, 1),//NO CVM Gillian 20170606
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1F}, 3, new byte[]{readerParam.aucTmCap[2]}, 1),//c8
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1A}, 3, new byte[] {(byte) 0x9F, (byte) 0x6A, (byte) 0x04}, 3),//\x9F\x6A\x04 //Default UDOL Gillian 20170606
				new TransParamTable(new byte[]{(byte) 0x9F,0x6D}, 2,  new byte[] {(byte) 0x00, (byte) 0x01}, 2),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1E}, 3, new byte[] {(byte) 0x10}, 1),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x2C}, 3, new byte[] {(byte) 0x00}, 1),

				new TransParamTable(new byte[]{(byte) 0x9F,0x40}, 2, readerParam.aucTmCapAd, 5),//00
				new TransParamTable(new byte[]{(byte) 0x9F,0x1A}, 2, readerParam.aucTmCntrCode, 2),//0344
				new TransParamTable(new byte[]{0x5F,0x2A}, 2, readerParam.aucTmTransCur, 2),//0344
				new TransParamTable(new byte[]{0x5F,0x36}, 2, new byte[]{readerParam.ucTmTransCurExp}, 1),//02
				new TransParamTable(new byte[]{(byte) 0x9F,0x3C}, 2, readerParam.aucTmRefCurCode, 2),
				new TransParamTable(new byte[]{(byte) 0x9F,0x3D}, 2, new byte[]{readerParam.ucTmRefCurExp}, 1),

				//new TransParamTable(new byte[]{(byte) 0x9F,0x5C}, 2,  new byte[] {(byte) 0x7A, (byte) 0x45, (byte) 0x12, (byte) 0x3E, (byte) 0xE5, (byte) 0x9C, (byte) 0x7E, (byte) 0x40}, 8),
				//new TransParamTable(new byte[]{(byte) 0x9F,0x5C}, 2,  new byte[] {(byte) 0}, 1), //don't support IDS 9f5c not present.
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x0D}, 3, new byte[] {(byte) 0x00}, 1),
				new TransParamTable(new byte[]{(byte) 0x9F,0x70}, 2,  new byte[] {(byte) 0x00}, 1),
				new TransParamTable(new byte[]{(byte) 0x9F,0x75}, 2,  new byte[] {(byte) 0x00}, 1),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x30}, 3, new byte[] {(byte) 0x00}, 1),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1C}, 3, new byte[] {(byte) 0x00, (byte) 0x00}, 2),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1D}, 3, new byte[] {(byte) 0x00}, 1),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x0C}, 3, new byte[] {(byte) 0x02}, 1),
				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x2D}, 3, new byte[] {(byte) 0x00}, 1),

				new TransParamTable(new byte[]{(byte) 0xDF,(byte) 0x81,0x1B}, 3, new byte[] {(byte) valorRRP}, 1), //MCD

				//9f1D
				new TransParamTable(new byte[]{(byte) 0x9F,0x1D}, 2,  new byte[]{(byte) 0x2C,(byte) 0x32,(byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00}, 8),//60
		};
		
		for (TransParamTable transParamTable : table) {
			ret = setDETData(transParamTable.tag, transParamTable.tag_len, transParamTable.value, transParamTable.value_len);
			if (ret != RetCode.EMV_OK) {
				break;
			}
		}

		if( ret == 0 ) {
				ret = setDETData(new byte[]{(byte) 0xDF, (byte) 0x81, 0x1B}, 3, new byte[]{(byte) 0xA0}, 1); // No Mag-Stripe

		}
		
		return ret;
	}
	
	private int setDETData(byte[] pucTag, int ucTagLen, byte[] pucData, int ucDataLen) {
		byte[] aucBuff = new byte[256];
		int ucBuffLen;
		int ret;

		if (pucTag == null || pucData == null)
		{
			return RetCode.CLSS_PARAM_ERR;
		}

		System.arraycopy(pucTag, 0, aucBuff, 0, ucTagLen);//Terminal Country Code
		ucBuffLen = ucTagLen;
		aucBuff[ucBuffLen++] = (byte) ucDataLen;
		System.arraycopy(pucData, 0, aucBuff, ucBuffLen, ucDataLen);
		ucBuffLen += ucDataLen;
		ret = ClssPassApi.Clss_SetTLVDataList_MC(aucBuff, ucBuffLen);


		return ret;
	}
	
	private void setParamByAidPass(Clss_MCAidParam aidParam, Clss_PreProcInfo procInfo) {
		
		byte[] aucBuff = new byte[64];

		if (aidParam != null)
		{
			setDETData(new byte[]{(byte) 0x9F,0x09}, 2, aidParam.version, 2);

			setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x20}, 3,	aidParam.tacDefault, 5);//TAC Default
			if (transParam != null && transParam.ucTransType == 0x20) {//refund rquired AAC
				setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x21}, 3, new byte[]{(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF,(byte) 0xFF}, 5);//TAC Denial
			}
			else
			{
				setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x21}, 3,	aidParam.tacDenial, 5);//TAC Denial
			}
			setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x22}, 3,	aidParam.tacOnline, 5);//TAC Online

			setDETData(new byte[]{(byte) 0x9F,0x01}, 2, aidParam.acquierId, 6);
		}


		if (procInfo != null)
		{

				byte[] Capabilities = null;
				Capabilities = new byte[]  {(byte)0xE0,(byte)0x20,(byte)0x08};

				setDETData(new byte[]{(byte) 0x9F,0x33}, 2, Capabilities, 3);


			// floor limit set for AID
			if (transParam.ucTransType == 0x20)
			{
				setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x23}, 3, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00}, 6);
			}
			else
			{

				setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x23}, 3, new byte[]{(byte) 0x00,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99}, 6);
			}


			// Reader Contactless Transaction Limit (No On-device CVM)

			setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x24}, 3, new byte[]{(byte) 0x00,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99}, 6);


			// Reader Contactless Transaction Limit (On-device CVM)

			setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x25}, 3, new byte[]{(byte) 0x00,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99,(byte) 0x99}, 6);

			// Reader CVM Required Limit
			setDETData(new byte[]{(byte) 0xDF,(byte) 0x81,0x26}, 3, new byte[]{(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00}, 6);

		}
		setDETData(new byte[]{(byte) 0x9F,0x1D}, 2,  new byte[]{(byte) 0x2C,(byte) 0x32,(byte) 0x80,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00}, 8);
	}


	class TransParamTable {
		
		byte[] tag;
		int tag_len;
		byte[] value;
		int value_len;
		
		public TransParamTable(byte[] tag, int tag_len, byte[] value, int value_len) {
			this.tag = new byte[4];
			System.arraycopy(tag, 0, this.tag, 0, tag_len);
			this.tag_len = tag_len;
			this.value = new byte[256];
			System.arraycopy(value, 0, this.value, 0, value_len);
			this.value_len = value_len;
		}
		
	}

}
