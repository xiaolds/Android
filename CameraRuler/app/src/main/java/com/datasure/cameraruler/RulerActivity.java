package com.datasure.cameraruler;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.datasure.orientation.Oriente;

import java.util.Arrays;

public class RulerActivity extends AppCompatActivity {

    private Oriente oriente;
    private Button dataBut;
    private TextView data;
    private float[] result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_ruler);

        dataBut = (Button) findViewById(R.id.data_but);
        data = (TextView) findViewById(R.id.data);



        //initial the Orientation
        oriente = new Oriente(this);
        oriente.init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        oriente.register();

        dataBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = oriente.getResult();
                if(result != null){
                    data.setText(Arrays.toString(result));
                }
            }
        });
    }



    @Override
    protected void onStop() {
        super.onStop();
    }
}
