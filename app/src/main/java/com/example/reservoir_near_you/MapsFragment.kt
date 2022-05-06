package com.example.reservoir_near_you

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.example.reservoir_near_you.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapsBinding



    private val callback = OnMapReadyCallback { googleMap ->

        val narvik = LatLng(68.438499, 17.427261)
        googleMap.addMarker(MarkerOptions().position(narvik).title("Marker in Narvik"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(narvik, 15f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        getUserLocation()

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_maps,
            container,
            false
        )
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible= false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.login_logout -> {
                AuthUI.getInstance().signOut(requireContext())
                var action = MapsFragmentDirections.actionMapsFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }
    @SuppressLint("MissingPermission")
    private fun getUserLocation() {

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    Log.d("wow", location?.latitude.toString())
                    Log.d("wow1", location?.longitude.toString())
                }
            } else {
                // Do otherwise
            }
        }

        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}