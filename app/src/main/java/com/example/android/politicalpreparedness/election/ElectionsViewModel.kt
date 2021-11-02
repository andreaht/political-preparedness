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

//Construct ViewModel and provide election datasource
class ElectionsViewModel(private val dataSource: ElectionDao): ViewModel() {

    // Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections : LiveData<List<Election>>
            get() = _upcomingElections

    //Create live data val for saved elections
    val savedElections = dataSource.getAllElections()

    init{
        getElections()
    }

    //Create val and functions to populate live data for upcoming elections from the API
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

    //Create functions to navigate to saved or upcoming election voter info
    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo
        get() = _navigateToVoterInfo

    fun onElectionClicked(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun doneNavigating() {
        _navigateToVoterInfo.value = null
    }


}