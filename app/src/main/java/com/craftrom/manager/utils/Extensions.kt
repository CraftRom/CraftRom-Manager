package com.craftrom.manager.utils

import android.content.Context
import android.content.pm.PackageInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

fun PackageInfo.name(context: Context) = applicationInfo.loadLabel(context.packageManager).toString()

val ioScope = CoroutineScope(Dispatchers.IO)

val uiScope = CoroutineScope(Dispatchers.Main)

fun <T> CoroutineScope.catchingAsync(block: suspend () -> T): Deferred<Result<T>> = ioScope.async { runCatching { block() } }

fun Boolean?.orFalse() = this ?: false