package com.craftrom.manager.receiver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.craftrom.manager.utils.app.NotificationUtil
import com.craftrom.manager.utils.updater.repository.SelfUpdateRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

	private val updatesRepository: SelfUpdateRepository by inject()
	private val notificationUtil: NotificationUtil by inject()

	override fun onReceive(context: Context, intent: Intent?): Unit = runBlocking {
		updatesRepository.checkForUpdatesAsync(context as Activity).await().fold(
			onSuccess = {
				notificationUtil.showUpdateNotification()
			},
			onFailure = { Log.e("AlarmReceiver", "onReceive", it) }
		)
	}

}