package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class CommunityQAFragment extends Fragment {

    private OnQuestionSelected mListener;
    private ArrayList<String> questions;
    private ArrayList<String> ids;

    public static CommunityQAFragment newInstance() {
        CommunityQAFragment fragment = new CommunityQAFragment();
        return fragment;
    }

    public CommunityQAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_community_q, container, false);
        final ListView listview = (ListView) v.findViewById(R.id.communityquestions);
        questions = new ArrayList<String>();
        ids = new ArrayList<String>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PostObject");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null) {
                    for (ParseObject item : objects) {
                        if (item != null) {
                            questions.add(item.getString("post_title"));
                            ids.add(item.getObjectId());
                        }
                    }
                    if(getActivity() != null) {
                        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), questions);
                        listview.setAdapter(adapter);
                        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, final View view,
                                                    final int position, long id) {
                                String questionId = ids.get(position).toString();
                                mListener.openQuestion(questionId);
                            }

                        });
                    }

                } else {
                    // Unable to retrieve list
                }
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnQuestionSelected) activity;
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

    public interface OnQuestionSelected {
        public void openQuestion(String qId);
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> qs;

        public MySimpleArrayAdapter(Context context, ArrayList<String> questions) {
            super(context, -1, questions);
            this.context = context;
            this.qs = questions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_questions, parent, false);
            TextView question = (TextView) convertView.findViewById(R.id.questionRow);
            question.setText(qs.get(position));
            return convertView;
        }
    }
}
