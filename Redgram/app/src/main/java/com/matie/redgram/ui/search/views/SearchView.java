package com.matie.redgram.ui.search.views;

import com.matie.redgram.ui.common.utils.DialogUtil;
import com.matie.redgram.ui.common.views.BaseContextView;
import com.matie.redgram.ui.common.views.widgets.postlist.PostRecyclerView;

/**
 * Created by matie on 12/04/15.
 */
public interface SearchView extends BaseContextView {
    public void showProgress(int source);
    public void hideProgress(int source);
    public void showInfoMessage();
    public void showErrorMessage();
    public void showToolbar();
    public void hideToolbar();

    public DialogUtil getDialogUtil();
    public PostRecyclerView getRecyclerView();
}
