package deprecated;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;

/**
 * Created by jack on 12/22/17.
 */
@Deprecated
public class ControllerSpinnerHandler extends SpinnerHandler {
    private final ActuatorSettingsAdaptor comex2;

    public ControllerSpinnerHandler(Context base, Spinner uiHandle, ActuatorSettingsAdaptor comex2) {
        super(base);
        this.uiHandle = uiHandle;
        String[] spinnerItems = {"Passive", "Quasi-Stiffness", "Manual", "Position"};
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.uiHandle.setAdapter(spinnerAdapter);
        this.uiHandle.setOnItemSelectedListener(this);
        this.comex2 = comex2;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                comex2.setController(comex2.CTRL_PASSIVE);
                break;
            case 1:
                comex2.setController(comex2.CTRL_QUASI);
                break;
            case 2:
                comex2.setController(comex2.CTRL_MANUAL);
                break;
            case 3:
                comex2.setController(comex2.CTRL_POSITION);
        }
    }
}
