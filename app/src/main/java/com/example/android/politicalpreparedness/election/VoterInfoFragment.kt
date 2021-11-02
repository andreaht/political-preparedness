package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import timber.log.Timber

class VoterInfoFragment : Fragment() {

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onViewCreated(), which we
     * do in this Fragment.
     */
    //Add ViewModel values and create ViewModel
    private val viewModel : VoterInfoViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        val application = activity.application
        val dataSource = ElectionDatabase.getInstance(application).electionDao
        val arguments = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = VoterInfoViewModelFactory(dataSource, arguments.argElectionId,
            arguments.argDivision)

        ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentVoterInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_voter_info, container, false)

        //Add binding values
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        //Handle loading of URLs
        viewModel.url.observe(viewLifecycleOwner, Observer { url ->
            url?.let {
                viewModel.setUrl(null)
                //load URL intents
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                Timber.i("done open page: %s", url)
            }
        })

        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root

    }


}