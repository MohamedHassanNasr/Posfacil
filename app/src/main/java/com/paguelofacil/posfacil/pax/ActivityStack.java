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
 *  2020/06/02 	         Qinny Zhou           	      Create
 *  ===========================================================================================
 */
package com.paguelofacil.posfacil.pax;

import android.app.Activity;

import com.pax.commonlib.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.Stack;

public class ActivityStack {
    private static final String TAG = "ActivityStack";
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private Stack<Activity> activities = new Stack<>();

    private ActivityStack() {
    }

    public void pop(Activity activity) {
        if (activity != null) {
            remove(activity);
        }
    }

    public void pop() {
        pop(activities.peek());
    }

    /**
     * pop to the specific activity and finish it
     *
     * @param activity the target activity
     */
    public void popTo(Activity activity) {
        if (activity != null) {
            while (true) {
                Activity lastCurrent;
                try {
                    lastCurrent = activities.peek();
                } catch (Exception e) {
                    LogUtils.e(TAG, "popTo: ", e);
                    lastCurrent = null;
                }
                if (lastCurrent == null || activity == lastCurrent) {
                    return;
                }
                remove(lastCurrent);
            }
        }
    }

    /**
     * pop to the specific activity and finish it
     *
     * @param clz the class of the target activity
     */
    public void popTo(Class clz) {
        if (clz != null) {
            while (true) {
                Activity lastCurrent = activities.peek();
                if (lastCurrent == null || clz == lastCurrent.getClass()) {
                    return;
                }
                remove(lastCurrent);
            }
        }
    }

    /**
     * get the top activity of the stack
     *
     * @return the top activity
     */
    public Activity top() {
        return sCurrentActivityWeakRef != null ? sCurrentActivityWeakRef.get() : null;
    }

    public void setTop(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference(activity);
    }

    /**
     * push an activity the the top
     *
     * @param activity the target activity
     */
    public void push(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    /**
     * pop all activities from the stack and finish them
     */
    public void popAll() {
        if (!activities.isEmpty()) {
            while (true) {
                try {
                    Activity activity;
                    if (activities.peek() != null) {
                        activity = activities.peek();
                    } else {
                        break;
                    }
                    remove(activity);
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getMessage());
                    return;
                }
            }
        }
    }

    /**
     * remove an activity from the stack and finish it
     *
     * @param activity the target activity
     */
    private void remove(Activity activity) {
        activity.finish();
        activities.remove(activity);
    }

    private static ActivityStack instance = null;

    public static ActivityStack getInstance() {
        if (instance == null) {
            instance = new ActivityStack();
        }
        return instance;
    }
}
