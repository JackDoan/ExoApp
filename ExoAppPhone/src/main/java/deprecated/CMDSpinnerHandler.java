package deprecated;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;

/**
 * Created by jack on 12/22/17.
 */
@Deprecated
public class CMDSpinnerHandler extends SpinnerHandler {
    private final ActuatorSettingsAdaptor comex2;
    private final EditText valueBox;


    public CMDSpinnerHandler(Context base, Spinner uiHandle, EditText valueBox, ActuatorSettingsAdaptor comex2) {
        super(base);
        this.uiHandle = uiHandle;
        String[] spinnerItems = {"Manual Torque", "Swing Gain", "Stance Gain", "Threshold", "Torque Ramp", "Manual Position"};
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.uiHandle.setAdapter(spinnerAdapter);
        this.uiHandle.setOnItemSelectedListener(this);
        this.valueBox = valueBox;
        this.comex2 = comex2;
    }

    public void sentClicked() {
        String arg = valueBox.getText().toString();
        switch(uiHandle.getSelectedItemPosition()) {
            case 0:
                comex2.setManualTorque(Float.valueOf(arg));
                break;
            case 1:
                comex2.setSwingGain(Float.valueOf(arg));
                break;
            case 2:
                comex2.setStanceGain(Float.valueOf(arg));
                break;
            case 3:
                comex2.setThreshold(Integer.valueOf(arg)); //todo clean up this typing
                break;
            case 4:
                comex2.setTorqueRamp(Integer.valueOf(arg));
                break;
            case 5:
                comex2.setPositionDeg(Float.valueOf(arg));
                break;
        }
        Toast.makeText(this, "Setting updated!", Toast.LENGTH_LONG).show();
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String fillBox = "";
        switch (position) {
            case 0:
                fillBox = Float.toString(comex2.getManualTorque());
                break;
            case 1:
                fillBox = Float.toString(comex2.getSwingGain());
                break;
            case 2:
                fillBox = Float.toString(comex2.getStanceGain());
                break;
            case 3:
                fillBox = Integer.toString(comex2.getThreshold());
                break;
            case 4:
                fillBox = Integer.toString(comex2.getTorqueRamp());
                break;
            case 5:
                fillBox = Float.toString(comex2.getPosDeg());
                break;
        }
        valueBox.setText(fillBox);
    }
}
