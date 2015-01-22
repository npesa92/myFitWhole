package com.nicky.myfit;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.nicky.myfitbackend.postEndpoint.PostEndpoint;
import com.nicky.myfitbackend.postEndpoint.model.Post;

import java.util.Comparator;

import javax.annotation.Nullable;

/**
 * Created by nicholas on 11/16/14.
 */
public class AppConstants {

    public static final String WEB_CLIENT_ID = "493201750581-2cmc77d039md4sgm7p17b6cv08rpc8oh.apps.googleusercontent.com";

    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;

    public static final JsonFactory JSON_FACT = new AndroidJsonFactory();

    public static final HttpTransport HTTP_TRANS = AndroidHttp.newCompatibleTransport();

    public static PostEndpoint getApiHandle(@Nullable GoogleAccountCredential credential) {
        PostEndpoint.Builder student = new PostEndpoint.Builder(AppConstants.HTTP_TRANS,
                AppConstants.JSON_FACT, credential)
                .setApplicationName("com.nicky.myfit");
        return student.build();
    }

    public static int countGoogleAccounts(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length < 1) {
            return 0;
        } else {
            return accounts.length;
        }
    }

    public static boolean checkGooglePlayServicesAvailable(Activity activity) {
        final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(activity, connectionStatusCode);
            return false;
        }
        return true;
    }

    public static void showGooglePlayServicesAvailabilityErrorDialog(final Activity activity,
                                                                     final int connectionStatusCode) {
        final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode, activity, REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    public static Comparator<Post> descendingTime = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o2.getPostTime().compareTo(o1.getPostTime());
        }
    };

    public static Comparator<Post> ascendingTime = new Comparator<Post>() {
        @Override
        public int compare(Post o1, Post o2) {
            return o1.getPostTime().compareTo(o2.getPostTime());
        }
    };
}
