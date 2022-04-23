package com.craftrom.manager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.craftrom.manager.utils.updater.repository.SelfUpdateRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

	 override fun onReceive(context: Context, intent: Intent?): Unit = runBlocking {
		 SelfUpdateRepository().updateAsync(context).await().onFailure { Log.e("AlarmReceiver", "onReceive", it) }
	}
}