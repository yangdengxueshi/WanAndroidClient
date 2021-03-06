package com.wan.android.data.network;

import com.wan.android.data.network.api.ApiCall;
import com.wan.android.data.network.model.AccountData;
import com.wan.android.data.network.model.ArticleData;
import com.wan.android.data.network.model.BannerData;
import com.wan.android.data.network.model.BranchData;
import com.wan.android.data.network.model.CollectData;
import com.wan.android.data.network.model.CollectDatas;
import com.wan.android.data.network.model.CommonResponse;
import com.wan.android.data.network.model.HotkeyData;
import com.wan.android.data.network.model.NavigationData;
import com.wan.android.data.network.model.ProjectData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * @author wzc
 * @date 2018/8/2
 */
@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiCall mApiCall;

    @Inject
    public AppApiHelper(ApiCall apiCall) {
        mApiCall = apiCall;
    }

    @Override
    public Observable<CommonResponse<ArticleData>> getHomeList(int page) {
        return mApiCall.getHomeList(page);
    }

    @Override
    public Observable<CommonResponse<AccountData>> login(String username, String password) {
        return mApiCall.login(username, password);
    }

    @Override
    public Observable<CommonResponse<AccountData>> register(String username, String password, String repassword) {
        return mApiCall.register(username, password, repassword);
    }

    @Override
    public Observable<CommonResponse<List<BannerData>>> getBanner() {
        return mApiCall.getBanner();
    }

    @Override
    public Observable<CommonResponse<List<HotkeyData>>> getHotkey() {
        return mApiCall.getHotkey();
    }

    @Override
    public Observable<CommonResponse<ArticleData>> search(int page, String k) {
        return mApiCall.search(page, k);
    }

    @Override
    public Observable<CommonResponse<List<BranchData>>> getTree() {
        return mApiCall.getTree();
    }

    @Override
    public Observable<CommonResponse<ArticleData>> getLeafArticles(int page, int id) {
        return mApiCall.getLeafArticles(page, id);
    }

    @Override
    public Observable<CommonResponse<List<NavigationData>>> getNavigation() {
        return mApiCall.getNavigation();
    }

    @Override
    public Observable<CommonResponse<List<ProjectData>>> getProject() {
        return mApiCall.getProject();
    }

    @Override
    public Observable<CommonResponse<ArticleData>> getProjectList(int page, int cid) {
        return mApiCall.getProjectList(page, cid);
    }

    @Override
    public Observable<CommonResponse<CollectData>> getMyCollection(int page) {
        return mApiCall.getMyCollection(page);
    }

    @Override
    public Observable<CommonResponse<String>> collectInSiteArticle(int id) {
        return mApiCall.collectInSiteArticle(id);
    }

    @Override
    public Observable<CommonResponse<CollectDatas>> collectOutOfSiteArticle(
            String title, String author, String link) {
        return mApiCall.collectOutOfSiteArticle(title, author, link);
    }

    @Override
    public Observable<CommonResponse<String>> uncollectArticleListArticle(int id) {
        return mApiCall.uncollectArticleListArticle(id);
    }

    @Override
    public Observable<CommonResponse<String>> uncollectMyCollectionArticle(int id, int originId) {
        return mApiCall.uncollectMyCollectionArticle(id, originId);
    }
}
