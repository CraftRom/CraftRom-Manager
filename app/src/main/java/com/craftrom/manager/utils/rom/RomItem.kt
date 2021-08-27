package com.craftrom.manager.utils.rom

class RomItem {
    var title = ""
    var link = ""
    var description = ""
    var pubDate = ""
    var author = ""
    var android = ""

    override fun toString(): String {
        return "RomItem(title='$title', link='$link', description='$description', pubDate='$pubDate', author='$author', android='$android')"
    }
}