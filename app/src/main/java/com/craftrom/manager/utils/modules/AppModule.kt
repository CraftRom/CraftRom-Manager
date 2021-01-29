package com.crazylegend.vigilante.di.modules

import android.content.Context
import com.craftrom.manager.utils.security.MagiskDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by crazy on 11/1/20 to long live and prosper !
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun magiskDetector(@ApplicationContext context: Context) = MagiskDetector(context)
}