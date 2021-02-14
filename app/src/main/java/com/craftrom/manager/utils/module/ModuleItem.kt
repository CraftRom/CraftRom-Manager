package com.craftrom.manager.utils.module

class ModuleItem{
        lateinit var title:String
        lateinit var description:String
        lateinit var pubDate:String
        lateinit var author:String
        lateinit var link:String


        constructor(title: String,description:String,pubDate:String,author:String) {
            this.title = title
            this.description = description
            this.pubDate = pubDate
            this.author = author
            this.link = author
        }

        constructor()
}