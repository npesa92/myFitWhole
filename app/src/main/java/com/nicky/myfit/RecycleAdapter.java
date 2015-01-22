package com.nicky.myfit;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nicky.myfitbackend.postEndpoint.model.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by nicholas on 11/15/14.
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<Post> dataSet;
    private static final int TYPE_HEADER=0, TYPE_ITEM=1;

    ImageLoader imageLoader;
    DisplayImageOptions displayOptions;

    Calendar c;
    private String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    TextView subtitleTV;
    FrameLayout headerContainer;
    View headerView;

    Context context;

    AdapterCallbacks listener;

    public RecycleAdapter(ArrayList<Post> dataSet, Context context, AdapterCallbacks listener) {
        this.dataSet = dataSet;
        imageLoader = ImageLoader.getInstance();
        c = new GregorianCalendar();
        this.context = context;
        this.listener = listener;
        displayOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_action_pic_error)
                .showImageOnLoading(R.drawable.ic_action_pic_loading).build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           if(viewType == TYPE_HEADER) {
               View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false);
               HeaderView hv = new HeaderView(v);
               listener.setHeaderViewInParent(hv.view);
               return hv;
           } else if(viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listlay, parent, false);
               return new ItemView(v);
           }

        throw new RuntimeException("There is no view:" + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderView) {
            this.subtitleTV = ((HeaderView) holder).subtitle;
            this.headerContainer = ((HeaderView) holder).headerCont;
            this.headerView = ((HeaderView) holder).view;
        } else if (holder instanceof ItemView) {
            Post current = getItem(position);
            c.setTimeInMillis(current.getPostTime());
            String[] s = current.getUserEmail().split("@");
            ((ItemView) holder).lazyText.setText(s[0]);
            ((ItemView) holder).postDate.setText(months[c.get(Calendar.MONTH)] + " " + c.get(Calendar.DAY_OF_MONTH));
            ((ItemView) holder).servingUrl= current.getServingUrl();
            ((ItemView) holder).json = current.getJsonClothingItems();
            imageLoader.displayImage(current.getServingUrl() + "=s1980", ((ItemView) holder).lazyImage, displayOptions, new LoadingListener(((ItemView) holder).postDate));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public View getHeader() {
        return headerView;
    }

    private boolean isPositionHeader(int position) {
        return position==0;
    }

    private Post getItem(int position) {
        return dataSet.get(position-1);
    }

    public void setTransalationHeaderY(float math) {
        headerContainer.setTranslationY(math);
    }

    public void setTranslationXTV(float math) {
        subtitleTV.setTranslationX(math);
    }

    public interface AdapterCallbacks {
        public void setHeaderViewInParent(View v);
    }

    class HeaderView extends RecyclerView.ViewHolder {
        public TextView subtitle;
        FrameLayout headerCont;
        View view;
        public HeaderView(View v) {
            super(v);
            subtitle = (TextView) v.findViewById(R.id.subtitle);
            headerCont = (FrameLayout) v.findViewById(R.id.headerContainer);
            view = v.findViewById(R.id.imageView);
        }
    }

    class ItemView extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final double EXPANDED_AMOUNT = 0.5;
        public ImageView lazyImage;
        public TextView lazyText, postDate, jsonClothingItems;
        private int originalHeight = 0;
        private boolean isViewExpanded = false;
        CardView view;
        public Bitmap image;
        public String servingUrl, json;
        public ItemView(View v) {
            super(v);
            view = (CardView) v.findViewById(R.id.cardView);
            lazyText = (TextView) v.findViewById(R.id.lazyUserEmail);
            lazyImage = (ImageView) v.findViewById(R.id.lazyImagePlace);
            postDate = (TextView) v.findViewById(R.id.postDate);
            jsonClothingItems = (TextView) v.findViewById(R.id.jsonClothingItems);

            view.setOnClickListener(this);
            postDate.setTextColor(context.getResources().getColor(android.R.color.white));
            setTextViewTypeFace(postDate, "Roboto-Thin.ttf");
            setTextViewTypeFace(lazyText, "Roboto-Light.ttf");

        }

        @Override
        public void onClick(final View v) {
            Intent detail = new Intent(context, DetailActivity.class);
            detail.putExtra("SERVING_URL", servingUrl);
            detail.putExtra("EMAIL", lazyText.getText().toString());
            detail.putExtra("CLOTHES", json);
            context.startActivity(detail);
        }

        private void expand() {
            ValueAnimator animator = slideAnimator(originalHeight, originalHeight + (int)(originalHeight*EXPANDED_AMOUNT));
            animator.start();
        }

        private void collapse() {
            ValueAnimator animator = slideAnimator(originalHeight + (int)(originalHeight*EXPANDED_AMOUNT), originalHeight);
            animator.start();
        }

        private ValueAnimator slideAnimator(int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.setDuration(300);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
               @Override
               public void onAnimationUpdate(ValueAnimator valueAnimator) {
                   Integer value = (Integer) valueAnimator.getAnimatedValue();
                   view.getLayoutParams().height = value.intValue();
                   view.requestLayout();


               }
            });
            return animator;
        }
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
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            super.onLoadingCancelled(imageUri, view);
        }
    }

    private void setTextViewTypeFace(TextView t, String tf) {
        Typeface type = Typeface.createFromAsset(context.getAssets(), tf);
        t.setTypeface(type);
    }


}
