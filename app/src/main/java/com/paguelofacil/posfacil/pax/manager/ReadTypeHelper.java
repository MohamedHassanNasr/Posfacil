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
 * Date	                 Author	                Action
 * 2020/5/18  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paguelofacil.posfacil.pax.manager;

import com.pax.dal.entity.EReaderType;

public class ReadTypeHelper {
    private static class LazyHolder {
        public static final ReadTypeHelper INSTANCE = new ReadTypeHelper();
    }

    public static ReadTypeHelper getInstance() {
        return LazyHolder.INSTANCE;
    }

    private ReadTypeHelper() {
    }

    private volatile Byte readType = EReaderType.DEFAULT.getEReaderType();

    public  void setReadType(Byte type) {
        this.readType = type;
    }

    public Byte getReadType() {
        return this.readType;
    }

}
