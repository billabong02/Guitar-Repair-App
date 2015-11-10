package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import com.msc.bilawalsoomro.guitarrepair.helpers.DatabaseHelper;

import java.util.ArrayList;

public class BookmarkFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;
    private ArrayList<BookmarkItem> items;
    private AbsListView mListView;
    private ListAdapter mAdapter;

    public static BookmarkFragment newInstance() {
        BookmarkFragment fragment = new BookmarkFragment();
        return fragment;
    }

    public BookmarkFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = new ArrayList<BookmarkItem>();

        try {
            DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
            myDbHelper.openDatabase();
            Cursor c = myDbHelper.getBookmarks();
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        String id = c.getString(c.getColumnIndex("_id"));
                        String title= c.getString(c.getColumnIndex("title"));
                        items.add(new BookmarkItem(id, title));
                    } while (c.moveToNext());
                }
            }
        }
        catch(SQLException sqle){
            throw sqle;
        }

        mAdapter = new ArrayAdapter<BookmarkItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, items);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        return view;
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentInteraction(items.get(position).id);
        }
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    public static class BookmarkItem {
        public String id;
        public String title;

        public BookmarkItem(String id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
