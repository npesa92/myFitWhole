package com.nicky.myfit;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melnykov.fab.*;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import at.markushi.ui.RevealColorView;


public class DetailActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {

    ImageView image;
    TextView userEmail;
    String bm;
    String passedEmail, clothes;

    ImageLoader imageLoader;
    DisplayImageOptions displayOptions;

    Toolbar toolbar;

    com.melnykov.fab.FloatingActionButton fab;

    FrameLayout clothingFrame;
    TextView clothingText;
    ImageButton fadeClothingFrame;
    LinearLayout outfitDesc;
    boolean isHidden = false;
    RevealAnimListener revealListener;
    AfterRevealAnimListener afterReveal;
    AnimListener animListener;
    int color, backgroundColor;
    RevealColorView revealView;

    RoundCornerView roundClothView;

    ArrayList<ClothingItem> clothingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(105);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageLoader = ImageLoader.getInstance();
        displayOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_action_pic_error)
                .showImageOnLoading(R.drawable.ic_action_pic_loading).build();

        image = (ImageView) findViewById(R.id.image);
        userEmail = (TextView) findViewById(R.id.detailEmail);

        fab = (FloatingActionButton) findViewById(R.id.addClothingItemsFab);

        color = getResources().getColor(R.color.md_red_a400);
        backgroundColor = getResources().getColor(android.R.color.transparent);

        clothingFrame = (FrameLayout) findViewById(R.id.clothingFrame);
        clothingText = (TextView) findViewById(R.id.clothingText);
        fadeClothingFrame = (ImageButton) findViewById(R.id.fadeClothingButton);
        outfitDesc = (LinearLayout) findViewById(R.id.outfitDesc);
        revealView = (RevealColorView) findViewById(R.id.revealView);

        revealListener = new RevealAnimListener(color, backgroundColor);
        afterReveal = new AfterRevealAnimListener();
        animListener = new AnimListener();

        roundClothView = (RoundCornerView) findViewById(R.id.roundClothView);

        Bundle extras = getIntent().getExtras();
        bm = (String) extras.get("SERVING_URL");
        passedEmail = (String) extras.get("EMAIL");
        clothes = (String) extras.get("CLOTHES");

        imageLoader.displayImage(bm + "=s1980", image, displayOptions, new LoadingListener(userEmail));
        userEmail.setText(passedEmail);

        clothingItems = new ArrayList<>();
        populateClothesList(clothes);

        fab.setOnClickListener(this);
        clothingText.setOnClickListener(this);
        fadeClothingFrame.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addClothingItemsFab:
                animateClothingDisplay();
                break;
            case R.id.clothingText:
                animateClothingDisplay();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadingListener extends SimpleImageLoadingListener {
        TextView mText;
        Palette p;

        public LoadingListener(TextView text) {
            mText = text;
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            super.onLoadingComplete(imageUri, view, loadedImage);
            p = Palette.generate(loadedImage);
            mText.setBackgroundColor(p.getVibrantColor(R.color.red_400));
            revealListener.setColor(p.getVibrantColor(R.color.red_a400));
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            super.onLoadingCancelled(imageUri, view);
        }
    }

    private void animateClothingDisplay() {
        if (!isHidden) {
            upY(image.getHeight());
            isHidden = true;
        } else {
            downY(image.getHeight());
            isHidden = false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //revealView.setVisibility(View.INVISIBLE);
                revealView.getBackground().setAlpha(155);
                break;
            case MotionEvent.ACTION_UP:
                //revealView.setVisibility(View.VISIBLE);
                revealView.getBackground().setAlpha(255);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    private void upY(int height) {
        ValueAnimator a;
        a = slideAnimator(0, -(int)((height * 0.5)));
        //a = slideAnimator(animationExtent, 0);
        a.start();
    }

    private void downY(int height) {
        ValueAnimator a;
        a = slideAnimator((-(int)(height * 0.5)), 0);
        //a = slideAnimator(0, animationExtent);
        a.start();
    }

    private Point getLocationInView(View src, View target) {
        final int[] l0 = new int[2];
        src.getLocationOnScreen(l0);

        final int[] l1 = new int[2];
        target.getLocationOnScreen(l1);

        l1[0] = (l1[0] - l0[0] + target.getWidth() / 2);
        l1[1] = (l1[1] - l0[1] + target.getHeight() /2);
        return new Point(l1[0], l1[1]);
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(400);
        animator.setInterpolator(new OvershootInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer value = (Integer) valueAnimator.getAnimatedValue();
                //clothingFrame.setTranslationY((float) value);
                //fab.setTranslationY((float) value);
            }

        });
        animator.addListener(revealListener);
        return animator;
    }

    public class RevealAnimListener implements Animator.AnimatorListener {

        int color;
        int backgroundColor;

        public RevealAnimListener(int color, int backgroundColor) {
            this.color = color;
            this.backgroundColor = backgroundColor;
        }

        public void setColor(int color) {
            this.color = color;
        }

        @Override
        public void onAnimationStart(Animator a) {
            if(!isHidden) {

            }
        }

        @Override
        public void onAnimationEnd(Animator a) {
            final Point p = getLocationInView(revealView, fab);
            if(isHidden) {

                revealView.reveal(p.x, p.y, color, fab.getHeight() / 2, 540, afterReveal);
                toolBarQuickReturnCallback(true);
                //clothingFrame.setVisibility(View.VISIBLE);
            } else {
                clothingFrame.setVisibility(View.GONE);
                revealView.hide(p.x, p.y, backgroundColor, 0, 300, afterReveal);
                //clothingFrame.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(Animator a) {

        }

        @Override
        public void onAnimationRepeat(Animator a) {

        }
    }

    public class AfterRevealAnimListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator a) {
            if(isHidden) {
                fab.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onAnimationEnd(Animator a) {
            if (isHidden) {
                clothingFrame.setVisibility(View.VISIBLE);

                //toolBarQuickReturnCallback(true);
            } else {
                toolBarQuickReturnCallback(false);
                fab.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(Animator a) {

        }

        @Override
        public void onAnimationRepeat(Animator a) {

        }
    }

    public void toolBarQuickReturnCallback(boolean state) {
        if (state) {
            if (toolbar.getVisibility() == View.VISIBLE) {
                animateToolbar(state);
            }
        } else if (!state) {
            if (toolbar.getVisibility() == View.INVISIBLE) {
                animateToolbar(state);
            }
        }

    }

    private void animateToolbar(boolean state) {
        animListener.setState(state);
        Animation anim = AnimationUtils.loadAnimation(this, (state) ? R.anim.roll_up : R.anim.roll_down);
        anim.setAnimationListener(animListener);
        toolbar.startAnimation(anim);
    }

    public class AnimListener implements Animation.AnimationListener {

        boolean state;

        public AnimListener() {

        }

        public void setState(boolean state) {
            this.state = state;
        }

        @Override
        public void onAnimationStart(Animation anim) {
        };

        @Override
        public void onAnimationRepeat(Animation anim) {
        };

        @Override
        public void onAnimationEnd(Animation anim) {
            if (state) {
                if (toolbar.getVisibility() == View.VISIBLE)
                    toolbar.setVisibility(View.INVISIBLE);

            } else if (!state) {
                if (toolbar.getVisibility() == View.INVISIBLE)
                    toolbar.setVisibility(View.VISIBLE);
            }
        };
    }

    private void populateClothesList(String json) {
        try {
            JSONArray clothes = new JSONArray(json);
            for (int i=0; i<clothes.length(); i++) {
                JSONObject item = clothes.getJSONObject(i);
                ClothingItem cloth = new ClothingItem(item.getString("TAG"), item.getString("TEXT"));
                clothingItems.add(cloth);
                outfitDesc.addView(getRoundTextView(cloth));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TextView getTextViewFromItem(ClothingItem item) {
        TextView tv = new TextView(this);
        tv.setText(item.getClothingTag() + ": " + item.getClothingText());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(params);
        tv.setTextSize(16f);
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private RoundCornerView getRoundTextView(ClothingItem item) {
        RoundCornerView v = new RoundCornerView(this);
        v.setLetter(item.getClothingTag() + ": " + item.getClothingText());
        //v.setLayoutParams((FrameLayout.LayoutParams) roundClothView.getLayoutParams());
        //v.setVisibility(View.VISIBLE);
        //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //v.setLayoutParams(params);
        return v;
    }
}
