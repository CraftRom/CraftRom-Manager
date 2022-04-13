/*
 * Copyright (c) 2020. Relsell Global
 */

package com.craftrom.manager.utils.rss

import android.util.Log
import com.craftrom.manager.utils.Constants
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream

class RssParser {
    private val rssItems = ArrayList<RssItem>()
    private lateinit var rssItem : RssItem
    private var text: String? = null

    fun parse(inputStream: InputStream):List<RssItem> {
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
                        rssItem = RssItem()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if (tagname.equals("item", ignoreCase = true)) {
                        // add employee object to list
                        rssItem.let { rssItems.add(it) }
                        foundItem = false
                    } else if ( foundItem && tagname.equals("title", ignoreCase = true)) {
                        rssItem.title = text.toString()
                    } else if (foundItem && tagname.equals("link", ignoreCase = true)) {
                        rssItem.link = text.toString()
                    } else if (foundItem && tagname.equals("description", ignoreCase = true)) {
                        rssItem.description = text.toString()
                    } else if (foundItem && tagname.equals("pubDate", ignoreCase = true)) {
                        rssItem.pubDate = text.toString()
                    } else if (foundItem && tagname.equals("image", ignoreCase = true)) {
                        rssItem.image = text.toString()
                    } else if (foundItem && tagname.equals("author", ignoreCase = true)) {
                        rssItem.author = text.toString()
                    }
                }

                eventType = parser.next()

            }

        } catch (e: XmlPullParserException) {
            Log.e(Constants.TAG, "RSS PARSER: " + e.printStackTrace())

        } catch (e: IOException) {
            Log.e(Constants.TAG, "RSS: " + e.printStackTrace())
        }
        return rssItems
    }
}