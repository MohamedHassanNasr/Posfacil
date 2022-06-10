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

public enum TransResultEnum {
    RESULT_OFFLINE_APPROVED,//1ST GAC Ac type TC.
    RESULT_OFFLINE_DENIED,//1ST GAC Ac type AAC.
    RESULT_REQ_ONLINE,//1ST GAC Ac type ARQC.
    RESULT_ONLINE_CARD_DENIED,//Online approve, but After 2st GAC, card denied
    RESULT_ONLINE_APPROVED, //Online approve, and After 2st GAC, card approve.
    RESULT_ONLINE_DENIED,//online denied.
    RESULT_FALLBACK,//fallback to swiped
    RESULT_TRY_AGAIN,
    RESULT_CLSS_SEE_PHONE,//tap phone
    RESULT_CLSS_TRY_ANOTHER_INTERFACE,//use contact
}
