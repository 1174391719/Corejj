package com.maxi.corejj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.maxi.corejj.callback.ItemClickCallback;

import java.util.List;

/**
 * Created by Administrator on 2019/05/18.
 */

public class SimpleRvAdapterImpl extends RecyclerView.Adapter<SimpleRvAdapterImpl.ViewHolder> implements SimpleRvAdapter {
    private Context mContext;
    private List<SimpleRvData> mData;
    private ItemClickCallback mCallback;

    public SimpleRvAdapterImpl(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_rv_adapter, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.one.setText(mData.get(i).getItemOne());
        viewHolder.tow.setText(mData.get(i).getItemTow());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onItemClickCallback(i, null, null);
                }
            }
        });
        if (i < mData.size() - 1) {
            viewHolder.divider.setVisibility(View.VISIBLE);
        } else {
            viewHolder.divider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return this;
    }

    public void update(List<SimpleRvData> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void setCallback(ItemClickCallback mCallback) {
        this.mCallback = mCallback;
    }

    @Override

    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView one;
        TextView tow;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            one = itemView.findViewById(R.id.tv_item_simple_rv_adapter_one);
            tow = itemView.findViewById(R.id.tv_item_simple_rv_adapter_tow);
            divider = itemView.findViewById(R.id.v_item_simple_rv_adapter_divider);
        }
    }
}
