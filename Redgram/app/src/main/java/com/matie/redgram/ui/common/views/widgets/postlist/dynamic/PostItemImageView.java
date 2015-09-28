package com.matie.redgram.ui.common.views.widgets.postlist.dynamic;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.matie.redgram.R;
import com.matie.redgram.data.managers.preferences.PreferenceManager;
import com.matie.redgram.data.models.main.items.PostItem;
import com.matie.redgram.ui.App;
import com.matie.redgram.ui.common.main.MainActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by matie on 04/04/15.
 */
public class PostItemImageView extends PostItemSubView {

    @InjectView(R.id.image_view)
    SimpleDraweeView imageView;

    @InjectView(R.id.image_overlay)
    RelativeLayout imageOverlay;

    @InjectView(R.id.image_text_view)
    PostItemTextView postItemTextView;

    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;

    public PostItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mainActivity = (MainActivity)getContext();
        sharedPreferences = ((App)mainActivity.getApplication()).getPreferenceManager().getSharedPreferences("image");
    }

    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    public void setupView(PostItem item) {

        postItemTextView.setupView(item);

        if(item.isAdult() && !sharedPreferences.getString("adult", "no").equals("yes")){
            imageOverlay.setVisibility(VISIBLE);
        }else{
            imageOverlay.setVisibility(GONE);
        }

        Uri thumbnailUri = Uri.parse(item.getThumbnail());
        ImageRequest thumbnail = ImageRequestBuilder.newBuilderWithSource(thumbnailUri)
                .build();

        Uri uri = Uri.parse(item.getUrl());
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .build();

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(thumbnail)
                .setImageRequest(request)
                .setOldController(imageView.getController());
;
        if(item.getType() == PostItem.Type.GIF)
            builder.setAutoPlayAnimations(true);

        DraweeController controller = builder.build();
        imageView.getHierarchy().setActualImageFocusPoint(new PointF(0.5f, 0f));
        imageView.setController(controller);
    }

    @OnClick(R.id.image_overlay)
    public void onClick(){
        if(imageOverlay.getVisibility() == VISIBLE){
            if(!sharedPreferences.getString("adult", "no").equals("yes")){
                mainActivity.getDialogUtil().init();
                mainActivity.getDialogUtil().getDialogBuilder()
                        .title("Are you over 18?")
                        .positiveText("Yes")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                sharedPreferences.edit().putString("adult", "yes").commit();
                                imageOverlay.setVisibility(GONE);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                            }
                        })
                        .show();
            }


            // TODO: 21/09/15 add animation, ex: fade in/out
//            if(mApp != null){
//                // TODO: 25/09/15 Logic is wrong, fix
//                // TODO: 25/09/15 Pass DialogUtil
//                SharedPreferences sharedPreferences = mApp.getPreferenceManager().getSharedPreferences(PreferenceManager.SEARCH_SP);
//
//                if(mApp.getPreferenceManager().getString(sharedPreferences, "above_18", "n") == "y") {
//                    imageOverlay.setVisibility(INVISIBLE);
//                }else{
//                    //show prompt to confirm above 18
//
//                    //if yes then..
//                    mApp.getPreferenceManager().setString(sharedPreferences, "above_18", "y");
//                    //else do nothing
//                }
//
//"
//                mApp.getToastHandler().showToast("This is working", Toast.LENGTH_SHORT);
//            }
        }
    }
}