package com.s16.poetry.injector.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.s16.poetry.data.*
import com.s16.poetry.injector.ViewModelFactory
import com.s16.poetry.injector.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CategoryModel::class)
    protected abstract fun categoryModel(viewModel: CategoryModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TagsModel::class)
    protected abstract fun tagsModel(viewModel: TagsModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RecordPagedModel::class)
    protected abstract fun recordPagedModel(viewModel: RecordPagedModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsModel::class)
    protected abstract fun detailsModel(viewModel: DetailsModel): ViewModel
}