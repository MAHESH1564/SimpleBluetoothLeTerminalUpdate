package de.kai_morich.simple_bluetooth_le_terminal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment

class InsertName    // TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private static final String ARG_PARAM1 = "param1";
//private static final String ARG_PARAM2 = "param2";
// TODO: Rename and change types of parameters
(private val args: Bundle) : Fragment() {
    // TODO: Rename and change types and number of parameters
    /*public static InsertName newInstance(String param1, String param2) {
        InsertName fragment = new InsertName();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view1 = inflater.inflate(R.layout.fragment_insert_name, container, false)
        val textbox = view1.findViewById<EditText>(R.id.filetext)
        val OKBtn = view1.findViewById<View>(R.id.button2)
        OKBtn.setOnClickListener { view: View? ->
            args.putString("filename", textbox.text.toString())
            val fragment: Fragment = TerminalFragment()
            fragment.arguments = args
            requireFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit()
        }
        return view1
    }
}