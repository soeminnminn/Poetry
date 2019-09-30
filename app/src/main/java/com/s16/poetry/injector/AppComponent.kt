package com.s16.poetry.injector

import android.app.Application
import com.s16.poetry.MainApp
import com.s16.poetry.injector.modules.ActivityModule
import com.s16.poetry.injector.modules.DatabaseModule
import com.s16.poetry.injector.modules.ViewModelModule
import dagger.Component
import javax.inject.Singleton
import dagger.BindsInstance

// https://android.jlelse.eu/7-steps-to-implement-dagger-2-in-android-dabc16715a3a

@Singleton
@Component(modules = [ DatabaseModule::class, ViewModelModule::class, ActivityModule::class ])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(app: MainApp)
}