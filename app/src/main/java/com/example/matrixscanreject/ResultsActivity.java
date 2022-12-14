package com.example.matrixscanreject;
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.matrixscanreject.data.ScanResult;
import com.scandit.datacapture.barcode.data.SymbologyDescription;

import java.util.ArrayList;
import java.util.HashSet;

public class ResultsActivity extends AppCompatActivity {

    public static final int RESULT_CODE_CLEAN = 1;
    private static final String ARG_SCAN_RESULTS = "scan-results";

    public static Intent getIntent(Context context, HashSet<ScanResult> scanResults) {
        return new Intent(context, ResultsActivity.class)
                .putExtra(ARG_SCAN_RESULTS, scanResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Setup recycler view.
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL));

        // Receive results from previous screen and set recycler view items.
        final ArrayList<ScanResult> scanResults = new ArrayList<>(
                (HashSet<ScanResult>) getIntent().getSerializableExtra(ARG_SCAN_RESULTS));
        recyclerView.setAdapter(new ScanResultsAdapter(this, scanResults));

        Button doneButton = findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CODE_CLEAN);
                finish();
            }
        });
    }

    private static class ScanResultsAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Context context;
        private ArrayList<ScanResult> items;

        ScanResultsAdapter(Context context, ArrayList<ScanResult> items) {
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(context).inflate(R.layout.scan_result_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.update(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dataTextView;
        private TextView typeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            dataTextView = itemView.findViewById(R.id.data_text);
            typeTextView = itemView.findViewById(R.id.type_text);
        }

        void update(ScanResult scanResult) {
            dataTextView.setText(scanResult.data);
            typeTextView.setText(
                    SymbologyDescription.create(scanResult.symbology).getReadableName()
            );
        }
    }
}
