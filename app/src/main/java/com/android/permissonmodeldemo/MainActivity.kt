package com.android.permissonmodeldemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    lateinit var preEdit: SharedPreferences.Editor
    lateinit var sp: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPrefs()
       /* requestForPermissions(Manifest.permission.WRITE_CALENDAR) {
            Log.e("LOG_PER: ", "GRANTED")
        }*/
        checkCameraPermission(Manifest.permission.CAMERA) {
            Log.e("LOG_PER: ", "GRANTED_NEW")
        }
    }

    private fun checkCameraPermission(permission: String, onGranted: () -> Unit) {
        val checkPermission = checkSelfPermission(permission)
        
        when{
            checkPermission == PackageManager.PERMISSION_GRANTED ->{
                // Already Granted & Do your work.
                onGranted.invoke()
            }
            shouldShowRequestPermissionRationale(permission) ->{
                // Denied & not checked never show again.
                // Show a alert message and ask for the permission.
            }
            getSharedPreferences("MY_APP", MODE_PRIVATE).getBoolean(permission,false)->{
                // Denied & Checked never show again. Show a alert/snack bar -> navigate to app settings to enable permissions.
            }
            else ->{
                // First time.
                getSharedPreferences("MY_APP", MODE_PRIVATE).edit().putBoolean(permission,true).apply()
                requestPermissions(arrayOf(permission),100)
            }
        }
    }

    
    private fun requestForPermissions(writeCalendar: String, onGranted: () -> Unit) {
        val permissionCheck = checkSelfPermission(writeCalendar)
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (shouldShowRequestPermissionRationale(writeCalendar)) {
                requestPermissions(arrayOf(writeCalendar), 101)
            } else {
                if (sp.getBoolean(writeCalendar, false)) {
                    AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("Permission needed to move further")
                        .setPositiveButton("Okay") { _, _ ->
                            val intent = Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.parse("package:$packageName")
                            }
                            startActivity(intent)
                        }.show()
                    Log.e("LOG_PER: ", "Denied for ever")
                } else {
                    savePermPref(writeCalendar)
                    requestPermissions(arrayOf(writeCalendar), 101)
                }
            }
        } else {
            onGranted.invoke()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun initPrefs() {
        sp = getSharedPreferences("PERM_DEMO", MODE_PRIVATE)
        preEdit = sp.edit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.e("LOG_PER: ", "Granted onRequestPermissionsResult")
                else{
                    Log.e("LOG_PER: ", "Denied onRequestPermissionsResult")
                }
            }
        }
    }

    private fun savePermPref(key: String) {
        preEdit.putBoolean(key, true)
        preEdit.commit()
    }
}

