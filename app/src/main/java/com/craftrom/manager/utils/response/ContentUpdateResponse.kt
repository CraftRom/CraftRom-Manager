package com.craftrom.manager.utils.response

data class ContentUpdateResponse(
    val name: String?,
    val type: String?,
    val version: String?,
    val fileName: String?,
    val dateTime: String?,
    val desc: String?,

    ) {

    fun isValid(): Boolean {
        return version != null && fileName != null && dateTime != null
    }
}