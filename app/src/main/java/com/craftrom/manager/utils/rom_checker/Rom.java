package com.craftrom.manager.utils.rom_checker;

import android.os.Build;

/**
 * Rom枚举
 * Created by Zhuliya on 2018/10/22
 */
public enum Rom {

    //已适配
    MIUI("xiaomi"),
    Flyme("meizu"),
    EMUI("huawei"),
    ColorOS("oppo"),
    Sense("htc"),
    Google("google"),
    MokeeOS(""),
    PE(""),
    Other(""); // CyanogenMod, Lewa OS, 百度云OS, Tencent OS, 深度OS, IUNI OS, Tapas OS, Mokee

    private String ma;//Rom制造商
    private int versionCode;//版本Code
    private String versionName;//版本名称
    private final String manufacturer = Build.MANUFACTURER;//当前手机制造商

    Rom(String ma) {
        this.ma = ma;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "ROM{" +
                "name='" + this.name() + '\'' +
                ",versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ",ma=" + ma + '\'' +
                ",manufacturer=" + manufacturer + '\'' +
                '}';
    }
}
