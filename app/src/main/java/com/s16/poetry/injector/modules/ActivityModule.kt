package com.s16.poetry.injector.modules

import com.s16.poetry.activity.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeDetailsActivity(): DetailsActivity

    @ContributesAndroidInjector()
    abstract fun contributeManageCategoriesActivity(): ManageCategoriesActivity

    @ContributesAndroidInjector()
    abstract fun contributeManageTagsActivity(): ManageTagsActivity

    @ContributesAndroidInjector()
    abstract fun contributeSelectCategoryActivity(): SelectCategoryActivity

    @ContributesAndroidInjector()
    abstract fun contributeSelectTagActivity(): SelectTagActivity
}