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
 * 20200528  	         JackHuang               Create
 * ===========================================================================================
 */
package com.paxsz.module.emv.process.contactless;

import com.pax.jemv.clcommon.KernType;

class ClssKernelProcessFactory {
    /**
     * when need to add a new clss kernel, create a object here
     * @param kernelType
     * @return
     */
    public ClssKernelProcess getKernelProcess(int kernelType){
        switch (kernelType) {
            case KernType.KERNTYPE_VIS:
                return new ClssPayWaveProcess();
            case KernType.KERNTYPE_MC:
                return new ClssPayPassProcess();
            default:
                throw new IllegalArgumentException("Unsupported Kernel " + kernelType);
        }
    }
}
