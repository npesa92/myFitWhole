package com.nicky.myfit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;
import com.nicky.myfitbackend.postEndpoint.model.Post;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements PostFeedFragment.OnFragmentInteractionListener,
                            AddPostFragment.OnFragmentInteractionListener, View.OnClickListener {

    private final String HASH_ICON = "hashIcon", HASH_TEXT = "hashText";

    public final String ADD_TAG = "addFragment", VIEW_TAG = "viewFragment";
    public String currentTag = "";
    Fragment currentFrag;


    DrawerLayout drawerLayout;
    FrameLayout contentFrame;
    Toolbar toolbar;
    FrameLayout navDrawerContainer;
    ListView navList;
    ActionBarDrawerToggle drawerToggle;
    SimpleAdapter navAdapter;
    ProgressBar progressBar;

    ArrayList<HashMap<String, String>> navMap;
    int[] navIcons = {R.drawable.ic_action_view_feed, R.drawable.ic_action_camera}, to = {R.id.navDrawerItemImage, R.id.navDrawerItemText};
    String[] navText = {"Photo Feed", "Add Photo"}, from = {HASH_ICON, HASH_TEXT};

    Button settings, navLogin;

    String userEmail;
    AuthorizationCheckTask authTask;
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;

    SharedPreferences prefs;
    SharedPreferences.Editor edit;

    TextView userTextEmail;
    CircularView letterCircularView;

    AnimListener animListener;

    Drawable toolbarShadowBackground;
    int toolbarBackground;

    PostFeedFragment postFrag;
    AddPostFragment addFrag;
    FragmentManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = getFragmentManager();

        if (savedInstanceState != null) {
            //this.onRestoreInstanceState(savedInstanceState);
        } else {

            drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
            contentFrame = (FrameLayout) findViewById(R.id.contentFrame);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            navDrawerContainer = (FrameLayout) findViewById(R.id.navDrawerContainer);
            navList = (ListView) findViewById(R.id.navList);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            drawerToggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.drawer_open, R.string.drawer_close
            );

            userTextEmail = (TextView) findViewById(R.id.userTextEmail);

            settings = (Button) findViewById(R.id.navSettings);
            navLogin = (Button) findViewById(R.id.navLogin);
            navLogin.setTextColor(getResources().getColor(R.color.grey_500));
            settings.setTextColor(getResources().getColor(R.color.grey_500));
            navLogin.setOnClickListener(this);
            settings.setOnClickListener(this);

            animListener = new AnimListener();

            letterCircularView = (CircularView) findViewById(R.id.letterCircularView);

            toolbarBackground = getResources().getColor(R.color.md_red_a200);
            toolbarShadowBackground =getResources().getDrawable(R.drawable.toolbar_shadow);

            prefs = getSharedPreferences("USER_PREFS", 0);
            edit = prefs.edit();


            setUpNavList();
            initializeImageLoader();
            setSupportActionBar(toolbar);
            drawerLayout.setDrawerListener(drawerToggle);

            userEmail = prefs.getString("userEmail", "");
            if (Strings.isNullOrEmpty(userEmail)) {
                Toast.makeText(MainActivity.this, "Please Log In", Toast.LENGTH_SHORT).show();
            } else {
                userTextEmail.setText(userEmail);
                String l = userEmail.toUpperCase().charAt(0) + "";
                letterCircularView.setLetter(l);
                postFrag = (PostFeedFragment) getFragmentManager().findFragmentByTag(VIEW_TAG);
                if (postFrag == null) {
                    postFrag = PostFeedFragment.newInstance(toolbar);
                }
                manager.beginTransaction().replace(contentFrame.getId(), postFrag, VIEW_TAG).commit();
            }


        }

    }

    public void setUpNavList() {
        navMap = new ArrayList<HashMap<String, String>>();
        for (int i=0; i<2; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put(HASH_ICON, Integer.toString(navIcons[i]));
            hm.put(HASH_TEXT, navText[i]);
            navMap.add(hm);
        }
        navAdapter = new SimpleAdapter(this, navMap, R.layout.drawer, from, to);
        navList.setAdapter(navAdapter);
        navList.setOnItemClickListener(new NavItemClickListener());
    }

    public void initializeImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .displayer(new FadeInBitmapDisplayer(300)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onProgressLoading(boolean state) {
        changeProgressBar(state);

    }

    @Override
    public void changeToolbarAlpha(int math) {
        toolbar.getBackground().setAlpha(math);
    }

    @Override
    public void toolbarQuickReturnAddFragment(boolean state) {
        animateToolbar(state);
    }

    @Override
    public void onAddPostProgress(boolean state) {
        changeProgressBar(state);
    }

    @Override
    public void toolbarQuickReturn(boolean state) {
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

    private void changeProgressBar(boolean state) {
        if(state)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.navLogin:
                onClickSignIn();
                break;
            case R.id.navSettings:
                break;
            default:
                Toast.makeText(this, "Nothing assigned to button yet", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    class NavItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            switch(position) {
                case 0:
                    postFrag = (PostFeedFragment) manager.findFragmentByTag(VIEW_TAG);
                    if (postFrag == null) {
                        postFrag = PostFeedFragment.newInstance(toolbar);
                    }
                    manager.beginTransaction().replace(contentFrame.getId(), postFrag, VIEW_TAG).commit();
                    drawerLayout.closeDrawer(navDrawerContainer);
                    currentFrag = postFrag;
                    currentTag = VIEW_TAG;
                    toolbar.getBackground().setAlpha(255);
                    highlightSelectedItem(0);
                    break;
                case 1:
                    addFrag = (AddPostFragment) manager.findFragmentByTag(ADD_TAG);
                    if (addFrag == null) {
                        addFrag = AddPostFragment.newInstance();
                    }
                    manager.beginTransaction().replace(contentFrame.getId(), addFrag, ADD_TAG).commit();
                    drawerLayout.closeDrawer(navDrawerContainer);
                    currentFrag = addFrag;
                    currentTag = ADD_TAG;
                    toolbar.getBackground().setAlpha(255);
                    highlightSelectedItem(1);
                    break;
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        /*FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (inState != null) {
            String fragTag = inState.getString("FRAG_TAG");
            System.out.println(fragTag);
            if (VIEW_TAG.equals(fragTag)) {
                PostFeedFragment postFrag = (PostFeedFragment) manager.getFragment(inState, VIEW_TAG);
                transaction.replace(R.id.contentFrame, postFrag, VIEW_TAG).commit();
                currentFrag = postFrag;
                currentTag = VIEW_TAG;
            } else if (ADD_TAG.equals(fragTag)) {
                AddPostFragment addFrag = (AddPostFragment) manager.getFragment(inState, ADD_TAG);
                transaction.replace(R.id.contentFrame, addFrag, ADD_TAG);
                currentFrag = addFrag;
                currentTag = ADD_TAG;
            }
        } */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        userEmail = accountName;
        performAuthCheck(accountName);
    }

    public void onClickSignIn() {
        int googleAccounts = AppConstants.countGoogleAccounts(this);
        if (googleAccounts == 0) {
            Toast.makeText(MainActivity.this, "No Account On Device", Toast.LENGTH_SHORT).show();
        } else if (googleAccounts == 1) {
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length >0) {
                userEmail = accounts[0].name;
                performAuthCheck(accounts[0].name);
            }
            Toast.makeText(MainActivity.this, "One Account, Using it", Toast.LENGTH_SHORT).show();
        } else {
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select the account to use with MyFit", null, null, null);
            startActivityForResult(accountSelector, ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }
    }

    public void performAuthCheck(String userEmail) {
        if (authTask != null) {
            try {
                authTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new AuthorizationCheckTask().execute(userEmail);
    }

    public void commitEmailToPrefs(String email) {
        edit.putString("userEmail", email);
        edit.commit();
    }

    class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... emails) {
            if (!AppConstants.checkGooglePlayServicesAvailable(MainActivity.this)) {
                return false;
            }
            String emailAccount = emails[0];
            authTask = this;

            if (Strings.isNullOrEmpty(emailAccount)) {
                Toast.makeText(MainActivity.this, "Null Email", Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                        MainActivity.this, AppConstants.AUDIENCE);
                credential.setSelectedAccountName(emailAccount);
                String accessToken = credential.getToken();
                userEmail = emailAccount;
                return true;
            } catch (GoogleAuthException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(MainActivity.this, "User Account: " + userEmail, Toast.LENGTH_SHORT).show();
                commitEmailToPrefs(userEmail);
            } else {
                Toast.makeText(MainActivity.this, "No Account Error", Toast.LENGTH_SHORT).show();
            }

            authTask = null;
        }

    }

    private void setTextViewTypeFace(TextView t, String tf) {
        Typeface type = Typeface.createFromAsset(getAssets(), tf);
        t.setTypeface(type);
    }

    public void highlightSelectedItem(int position) {

    }


}
