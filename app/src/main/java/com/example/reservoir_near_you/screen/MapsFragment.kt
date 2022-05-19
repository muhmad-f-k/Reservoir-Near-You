package com.example.reservoir_near_you.screen

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMapsBinding
import com.example.reservoir_near_you.model.Magasin
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {

    private lateinit var viewModel: MagasinViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    private var locationArrayList: ArrayList<LatLng> = ArrayList()
    private var locationHashMap: HashMap<LatLng, String> = HashMap<LatLng, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        getUserLocation()
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_maps,
            container,
            false
        )

        setHasOptionsMenu(true)
        val repository = Repository()
        val viewModelFactory = MagasinViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getAllMagasin()
        viewModel.allMagasinResponse.observe(viewLifecycleOwner, Observer { response ->
            if(response.isSuccessful){
                for (i in 0 until response.body()?.Magasin!!.size){
                    val place = LatLng(response.body()!!.Magasin.get(i).latitude, response.body()!!.Magasin.get(i).longitude)
                    locationArrayList.add(place)
                    locationHashMap.put(place, response.body()!!.Magasin.get(i).name)
                }
            }
        })
        binding.magasinViewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
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
                val action = MapsFragmentDirections.actionMapsFragmentToMainFragment()
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
            } else {
                Toast.makeText(requireContext(), "Beklager, du har valgt å ikke dele din posisjon", Toast.LENGTH_SHORT).show()
                val action = MapsFragmentDirections.actionMapsFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
            }
        }

        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            val markerPos = LatLng(location!!.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(markerPos))
            val cameraPosition = CameraPosition.Builder()
                .target(markerPos)
                .zoom(10f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            val markers = mutableListOf<Marker>()
            var markerIndex = 0
            for ((key, value ) in locationHashMap) {
                mMap.addMarker(MarkerOptions().position(key).title(value))?.let { markers.add(it) }
                markers[markerIndex].tag = markerIndex + 1
                markerIndex += 1
            }
            mMap.setOnMarkerClickListener(this)
        }
    }

    override fun onMapClick(p0: LatLng) {
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val reservoirId = marker.tag as Int
        val action = MapsFragmentDirections.actionMapsFragmentToMagasinFragment(reservoirId)
        view?.findNavController()?.navigate(action)
        return true
    }


}