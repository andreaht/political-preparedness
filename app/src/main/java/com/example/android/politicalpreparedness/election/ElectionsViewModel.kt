package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    //TODO: Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
            get() = _upcomingElections

    //TODO: Create live data val for saved elections

    init{
        getElections()
    }

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database

    private fun getElections() {
        viewModelScope.launch {
            // Try statement is used to catch Network Exceptions so the app does not
            // crash when attempting to load without a network connection
            try {
                _upcomingElections.value = CivicsApi.retrofitService.getElections().elections
            } catch (e: Exception) {
                _upcomingElections.value = ArrayList()
                Timber.e(e)
            }
        }
    }

    //TODO: Create functions to navigate to saved or upcoming election voter info

}