/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) 2019-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                  Author	                 Action
 * 20190108  	         guanjw                  Create
 * ===========================================================================================
 */

package com.paguelofacil.posfacil.pax.util;

import com.pax.commonlib.utils.LogUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {
    private static final String TAG = "ThreadPoolManager";

    private ThreadPoolExecutor executor;

    private ThreadPoolManager() {
        int corePoolSize = Runtime.getRuntime().availableProcessors();
//        int corePoolSize = Runtime.getRuntime().availableProcessors()*2+1;
        LogUtils.i(TAG, "corePoolSize:" + corePoolSize);
        executor = new ThreadPoolExecutor(
                corePoolSize,
                Integer.MAX_VALUE,
                1,
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    public static ThreadPoolManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.remove(runnable);
    }

    private static class LazyHolder {
        public static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
    }

}
