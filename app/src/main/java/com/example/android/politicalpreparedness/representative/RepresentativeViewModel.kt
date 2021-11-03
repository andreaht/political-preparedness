package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel : ViewModel() {

    //Establish live data for representatives and address
    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    //Create function to fetch representatives from API from a provided address
    fun findRepresentatives() {
        Timber.i("findRepresentatives: %s", address.value)
        viewModelScope.launch {
            // Try statement is used to catch Network Exceptions so the app does not
            // crash when attempting to load without a network connection
            try {
                val addressString = address.value?.toFormattedString()
                if (addressString != null) {
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(
                        addressString
                    )
                    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                    Timber.i("Representatives: %s", _representatives.value)
                }

            } catch (e: Exception) {
                _representatives.value = ArrayList()
                Timber.e(e)
            }
        }
    }

    //Create function get address from geo location
    //Create function to get address from individual fields
    fun setAddress(address: Address) {
        Timber.i("Address: %s", address)
        _address.value = address
    }

}
