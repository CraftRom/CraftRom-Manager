/*
 * Copyright (c) 2020. Relsell Global
 */

package com.craftrom.manager.utils.module

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class ModuleParser {
    private val moduleItems = ArrayList<ModuleItem>()
    private var moduleItem : ModuleItem ?= null
    private var text: String? = null

    fun parse(inputStream: InputStream):List<ModuleItem> {
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)
            var eventType = parser.eventType
            var foundItem = false
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals("item", ignoreCase = true)) {
                        // create a new instance of employee
                        foundItem = true
                        moduleItem = ModuleItem()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("item", ignoreCase = true)) {
                        // add employee object to list
                        moduleItem?.let { moduleItems.add(it) }
                        foundItem = false
                    } else if ( foundItem && tagname.equals("title", ignoreCase = true)) {
                        moduleItem!!.title = text.toString()
                    } else if (foundItem && tagname.equals("link", ignoreCase = true)) {
                        moduleItem!!.link = text.toString()
                    } else if (foundItem && tagname.equals("description", ignoreCase = true)) {
                        moduleItem!!.description = text.toString()
                    } else if (foundItem && tagname.equals("pubDate", ignoreCase = true)) {
                        moduleItem!!.pubDate = text.toString()
                    } else if (foundItem && tagname.equals("author", ignoreCase = true)) {
                        moduleItem!!.author = text.toString()
                    } else if (foundItem && tagname.equals("version", ignoreCase = true)) {
                        moduleItem!!.version = text.toString()
                    }
                }

                eventType = parser.next()

            }

        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return moduleItems
    }
}