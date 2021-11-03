package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(
    private val dataSource: ElectionDao,
    private val electionId: Int,
    private val division: Division) : ViewModel() {

    //Add live data to hold voter info
    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo : LiveData<VoterInfoResponse>
        get() = _voterInfo

    private val _url = MutableLiveData<String>()
    val url : LiveData<String>
        get() = _url

    //Add var and methods to populate voter info
    init {
        getVoterInfo()
    }

    //Add var and methods to support loading URLs
    private fun getVoterInfo() {
        viewModelScope.launch {
            // Try statement is used to catch Network Exceptions so the app does not
            // crash when attempting to load without a network connection
            try {
                var address = "";
                if (division.state.isNotBlank()) address = division.state
                if (division.state.isNotBlank() && division.country.isNotBlank()) address += ", "
                if (division.country.isNotBlank()) address += division.country

                _voterInfo.value = CivicsApi.retrofitService.getVoterInfo(address, electionId)
            } catch (e: Exception) {
                _voterInfo.value = null
                Timber.e(e)
            }
        }
    }


    // Populate initial state of save button to reflect proper action based on election saved status
    val isElectionFollowed = dataSource.exists(electionId)

    //Add var and methods to save and remove elections to local database
    fun onFollowButtonClick() {
        viewModelScope.launch {
            if (isElectionFollowed.value == true)
                dataSource.deleteByElectionId(electionId)
            else
                voterInfo.value?.let { dataSource.insert(it.election) }
        }
    }

    fun setUrl (url: String?) {
        _url.value = url
    }
}