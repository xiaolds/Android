package com.datasure.setting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.datasure.cameraruler.R;


public class HeightFragment extends DialogFragment {

    private EditText _H;
    private EditText _h;


    public HeightFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //get AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_height, null);
        //get the EditText
        _H = (EditText) view.findViewById(R.id.id_edit_H);
        _h = (EditText) view.findViewById(R.id.id_edit_h);

        SharedPreferences setting = getActivity().getSharedPreferences("setting", 0);
        _H.setText(String.valueOf(setting.getFloat("H",0f)));
        _h.setText(String.valueOf(setting.getFloat("h",1.75f)));

        //inflate fragment_height.xml
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get shared preference
                        SharedPreferences setting = getActivity().getSharedPreferences("setting", 0);
                        SharedPreferences.Editor editor = setting.edit();

                        //set H & h
                        editor.putFloat("H", Float.valueOf(_H.getText().toString()));
                        editor.putFloat("h", Float.valueOf(_h.getText().toString()));
                        editor.commit();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HeightFragment.this.getDialog().cancel();
                    }
                });

        builder.setTitle("设置高度");


        return builder.create();
    }
}
