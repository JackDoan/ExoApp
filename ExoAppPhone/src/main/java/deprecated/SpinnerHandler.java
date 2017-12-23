package deprecated;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by jack on 12/22/17.
 */
@Deprecated
public abstract class SpinnerHandler extends ContextWrapper implements AdapterView.OnItemSelectedListener {

    protected final Context base;
    protected String[] items;
    protected Spinner uiHandle;
    ArrayAdapter<String> spinnerAdapter;
    //protected String[] spinnerItems;

    SpinnerHandler(Context base) {
        super(base);
        this.base = base;
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
}