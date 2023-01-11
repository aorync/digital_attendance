package com.syntxr.digitalattendance

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.syntxr.digitalattendance.`object`.Objects
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        val api = Objects.create()
        val userId = arguments?.getInt("usersId")
        val id = arguments?.getInt("id")
        lifecycleScope.launch{
            try {
                val rensponse = api.getUserPosDetail("eq.$id","*",)
                if (rensponse.isNotEmpty()){
                    val posIn = LatLng(rensponse.first().latitudeIn, rensponse.first().longitudeIn)
                    googleMap.addMarker(MarkerOptions().position(posIn).title("Check In ${rensponse.first().name}"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(posIn))

                    val posOut = LatLng(rensponse.first().latitudeOut,rensponse.first().longitudeOut)
                    googleMap.addMarker(MarkerOptions().position(posOut).title("Check Out ${rensponse.first().name}"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(posOut))
                    return@launch
                }
                findNavController().navigate(R.id.action_mapsFragment_to_history)
            }catch (e :Exception){
                Toast.makeText(requireContext(), "error ${e.message}", Toast.LENGTH_SHORT).show()
            }


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}