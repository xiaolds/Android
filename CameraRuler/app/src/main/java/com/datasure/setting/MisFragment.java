package com.datasure.setting;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.datasure.cameraruler.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MisFragment extends DialogFragment {

    private EditText mis;

    public MisFragment() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        //get AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_mis, null);
        //get the EditText
        mis = (EditText) view.findViewById(R.id.id_edit_mis);
        SharedPreferences setting = getActivity().getSharedPreferences("setting", 0);
        mis.setText(String.valueOf(setting.getInt("mis",100)));

        //inflate fragment_height.xml
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get shared preference
                        SharedPreferences setting = getActivity().getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = setting.edit();

                        //set mis
                        editor.putFloat("mis", Float.valueOf(mis.getText().toString()));
                        editor.commit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MisFragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle("调整精度");


        return builder.create();


    }
}
