package com.example.reservoir_near_you.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMapsBinding
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModelFactories.MagasinViewModelFactory
import com.example.reservoir_near_you.viewModels.MagasinViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var viewModel: MagasinViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    var mode = "light"

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
            Toast.makeText(requireContext(), "Du har gitt oss tillatelse til å bruke din posisjon", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Beklager, du har valgt å ikke dele din posisjon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = Repository()
        val viewModelFactory = MagasinViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getAllMagasin()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_maps,
            container,
            false
        )

        setHasOptionsMenu(true)
        binding.magasinViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        loadSettings()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
        menu.findItem(R.id.login).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
                val action = MapsFragmentDirections.actionMapsFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            R.id.settings -> {
                val action = MapsFragmentDirections.actionMapsFragmentToSettingsFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (mode == "dark"){
            mMap.setMapStyle(
                MapStyleOptions(
                    resources
                        .getString(R.string.style_json)
                )
            )
        }

        viewModel.allMagasinResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful){
                for (i in 0 until response.body()?.Magasin!!.size){
                    val place = LatLng(response.body()!!.Magasin[i].latitude, response.body()!!.Magasin[i].longitude)
                    mMap.addMarker(MarkerOptions()
                        .position(place)
                        .title(response.body()!!.Magasin[i].name))
                }
            }
        })

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            val markerPos = LatLng(location!!.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                .target(markerPos)
                .zoom(10f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        val reservoirName = marker.title as String
        val action = MapsFragmentDirections.actionMapsFragmentToMagasinFragment(reservoirName)
        view?.findNavController()?.navigate(action)
    }

    private fun loadSettings() {
        val sp = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val dark_mode = sp?.getBoolean("dark_mode", false)

        if (dark_mode == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            mode = "dark"
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            mode = "light"
        }
    }
}