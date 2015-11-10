package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewQuestionFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    TextView title;
    TextView details;
    TextView component;
    TextView guitar;
    EditText comment;
    Button submit;

    ArrayList<String> answers = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    private OnFragmentInteractionListener mListener;

    public static ViewQuestionFragment newInstance(String id) {
        ViewQuestionFragment fragment = new ViewQuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_question, container, false);
        title = (TextView) v.findViewById(R.id.QPostTitle);
        details = (TextView) v.findViewById(R.id.QPostDetails);
        component = (TextView) v.findViewById(R.id.QComponent);
        comment = (EditText) v.findViewById(R.id.QMyAnswer);
        submit = (Button) v.findViewById(R.id.QSubmitAnswer);
        guitar = (TextView) v.findViewById(R.id.QGuitar);



        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, answers);
        ListView ls = (ListView) v.findViewById(R.id.QAnswerList);
        ls.setAdapter(adapter);

        ParseQuery<ParseObject> postQuery = ParseQuery.getQuery("PostObject");
        Log.d("checking id again", mParam1);
        postQuery.getInBackground(mParam1, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    title.setText(object.getString("post_title"));
                    details.setText(object.getString("post_details"));
                    component.setText(object.getString("component_name"));
                    guitar.setText(object.getString("guitar_type"));

                    ParseQuery<ParseObject> commentsQuery = ParseQuery.getQuery("CommentObject");
                    commentsQuery.whereEqualTo("parent", object);
                    commentsQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> comments, ParseException e) {
                            if (e == null) {
                                for (ParseObject item : comments) {
                                    addAnswerToView(item.getString("content").toString());
                                }
                            } else {
                                Log.d("comments", e.toString());
                            }
                        }
                    });

                } else {
                    Log.d("question", e.toString());
                    // An error occured
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer = comment.getText().toString();
                ParseObject myComment = new ParseObject("CommentObject");
                myComment.put("content", answer);
                myComment.put("parent", ParseObject.createWithoutData("PostObject", mParam1));
                myComment.saveInBackground();
                comment.setText("");
                addAnswerToView(answer);
            }
        });

        return v;
    }

    public void addAnswerToView(String answer) {
        answers.add(answer);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
