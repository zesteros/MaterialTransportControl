package com.mx.vise.acarreos.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mx.vise.acarreos.R;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView
        .Adapter<RecyclerViewAdapter.DataHolder> {
    private ArrayList<RecyclerViewModel> mDataset;

    public static class DataHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle1;
        TextView subtitle2;
        TextView content1;
        TextView content2;

        public DataHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.data_title);
            subtitle1 = (TextView) itemView.findViewById(R.id.subtitle_content_1);
            content1 = (TextView) itemView.findViewById(R.id.data_content_1);
            subtitle2 = (TextView) itemView.findViewById(R.id.subtitle_content_2);
            content2 = (TextView) itemView.findViewById(R.id.data_content_2);
        }
    }

    public RecyclerViewAdapter(ArrayList<RecyclerViewModel> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_recycler, parent, false);

        DataHolder dataObjectHolder = new DataHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataHolder holder, int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.subtitle1.setText(mDataset.get(position).getSubtitle1());
        holder.content1.setText(mDataset.get(position).getContent1());
        if (mDataset.get(position).getSubtitle2() != null) {
            holder.subtitle2.setText(mDataset.get(position).getSubtitle2());
            holder.subtitle2.setVisibility(View.VISIBLE);
        } else
            holder.subtitle2.setVisibility(View.GONE);
        if (mDataset.get(position).getContent2() != null) {
            holder.content2.setText(mDataset.get(position).getContent2());
            holder.content2.setVisibility(View.VISIBLE);
        } else
            holder.content2.setVisibility(View.GONE);
    }


    public void addItem(RecyclerViewModel dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
