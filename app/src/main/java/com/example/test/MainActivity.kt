package com.example.test

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

 class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap:GoogleMap
    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        initViews()
    }
    override fun onMapReady( googleMap: GoogleMap) {

        mMap = googleMap
        mMap.addMarker(
            MarkerOptions().position(LatLng(54.72338163577925, 25.337885873885817)).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            )
        )
        val initialPosition = LatLng(54.72338163577925, 25.337885873885817)
        val zoomLevel = 15.0f //
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, zoomLevel))
        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if(isGranted){
            showCamera()
        }
    }

    private val scanLauncher = registerForActivityResult(ScanContract()) {
            result: ScanIntentResult ->
            run {
                if(result.contents == null) {
                    Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(this@MainActivity, PaymentActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }


    private fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
    private fun showCamera(){
        val options = ScanOptions()

        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")

        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(true)

        scanLauncher.launch(options)
    }





    private fun initViews() {
        binding.scanQrCode.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showCamera()
            }
            else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){
                Toast.makeText(this, "CAMERA permission required",Toast.LENGTH_SHORT).show()
            }
            else{
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
}