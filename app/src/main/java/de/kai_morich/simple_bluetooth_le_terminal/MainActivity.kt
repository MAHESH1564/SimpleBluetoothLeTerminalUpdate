package de.kai_morich.simple_bluetooth_le_terminal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import java.util.Objects

class MainActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener, OnRequestPermissionsResultCallback {
    private var mLayout: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mLayout = findViewById(R.id.fragment)
        requestBluetoothPermissions()
        updateOrRequestPermission()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportFragmentManager.addOnBackStackChangedListener(this)
        if (savedInstanceState == null) supportFragmentManager.beginTransaction().add(R.id.fragment, DevicesFragment(), "devices").commit() else onBackStackChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 4) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout!!, "Bluetooth turned on",
                        Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                Snackbar.make(mLayout!!, "Bluetooth turned off",
                        Snackbar.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun requestBluetoothPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.BLUETOOTH_CONNECT)) {
            Snackbar.make(mLayout!!, "Need Bluetooth for scanning",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK") { view: View? ->
                // Request the permission
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        4)
            }.show()
        } else {
            Snackbar.make(mLayout!!, "Bluetooth unavailable", Snackbar.LENGTH_SHORT).show()
            // Request the permission. The result will be received in onRequestPermissionResult().
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 4)
            }
        }
    }

    private fun updateOrRequestPermission() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSDK29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        if (minSDK29) return
        val PermissionsTORequest = ArrayList<String>()
        if (!hasWritePermission) {
            PermissionsTORequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!hasReadPermission) {
            PermissionsTORequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!PermissionsTORequest.isEmpty()) {
            Snackbar.make(mLayout!!, "Need To store files",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK") { view: View? ->
                // Request the permission
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        40)
            }.show()
        }
    }

    override fun onBackStackChanged() {
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(supportFragmentManager.backStackEntryCount > 0)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}