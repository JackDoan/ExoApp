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
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import edu.utdallas.locolab.exoapp.bluetooth.BluetoothExoDecorator;

/**
 * Created by douglas on 10/04/2017
 *
 * This class generates the little cards in MainActivity for each bluetooth device
 *
 */

public class DeviceItemAdapter extends RecyclerView.Adapter<DeviceItemAdapter.ViewHolder> {

    private final Context mContext;
    private final List<BluetoothExoDecorator> mDevices;
    private final LayoutInflater mInflater;
    private OnAdapterItemClickListener mOnItemClickListener;


    public DeviceItemAdapter(Context context) {
        super();
        mContext = context;
        mDevices = new ArrayList<>();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DeviceItemAdapter(Context context, List<BluetoothDevice> devices) {
        super();
        mContext = context;
        mDevices = decorateDevices(devices);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public DeviceItemAdapter(Context context, Set<BluetoothDevice> devices) {
        this(context, new ArrayList<>(devices));
    }

    public static List<BluetoothExoDecorator> decorateDevices(Collection<BluetoothDevice> btDevices) {
        List<BluetoothExoDecorator> devices = new ArrayList<>();
        for (BluetoothDevice dev : btDevices) {
            devices.add(new BluetoothExoDecorator(dev, 0));
        }
        return devices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.device_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final BluetoothExoDecorator device = mDevices.get(position);

        holder.tvName.setText(TextUtils.isEmpty(device.getName()) ? "---" : device.getName());
        holder.tvAddress.setText(device.getAddress());
        holder.tvRSSI.setText(String.format(Locale.US, "%d", device.getRSSI()));
        if(device.isExo()) {
            holder.ivIsExo.setVisibility(View.VISIBLE);
        }
        else {
            holder.ivIsExo.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(device, position));
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    public List<BluetoothExoDecorator> getDevices() {
        return mDevices;
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnAdapterItemClickListener {
        void onItemClick(BluetoothExoDecorator device, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvAddress;
        private final TextView tvRSSI;
        private ImageView ivIsExo;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvRSSI = itemView.findViewById(R.id.tv_rssi);
            ivIsExo = itemView.findViewById(R.id.is_exo);
        }
    }
}
