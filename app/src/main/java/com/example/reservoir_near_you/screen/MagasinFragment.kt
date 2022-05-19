package com.example.reservoir_near_you.screen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMagasinBinding
import com.example.reservoir_near_you.model.Magasin
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModelFactories.MagasinViewModelFactory
import com.example.reservoir_near_you.viewModels.MagasinViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MagasinFragment : Fragment() {

    private lateinit var viewModel: MagasinViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMagasinBinding

    val args: MagasinFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_magasin,
            container,
            false
        )

        setHasOptionsMenu(true)
        val repository = Repository()
        val viewModelFactory = MagasinViewModelFactory(repository)
        val magasinId = args.magasinId
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getMagasin(magasinId)
        viewModel.allMagasinResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful){
                response.body()?.let { binding.textView.text = it.toString() }
            }
        })

        binding.magasinViewModel = viewModel

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.map -> {
                val action = MagasinFragmentDirections.actionMagasinFragmentToMapsFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            R.id.login_logout -> {
                AuthUI.getInstance().signOut(requireContext())
                val action = MagasinFragmentDirections.actionMagasinFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }

}