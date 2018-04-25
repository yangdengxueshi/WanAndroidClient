package com.wan.android.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wan.android.R;
import com.wan.android.data.bean.CollectDatas;

import java.util.List;

/**
 * @author wzc
 * @date 2018/2/2
 */
public class CollectAdapter extends BaseQuickAdapter<CollectDatas, BaseViewHolder> {

    public CollectAdapter(@LayoutRes int layoutResId, @Nullable List<CollectDatas> data) {
        super(layoutResId, data);

    }

    @Override
    protected void convert(BaseViewHolder helper, CollectDatas item) {
        // title
        helper.setText(R.id.tv_home_item_view_title, item.getTitle());
        // chapterName
        helper.setText(R.id.tv_home_item_view_chapter_name, item.getChaptername());
        // niceDate
        helper.setText(R.id.tv_home_item_view_nice_date, item.getNicedate());
        // authorName
        helper.setText(R.id.tv_home_item_view_author, item.getAuthor());
        helper.addOnClickListener(R.id.iv_home_item_view_collect);
        // 类别可以点击
        helper.addOnClickListener(R.id.tv_home_item_view_chapter_name);
        // 作者名字可点击
        helper.addOnClickListener(R.id.tv_home_item_view_author);
        helper.setImageResource(R.id.iv_home_item_view_collect, R.drawable.ic_favorite);
    }
}
