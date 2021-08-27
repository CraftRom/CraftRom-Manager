package com.craftrom.manager.utils.rom_checker.rom_list;

import android.text.TextUtils;

import com.craftrom.manager.utils.rom_checker.Rom;
import com.craftrom.manager.utils.rom_checker.utils.RomProperties;


/**
 * PixelExperience ROM
 * Created by melels1991 on 2021/08/28
 */
public class PEChecker extends Checker {

    private static final String PE_VERSION_NAME = "org.pixelexperience.version.display";

    @Override
    public Rom getRom() {
        return Rom.PE;
    }

    @Override
    public boolean checkBuildProp(RomProperties romProperties) {
        String versionName = romProperties.getProperty(PE_VERSION_NAME);

        if (!TextUtils.isEmpty(versionName)) {

            parseVersionCode(versionName);
            getRom().setVersionName(versionName);

            return true;
        }

        return false;
    }
}
