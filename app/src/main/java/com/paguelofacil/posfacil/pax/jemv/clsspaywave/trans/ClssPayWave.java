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
 * 10:54:05 2017-3-8  	           HuangJs           	    Create
 * ============================================================================		
 */	
package com.paguelofacil.posfacil.pax.jemv.clsspaywave.trans;

import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_MSD_20;
import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_WAVE_2;
import static com.pax.jemv.clcommon.VisaSchemeID.SCHEME_VISA_WAVE_3;

import android.util.Log;

import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.trans.ClssEntryPoint;
import com.pax.jemv.clcommon.ACType;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_ProgramID;
import com.pax.jemv.clcommon.Clss_ReaderParam;
import com.pax.jemv.clcommon.Clss_SchemeID_Info;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.Clss_VisaAidParam;
import com.pax.jemv.clcommon.CvmType;
import com.pax.jemv.clcommon.DDAFlag;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.clcommon.TransactionPath;
import com.pax.jemv.paywave.api.ClssWaveApi;

import java.util.Arrays;


/** 
 *  
 */

public class ClssPayWave {
	
	private static final String TAG = "ClssPayWave";
	
	private Clss_TransParam transParam;
	private TransactionPath transPath = new TransactionPath();
	private DDAFlag ddaFlag = new DDAFlag();
	private CvmType cvmType = new CvmType();
	private Clss_PreProcInfo procInfo;
	private Clss_VisaAidParam visaAidParam;
	private String PosEntryMode;
	private static ClssPayWave instance;
	
	//private TransCallback callback;
	//private ConditionVariable cv;
	private static ClssEntryPoint entryPoint ;



	/*public void setCallback(TransCallback callback) {
		this.callback = callback;
	}*/

	public static ClssPayWave getInstance() {
		if (instance == null) {
			instance = new ClssPayWave();
		}

		entryPoint = ClssEntryPoint.getInstance( );
		return instance;
	}
	
	public int getDDAFlag() {
		return ddaFlag.flag;
	}
	
	public int getCVMType() {
		return cvmType.type;
	}
	
	/**
	 * 
	 * @return
	 */
	public int coreInit() {
		return ClssWaveApi.Clss_CoreInit_Wave();
	}
	
	/**
	 * 
	 * @return
	 */
	public String readVersion() {
		ByteArray version = new ByteArray();
		ClssWaveApi.Clss_ReadVerInfo_Wave(version);
		String entryVer = Arrays.toString(version.data);
		return entryVer.substring(0, version.length);
	}
	
	/**
	 * 
	 * @param visaAidParam
	 * @param procInfo
	 * @return
	 */
	public int setConfigParam(Clss_VisaAidParam visaAidParam, Clss_PreProcInfo procInfo) {

		this.transParam = entryPoint.getTransParam();
		this.procInfo = procInfo;
		this.visaAidParam = visaAidParam;

		return RetCode.EMV_OK;
	}










	public int waveProcessStep1() {


		int ret;

		EntryOutParam outParam = entryPoint.getOutParam();
		Clss_PreProcInterInfo interInfo = entryPoint.getInterInfo();

		ret = waveFlowBegin(outParam, interInfo,visaAidParam);

		return ret;
	}




