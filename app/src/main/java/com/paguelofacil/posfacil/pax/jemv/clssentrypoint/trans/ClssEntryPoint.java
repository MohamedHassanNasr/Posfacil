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
 * 10:52:41 2017-3-8  	           HuangJs           	    Create
 * ============================================================================		
 */	
package com.paguelofacil.posfacil.pax.jemv.clssentrypoint.trans;


import com.paguelofacil.posfacil.pax.jemv.clssentrypoint.model.EntryOutParam;
import com.pax.jemv.clcommon.ByteArray;
import com.pax.jemv.clcommon.ClssTmAidList;
import com.pax.jemv.clcommon.Clss_PreProcInfo;
import com.pax.jemv.clcommon.Clss_PreProcInterInfo;
import com.pax.jemv.clcommon.Clss_TransParam;
import com.pax.jemv.clcommon.KernType;
import com.pax.jemv.clcommon.RetCode;
import com.pax.jemv.device.DeviceManager;
import com.pax.jemv.device.model.TransactionInterface;
import com.pax.jemv.entrypoint.api.ClssEntryApi;

import java.util.Arrays;

//import com.pax.tradepaypw.utils.Utils;

/** 
 *  
 */

public class ClssEntryPoint {

	private static final String TAG = "ClssEntryPoint";

