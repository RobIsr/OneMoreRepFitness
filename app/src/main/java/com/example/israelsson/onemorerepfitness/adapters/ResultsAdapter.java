package com.example.israelsson.onemorerepfitness.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.israelsson.onemorerepfitness.R;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder> {
    ArrayList resultItems = new ArrayList();
    private OnItemClickHandler handler;

    public ResultsAdapter(ArrayList resultItems, OnItemClickHandler clickHandler) {
        this.resultItems = resultItems;
        this.handler = clickHandler;
    }

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
        void onClick(View str);
    }

    class ResultViewHolder extends ViewHolder implements OnClickListener {
        TextView resultListItem;

        public ResultViewHolder(View itemView) {
            super(itemView);
            resultListItem = itemView.findViewById(R.id.tv_result_item);
            itemView.setOnClickListener(this);
        }

        void bind(int position) {
            resultListItem.setText(ResultsAdapter.this.resultItems.get(position).toString());
        }

        public void onClick(View v) {
            ResultsAdapter.this.handler.onClick(v);
        }
    }
}