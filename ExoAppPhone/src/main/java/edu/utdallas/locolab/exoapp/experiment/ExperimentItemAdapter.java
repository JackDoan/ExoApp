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

package edu.utdallas.locolab.exoapp.experiment;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.utdallas.locolab.exoapp.packet.DataPacket;
import edu.utdallas.locolab.exoapp.phone.ExoApplication;
import edu.utdallas.locolab.exoapp.phone.R;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import io.objectbox.query.QueryConsumer;

/**
 * Created by douglas on 10/04/2017
 *
 * This class generates the little cards for each experiment
 *
 */

public class ExperimentItemAdapter extends RecyclerView.Adapter<ExperimentItemAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ExperimentItem> experimentItems;
    private List<ExperimentItem> recordingExperiments;
    private final LayoutInflater mInflater;
    private Box<ExperimentItem> experimentBox;


    private ItemTouchHelper touchHelper;

    public ExperimentItemAdapter(Context context, BoxStore boxStore) {
        super();
        mContext = context;
        experimentBox = boxStore.boxFor(ExperimentItem.class);
        recordingExperiments = new ArrayList<>();
        experimentItems = importExperiments();
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        touchHelper = new ItemTouchHelper(touchHelperCallback);
    }

    private List<ExperimentItem> importExperiments() {
        QueryBuilder<ExperimentItem> builder = experimentBox.query();
        Query<ExperimentItem> q = builder.notNull(ExperimentItem_.id).build(); //not ordering so we can use forEach
        List<ExperimentItem> experimentItems = q.find();
        q.forEach(expr -> {
            if(expr.getRecording()) {
                recordingExperiments.add(expr);
            }
        });
        return experimentItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(mInflater.inflate(R.layout.experiment_item, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ExperimentItem experiment = experimentItems.get(position);

        holder.experimentName.setText(experiment.getName());
        holder.recordSwitch.setChecked(experiment.getRecording());
        holder.experimentName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override public void afterTextChanged(Editable s) {
                experiment.setName(s.toString());
            }
        });
        holder.recordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            experiment.setRecording(isChecked);
            if(isChecked) {
                recordingExperiments.add(experiment);
            }
            else {
                recordingExperiments.remove(experiment);
            }
            //Toast.makeText(buttonView.getContext(), holder.experimentName.getText() + " " + isChecked, Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return experimentItems.size();
    }

    public List<ExperimentItem> getExperiments() {
        return experimentItems;
    }

    public List<ExperimentItem> getRecordingExperiments() {
        return recordingExperiments;
    }

    public void add() {
        this.add(0);
    }

    public void add(int pos) {
        ExperimentItem expr = new ExperimentItem();
        //todo add experiment to the db
        this.experimentItems.add(expr);
        this.notifyItemInserted(0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextInputEditText experimentName;
        private Switch recordSwitch;
        ViewHolder(View itemView) {
            super(itemView);
            experimentName = itemView.findViewById(R.id.experimentName);
            recordSwitch = itemView.findViewById(R.id.record_switch);
        }
    }

    public ItemTouchHelper getTouchHelper() {return touchHelper;}
    private ItemTouchHelper.Callback touchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
        public boolean onMove(RecyclerView recyclerView,
                              RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            final int fromPos = viewHolder.getAdapterPosition();
            final int toPos = target.getAdapterPosition();
            //todo move item in `fromPos` to `toPos` in adapter.
            return true;// true if moved, false otherwise
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            //todo display an undo snackbar
            //todo when the snackbar goes away:
            //todo delete the experiment
            //todo fire off a query to remove orphaned data items

            Toast.makeText(viewHolder.itemView.getContext(), "You tried to remove this!", Toast.LENGTH_SHORT).show();
        }
    };

}
