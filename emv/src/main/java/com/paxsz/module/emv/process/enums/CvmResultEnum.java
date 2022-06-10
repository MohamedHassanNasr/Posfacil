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
 * 20200526  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process.enums;

public enum CvmResultEnum {
    CVM_NO_CVM,// no cvm
    CVM_OFFLINE_PIN,//plaintext pin or enciphered pin, just for contact
    CVM_ONLINE_PIN,//onlin pin
    CVM_SIG,//signature
    CVM_ONLINE_PIN_SIG,// online pin plus signature
    CVM_CONSUMER_DEVICE,//see phone
}
