package com.craftrom.manager.utils.response

import com.craftrom.manager.utils.Const
import com.craftrom.manager.utils.DeviceSystemInfo

data class ContentUpdateResponse(
    val name: String?,
    val type: String?,
    val version: String?,
    val fileName: String?,
    val dateTime: String?,
    ) {
    var baseUrl: String = Const.EXODUSOS_FILE_URL

    fun isValid(): Boolean {
        return version != null && fileName != null && dateTime != null
    }

    fun fileUrl(): String {
        return baseUrl + "/" + DeviceSystemInfo.device() + "/" + version + "/" + fileName
    }
}