package com.fongmi.android.tv.ui.fragment.child;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.api.ApiConfig;
import com.fongmi.android.tv.bean.Filter;
import com.fongmi.android.tv.bean.Vod;
import com.fongmi.android.tv.databinding.FragmentTypeBinding;
import com.fongmi.android.tv.model.SiteViewModel;
import com.fongmi.android.tv.ui.activity.BaseFragment;
import com.fongmi.android.tv.ui.adapter.FilterAdapter;
import com.fongmi.android.tv.ui.adapter.ValueAdapter;
import com.fongmi.android.tv.ui.adapter.VodAdapter;
import com.fongmi.android.tv.ui.custom.CustomScroller;
import com.fongmi.android.tv.ui.custom.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TypeFragment extends BaseFragment implements CustomScroller.Callback, ValueAdapter.OnClickListener, VodAdapter.OnClickListener {

    private HashMap<String, String> mExtend;
    private FragmentTypeBinding mBinding;
    private FilterAdapter mFilterAdapter;
    private CustomScroller mScroller;
    private SiteViewModel mViewModel;
    private VodAdapter mVodAdapter;
    private List<Filter> mFilters;
    private List<String> mTypeIds;
    private boolean mOpen;

    public static TypeFragment newInstance(String typeId, String filter, boolean folder) {
        Bundle args = new Bundle();
        args.putString("typeId", typeId);
        args.putString("filter", filter);
        args.putBoolean("folder", folder);
        TypeFragment fragment = new TypeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private String getTypeId() {
        return getArguments().getString("typeId");
    }

    private String getFilter() {
        return getArguments().getString("filter");
    }

    private boolean isFolder() {
        return getArguments().getBoolean("folder");
    }

    @Override
    protected ViewBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return mBinding = FragmentTypeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initView() {
        mTypeIds = new ArrayList<>();
        mExtend = new HashMap<>();
        mFilters = Filter.arrayFrom(getFilter());
        mBinding.progressLayout.showProgress();
        setRecyclerView();
        setViewModel();
        getVideo();
    }

    private void setRecyclerView() {
        mBinding.filter.setHasFixedSize(true);
        mBinding.filter.setAdapter(mFilterAdapter = new FilterAdapter(this));
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setAdapter(mVodAdapter = new VodAdapter(this));
        mBinding.recycler.addOnScrollListener(mScroller = new CustomScroller(this));
        mBinding.recycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mBinding.recycler.addItemDecoration(new SpaceItemDecoration(3, 16));
    }

    private void setViewModel() {
        mViewModel = new ViewModelProvider(this).get(SiteViewModel.class);
        mViewModel.result.observe(getViewLifecycleOwner(), result -> {
            mBinding.progressLayout.showContent(isFolder(), result.getList().size());
            mScroller.endLoading(result.getList().isEmpty());
            mVodAdapter.addAll(result.getList());
            checkPage();
        });
    }

    private void getVideo() {
        mScroller.reset();
        getVideo(getTypeId(), "1");
    }

    private void checkPage() {
        if (mScroller.getPage() != 1 || mVodAdapter.getItemCount() >= 4 || isFolder()) return;
        if (mScroller.addPage()) getVideo(getTypeId(), "2");
    }

    private void getVideo(String typeId, String page) {
        if (isFolder()) mTypeIds.add(typeId);
        if (isFolder() && !mOpen) mBinding.recycler.scrollToPosition(0);
        if (page.equals("1")) mVodAdapter.clear();
        mViewModel.categoryContent(ApiConfig.get().getHome().getKey(), typeId, page, true, mExtend);
    }

    private void addFilter() {
        mFilterAdapter.addAll(mFilters);
    }

    private void clearFilter() {
        mFilterAdapter.clear();
    }

    /*private void setClick(ArrayObjectAdapter adapter, String key, Filter.Value item) {
        for (int i = 0; i < adapter.size(); i++) ((Filter.Value) adapter.get(i)).setActivated(item);
        adapter.notifyArrayItemRangeChanged(0, adapter.size());
        mExtend.put(key, item.getV());
        if (isFolder()) refresh(1);
        else getVideo();
    }*/

    private void refresh(int num) {
        String typeId = mTypeIds.get(mTypeIds.size() - num);
        mTypeIds = mTypeIds.subList(0, mTypeIds.size() - num);
        getVideo(typeId, "1");
    }

    public void toggleFilter(boolean open) {
        if (open) addFilter();
        else clearFilter();
        mOpen = open;
    }

    public boolean canGoBack() {
        return mTypeIds.size() > 1;
    }

    public void goBack() {
        refresh(2);
    }

    @Override
    public void onLoadMore(String page) {
        if (isFolder()) return;
        mScroller.setLoading(true);
        getVideo(getTypeId(), page);
    }

    @Override
    public void onItemClick(String key, Filter.Value item) {
    }

    @Override
    public void onItemClick(Vod item) {
    }

    @Override
    public boolean onLongClick(Vod item) {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mBinding != null && !isVisibleToUser) mBinding.recycler.scrollToPosition(0);
    }
}