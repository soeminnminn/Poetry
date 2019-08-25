package com.s16.poetry.data

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData

class CategoryModel(application: Application) : AndroidViewModel(application) {
    private var provider = DbManager(application).provider()
    val categories: LiveData<List<Category>> = provider.listCategories()
}

class TagsModel(application: Application) : AndroidViewModel(application) {
    private var provider = DbManager(application).provider()
    val tags: LiveData<List<Tags>> = provider.listTags()
}

class RecordPagedModel(application: Application) : AndroidViewModel(application) {
    private var provider = DbManager(application).provider()

    private var records: LiveData<PagedList<Record>> = MutableLiveData()
    private var allRecords: LiveData<PagedList<Record>>

    val filterData: MediatorLiveData<PagedList<Record>> = MediatorLiveData()

    init {
        allRecords = provider.listPagedRecords().toLiveData(PAGE_SIZE)
        filterData.addSource(allRecords) {
            filterData.value = it
        }
    }

    fun filter(category: String?): RecordPagedModel {
        if (category == null || category.isEmpty()) {
            filterData.removeSource(records)
            allRecords = provider.listPagedRecords().toLiveData(PAGE_SIZE)
            filterData.addSource(allRecords) {
                filterData.value = it
            }
        } else {
            filterData.removeSource(allRecords)
            records = provider.listPagedRecordsByCategory(category).toLiveData(PAGE_SIZE)
            filterData.addSource(records) {
                filterData.value = it
            }
        }
        return this
    }

    companion object {
        const val PAGE_SIZE = 50
    }
}

@Suppress("UNCHECKED_CAST")
class DetailsModelFactory(private val application: Application, val id: Long) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailsModel(application, id) as T
    }
}

class DetailsModel(application: Application, id: Long) : AndroidViewModel(application) {
    private var provider = DbManager(application).provider()
    val data: LiveData<DetailRecord> = provider.getDetailRecord(id)
}