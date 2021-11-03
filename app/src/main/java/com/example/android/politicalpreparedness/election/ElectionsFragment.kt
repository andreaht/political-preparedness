package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import timber.log.Timber

class ElectionsFragment: Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    private val viewModel : ElectionsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val application = activity.application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val viewModelFactory = ElectionsViewModelFactory(dataSource)

        ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_election, container, false)

        //Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        //Link elections to voter info
        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, { election ->
            election?.let {

                this.findNavController().navigate(
                    ElectionsFragmentDirections
                        .actionElectionsFragmentToVoterInfoFragment(election.id,election.division))
                viewModel.doneNavigating()
                Timber.i("doneNavigating: %s", it.name)
            }
        })

        //Initiate recycler adapters
        //Populate recycler adapters
        binding.upcomingElectionsList.adapter = ElectionListAdapter(ElectionListener {
            // When an election is clicked this block or lambda will be called by ElectionAdapter
            Timber.i("election clicked: %s", it.name)
            viewModel.onElectionClicked(it)
        })

        binding.savedElectionsList.adapter = ElectionListAdapter(ElectionListener {
            // When an election is clicked this block or lambda will be called by ElectionAdapter
            Timber.i("election clicked: %s", it.name)
            viewModel.onElectionClicked(it)
        })

        return binding.root
    }


}