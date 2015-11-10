package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.msc.bilawalsoomro.guitarrepair.helpers.DatabaseHelper;

import java.util.ArrayList;

public class ArticleListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private onGuitarArticleSelected mListener;
    private Cursor c;

    public static ArticleListFragment newInstance(String param1, String param2) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ArticleListFragment newInstance(int id) {
        ArticleListFragment fragment = new ArticleListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Integer.toString(id));
        fragment.setArguments(args);
        return fragment;
    }

    public ArticleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_article_list, container, false);
        final ListView listview = (ListView) v.findViewById(R.id.articlelistview);
        final ArrayList<String> articles = new ArrayList<String>();

        try {
            DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
            myDbHelper.openDatabase();
            c = myDbHelper.listArticlesCursor(mParam1);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String component = c.getString(c.getColumnIndex("title"));
                        articles.add(component);
                    } while (c.moveToNext());
                }
            }
        }
        catch(SQLException sqle){
            throw sqle;
        }

        final String[] values = new String[articles.size()];
        articles.toArray(values);

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), list);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                c.moveToPosition(position);
                mListener.onArticleSelected(c.getInt(0), c.getString(2));
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onGuitarArticleSelected) activity;
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

    public interface onGuitarArticleSelected {
        public void onArticleSelected(int id, String compId);
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public MySimpleArrayAdapter(Context context, ArrayList<String> values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_row_articles, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.componentName);
            textView.setText(values.get(position));
            return rowView;
        }
    }
}
