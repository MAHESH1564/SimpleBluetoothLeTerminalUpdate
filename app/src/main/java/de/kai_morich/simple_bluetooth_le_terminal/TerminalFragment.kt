package de.kai_morich.simple_bluetooth_le_terminal

import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.opencsv.CSVWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.ArrayDeque
import java.util.Arrays
import java.util.Objects

class TerminalFragment : Fragment(), ServiceConnection, SerialListener {
    private enum class Connected {
        False, Pending, True
    }

    private var deviceAddress: String? = null
    private var service: SerialService? = null
    private var receiveText: TextView? = null
    private val sendText: TextView? = null
    private val hexWatcher: HexWatcher? = null
    private var connected = Connected.False
    private var initialStart = true
    private var hexEnabled = false
    private var pendingNewline = false
    private var newline: String? = TextUtil.newline_crlf
    private var file_counter = 0
    private var towhichfile = 0
    private var accel_data: CSVWriter? = null
    private var gyro_data: CSVWriter? = null
    private var ir_data: CSVWriter? = null

    /*
     * Lifecycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        assert(arguments != null)
        deviceAddress = arguments!!.getString("device")
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        requireActivity().stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service!!.attach(this) else requireActivity().startService(Intent(activity, SerialService::class.java)) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    override fun onStop() {
        if (service != null && !requireActivity().isChangingConfigurations) service!!.detach()
        super.onStop()
        try {
            accel_data.close()
            gyro_data.close()
            ir_data.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        send("stop")
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        requireActivity().bindService(Intent(getActivity(), SerialService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onDetach() {
        try {
            requireActivity().unbindService(this)
        } catch (ignored: Exception) {
        }
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        if (initialStart && service != null) {
            initialStart = false
            requireActivity().runOnUiThread { connect() }
            /*try {
                accel_data.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as SerialBinder).getService()
        service!!.attach(this)
        if (initialStart && isResumed) {
            initialStart = false
            requireActivity().runOnUiThread { connect() }
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }

    /*
     * UI
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_terminal, container, false)
        receiveText = view.findViewById<TextView>(R.id.receive_text) // TextView performance decreases with number of spans
        receiveText.setTextColor(resources.getColor(R.color.colorRecieveText)) // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance())
        val startbtn = view.findViewById<View>(R.id.startbtn)
        val stopbtn = view.findViewById<View>(R.id.stopbtn)
        startbtn.setOnClickListener { v: View? -> send("start") }
        stopbtn.setOnClickListener { v: View? -> send("stop") }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_terminal, menu)
        menu.findItem(R.id.hex).isChecked = hexEnabled
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return if (id == R.id.clear) {
            receiveText.setText("")
            true
        } else if (id == R.id.newline) {
            val newlineNames = resources.getStringArray(R.array.newline_names)
            val newlineValues = resources.getStringArray(R.array.newline_values)
            val pos = Arrays.asList(*newlineValues).indexOf(newline)
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Newline")
            builder.setSingleChoiceItems(newlineNames, pos, DialogInterface.OnClickListener { dialog: DialogInterface, item1: Int ->
                newline = newlineValues[item1]
                dialog.dismiss()
            })
            builder.create().show()
            true
        } else if (id == R.id.hex) {
            hexEnabled = !hexEnabled
            sendText.setText("")
            hexWatcher.enable(hexEnabled)
            sendText.setHint(if (hexEnabled) "HEX mode" else "")
            item.isChecked = hexEnabled
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /*
     * Serial + UI
     */
    private fun connect() {
        try {
            val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress)
            status("connecting...")
            connected = Connected.Pending
            val socket = SerialSocket(requireActivity().applicationContext, device)
            createFile()
            service!!.connect(socket)
        } catch (e: Exception) {
            onSerialConnectError(e)
        }
    }

    private fun createFile() {
        var req = false
        assert(arguments != null)
        val folder = File(requireActivity().getExternalFilesDir(Environment.MEDIA_SHARED)!!.path + "/" + arguments!!.getString("filename"))
        if (!folder.exists()) req = folder.mkdirs()
        if (!req && !folder.exists()) {
            Toast.makeText(service, "The storage location cannot be created. The app might crash", Toast.LENGTH_SHORT).show()
        }
        val accel: FileWriter
        val gyro: FileWriter
        val ir: FileWriter
        file_counter = (Objects.requireNonNull(folder.listFiles()).size / 3).toString().toInt()
        try {
            accel = FileWriter("$folder/accel_data$file_counter.csv")
            gyro = FileWriter("$folder/gyro_data$file_counter.csv")
            ir = FileWriter("$folder/ir_data$file_counter.csv")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        accel_data = CSVWriter(accel, '\u0000', '\u0000', '\u0000', "")
        gyro_data = CSVWriter(gyro, '\u0000', '\u0000', '\u0000', "")
        ir_data = CSVWriter(ir, '\u0000', '\u0000', '\u0000', "")
    }

    private fun disconnect() {
        connected = Connected.False
        service!!.disconnect()
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val msg: String
            val data: ByteArray?
            if (hexEnabled) {
                val sb = StringBuilder()
                toHexString(sb, TextUtil.fromHexString(str))
                toHexString(sb, newline!!.toByteArray())
                msg = sb.toString()
                data = TextUtil.fromHexString(msg)
            } else {
                msg = str
                data = (str + newline).toByteArray()
            }
            val spn = SpannableStringBuilder("""
    $msg
    
    """.trimIndent())
            spn.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorSendText)), 0, spn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            receiveText.append(spn)
            service!!.write(data)
        } catch (e: Exception) {
            onSerialIoError(e)
        }
    }

    private fun receive(datas: ArrayDeque<ByteArray>?) {
        val spn = SpannableStringBuilder()
        for (data in datas!!) {
            if (hexEnabled) {
                spn.append(toHexString(data)).append('\n')
            } else {
                var msg = String(data)
                if (newline == TextUtil.newline_crlf && msg.length > 0) {
                    // don't show CR as ^M if directly before LF
                    msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf)
                    // special handling if CR and LF come in separate fragments
                    if (pendingNewline && msg[0] == '\n') {
                        if (spn.length >= 2) {
                            spn.delete(spn.length - 2, spn.length)
                        } else {
                            val edt: Editable = receiveText.getEditableText()
                            if (edt != null && edt.length >= 2) edt.delete(edt.length - 2, edt.length)
                        }
                    }
                    pendingNewline = msg[msg.length - 1] == '\r'
                }
                spn.append(toCaretString(msg, newline!!.length != 0))
                writeToCsv(msg)
            }
        }
        receiveText.append(spn)
    }

    private fun writeToCsv(msg: String) {
        //towriteto = towriteto.replace(TextUtil.newline_lf,"");
        /*accel_data.writeNext(new String[] {towriteto});
        if (msg.charAt(msg.length()-1)=='\n') {
            towriteto = "";
        }*/
        when (towhichfile) {
            0 -> accel_data.writeNext(arrayOf(msg))
            1 -> gyro_data.writeNext(arrayOf(msg))
            2 -> ir_data.writeNext(arrayOf(msg))
        }
        val last = msg[msg.length - 1]
        if (msg[msg.length - 1] == '\n') {
            towhichfile = (towhichfile + 1) % 3
        }
    }

    private fun status(str: String) {
        val spn = SpannableStringBuilder("""
    $str
    
    """.trimIndent())
        spn.setSpan(ForegroundColorSpan(resources.getColor(R.color.colorStatusText)), 0, spn.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        receiveText.append(spn)
    }

    /*
     * SerialListener
     */
    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
    }

    override fun onSerialConnectError(e: Exception?) {
        status("connection failed: " + e!!.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray) {
        val datas = ArrayDeque<ByteArray>()
        datas.add(data)
        receive(datas)
    }

    override fun onSerialRead(datas: ArrayDeque<ByteArray>?) {
        receive(datas)
    }

    override fun onSerialIoError(e: Exception?) {
        status("connection lost: " + e!!.message)
        disconnect()
    }
}