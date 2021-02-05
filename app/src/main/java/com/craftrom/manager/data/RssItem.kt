package com.craftrom.manager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_items")
data class RssItem(
    val url: String,
    val name: String,
    val lastOpenDate: String,
    val createDate: String,
    val updateDate : String
) {
    @ColumnInfo(name = "rss_id")
    @PrimaryKey(autoGenerate = true)
    var rssId: Long = 0

    override fun toString() = "$name $url"
}