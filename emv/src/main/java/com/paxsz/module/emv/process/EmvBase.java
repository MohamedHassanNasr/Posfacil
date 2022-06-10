/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2020-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                  Author	                 Action
 * 20200518  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process;

import com.pax.jemv.clcommon.ByteArray;
import com.paxsz.module.emv.param.EmvProcessParam;
import com.paxsz.module.emv.process.entity.IssuerRspData;
import com.paxsz.module.emv.process.entity.TransResult;

public abstract class EmvBase {
    protected IStatusListener statusListener;

    public static void loadLibrary(){
        //load common
        System.loadLibrary("F_DEVICE_LIB_PayDroid");
        System.loadLibrary("F_PUBLIC_LIB_PayDroid");

        //load contact
        System.loadLibrary("F_EMV_LIBC_PayDroid");
        System.loadLibrary("F_EMV_LIB_PayDroid");
        System.loadLibrary("JNI_EMV_v102");

        //load entry
        System.loadLibrary("F_ENTRY_LIB_PayDroid");
        System.loadLibrary("JNI_ENTRY_v103");


        //load paypass
        System.loadLibrary("F_MC_LIB_PayDroid");
        System.loadLibrary("JNI_MC_v100_01");

        //load paywave
        System.loadLibrary("F_WAVE_LIB_PayDroid");
        System.loadLibrary("JNI_WAVE_v100");
    }

    /**
     * Emv process:
     *      1.core init
     *      2.add  aid
     * Clss process:
     *      1.entry init
     *      2.add aid
     */
    public abstract int preTransProcess(EmvProcessParam emvParam);


    /**
     * Process as below:
     *      1.detected card
     *      2.select application
     *      3.application initialization
     *      4.read application data
     *      5.offline data authentication
     *      6.terminal risk management
     *      7.ardholder authentication
     *      8.terminal behavior analysis
     *      9.First Generate AC
     */
    public abstract TransResult startTransProcess();


    /**
     * Process as below:
     *      1.Issuer Authentication
     *      2.Script Processing
     *      3.Complete Trans
     */
    public abstract TransResult completeTransProcess(IssuerRspData issuerRspData);

    /**
     * get tag value from emv kernel.
     * @param tag, Emv tag
     * @param value
     * @return
     */
    public abstract int getTlv(int tag, ByteArray value);

    public void registerStatusListener(IStatusListener statusListener) {
        this.statusListener = statusListener;
    }

}
