package com.example.reservoir_near_you.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMapsBinding
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModelFactories.MagasinViewModelFactory
import com.example.reservoir_near_you.viewModels.MagasinViewModel
import com.example.reservoir_near_you.viewModels.MapViewModel
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
import retrofit2.Response


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private lateinit var viewModel: MagasinViewModel
    private lateinit var mapViewModel: MapViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mMap: GoogleMap
    private var locationHashMap: HashMap<LatLng, String> = HashMap<LatLng, String>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
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
        val repository = Repository()
        binding.lifecycleOwner = this
        val viewModelFactory = MagasinViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getAllMagasin()
        viewModel.allMagasinResponse.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful){
                for (i in 0 until response.body()?.Magasin!!.size){
                    val place = LatLng(response.body()!!.Magasin.get(i).latitude, response.body()!!.Magasin.get(i).longitude)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markers = mutableListOf<Marker>()
        var markerIndex = 0
        for ((key, value ) in locationHashMap) {
            mMap.addMarker(MarkerOptions()
                .position(key)
                .title(value))
                ?.let { markers.add(it) }
            markers[markerIndex].tag = markerIndex + 1
            markerIndex += 1
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            val markerPos = LatLng(location!!.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                .target(markerPos)
                .zoom(10f).build()
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnInfoWindowClickListener(this)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        return false
    }

    override fun onInfoWindowClick(marker: Marker) {
        val reservoirId = marker.tag as Int
        val action = MapsFragmentDirections.actionMapsFragmentToMagasinFragment(reservoirId)
        view?.findNavController()?.navigate(action)
    }
}