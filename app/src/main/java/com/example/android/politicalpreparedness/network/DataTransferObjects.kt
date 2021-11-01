package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.models.Election
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkElection(
    val elections : List<Election>,
    val kind : String
)