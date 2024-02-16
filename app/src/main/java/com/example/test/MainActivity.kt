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

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if(isGranted){
            showCamera()
        }


    }


    private val scanLauncher = registerForActivityResult(ScanContract()){

            result: ScanIntentResult ->
            run{
                if(result.contents == null){
                    Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
                }
                else{
                    setResult(result.contents)
                }
            }


    }



    private fun setResult(string: String){
        binding.textView.text = string
    }



    private lateinit var binding: ActivityMainBinding
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





    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        initBinding()
        initViews()
    }


    private fun initViews(){
        binding.floatingActionButton.setOnClickListener{
            checkPermissionCamera(this)
        }

    }

    private fun checkPermissionCamera(context: Context){
        if(ContextCompat.checkSelfPermission(context,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            showCamera()
        else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)){

            Toast.makeText(context, "CAMERA permission required",Toast.LENGTH_SHORT).show()
        }
        else{
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }



    private fun initBinding(){
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



}