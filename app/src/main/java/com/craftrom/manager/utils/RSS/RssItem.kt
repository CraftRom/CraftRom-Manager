package com.craftrom.manager.utils.RSS

class RssItem {
    var title = ""
    var link = ""
    var description = ""
    var pubDate = ""
    var image = ""
    var author = ""

    override fun toString(): String {
        return "RssItem(title='$title', link='$link', description='$description', pubDate='$pubDate', image='$image', author='$author')"
    }
}