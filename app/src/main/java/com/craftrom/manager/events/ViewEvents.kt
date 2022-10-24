package com.craftrom.manager.events
import android.app.Activity

class RecreateEvent : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: Activity) {
        activity.recreate()
    }
}