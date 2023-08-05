package com.fongmi.android.tv.ui.holder;

import androidx.annotation.NonNull;

import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.databinding.AdapterVodOvalBinding;
import com.fongmi.android.tv.ui.adapter.VodAdapter;
import com.fongmi.android.tv.ui.base.BaseVodHolder;
import com.fongmi.android.tv.utils.ImgUtil;

public class VodOvalHolder extends BaseVodHolder {

    private final VodAdapter.OnClickListener listener;
    private final AdapterVodOvalBinding binding;

    public VodOvalHolder(@NonNull AdapterVodOvalBinding binding, VodAdapter.OnClickListener listener) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
    }

    public VodOvalHolder size(int[] size) {
        this.itemView.getLayoutParams().width = size[0];
        this.itemView.getLayoutParams().height = size[1];
        return this;
    }

    @Override
    public void initView(Vod item) {
        ImgUtil.load(item.getVodPic(), binding.image);
        binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        binding.getRoot().setOnLongClickListener(v -> listener.onLongClick(item));
    }
}