package com.craftrom.manager.utils.rom_checker.rom_list;

import android.text.TextUtils;

import com.craftrom.manager.utils.rom_checker.Rom;
import com.craftrom.manager.utils.rom_checker.utils.RomProperties;

/**
 * 小米MIUI ROM
 * Created by Zhuliya on 2018/10/22
 */
public class MokeeChecker extends Checker {

    public static final String MOKEE_VERSION_NAME = "ro.mk.version"; // "7.6.15"
    public static final String MOKEE_VERSION = "ro.mk.version"; // "V8"

    /**
     * 获取Rom信息
     *
     * @return
     */
    @Override
    public Rom getRom() {
        return Rom.MokeeOS;
    }

    /**
     * 检查系统属性
     *
     * @param romProperties
     * @return
     */
    @Override
    public boolean checkBuildProp(RomProperties romProperties) {
        String version = romProperties.getProperty(MOKEE_VERSION);

        if (!TextUtils.isEmpty(version)) {

            String versionName = romProperties.getProperty(MOKEE_VERSION_NAME);

            parseVersionCode(versionName);
            getRom().setVersionName(versionName);

            return true;
        }

        return false;
    }


}
