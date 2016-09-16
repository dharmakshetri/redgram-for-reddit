package com.matie.redgram.ui.common.user;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.matie.redgram.R;
import com.matie.redgram.data.managers.presenters.UserListPresenter;
import com.matie.redgram.data.managers.storage.db.DatabaseHelper;
import com.matie.redgram.data.models.db.User;
import com.matie.redgram.data.models.main.items.UserItem;
import com.matie.redgram.ui.App;
import com.matie.redgram.ui.common.auth.AuthActivity;
import com.matie.redgram.ui.common.base.BaseActivity;
import com.matie.redgram.ui.common.user.views.UserListControllerView;
import com.matie.redgram.ui.common.utils.widgets.DialogUtil;
import com.matie.redgram.ui.common.views.BaseContextView;
import com.matie.redgram.ui.common.views.DrawerView;
import com.matie.redgram.ui.common.views.adapters.UserAdapter;
import com.matie.redgram.ui.common.views.widgets.drawer.UserRecyclerView;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * // TODO: 2016-09-15 selects (highlights) the username in the session
 * Created by matie on 2016-09-15.
 */
public class UserListView extends FrameLayout implements UserListControllerView {

    public static final int ADD_ACCOUNT = 11;

    @InjectView(R.id.user_add_account)
    TextView addAccountView;
    @InjectView(R.id.user_recycler_view)
    UserRecyclerView userRecyclerView;
    @InjectView(R.id.user_cancel)
    ImageView cancelView;

    @Inject
    App app;
    @Inject
    UserListPresenter presenter;
    @Inject
    DialogUtil dialogUtil;

    private final Context context;
    private UserListComponent component;

    public UserListView(Context context) {
        super(context);
        this.context = context;
    }

    public UserListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public UserListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        userRecyclerView.setListener(this);
    }

    @OnClick(R.id.user_cancel)
    public void onCancelClick(View v){
        close();
    }

    @OnClick(R.id.user_add_account)
    public void onAddClick(View v){
        addAccount();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showInfoMessage() {

    }

    @Override
    public void showErrorMessage(String error) {

    }

    @Override
    public RxAppCompatActivity getBaseActivity() {
        return null;
    }

    @Override
    public RxFragment getBaseFragment() {
        return null;
    }

    @Override
    public void addAccount() {
        BaseContextView contextView = (BaseContextView) context;
        if(contextView != null){
            RxAppCompatActivity baseActivity = contextView.getBaseActivity();
            if(baseActivity instanceof BaseActivity){
                ((BaseActivity)baseActivity).openIntentForResult(AuthActivity.intent(context, false), ADD_ACCOUNT, 0, 0);
            }
        }
    }

    @Override
    public void addAccount(String username) {
        // TODO: 2016-09-15 happens on success - add to DB and then call selectAccount
        UserItem userItem = new UserItem(username);
        getItems().add(userItem);
        userRecyclerView.getAdapter().notifyItemInserted(getItems().size()-1);
    }

    @Override
    public void selectAccount(int position) {
        // TODO: 2016-09-15 updates session and restarts activity
        String username = getItem(position).getUserName();
        Log.d("username", username);
    }

    @Override
    public void removeAccount(int position) {
        // TODO: 2016-09-15 removes user from local db and then from adapter on success
        // TODO: 2016-09-15 if it's the only account, switch to Applicastion Only Auth, otherwise, prompt the user to choose a different account
        String username = getItem(position).getUserName();
        Log.d("username", username);
    }

    @Override
    public void close() {
        DrawerView drawerView = (DrawerView)context;
        drawerView.resetNavDrawer();
    }

    @Override
    public UserItem getItem(int position) {
        return ((UserAdapter)userRecyclerView.getAdapter()).getItem(position);
    }

    @Override
    public List<UserItem> getItems() {
        return ((UserAdapter)userRecyclerView.getAdapter()).getItems();
    }

    public void setComponent(UserListComponent component) {
        this.component = component;
        this.component.inject(this);
    }

    public UserListComponent getComponent() {
        return component;
    }

    public void setUp(Realm realm) {
        RealmResults<User> result = realm.where(User.class).findAll();
        List<UserItem> userItems = new ArrayList<>();
        for(User user : result){
            UserItem userItem = new UserItem(user.getUserName());
            userItems.add(userItem);
        }

        userRecyclerView.replaceWith(userItems);

        //make global?
        User sessionUser = DatabaseHelper.getSessionUser(realm);
        if(sessionUser != null){
            /** TODO: 2016-05-05 set user in session to selected state in list.
             *  todo: UserItemView should have a custom drawable as background for selected state color
             */
        }

    }
}
