package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class DetailFragment : Fragment() {

    companion object {
        //Add Constant for Location request
        const val PERMISSION_REQUEST_CODE = 475
    }


    private val viewModel by viewModels<RepresentativeViewModel>()
    private lateinit var binding: FragmentRepresentativeBinding

    private var snackbar: Snackbar? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRepresentativeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_representative, container, false)

        //Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        //Establish button listeners for field and location search
        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            if (checkLocationPermissions())
                getLocation()
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            val address = Address(
                binding.addressLine1.text.toString(),
                binding.addressLine2.text.toString(),
                binding.city.text.toString(),
                binding.state.selectedItem.toString(),
                binding.zip.text.toString()
            )/*
            val address =
                Address("Amphitheatre Parkway", "1600", "Mountain View", "California", "94043")*/
            viewModel.setAddress(address)
        }

        //observe address changes and searche new representatives to update recycler view
        viewModel.address.observe(viewLifecycleOwner, { t ->
            t?.let {
                viewModel.findRepresentatives()
            }
        })

        //Define and assign Representative adapter
        //Populate Representative adapter
        binding.representativesList.adapter = RepresentativeListAdapter()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //Handle location permission result to get location on permission granted

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission is granted. Continue the action or workflow
                    getLocation()
                } else {
                    // Permission denied.
                    snackbar = Snackbar.make(
                        binding.root,
                        R.string.permission_denied_explanation, Snackbar.LENGTH_SHORT
                    )
                    snackbar?.setAction(R.string.settings) {
                        // Displays App settings screen.
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }?.show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    //Check if permission is already granted and return (true = granted, false = denied/other)
    private fun checkLocationPermissions(): Boolean {
        return when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                // You can use the API that requires the permission.
                true
            }
            else -> {
                // You can directly ask for the permission.
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    PERMISSION_REQUEST_CODE
                )
                false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //Get location from LocationServices
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                val address = location?.let { geoCodeLocation(it) }
                address?.let {
                    viewModel.setAddress(address)
                }
            }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}