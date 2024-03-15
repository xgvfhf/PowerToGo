package com.example.test

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.test.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
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
                    //setResult(result.contents)
                }
            }
    }

    /*
    private fun setResult(string: String){
        binding.textView.text = string
    }

     */

    private fun showCamera(){
        val options = ScanOptions()

        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setBarcodeImageEnabled(true)
        options.setOrientationLocked(false)

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