	/*private  void leerTrack2chip(AKPCardBasicData cardData) {
		try
		{
			ByteArray track2 = new ByteArray();

			int ret = ClssWaveApi.Clss_GetTLVData_Wave( (short) EMVTags.TAG_57_TRACK2_EQUIVALENT_DATA,  track2);
			if (ret == RetCode.EMV_OK) {

				String track2Str = HexUtil.byteArrayToHexString(track2.data, 0, track2.length);
				String track2ToByte = track2Str.replace("=", "D");
				int j = track2ToByte.indexOf('F');

				if (j > 0)
					track2ToByte = track2ToByte.substring(0, j); //hay que eliminar la F ya que el parseador de ISO no lo requeire

				if( track2ToByte.length() > 37 )
					track2ToByte = track2ToByte.substring(0, 37); // truncar hasta maximo 37 char.

				cardData.setPAN(CardUtil.obtenerPanTrack2(track2Str));
				String binStr = cardData.getPAN().substring(0, 6);
				long bin = Long.valueOf(binStr) * 10000;

				cardData.setExpDate(CardUtil.obtenerCardExpDateTrack2(track2Str));
				cardData.setServiceCode(CardUtil.obtenerServiceCode(track2Str));
				cardData.setTrack2(track2ToByte);
			}

			ByteArray cardHolder = new ByteArray();

			ret = ClssWaveApi.Clss_GetTLVData_Wave((short)EMVTags.TAG_5F20_CARDHOLDER_NAME, cardHolder);



			if (ret == 0) {
				byte cardHolderBuff[] = new byte[cardHolder.length];
				System.arraycopy(cardHolder.data, 0, cardHolderBuff, 0, cardHolder.length);
				cardData.setCardHolder(new String(cardHolderBuff, "UTF-8"));
			}


		}
		catch (Exception e) {

		}

	}*/

	private int actualizarKernel() {

		int ret = RetCode.EMV_OK;




		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F1A, new byte[]  {(byte)0x05,(byte)0x91}, 2);
		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F09,new byte[]  {(byte)0x00,(byte)0x8C} ,2);
		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F40,  new byte[]  {(byte)0xE0,(byte)0x00,(byte)0xF0,(byte)0xA0,(byte)0x01}, 5);



		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F39,new byte[]  {(byte)0x71}, 1);


        ClssWaveApi.Clss_SetTLVData_Wave((short) 0x5F2A, new byte[]  {(byte)0x08,(byte)0x40}, 2);





		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F16, "123456789012345".getBytes(),15);
		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F1C, "12345678".getBytes(),8);
		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F35, new byte[]{0x22}, 1);



		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F33,  new byte[]  {(byte)0xE0,(byte)0xB0,(byte)0xC8}, 3);



			ClssWaveApi.Clss_SetTLVData_Wave((short) 0x5F36, new byte[]{(byte) 0x02}, 1);


		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F3C, new byte[]  {(byte)0x05,(byte)0x91}, 2);
		ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F3D, new byte[]{2}, 1);


			ClssWaveApi.Clss_SetTLVData_Wave((short)0x9F1E,"12345678".getBytes(), 8);

		return ret;
	}

	public int waveFlowBegin(EntryOutParam outParam, Clss_PreProcInterInfo interInfo, Clss_VisaAidParam visaAidParam) {

		int ret;



		ret = ClssWaveApi.Clss_SetFinalSelectData_Wave(outParam.sDataOut, outParam.iDataLen);
		if (ret != RetCode.EMV_OK) {
			Log.e(TAG, "ClssWaveApi.clssWaveSetFinalSelectData(outParam.sDataOut, outParam.iDataLen) error, ret = "+ ret);
			return ret;
		}

		Clss_ReaderParam szReaderParam = new Clss_ReaderParam();

		ret = ClssWaveApi.Clss_GetReaderParam_Wave(szReaderParam);
		Log.i("WaveGetReaderParam", "ret = " + ret);
		if (ret != RetCode.EMV_OK) {
			Log.e(TAG, "ClssWaveApi.WaveGetReaderParam(szReaderParam) error, ret = "+ ret);
			return ret;
		}

		//Gillian 20170511
		szReaderParam.acquierId = new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x56};
		szReaderParam.ucTmType =  0x22;
		szReaderParam.aucTmCap = new byte[]  {(byte)0xE0,(byte)0xB0,(byte)0xC8};
		szReaderParam.aucTmCntrCode = new byte[]  {(byte)0x05,(byte)0x91};



			szReaderParam.aucTmTransCur= new byte[]  {(byte)0x08,(byte)0x40};


		szReaderParam.ucTmTransCurExp =2;

		szReaderParam.aucTmCapAd =  new byte[] {(byte) 0xE0, (byte) 0x00, (byte) 0xF0, (byte) 0xA0, (byte) 0x01};


			szReaderParam.aucTmRefCurCode =  new byte[]  {(byte)0x08,(byte)0x40};


		szReaderParam.ucTmRefCurExp = 2;

		//Gillian end
		ret = ClssWaveApi.Clss_SetReaderParam_Wave(szReaderParam);
		if (ret != RetCode.EMV_OK) {
			Log.e(TAG, "ClssWaveApi.Clss_SetReaderParam_Wave(szReaderParam) error, ret = "+ ret);
			return ret;
		}

		//Gillian 20170613
        Clss_SchemeID_Info[] szSchemeIDInfo = new Clss_SchemeID_Info[3];
        szSchemeIDInfo[0] = new Clss_SchemeID_Info();
        szSchemeIDInfo[1] = new Clss_SchemeID_Info();
        szSchemeIDInfo[2] = new Clss_SchemeID_Info();

        szSchemeIDInfo[0].ucSchemeID = (byte)SCHEME_VISA_WAVE_2;/*VisaSchemeID.SCHEME_VISA_WAVE_2*/;
        szSchemeIDInfo[0].ucSupportFlg = (byte)1;
        szSchemeIDInfo[1].ucSchemeID = (byte)SCHEME_VISA_WAVE_3;
        szSchemeIDInfo[1].ucSupportFlg = (byte)1;
        szSchemeIDInfo[2].ucSchemeID = (byte)SCHEME_VISA_MSD_20;
        szSchemeIDInfo[2].ucSupportFlg = (byte)1;

        ClssWaveApi.Clss_SetRdSchemeInfo_Wave((byte) 3, szSchemeIDInfo);

		//TODO:for test
		ByteArray proID = new ByteArray();
