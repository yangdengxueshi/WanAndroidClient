package com.wan.android.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.wan.android.BuildConfig;
import com.wan.android.R;
import com.wan.android.activity.ContentActivity;
import com.wan.android.adapter.CollectAdapter;
import com.wan.android.bean.CollectData;
import com.wan.android.bean.CollectDatas;
import com.wan.android.bean.CommonResponse;
import com.wan.android.callback.EmptyCallback;
import com.wan.android.client.CollectListClient;
import com.wan.android.client.CollectOtherClient;
import com.wan.android.client.UncollectAllClient;
import com.wan.android.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author wzc
 * @date 2018/3/5
 */
public class CollectionFragement extends BaseListFragment {
    private static final String TAG = MyFragment.class.getSimpleName();
    private CollectAdapter mCollectAdapter;

    public static CollectionFragement newInstance() {

        Bundle args = new Bundle();

        CollectionFragement fragment = new CollectionFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        initRefreshLayout();
        refresh();
    }

    private void initRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    @Override
    protected void refresh() {
        // 防止下拉刷新时,还可以上拉加载
        mCollectAdapter.setEnableLoadMore(false);
        CollectListClient client = RetrofitClient.create(CollectListClient.class);
        Call<CommonResponse<CollectData>> call = client.getCollect(0);
        call.enqueue(new Callback<CommonResponse<CollectData>>() {
            @Override
            public void onResponse(Call<CommonResponse<CollectData>> call, Response<CommonResponse<CollectData>> response) {

                mCollectAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "response:" + response);
                }
                CommonResponse<CollectData> body = response.body();
                if (body == null) {
                    return;
                }
                CollectData data = body.getData();
                if (data == null) {
                    return;
                }
                List<CollectDatas> datas = data.getDatas();
                if (datas.size() == 0) {
                    mLoadService.showCallback(EmptyCallback.class);
                    return;
                }
                mDatasList.clear();
                mDatasList.addAll(datas);
                mCollectAdapter.notifyDataSetChanged();
                mLoadService.showSuccess();
            }

            @Override
            public void onFailure(Call<CommonResponse<CollectData>> call, Throwable t) {
                mCollectAdapter.setEnableLoadMore(true);
                mSwipeRefreshLayout.setRefreshing(false);
                mLoadService.showCallback(EmptyCallback.class);
                mSwipeRefreshLayout.setSwipeableChildren(R.id.recyclerview_view, R.id.ll_error, R.id.ll_empty, R.id.ll_loading);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "t:" + t);
                }
            }
        });
    }

    private List<CollectDatas> mDatasList = new ArrayList<>();

    private void initAdapter() {
        mCollectAdapter = new CollectAdapter(R.layout.list_item_view, mDatasList);
        mCollectAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(mCollectAdapter);
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {

                CollectDatas data = mDatasList.get(position);
                ContentActivity.start(mActivity, data.getTitle(), data.getLink(), data.getId());
            }
        });
        mCollectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_home_item_view_collect:
                        // 取消收藏
                        unCollect(position);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void unCollect(final int position) {
        UncollectAllClient client = RetrofitClient.create(UncollectAllClient.class);
        Call<CommonResponse<String>> call = client.uncollectAll(mDatasList.get(position).getId(),mDatasList.get(position).getOriginid());
        call.enqueue(new Callback<CommonResponse<String>>() {
            @Override
            public void onResponse(Call<CommonResponse<String>> call, Response<CommonResponse<String>> response) {
                CommonResponse<String> body = response.body();
                if (body.getErrorcode() != 0) {
                    Toast.makeText(mActivity, body.getErrormsg(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(mActivity, R.string.uncollect_successfully, Toast.LENGTH_SHORT).show();
                mDatasList.remove(position);
                mCollectAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onFailure(Call<CommonResponse<String>> call, Throwable t) {
                Toast.makeText(mActivity, getString(R.string.uncollect_failed) + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private int mNextPage = 1;

    private void loadMore() {
        CollectListClient client = RetrofitClient.create(CollectListClient.class);
        Call<CommonResponse<CollectData>> call = client.getCollect(mNextPage);
        call.enqueue(new Callback<CommonResponse<CollectData>>() {
            @Override
            public void onResponse(Call<CommonResponse<CollectData>> call, Response<CommonResponse<CollectData>> response) {
                CommonResponse<CollectData> body = response.body();
                if (body == null) {
                    return;
                }
                CollectData data = body.getData();
                if (data == null) {
                    return;
                }
                mNextPage++;
                if (mNextPage < response.body().getData().getPagecount()) {
                    mCollectAdapter.loadMoreComplete();
                } else {
                    mCollectAdapter.loadMoreEnd();
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "response:" + response);
                }

                List<CollectDatas> datas = data.getDatas();
                mDatasList.addAll(datas);
                mCollectAdapter.notifyDataSetChanged();
                mLoadService.showSuccess();
            }

            @Override
            public void onFailure(Call<CommonResponse<CollectData>> call, Throwable t) {
                mCollectAdapter.loadMoreFail();
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_collect, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_collect_add:
                LayoutInflater factory = LayoutInflater.from(mActivity);
                final View textEntryView = factory.inflate(R.layout.dialog_add_collect, null);
                final View textEntryTitle = factory.inflate(R.layout.dialog_add_title, null);
                final EditText etTitle = (EditText) textEntryView.findViewById(R.id.title_edit);
                final EditText etAuthor = (EditText) textEntryView.findViewById(R.id.author_edit);
                final EditText etLink = (EditText) textEntryView.findViewById(R.id.link_edit);
                final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setCustomTitle(textEntryTitle)
                        .setView(textEntryView)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_dialog_ok, null)
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = etTitle.getText().toString();
                        String author = etAuthor.getText().toString();
                        String link = etLink.getText().toString();
                        if (TextUtils.isEmpty(title)) {
                            Toast.makeText(mActivity, R.string.title_cannot_be_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(link)) {
                            Toast.makeText(mActivity, R.string.link_cannot_be_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CollectOtherClient client = RetrofitClient.create(CollectOtherClient.class);
                        Call<CommonResponse<CollectDatas>> call = client.collectOther(title, author, link);
                        call.enqueue(new Callback<CommonResponse<CollectDatas>>() {
                            @Override
                            public void onResponse(Call<CommonResponse<CollectDatas>> call, Response<CommonResponse<CollectDatas>> response) {
                                CommonResponse<CollectDatas> body = response.body();
                                if (body.getErrorcode() != 0) {
                                    Toast.makeText(mActivity, mActivity.getString(R.string.collect_failed) + " : " + body.getErrormsg(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(mActivity, R.string.collect_successfully, Toast.LENGTH_SHORT).show();
                                refresh();
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<CommonResponse<CollectDatas>> call, Throwable t) {
                                Toast.makeText(mActivity, mActivity.getString(R.string.collect_failed) + " : " + t.toString(), Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });


                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
