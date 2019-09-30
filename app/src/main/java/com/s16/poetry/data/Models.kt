package com.s16.poetry.data

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import javax.inject.Inject

class CategoryModel @Inject constructor(provider: DataProvider): ViewModel() {
    val data: LiveData<List<Category>> = provider.listCategories()
}

class TagsModel @Inject constructor(provider: DataProvider): ViewModel() {
    val data: LiveData<List<Tags>> = provider.listTags()
}

class RecordPagedModel @Inject constructor(private val provider: DataProvider): ViewModel() {
    private val filterData = MutableLiveData<String?>()

    val data: LiveData<PagedList<Record>> = Transformations.switchMap(filterData) { category ->
        if (category == null || category.isEmpty()) {
            provider.listPagedRecords().toLiveData(PAGE_SIZE)
        } else {
            provider.listPagedRecordsByCategory(category).toLiveData(PAGE_SIZE)
        }
    }

    init {
        filterData.value = null
    }

    fun filter(category: String?) {
        filterData.value = category
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}

class DetailsModel @Inject constructor(private val provider: DataProvider): ViewModel() {
    private val idData = MutableLiveData<Long>()

    val data: LiveData<DetailRecord> = Transformations.switchMap(idData) { id ->
        provider.getDetailRecord(id)
    }

    fun get(id: Long): LiveData<DetailRecord> {
        idData.value = id
        return data
    }
}