package com.nicky.myfit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.common.base.Strings;
import com.melnykov.fab.*;
import com.melnykov.fab.FloatingActionButton;
import com.nicky.myfit.view.ObservableRecyclerView;
import com.nicky.myfitbackend.postEndpoint.model.CollectionResponsePost;
import com.nicky.myfitbackend.postEndpoint.model.Post;
import com.nicky.myfitbackend.postEndpoint.PostEndpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFeedFragment extends Fragment implements View.OnClickListener, ObservableRecyclerView.Callbacks, RecycleAdapter.AdapterCallbacks {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int STATE_ONSCREEN = 0, STATE_OFFSCREEN = 1, STATE_RETURNING = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    ArrayList<Post> postsList = new ArrayList<Post>();
    PostEndpoint service;
    CollectionResponsePost posts;
    ObservableRecyclerView recyclerView;
    TextView noContentText;
    String userEmail, nextPageToken;
    SharedPreferences prefs;
    GoogleAccountCredential credential;
    RecycleAdapter recyclerAdapter;
    LinearLayoutManager layoutManager;

    int headerHeight = 800;
    int minHeaderHeight = 56;
    int toolbarTitleLeftMargin = 72;

    com.melnykov.fab.FloatingActionButton refreshButton;

    View mPlaceholderView;
    static Toolbar mQuickReturnView;
    private int mMinRawY = 0, mState = STATE_ONSCREEN, mQuickReturnHeight, mMaxScrollY;

    RecycleAdapter.AdapterCallbacks c;



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment PostFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFeedFragment newInstance(Toolbar toolbar) {
        PostFeedFragment fragment = new PostFeedFragment();
        Bundle argument = new Bundle();
        mQuickReturnView = toolbar;
        return fragment;
    }

    public PostFeedFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        layoutManager = new LinearLayoutManager(getActivity());

        prefs = getActivity().getSharedPreferences("USER_PREFS", 0);
        userEmail = prefs.getString("userEmail", "");
        if (Strings.isNullOrEmpty(userEmail)) {
            Toast.makeText(getActivity(), "No User Credentials, Cannot make Requests", Toast.LENGTH_SHORT).show();
        } else {
            credential = GoogleAccountCredential.usingAudience(getActivity(), AppConstants.AUDIENCE);
            credential.setSelectedAccountName(userEmail);
            service = AppConstants.getApiHandle(credential);
        }
        c = this;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View v = inflater.inflate(R.layout.fragment_post_feed, container, false);
            recyclerView = (ObservableRecyclerView) v.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(layoutManager);

            recyclerView.setCallbacks(this);
            noContentText = (TextView) v.findViewById(R.id.noContentTextView);

            refreshButton = (FloatingActionButton) v.findViewById(R.id.refreshButton);
            refreshButton.setOnClickListener(this);

            new GetPosts(c).execute();

            return v;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.refreshButton:
                refreshPostList();
                break;
        }
    }

    /* TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    public void progressLoadingCallback(boolean state) {
        if (mListener != null) {
            mListener.onProgressLoading(state);
        }
    }

    public void changeToolbarAlphaCallback(int math) {
        if (mListener != null) {
            mListener.changeToolbarAlpha(math);
        }
    }

    public void toolbarQuickReturnCallBack(boolean state) {
        if (mListener != null) {
            mListener.toolbarQuickReturn(state);
        }

    }

    @Override
    public void setHeaderViewInParent(View v) {
        mPlaceholderView = v;
    }

    class GetPosts extends AsyncTask<Void, Void, CollectionResponsePost> {
        RecycleAdapter.AdapterCallbacks c;

        public GetPosts(RecycleAdapter.AdapterCallbacks c) {
            this.c = c;
        }

        @Override
        protected void onPreExecute() {
            progressLoadingCallback(true);
        }

        @Override
        protected CollectionResponsePost doInBackground(Void... params) {
            try {
                //service = AppConstants.getApiHandle(credential);
                posts = service.list().execute();
                return posts;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(CollectionResponsePost result) {
            progressLoadingCallback(false);
            if (result != null) {
                postsList = (ArrayList) result.getItems();
                if (postsList!= null) {
                    nextPageToken = result.getNextPageToken();
                    Collections.sort(postsList, AppConstants.descendingTime);
                    recyclerAdapter = new RecycleAdapter(postsList, getActivity(), c);
                    recyclerView.setAdapter(recyclerAdapter);
                    noContentText.setVisibility(View.GONE);
                    //mPlaceholderView = recyclerAdapter.getHeader();
                    recyclerView.setReturningView(mQuickReturnView);

                    Toast.makeText(getActivity(), nextPageToken, Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("No Posts");
                }
            }
        }
    }

    public void refreshPostList() {
        new AsyncTask<Void, Void, CollectionResponsePost>() {

            @Override
            protected void onPreExecute() {
                progressLoadingCallback(true);
            }

            @Override
            public CollectionResponsePost doInBackground(Void... params) {
                try {
                    posts = service.list().execute();
                    return posts;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(CollectionResponsePost result) {
                progressLoadingCallback(false);
                if (result != null) {
                    postsList = (ArrayList) result.getItems();
                    //Collections.sort(postsList, AppConstants.ascendingTime);
                    Collections.reverse(postsList);
                    recyclerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Null Result", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
        public void onProgressLoading(boolean state);
        public void changeToolbarAlpha(int math);
        public void toolbarQuickReturn(boolean state);
    }

    @Override
    public void onScrollChanged(int scrollY) {

        changeToolbarAlphaCallback(scrollY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }

}
