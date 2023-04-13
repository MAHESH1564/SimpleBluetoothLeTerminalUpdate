package de.kai_morich.simple_bluetooth_le_terminal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
public class InsertName extends Fragment {

    private Bundle args;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    public InsertName(Bundle args) {
        this.args=args;
    }
    // TODO: Rename and change types and number of parameters
    /*public static InsertName newInstance(String param1, String param2) {
        InsertName fragment = new InsertName();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view1 = inflater.inflate(R.layout.fragment_insert_name, container, false);
        EditText textbox = view1.findViewById(R.id.filetext);
        View OKBtn = view1.findViewById(R.id.button2);
        OKBtn.setOnClickListener(view -> {
            args.putString("filename", textbox.getText().toString());
            Fragment fragment = new TerminalFragment();
            fragment.setArguments(args);
            requireFragmentManager().beginTransaction().replace(R.id.fragment, fragment, "terminal").addToBackStack(null).commit();
        });
        return view1;
    }

}