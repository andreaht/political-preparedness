package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDao

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
@Suppress("UNCHECKED_CAST")
class ElectionsViewModelFactory(private val dataSource: ElectionDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ElectionsViewModel(dataSource) as T)
}