	private static byte[] RID_TYPE_VISA = new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x03};
	private static byte[] RID_TYPE_MC = new byte[]{(byte)0xA0, 0x00, 0x00, 0x00, 0x04};
	private static byte[] RID_TYPE_DPAS = new byte[]{(byte)0xA0, 0x00, 0x00, 0x01, 0x52};
	private static byte[] RID_TYPE_QPBOC = new byte[]{(byte)0xA0, 0x00, 0x00, 0x03, 0x33};
	
	private static ClssEntryPoint instance;
	private Clss_PreProcInterInfo interInfo = new Clss_PreProcInterInfo();
	private EntryOutParam outParam = new EntryOutParam();
	private Clss_TransParam transParam;

	public static ClssEntryPoint getInstance() {
		if (instance == null) {
			instance = new ClssEntryPoint();
		}

		return instance;
	}

	/**
	 * 
	 *
	 */
	public void coreInit() {
		ClssEntryApi.Clss_CoreInit_Entry();
		ClssEntryApi.Clss_SetMCVersion_Entry((byte) 0x03);
	}
	
	/**
	 * 
	 * @return
	 */
	public String readVerInfo()
	{
		ByteArray version = new ByteArray();
		ClssEntryApi.Clss_ReadVerInfo_Entry(version);
		String entryVer = Arrays.toString(version.data);
		return entryVer.substring(0, version.length);
	}
	
	/**
	 * 
	 * @param tmAidList
	 * @param preProcInfo
	 */
	public int setConfigParam(byte ttq, boolean isForceOnline, ClssTmAidList[] tmAidList, Clss_PreProcInfo[] preProcInfo) {
		
		int ret = RetCode.EMV_OK;
		
		if (tmAidList == null || preProcInfo == null) {
			return RetCode.CLSS_PARAM_ERR;
		}
		
		ClssEntryApi.Clss_DelAllAidList_Entry();
		ClssEntryApi.Clss_DelAllPreProcInfo();
		
		for (ClssTmAidList clssTmAidList : tmAidList)
		{
			if (clssTmAidList == null)
			{
				return RetCode.CLSS_PARAM_ERR;
			}

			ret = ClssEntryApi.Clss_AddAidList_Entry(clssTmAidList.aucAID, clssTmAidList.ucAidLen, clssTmAidList.ucSelFlg, clssTmAidList.ucKernType);

			if (ret != RetCode.EMV_OK)
			{
				return ret;
			}
		}

		for (Clss_PreProcInfo clssPreProcInfo : preProcInfo)
		{
			if (isForceOnline)
			{
				clssPreProcInfo.ulRdClssFLmt = 0;
			}

			byte[] rid = new byte[5];

			System.arraycopy(clssPreProcInfo.aucAID, 0, rid, 0, 5);

			clssPreProcInfo.aucReaderTTQ[0] = ttq		;
			clssPreProcInfo.aucReaderTTQ[1] = (byte)0x00;
			clssPreProcInfo.aucReaderTTQ[2] = (byte)0x00;
			clssPreProcInfo.aucReaderTTQ[3] = (byte)0x00;



			ret = ClssEntryApi.Clss_SetPreProcInfo_Entry(clssPreProcInfo);

			if (ret != RetCode.EMV_OK)
			{
				return ret;
			}

		}
		
		return ret;
	}
	
	public int entryProcess(Clss_TransParam transParam, EntryOutParam entryOut) {

		int ret;
		
		if(transParam == null) {
			return RetCode.CLSS_PARAM_ERR;
		}

		this.transParam = transParam;
		
		//Transaction Pre-Processing. 
		ret = ClssEntryApi.Clss_PreTransProc_Entry(transParam);
		if (ret != RetCode.EMV_OK) {
			return ret; //Verificar si pide contacto
		}
		
		DeviceManager.getInstance().setIccSlot((byte) 0);
		int i= DeviceManager.getInstance().iccSetTxnIF((byte) TransactionInterface.DEVICE_CLSS_TXNIF);

        //Application selection
		ret = ClssEntryApi.Clss_AppSlt_Entry(0, 0);


		if(ret != RetCode.EMV_OK) {
			if (ret == RetCode.ICC_CMD_ERR)
				return RetCode.CLSS_TRY_AGAIN;
			return ret;
		}



		//Begin Entry Flow
		ret = entryFlowBegin();



		entryOut.iAIDLen = this.getOutParam().iAIDLen;
        entryOut.sAID = this.getOutParam().sAID;
		
		return ret;
	}
	
	/**
	 * 
	 *
	 * @return
	 */
	public int entryFlowBegin() {
		
		int ret ;
		KernType kernType = new KernType();
		ByteArray dataOut = new ByteArray();
		
		ret = ClssEntryApi.Clss_FinalSelect_Entry(kernType, dataOut);
		if(ret != RetCode.EMV_OK) {
			if (ret == RetCode.ICC_CMD_ERR){
				return RetCode.CLSS_TRY_AGAIN;
			}
			if((RetCode.EMV_RSP_ERR == ret) || (RetCode.EMV_APP_BLOCK == ret)
					|| (RetCode.ICC_BLOCK  == ret) || (RetCode.CLSS_RESELECT_APP == ret)) {
                ret = ClssEntryApi.Clss_DelCurCandApp_Entry();
                if (ret != RetCode.EMV_OK) {
                    // 候选列表为空，进行相应错误处理，退出
                    return ret;
                }
                return RetCode.CLSS_TRY_AGAIN;
            }
            return ret;
		}
		
		outParam.ucKernType = kernType.kernType;
		System.arraycopy(dataOut.data, 0, outParam.sAID, 0, dataOut.length);
		outParam.iAIDLen = dataOut.length;

		ret = ClssEntryApi.Clss_GetPreProcInterFlg_Entry(interInfo);
		if(ret != RetCode.EMV_OK) {
			return ret;
		}
		ret = ClssEntryApi.Clss_GetFinalSelectData_Entry(dataOut);
		if(ret != RetCode.EMV_OK) {
			return ret;
		}
		System.arraycopy(dataOut.data, 0, outParam.sDataOut, 0, dataOut.length);
		outParam.iDataLen = dataOut.length;
		return RetCode.EMV_OK;
	}
	
	public Clss_PreProcInterInfo getInterInfo() {
		return this.interInfo;
	}

	public EntryOutParam getOutParam() {
		return this.outParam;
	}

	public Clss_TransParam getTransParam() {
		return this.transParam;
	}
	
}
