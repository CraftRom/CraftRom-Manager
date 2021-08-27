package com.craftrom.manager.utils.rom_checker;



import com.craftrom.manager.utils.rom_checker.rom_list.Checker;
import com.craftrom.manager.utils.rom_checker.rom_list.ColorOsChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.EmuiChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.FlymeChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.GoogleChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.MiuiChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.MokeeChecker;
import com.craftrom.manager.utils.rom_checker.rom_list.PEChecker;
import com.craftrom.manager.utils.rom_checker.utils.RomProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Rom 识别入口类
 * <p>
 * 识别逻辑：
 * 第一步：检查所有Checker系统属性是否与当前手机匹配，匹配则返回对应Rom，否则执行第二步。
 * 第二步：检查所有Checker手机制造商是否与当前手机匹配(默认开启)，匹配则返回对应Rom，可能不准确
 * <p>
 * <p>
 * Created by Zhuliya on 2018/10/22
 */
public final class RomIdentifier {

    private static volatile Rom rom;//已识别的Rom

    private static boolean isMaCheck = true;//是否开启根据手机制造商进行Rom判断，默认开启

    private RomIdentifier() {
    }

    /**
     * 获取默认识别器
     *
     * @return
     */
    public static List<Checker> getChecker() {
        return Arrays.asList(
                new MiuiChecker(),
                new EmuiChecker(),
                new ColorOsChecker(),
                new FlymeChecker(),
                new GoogleChecker(),
                new MokeeChecker(),
                new PEChecker()
        );
    }

    /**
     * 设置是否开启根据手机制造商进行Rom判断
     *
     * @param maCheck
     */
    public static void setMaCheck(boolean maCheck) {
        isMaCheck = maCheck;
    }

    /**
     * 获取当前手机Rom
     * 使用默认识别器
     *
     * @return
     */
    public static Rom getRom() {
        return getRom(null);
    }

    /**
     * 获取当前手机Rom（单例）
     *
     * @param checkers 自定义识别器
     * @return
     */
    public static Rom getRom(List<Checker> checkers) {
        if (rom == null) {
            synchronized (RomIdentifier.class) {
                if (rom == null) {
                    rom = getRomType(checkers);
                }
            }
        }
        return rom;
    }

    /**
     * 获取当前手机Rom
     *
     * @return
     */
    private static Rom getRomType(List<Checker> checkers) {

        if (checkers == null) {
            checkers = getChecker();
        }

        RomProperties romProperties = new RomProperties();

        //检查所有Checker系统属性是否与当前手机匹配，匹配则返回对应Rom，否则执行第二步。
        for (Checker checker : checkers) {
            if (checker.checkBuildProp(romProperties)) {
                return checker.getRom();
            }
        }

        //检查是否开启手机制造商检查
        if (isMaCheck) {
            //第二步，检查所有Checker手机制造商是否与当前手机匹配，匹配则返回对应Rom
            for (Checker checker : checkers) {
                if (checker.checkManufacturer()) {
                    return checker.getRom();
                }
            }
        }

        return Rom.Other;
    }
}
