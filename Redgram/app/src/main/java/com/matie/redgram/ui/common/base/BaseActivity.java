package com.matie.redgram.ui.common.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.matie.redgram.ui.App;
import com.matie.redgram.ui.AppComponent;
import com.matie.redgram.ui.common.main.MainActivity;
import com.matie.redgram.ui.common.utils.widgets.DialogUtil;
import com.matie.redgram.ui.subcription.SubscriptionActivity;
import com.trello.rxlifecycle.components.RxActivity;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import icepick.Icepick;

/**
 * Created by matie on 09/06/15.
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(getLayoutId());

        AppComponent appComponent = App.get(this).component();
        setupComponent(appComponent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override protected void onResume(){
        super.onResume();

        //onResume of any activity, show connection status
        ((App)getApplication()).getConnectionManager().showConnectionStatus(true);
    }
    protected abstract void setupComponent(AppComponent appComponent);
    public abstract AppComponent component();
    public abstract App app();
    public abstract DialogUtil getDialogUtil();
    protected abstract BaseActivity activity();
    protected abstract int getLayoutId();
    protected abstract int getContainerId();

    public void openIntent(Intent intent, int enterAnim, int exitAnim){
        startActivity(intent);
        overridePendingTransition(enterAnim, exitAnim);
    }

    public void openIntentForResult(Intent intent, int requestCode, int enterAnim, int exitAnim){
        startActivityForResult(intent, requestCode);
        overridePendingTransition(enterAnim, exitAnim);
    }

    public void openFragmentWithResult(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(tag != null){
            transaction = transaction.replace(getContainerId(), fragment, tag);
        }else{
            transaction = transaction.replace(getContainerId(), fragment);
        }

        //important to avoid IllegalStateException
        transaction.commit();
    }

}
