package com.nicky.myfit;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.Strings;
import com.melnykov.fab.FloatingActionButton;
import com.nicky.myfit.util.FileUtil;
import com.nicky.myfitbackend.postEndpoint.PostEndpoint;
import com.nicky.myfitbackend.postEndpoint.model.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import at.markushi.ui.ActionView;
import at.markushi.ui.RevealColorView;
import at.markushi.ui.action.CloseAction;
import at.markushi.ui.action.PlusAction;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPostFragment extends Fragment implements View.OnClickListener, View.OnTouchListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ImageView imagePlace;

    ImageButton addClothingButton, fadeClothingButton;
    LinearLayout outfitDesc;
    FrameLayout clothingFrame;
    FrameLayout.LayoutParams clothingParams;
    View selectedView;
    FrameLayout content;
    View topShadow;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    String userEmail;
    long userPostId;
    String servingUrl;
    String blobKey;
    String imagePath;

    GoogleAccountCredential credential;
    PostEndpoint service;

    Bitmap selectedImage;
    ImageLoader imageLoader;

    String uploadurl = "http://arctic-pagoda-766.appspot.com/upload.jsp";

    TextView clothingText;

    private int addButtonIcon = R.drawable.ic_create,
                deleteButtonIcon = R.drawable.ic_clear,
                choosePhotoIcon = R.drawable.ic_perm_media;

    FloatingActionButton fab, addClothingItemsFab;

    int i = 0;
    int xDelta, yDelta;
    boolean isHidden;
    int animationExtent;

    int imageHeight = 0, imageWidth = 0;

    CircularView cView;
    DisplayImageOptions displayOptions;

    RevealColorView revealView;
    RevealAnimListener revealListener;
    AfterRevealAnimListener afterReveal;

    Palette p;
    int revealColor, hideColor;

    String[] spinnerChoices = {"top", "bottom", "shoes", "hat"};
    String spinnerChoice="", clothingInput = "";

    ArrayList<ClothingItem> clothingItems;

    FileUtil fileUtil;

    public static AddPostFragment newInstance() {
        AddPostFragment fragment = new AddPostFragment();
        return fragment;
    }

    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("USER_PREFS", 0);
        edit = prefs.edit();

        imageLoader = ImageLoader.getInstance();

        userEmail = prefs.getString("userEmail", "");
        if (Strings.isNullOrEmpty(userEmail)) {
            Toast.makeText(getActivity(), "No CREDENTIAL", Toast.LENGTH_SHORT).show();
        } else {
            credential = GoogleAccountCredential.usingAudience(getActivity(), AppConstants.AUDIENCE);
            credential.setSelectedAccountName(userEmail);
            service = AppConstants.getApiHandle(credential);
        }

        displayOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).resetViewBeforeLoading(true)
                .showImageOnFail(R.drawable.ic_action_pic_error)
                .showImageOnLoading(R.drawable.ic_action_pic_loading).build();

        fileUtil = new FileUtil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_post, container, false);
        imagePlace = (ImageView) v.findViewById(R.id.imageUpload);
        fab = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);
        addClothingButton = (ImageButton) v.findViewById(R.id.addClothingItems);
        outfitDesc = (LinearLayout) v.findViewById(R.id.outfitDesc);
        fab.setOnClickListener(this);
        fab.setType(0);
        imagePlace.setOnClickListener(this);
        addClothingButton.setOnClickListener(this);
        addClothingButton.setColorFilter(getResources().getColor(android.R.color.white));

        addClothingItemsFab = (FloatingActionButton) v.findViewById(R.id.addClothingItemsFab);

        content = (FrameLayout) v.findViewById(R.id.contentRelativeLay);
        animationExtent = 300;
        outfitDesc.setOnTouchListener(this);

        clothingFrame = (FrameLayout) v.findViewById(R.id.clothingFrame);
        clothingFrame.setOnClickListener(this);
        addClothingItemsFab.setOnClickListener(this);
        clothingParams = (FrameLayout.LayoutParams) clothingFrame.getLayoutParams();

        //cView = (CircularView) v.findViewById(R.id.cView);
        //cView.setDrawingCacheEnabled(true);

        clothingText = (TextView) v.findViewById(R.id.clothingText);
        clothingText.setOnClickListener(this);

        hideColor = getResources().getColor(android.R.color.transparent);
        revealColor = getResources().getColor(R.color.md_red_a200);

        revealView = (RevealColorView) v.findViewById(R.id.revealView);
        revealListener = new RevealAnimListener(revealColor, hideColor);
        afterReveal = new AfterRevealAnimListener();

        fadeClothingButton = (ImageButton) v.findViewById(R.id.fadeClothingButton);
        fadeClothingButton.setOnTouchListener(this);

        clothingItems = new ArrayList<>();

        userPostId = prefs.getLong("userPostId", 0);
        servingUrl = prefs.getString("servingUrl", "");
        blobKey = prefs.getString("blobKey", "");
        if(Strings.isNullOrEmpty(servingUrl)) {
            imagePlace.setImageResource(choosePhotoIcon);
        } else {
            displayImage(servingUrl);
            fab.setImageResource(deleteButtonIcon);
        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imageUpload:
                if (Strings.isNullOrEmpty(servingUrl) && Strings.isNullOrEmpty(blobKey) && userPostId == 0) {
                    //Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.setType("image/*");
                    startActivityForResult(Intent.createChooser(i, "Select Picture"), 1);
                } else {
                    Toast.makeText(getActivity(), "Delete Post before adding another!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.floatingActionButton:
                if (Strings.isNullOrEmpty(servingUrl) && Strings.isNullOrEmpty(blobKey) && (userPostId == 0)) {
                    if (Strings.isNullOrEmpty(imagePath))
                        Toast.makeText(getActivity(), "Select Picture First!", Toast.LENGTH_SHORT).show();
                    else
                        blobAndStore(imagePath);
                } else {
                    deletePost(userPostId);
                }
                break;
            case R.id.addClothingItems:
                if (i<4) {

                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.clothing_dialog);
                    dialog.setTitle("Add Clothing Item");
                    Spinner spr = (Spinner) dialog.findViewById((R.id.clothingSpinner));
                    final EditText edit = (EditText) dialog.findViewById(R.id.clothingEdit);
                    ImageButton clothingButton = (ImageButton) dialog.findViewById(R.id.clothingButton);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerChoices);
                    spr.setAdapter(adapter);
                    spr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            spinnerChoice = spinnerChoices[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    clothingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clothingInput = edit.getText().toString();
                            TextView tv = new TextView(getActivity());
                            tv.setText(spinnerChoice + ": " + clothingInput);
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                            params.gravity=Gravity.CENTER_VERTICAL;
                            tv.setLayoutParams(params);
                            //tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                            tv.setTextSize(16f);
                            tv.setPadding(8, 8, 8, 8);
                            //cView.buildDrawingCache();
                            //tv.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), Bitmap.createBitmap(cView.getDrawingCache())), null, null, null);
                            //tv.setCompoundDrawablePadding(8);
                            outfitDesc.addView(tv);
                            clothingItems.add(new ClothingItem(spinnerChoice, clothingInput));
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    i++;
                }
                break;
            case R.id.addClothingItemsFab:
                animateClothingDisplay();
                break;
            case R.id.clothingText:
                animateClothingDisplay();
                break;

        }
    }

    private void animateClothingDisplay() {
        if (!isHidden) {
            upY(imagePlace.getHeight());
            isHidden = true;
        } else {
            downY(imagePlace.getHeight());
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


        int sideDiff = 16;

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
                addClothingItemsFab.setTranslationY((float) value);
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

        }

        @Override
        public void onAnimationEnd(Animator a) {
            final Point p = getLocationInView(revealView, addClothingItemsFab);
            if(isHidden) {

                revealView.reveal(p.x, p.y, revealColor, addClothingItemsFab.getHeight() / 2, 340, afterReveal);
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

        }

        @Override
        public void onAnimationEnd(Animator a) {
            if (isHidden) {
                clothingFrame.setVisibility(View.VISIBLE);
                addClothingItemsFab.setVisibility(View.INVISIBLE);
                //toolBarQuickReturnCallback(true);
            } else {
                toolBarQuickReturnCallback(false);
                addClothingItemsFab.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationCancel(Animator a) {

        }

        @Override
        public void onAnimationRepeat(Animator a) {

        }
    }

    private void XmlAnimators(int X, int Y, View view) {
        RelativeLayout.LayoutParams lparams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        xDelta = X - (lparams.leftMargin + lparams.rightMargin);
        yDelta = Y - (lparams.topMargin + lparams.bottomMargin);
        Animation a;
        if (!isHidden) {
            lparams.bottomMargin = - (int) (0.2 * view.getHeight());
            isHidden=true;
            a = AnimationUtils.loadAnimation(view.getContext(), R.anim.linear_jump_up);
        } else {
            lparams.bottomMargin = 0;
            isHidden=false;
            a = AnimationUtils.loadAnimation(view.getContext(), R.anim.linear_fall_down);
        }
        a.setAnimationListener(new AnimListener(view, lparams));
        view.startAnimation(a);
        //view.setLayoutParams(lparams);
    }

    public class AnimListener implements Animation.AnimationListener {

        View v;
        RelativeLayout.LayoutParams params;

        public AnimListener(View v, RelativeLayout.LayoutParams params) {
            this.v = v;
            this.params = params;
        }

        @Override
        public void onAnimationStart(Animation a) {

        }

        @Override
        public void onAnimationRepeat(Animation a) {

        }

        @Override
        public void onAnimationEnd(Animation a) {
            v.setLayoutParams(params);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 1) && (resultCode == getActivity().RESULT_OK) && (null != data)) {
            Uri uri = data.getData();
            getPaletteColorAndSetBitmap(uri);
            getFilePathFromUtil(uri);

        }
    }

    public void getFilePathFromUtil(Uri uri) {
        String imagePath = fileUtil.getPath(getActivity(), uri);
        this.imagePath = imagePath;
    }

    public void getPaletteColorAndSetBitmap(Uri uri) {
        try {
            InputStream stream = getActivity().getContentResolver().openInputStream(uri);
            selectedImage = BitmapFactory.decodeStream(stream);
            stream.close();
            imagePlace.setImageBitmap(selectedImage);
            p = Palette.generate(selectedImage);
            revealColor = p.getVibrantColor(R.color.md_red_a200);
            revealListener.setColor(revealColor);
            clothingText.setBackgroundColor(revealColor);
            addClothingButton.setBackgroundColor(revealColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void useImageLoaderResult(Context c, Intent data) {
        try {
            displayImage(data.getData().toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getRealPathFromUri(Context context, Uri uri) {
        Cursor cursor = null;
        String wholeId = DocumentsContract.getDocumentId(uri);
        String id = wholeId.split(":")[1];

        try {
            String proj[] = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                        proj, sel, new String[]{id}, null);
            int column_index = cursor.getColumnIndexOrThrow(proj[0]);
            String filePath = "";
            if (cursor.moveToFirst()){
                filePath = cursor.getString(column_index);
            }
            cursor.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public void onAddPostCallback(boolean state) {
        if (mListener != null) {
            mListener.onAddPostProgress(state);
        }
    }

    public void toolBarQuickReturnCallback(boolean state) {
        if (mListener != null) {
            mListener.toolbarQuickReturnAddFragment(state);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAddPostProgress(boolean state);
        public void toolbarQuickReturnAddFragment(boolean state);
    }

    public void blobAndStore(String imagePath) {
        new AsyncTask<String, Void, Post>() {

            Calendar c = new GregorianCalendar();
            Post result;

            @Override
            protected void onPreExecute() {
                onAddPostCallback(true);
            }

            @Override
            protected Post doInBackground(String... params) {
                System.out.println(params[0]);
                String x = getUploadUrl();
                String blobJson = POST(params[0], x);
                System.out.println(blobJson);
                try {
                    JSONObject json = new JSONObject(blobJson);
                    servingUrl = json.getString("servingUrl");
                    blobKey = json.getString("blobKey");

                    Post temp = new Post();
                    c.setTime(new Date());
                    temp.setUserEmail(userEmail);
                    temp.setServingUrl(servingUrl);
                    temp.setBlobKey(blobKey);
                    temp.setPostTime(c.getTimeInMillis());
                    if(clothingItems != null) {
                        temp.setJsonClothingItems(getClothingItemsAsJson(clothingItems));
                    }

                    result = service.insert(temp).execute();
                    return result;
                } catch(JSONException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Post result) {
                onAddPostCallback(false);
                if (result != null) {
                    commitUserPrefs(result.getId(), result.getServingUrl(), result.getBlobKey(), result.getPostTime());
                    fab.setImageResource(deleteButtonIcon);
                    userPostId = result.getId();
                    displayImage(result.getServingUrl());
                } else {
                    Toast.makeText(getActivity(), "Exception: Null Post", Toast.LENGTH_SHORT).show();
                    servingUrl = "";
                    blobKey = "";
                    commitUserPrefs(0, "", "", 0);
                }
            }

        }.execute(imagePath);
    }

    public void deletePost(Long postId) {
        new AsyncTask<Long, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Long... params) {
                String bKeyResult = GET(blobKey);
                Boolean result = false;
                try {
                    int x = Integer.parseInt(bKeyResult);
                    if (x==1) {
                        service.remove(params[0]).execute();
                        result = true;
                    }
                } catch (NumberFormatException n) {
                    n.printStackTrace();
                    result = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    commitUserPrefs(0, "", "", 0);
                    imagePlace.setImageResource(choosePhotoIcon);
                    servingUrl = "";
                    blobKey = "";
                    userPostId = 0;
                    fab.setImageResource(addButtonIcon);
                }
            }
        }.execute(postId);
    }

    public String GET(String blobKey) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://arctic-pagoda-766.appspot.com/blob/delete?blobString=" + blobKey);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            InputStream iStream = response.getEntity().getContent();
            return convertStreamToString(iStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    private String getClothingItemsAsJson(ArrayList<ClothingItem> clothingItems) {
        JSONArray clothingJsonArray = new JSONArray();
        for (ClothingItem item : clothingItems) {
            JSONObject json = new JSONObject();
            try {
                json.put("TAG", item.getClothingTag());
                json.put("TEXT", item.getClothingText());
                clothingJsonArray.put(json);
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        return clothingJsonArray.toString();
    }

    public String POST(String imgPath, String uploadUrl) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(uploadUrl);

        File f = new File(imgPath);
        FileBody fileBody = new FileBody(f);

        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("myFile", fileBody);

        httpPost.setEntity(reqEntity);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            InputStream iStream = response.getEntity().getContent();
            return convertStreamToString(iStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUploadUrl() {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://arctic-pagoda-766.appspot.com/blob/upload");
        try {
            HttpResponse response = client.execute(httpGet);
            InputStream input = response.getEntity().getContent();
            String res = convertStreamToString(input);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String convertStreamToString(InputStream iStream) {
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(iStream));
            String result = "";
            String line = "";
            while ((line = bReader.readLine()) != null) {
                result += line;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getImageFromStream(String servingUrl) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... url) {
                Bitmap blobbedImage = null;
                try {
                    InputStream in = new java.net.URL(url[0]).openStream();
                    blobbedImage = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return blobbedImage;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                imagePlace.setImageBitmap(result);
            }
        }.execute(servingUrl);
    }

    public void commitUserPrefs(long postId, String servingUrl, String blobKey, long postTime) {
        edit.putLong("userPostId", postId);
        edit.putString("servingUrl", servingUrl);
        edit.putString("blobKey", blobKey);
        edit.putLong("postTime", postTime);
        edit.commit();
    }

    private void displayImage(String servingUrl) {
        imageLoader.displayImage(servingUrl, imagePlace, displayOptions, new LoadingListener(clothingText));
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
            revealColor = p.getVibrantColor(R.color.blue_a400);
            mText.setBackgroundColor(revealColor);
            addClothingButton.setBackgroundColor(revealColor);
            revealListener.setColor(revealColor);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            super.onLoadingCancelled(imageUri, view);
        }
    }





}