//		ret = ClssWaveApi.clssWaveSetTLVData((short) 0x9F5A, "123".getBytes(), 3);
		ClssWaveApi.Clss_GetTLVData_Wave((short) 0x9F5A, proID);

		if (proID.length != 0) {
			if (procInfo != null) {
				Clss_ProgramID stDRLParam = new Clss_ProgramID(procInfo.ulRdClssTxnLmt, procInfo.ulRdCVMLmt,
						procInfo.ulRdClssFLmt, procInfo.ulTermFLmt, proID.data, (byte) proID.length,
						procInfo.ucRdClssFLmtFlg, procInfo.ucRdClssTxnLmtFlg, procInfo.ucRdCVMLmtFlg,
						procInfo.ucTermFLmtFlg, procInfo.ucStatusCheckFlg, (byte) 0, new byte[4]);
				ret = ClssWaveApi.Clss_SetDRLParam_Wave(stDRLParam);
				if(ret != RetCode.EMV_OK) {
					Log.e(TAG, "ClssWaveApi.clssWaveSetDRLParam(stDRLParam) error, ret = "+ ret);
					return ret;
				}
			}

		}

		if (visaAidParam != null) {
			visaAidParam.ulTermFLmt = 99999999;
			ret = ClssWaveApi.Clss_SetVisaAidParam_Wave(visaAidParam);
			if (ret != RetCode.EMV_OK) {
				return ret;
			}
		}

		if (transParam != null) {
			ClssWaveApi.Clss_SetTLVData_Wave((short) 0x9c, new byte[]{transParam.ucTransType}, 1);
			ret = ClssWaveApi.Clss_SetTransData_Wave(transParam, interInfo);
			if (ret != RetCode.EMV_OK) {
				Log.e(TAG, "ClssWaveApi.Clss_SetTransData_Wave(transParam, interInfo) error, ret = "+ ret);
				return ret;
			}
		}


		//leerTrack2chip();

		actualizarKernel();



        ACType actype = new ACType();

		ret = ClssWaveApi.Clss_Proctrans_Wave(transPath, actype);
		if (ret != RetCode.EMV_OK) {
			Log.e(TAG, "ClssWaveApi.Clss_Proctrans_Wave(transPath, actype) error, ret = "+ ret);
		}


		ByteArray datos = new ByteArray();
		ClssWaveApi.Clss_nGetTrack2MapData_Wave(datos);


       // leerTrack2chip(cardData);
        return ret;

	}


}
