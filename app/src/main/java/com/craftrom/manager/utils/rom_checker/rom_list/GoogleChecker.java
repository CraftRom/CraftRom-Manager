package com.craftrom.manager.utils.rom_checker.rom_list;

import android.os.Build;
import android.text.TextUtils;

import com.craftrom.manager.utils.rom_checker.Rom;
import com.craftrom.manager.utils.rom_checker.utils.RomProperties;


/**
 * Google ROM
 * 由于手机有限，暂时使用该方式判断
 * Created by Zhuliya on 2018/11/12
 */
public class GoogleChecker extends Checker {

    private static final String GOOGLE_CLIENT_ID_BASE = "ro.com.google.clientidbase";//android-google
    //    private static final String GOOGLE_THEME_ID = "ro.com.google.ime.theme_id";//5
    private static final String GOOGLE_VERSION_NAME = "ro.build.version.release";//7.1.1

    @Override
    public Rom getRom() {
        return Rom.Google;
    }

    @Override
    public boolean checkBuildProp(RomProperties romProperties) {
        String clientIdBase = romProperties.getProperty(GOOGLE_CLIENT_ID_BASE);
//        String themeId = romProperties.getProperty(GOOGLE_THEME_ID);

        if ("android-google".equals(clientIdBase)) {
            String versionName = romProperties.getProperty(GOOGLE_VERSION_NAME);

            getRom().setVersionCode(Build.VERSION.SDK_INT);
            getRom().setVersionName(versionName);

            return true;
        }

        return false;
    }
}
