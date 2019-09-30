package com.s16.poetry.injector.modules

import android.app.Application
import com.s16.poetry.data.DataProvider
import com.s16.poetry.data.DbManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDbManager(application: Application): DbManager = DbManager(application)

    @Provides
    @Singleton
    internal fun provideDataProvider(dbManager: DbManager): DataProvider {
        return dbManager.provider()
    }
}