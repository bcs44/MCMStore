package mestrado.ipg.mcmstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SensorSwitch extends AppCompatActivity {

    Switch switch1, switch2, switch3, switch4, switch5, switch6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_switch);


        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        switch3 = findViewById(R.id.switch3);
        switch4 = findViewById(R.id.switch4);
        switch5 = findViewById(R.id.switch5);
        switch6 = findViewById(R.id.switch6);


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch1 " + x, Toast.LENGTH_LONG).show();
            }
        });

        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch2 " + x, Toast.LENGTH_LONG).show();
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch3 " + x, Toast.LENGTH_LONG).show();
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch4 " + x, Toast.LENGTH_LONG).show();
            }
        });
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch5 " + x, Toast.LENGTH_LONG).show();
            }
        });
        switch6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String x = String.valueOf(isChecked);
                Toast.makeText(SensorSwitch.this, "isChecked switch6 " + x, Toast.LENGTH_LONG).show();
            }
        });


    }
}
