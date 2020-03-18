package com.s16.poetry.data

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData

class CategoryModel(application: Application) : AndroidViewModel(application) {
    private val dbManager = DbManager(application)
    private val provider = dbManager.provider()
    val data: LiveData<List<Category>> = provider.listCategories()
}

class TagsModel(application: Application) : AndroidViewModel(application) {
    private val dbManager = DbManager(application)
    private val provider = dbManager.provider()
    val data: LiveData<List<Tags>> = provider.listTags()
}

class RecordPagedModel(application: Application) : AndroidViewModel(application) {
    private val dbManager = DbManager(application)
    private val provider = dbManager.provider()
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

class DetailsModel(application: Application) : AndroidViewModel(application) {
    private val dbManager = DbManager(application)
    private val provider = dbManager.provider()
    private val idData = MutableLiveData<Long>()

    val data: LiveData<DetailRecord> = Transformations.switchMap(idData) { id ->
        provider.getDetailRecord(id)
    }

    fun get(id: Long): LiveData<DetailRecord> {
        idData.value = id
        return data
    }
}