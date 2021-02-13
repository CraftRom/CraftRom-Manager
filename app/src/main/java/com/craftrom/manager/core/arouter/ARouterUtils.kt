package com.craftrom.manager.core.arouter

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.craftrom.manager.BuildConfig

object ARouterUtils {
    /**
     * App onCreate()执行
     */
    fun init(application: Application) {
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application) // 尽可能早，推荐在Application中初始化
    }

    /**
     * App onTerminate()执行
     */
    fun destroy() {
        ARouter.getInstance().destroy()
    }

    /**
     * 在activity中添加
     */
    fun injectActivity(activity: FragmentActivity?) {
        activity?.let { ARouter.getInstance().inject(it) }
    }

    /**
     * 在fragment中添加
     */
    fun injectFragment(fragment: Fragment?) {
        fragment?.let { ARouter.getInstance().inject(fragment) }
    }

    /**
     * 简单跳转
     */
    fun navigation(path: String?) {
        path?.let {
            ARouter.getInstance()
                    .build(it)
                    .navigation()
        }
    }


}