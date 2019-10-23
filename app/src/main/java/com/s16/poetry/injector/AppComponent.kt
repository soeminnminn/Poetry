package com.s16.poetry.injector

import android.app.Application
import com.s16.poetry.MainApp
import com.s16.poetry.injector.modules.ActivityModule
import com.s16.poetry.injector.modules.DatabaseModule
import com.s16.poetry.injector.modules.ViewModelModule
import dagger.Component
import javax.inject.Singleton
import dagger.BindsInstance
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

// https://android.jlelse.eu/7-steps-to-implement-dagger-2-in-android-dabc16715a3a

@Singleton
@Component(modules = [ AndroidInjectionModule::class, DatabaseModule::class, ViewModelModule::class, ActivityModule::class ])
interface AppComponent : AndroidInjector<MainApp> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(app: MainApp)
}