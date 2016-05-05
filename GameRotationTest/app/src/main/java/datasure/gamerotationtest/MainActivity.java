package datasure.gamerotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import datasure.gamerotationtest.sensor.GameRotationWrapper;

public class MainActivity extends AppCompatActivity {

    private GameRotationWrapper wrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        wrapper = new GameRotationWrapper(this);
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wrapper.destory();
    }
}
