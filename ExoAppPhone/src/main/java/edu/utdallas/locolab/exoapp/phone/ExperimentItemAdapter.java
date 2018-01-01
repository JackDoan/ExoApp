/*
 * MIT License
 *
 * Copyright (c) 2015 Douglas Nassif Roma Junior
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.utdallas.locolab.exoapp.phone;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by douglas on 10/04/2017
 *
 * This class generates the little cards in MainActivity for each bluetooth device
 *
 */

public class ExperimentItemAdapter extends RecyclerView.Adapter<ExperimentItemAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ExperimentItem> experimentItems;
    private final LayoutInflater mInflater;
    private OnAdapterItemClickListener mOnItemClickListener;


    public ExperimentItemAdapter(Context context) {
        super();
        mContext = context;
        experimentItems = new ArrayList<>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ExperimentItemAdapter(Context context, List<BluetoothDevice> devices) {
        super();
        mContext = context;
        experimentItems = decorateDevices(devices);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ExperimentItemAdapter(Context context, Set<BluetoothDevice> devices) {
        this(context, new ArrayList<>(devices));
    }

    public static List<ExperimentItem> decorateDevices(Collection<BluetoothDevice> btDevices) {
        List<ExperimentItem> devices = new ArrayList<>();
        for (BluetoothDevice dev : btDevices) {
            devices.add(new ExperimentItem());
        }
        return devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.experiment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ExperimentItem device = experimentItems.get(position);

        holder.experimentName.setText("Experiment " + getItemCount());
        holder.recordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
        //holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(device, position));
    }

    @Override
    public int getItemCount() {
        return experimentItems.size();
    }

    public List<ExperimentItem> getExperiments() {
        return experimentItems;
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnAdapterItemClickListener {
        void onItemClick(ExperimentItem device, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextInputEditText experimentName;
        private Switch recordSwitch;
        public ViewHolder(View itemView) {
            super(itemView);
            experimentName = itemView.findViewById(R.id.experimentName);
            recordSwitch = itemView.findViewById(R.id.record_switch);
        }
    }
}
