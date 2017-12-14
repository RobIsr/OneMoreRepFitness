package com.example.israelsson.onemorerepfitness;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {
    private ArrayList resultItems = new ArrayList();
    private OnItemClickHandler handler;

    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        inflater.inflate(R.layout.result_list_item, parent, false);
        return new ResultViewHolder(inflater.inflate(R.layout.result_list_item, parent, false));
    }

    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.bind(position);
    }

    public int getItemCount() {
        return resultItems.size();
    }

    public interface OnItemClickHandler {
        void onClick(String str);
    }

    class ResultViewHolder extends ViewHolder implements OnClickListener {
        TextView resultListItem;

        ResultViewHolder(View itemView) {
            super(itemView);
            resultListItem = itemView.findViewById(R.id.tv_result_item);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            resultListItem.setText(ResultsAdapter.this.resultItems.get(position).toString());
        }

        public void onClick(View v) {
            ResultsAdapter.this.handler.onClick(resultListItem.getText().toString());
        }
    }
}