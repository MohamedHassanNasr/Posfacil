/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) $YEAR-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 2020/4/30  	         Qinny Zhou           	Create/Add/Modify/Delete
 * ===========================================================================================
 */
package com.paxsz.module.pos.mag;

import com.pax.api.MagException;
import com.pax.api.MagManager;
import com.pax.dal.IMag;
import com.pax.dal.entity.TrackData;
import com.pax.dal.exceptions.MagDevException;

public class PosApiMag implements IMag {
    private MagManager magManager;

    public PosApiMag() {
        try {
            magManager = MagManager.getInstance();
        } catch (MagException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() throws MagDevException {
        if (magManager == null) {
            return;
        }
        try {
            magManager.magOpen();
        } catch (MagException e) {
            throw new MagDevException(e.exceptionCode, e.getMessage());
        }
    }

    @Override
    public void reset() throws MagDevException {
        if (magManager == null) {
            return;
        }
        try {
            magManager.magReset();
        } catch (MagException e) {
            throw new MagDevException(e.exceptionCode, e.getMessage());
        }
    }

    @Override
    public boolean isSwiped() throws MagDevException {
        if (magManager == null) {
            return false;
        }
        try {
            return magManager.magSwiped();
        } catch (MagException e) {
            throw new MagDevException(e.exceptionCode, e.getMessage());
        }

    }

    @Override
    public TrackData read() throws MagDevException {
        TrackData trackData = new TrackData();
        if (magManager != null) {
            try {
                MagManager.TrackInfo trackInfo = magManager.magRead();
                if (trackInfo != null) {
                    trackData.setResultCode(trackInfo.resultCode);
                    trackData.setTrack1(trackInfo.track1);
                    trackData.setTrack2(trackInfo.track2);
                    trackData.setTrack3(trackInfo.track3);
                }
            } catch (MagException e) {
                throw new MagDevException(e.exceptionCode, e.getMessage());
            }
        }

        return trackData;
    }

    @Override
    public void close() throws MagDevException {
        if (magManager == null) {
            return;
        }
        try {
            magManager.magClose();
        } catch (MagException e) {
            throw new MagDevException(e.exceptionCode, e.getMessage());
        }
    }

    @Override
    public TrackData readExt() throws MagDevException {
        TrackData trackData = new TrackData();
        if (magManager != null) {
            try {
                MagManager.ExtTrackInfo trackInfo = magManager.magReadExt();
                if (trackInfo != null) {
                    trackData.setResultCode(trackInfo.resultCode);
                    trackData.setTrack1(trackInfo.track1);
                    trackData.setTrack2(trackInfo.track2);
                    trackData.setTrack3(trackInfo.track3);
                    trackData.setTrack4(trackInfo.track4);
                }
            } catch (MagException e) {
                throw new MagDevException(e.exceptionCode, e.getMessage());
            }
        }
        return trackData;
    }
}
