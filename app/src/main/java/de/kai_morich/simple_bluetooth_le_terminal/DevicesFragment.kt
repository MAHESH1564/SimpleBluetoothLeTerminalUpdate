package de.kai_morich.simple_bluetooth_le_terminal

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import java.util.Collections

/**
 * show list of BLE devices
 */
class DevicesFragment : ListFragment() {
    private enum class ScanState {
        NONE, LE_SCAN, DISCOVERY, DISCOVERY_FINISHED
    }

    private var scanState = ScanState.NONE
    private val leScanStopHandler = Handler()
    private val leScanCallback: LeScanCallback
    private val leScanStopCallback: Runnable
    private val discoveryBroadcastReceiver: BroadcastReceiver
    private val discoveryIntentFilter: IntentFilter
    private var menu: Menu? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val listItems = ArrayList<BluetoothDevice?>()
    private var listAdapter: ArrayAdapter<BluetoothDevice>? = null
    var requestBluetoothPermissionLauncherForStartScan: ActivityResultLauncher<Array<String>?>
    var requestLocationPermissionLauncherForStartScan: ActivityResultLauncher<String>

    init {
        leScanCallback = LeScanCallback { device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray? ->
            if (device != null && activity != null) {
                requireActivity().runOnUiThread { updateScan(device) }
            }
        }
        discoveryBroadcastReceiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {
                if (BluetoothDevice.ACTION_FOUND == intent.action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device!!.type != BluetoothDevice.DEVICE_TYPE_CLASSIC && activity != null) {
                        activity!!.runOnUiThread { updateScan(device) }
                    }
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == intent.action) {
                    scanState = ScanState.DISCOVERY_FINISHED // don't cancel again
                    stopScan()
                }
            }
        }
        discoveryIntentFilter = IntentFilter()
        discoveryIntentFilter.addAction(BluetoothDevice.ACTION_FOUND)
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        leScanStopCallback = Runnable { stopScan() } // w/o explicit Runnable, a new lambda would be created on each postDelayed, which would not be found again by removeCallbacks
        requestBluetoothPermissionLauncherForStartScan = registerForActivityResult<Array<String>?, Map<String, Boolean>>(
                ActivityResultContracts.RequestMultiplePermissions(),
                ActivityResultCallback<Map<String, Boolean>> { granted: Map<String, Boolean> -> BluetoothUtil.onPermissionsResult(this, granted) { startScan() } })
        requestLocationPermissionLauncherForStartScan = registerForActivityResult<String, Boolean>(
                ActivityResultContracts.RequestPermission()
        ) { granted: Boolean ->
            if (granted) {
                Handler(Looper.getMainLooper()).postDelayed({ startScan() }, 1) // run after onResume to avoid wrong empty-text
            } else {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(getText(R.string.location_permission_title))
                builder.setMessage(getText(R.string.location_permission_denied))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        listAdapter = object : ArrayAdapter<BluetoothDevice?>(activity!!, 0, listItems) {
            override fun getView(position: Int, view: View?, parent: ViewGroup): View {
                var view = view
                val device = listItems[position]
                if (view == null) view = requireActivity().layoutInflater.inflate(R.layout.device_list_item, parent, false)
                val text1 = view!!.findViewById<TextView>(R.id.text1)
                val text2 = view.findViewById<TextView>(R.id.text2)
                @SuppressLint("MissingPermission") var deviceName = device!!.name
                if (deviceName == null || deviceName.isEmpty()) deviceName = "<unnamed>"
                text1.text = deviceName
                text2.text = device.address
                return view
            }
        }
        /*if(bluetoothAdapter == null) {
            setEmptyText("<bluetooth LE not supported>");
        } else if(!bluetoothAdapter.isEnabled()) {
            if(menu!=null)
                menu.findItem(R.id.ble_scan).setEnabled(false);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            setEmptyText("<bluetooth is disabled>");
            if (menu != null) {
                listItems.clear();
                listAdapter.notifyDataSetChanged();
                menu.findItem(R.id.ble_scan).setEnabled(false);
            }
        } else {
            setEmptyText("<use SCAN to refresh devices>");
            if (menu != null)
                menu.findItem(R.id.ble_scan).setEnabled(true);
        }*/
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setListAdapter(null)
        val header = requireActivity().layoutInflater.inflate(R.layout.device_list_header, null, false)
        listView.addHeaderView(header, null, false)
        setEmptyText("initializing...")
        (listView.emptyView as TextView).textSize = 18f
        setListAdapter(listAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_devices, menu)
        this.menu = menu
        if (bluetoothAdapter == null) {
            menu.findItem(R.id.bt_settings).isEnabled = false
            menu.findItem(R.id.ble_scan).isEnabled = false
        } else if (!bluetoothAdapter!!.isEnabled) {
            menu.findItem(R.id.ble_scan).isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(discoveryBroadcastReceiver, discoveryIntentFilter)
        if (bluetoothAdapter == null) {
            setEmptyText("<bluetooth LE not supported>")
        } else if (!bluetoothAdapter!!.isEnabled) {
            if (menu != null) menu!!.findItem(R.id.ble_scan).isEnabled = false
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            setEmptyText("<bluetooth is disabled>")
            if (menu != null) {
                listItems.clear()
                listAdapter!!.notifyDataSetChanged()
                menu!!.findItem(R.id.ble_scan).isEnabled = false
            }
        } else {
            setEmptyText("<use SCAN to refresh devices>")
            if (menu != null) menu!!.findItem(R.id.ble_scan).isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        stopScan()
        requireActivity().unregisterReceiver(discoveryBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        menu = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.ble_scan) {
            startScan()
            true
        } else if (id == R.id.ble_scan_stop) {
            stopScan()
            true
        } else if (id == R.id.bt_settings) {
            val intent = Intent()
            intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
            startActivity(intent)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun startScan() {
        if (scanState != ScanState.NONE) return
        val nextScanState = ScanState.LE_SCAN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!BluetoothUtil.hasPermissions(this, requestBluetoothPermissionLauncherForStartScan)) return
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                scanState = ScanState.NONE
                val builder = AlertDialog.Builder(activity)
                builder.setTitle(R.string.location_permission_title)
                builder.setMessage(R.string.location_permission_grant)
                builder.setPositiveButton(android.R.string.ok
                ) { dialog: DialogInterface?, which: Int -> requestLocationPermissionLauncherForStartScan.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                builder.show()
                return
            }
            val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var locationEnabled = false
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ignored: Exception) {
            }
            try {
                locationEnabled = locationEnabled or locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ignored: Exception) {
            }
            if (!locationEnabled) {
            }
            // Starting with Android 6.0 a bluetooth scan requires ACCESS_COARSE_LOCATION permission, but that's not all!
            // LESCAN also needs enabled 'location services', whereas DISCOVERY works without.
            // Most users think of GPS as 'location service', but it includes more, as we see here.
            // Instead of asking the user to enable something they consider unrelated,
            // we fall back to the older API that scans for bluetooth classic _and_ LE
            // sometimes the older API returns less results or slower
        }
        scanState = nextScanState
        listItems.clear()
        listAdapter!!.notifyDataSetChanged()
        setEmptyText("<scanning...>")
        menu!!.findItem(R.id.ble_scan).isVisible = false
        menu!!.findItem(R.id.ble_scan_stop).isVisible = true
        if (scanState == ScanState.LE_SCAN) {
            leScanStopHandler.postDelayed(leScanStopCallback, LE_SCAN_PERIOD)
            Thread({ bluetoothAdapter!!.startLeScan(null, leScanCallback) }, "startLeScan")
                    .start() // start async to prevent blocking UI, because startLeScan sometimes take some seconds
        } else {
            bluetoothAdapter!!.startDiscovery()
        }
    }

    private fun updateScan(device: BluetoothDevice?) {
        if (scanState == ScanState.NONE) return
        if (!listItems.contains(device)) {
            listItems.add(device)
            Collections.sort(listItems) { obj: BluetoothDevice?, a: BluetoothDevice? -> BluetoothUtil.compareTo(a!!) }
            listAdapter!!.notifyDataSetChanged()
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        if (scanState == ScanState.NONE) return
        setEmptyText("<no bluetooth devices found>")
        if (menu != null) {
            menu!!.findItem(R.id.ble_scan).isVisible = true
            menu!!.findItem(R.id.ble_scan_stop).isVisible = false
        }
        when (scanState) {
            ScanState.LE_SCAN -> {
                leScanStopHandler.removeCallbacks(leScanStopCallback)
                bluetoothAdapter!!.stopLeScan(leScanCallback)
            }

            ScanState.DISCOVERY -> bluetoothAdapter!!.cancelDiscovery()
            else -> {}
        }
        scanState = ScanState.NONE
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        stopScan()
        val device = listItems[position - 1]
        val args = Bundle()
        args.putString("device", device!!.address)
        val txt: Fragment = InsertName(args)
        requireFragmentManager().beginTransaction().replace(R.id.fragment, txt, "filename").addToBackStack(null).commit()
        /*Fragment fragment = new TerminalFragment();
        fragment.setArguments(args);
        requireFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit();*/
    }

    companion object {
        private const val REQUEST_ENABLE_BT = 99
        private const val LE_SCAN_PERIOD: Long = 10000 // similar to bluetoothAdapter.startDiscovery
    }
}