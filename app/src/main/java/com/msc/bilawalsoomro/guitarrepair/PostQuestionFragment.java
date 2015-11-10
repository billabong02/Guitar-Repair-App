package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class PostQuestionFragment extends Fragment {

    private onSubmitPost mListener;
    private EditText title;
    private EditText details;
    private Spinner components;
    private Spinner guitars;

    public static PostQuestionFragment newInstance(String param1, String param2) {
        PostQuestionFragment fragment = new PostQuestionFragment();
        return fragment;
    }

    public PostQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_question, container, false);
        setHasOptionsMenu(true);
        title = (EditText) v.findViewById(R.id.posttitle);
        details = (EditText) v.findViewById(R.id.postdetails);
        components = (Spinner) v.findViewById(R.id.spinner);
        guitars = (Spinner) v.findViewById(R.id.spinner2);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        getActivity().getMenuInflater().inflate(R.menu.menu_post_question,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:
                String pTitle = title.getText().toString();
                String pDetails = details.getText().toString();
                String pComponent = components.getSelectedItem().toString();
                String pGuitar = guitars.getSelectedItem().toString();

                final ParseObject postObject = new ParseObject("PostObject");
                postObject.put("post_title", pTitle);
                postObject.put("component_name", pComponent);
                postObject.put("post_details", pDetails);
                postObject.put("guitar_type", pGuitar);
                postObject.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            String id = postObject.getObjectId();
                            mListener.onPostSubmitted(id);
                        } else {
                            // The save failed.
                        }
                    }
                });
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onSubmitPost) activity;
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

    public interface onSubmitPost {
        public void onPostSubmitted(String id);
    }

}
