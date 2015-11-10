package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msc.bilawalsoomro.guitarrepair.helpers.DatabaseHelper;

import java.util.ArrayList;


public class ViewArticleFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private TextView tv;
    private TextView tv2;
    private MenuItem bookmarkMenu;
    private Boolean isBookmarked;
    private ViewPager mViewPager;
    private ArrayList <Integer> imagesResouceId;


    private OnFragmentInteractionListener mListener;

    public static ViewArticleFragment newInstance(String param1, String param2) {
        ViewArticleFragment fragment = new ViewArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ViewArticleFragment newInstance(int id, String ComponentId) {
        ViewArticleFragment fragment = new ViewArticleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, Integer.toString(id));
        args.putString(ARG_PARAM2, ComponentId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewArticleFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_view_article, container, false);
        setHasOptionsMenu(true);
        tv = (TextView) v.findViewById(R.id.articleTitle);
        tv2 = (TextView) v.findViewById(R.id.articleContent);
        ImageView icon = (ImageView) v.findViewById(R.id.articleIcon);
        try {
            DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
            myDbHelper.openDatabase();

            if (mParam2 != null) {
                Cursor i = myDbHelper.getComponent(mParam2);
                if(i != null) {
                    if (i.moveToFirst()) {
                        do {
                            Resources resources = getActivity().getResources();
                            int resourceId = resources.getIdentifier(i.getString(i.getColumnIndex("image_resource")), "drawable", getActivity().getPackageName());
                            icon.setImageResource(resourceId);
                        } while (i.moveToNext());
                    }
                }
            }

            Cursor c = myDbHelper.viewArticleCursor(mParam1);
            if (c != null) {
                int rows = c.getCount();
                if (c.moveToFirst()) {
                    do {
                        String title = c.getString(c.getColumnIndex("title"));
                        String content = c.getString(c.getColumnIndex("content"));
                        tv.setText(title);
                        tv2.setText(content);
                    } while (c.moveToNext());
                }
                //Toast.makeText(getActivity(), "rows " + Integer.toString(rows), Toast.LENGTH_SHORT).show();
            }

            Cursor d = myDbHelper.getImagesForArticle(mParam1);
            Resources resources = getActivity().getResources();
            imagesResouceId = new ArrayList<Integer>();
            if (d != null) {
                int rows2 = d.getCount();
                if (d.moveToFirst()) {
                    do {
                        String img = d.getString(d.getColumnIndex("image_resource"));
                        int resourceId = resources.getIdentifier(img, "drawable",
                                getActivity().getPackageName());
                        imagesResouceId.add(resourceId);
                    } while (d.moveToNext());
                }
            }
        }
        catch(SQLException sqle){
            throw sqle;
        }

        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        if(imagesResouceId.size() > 0) {
            Integer[] myArr = new Integer[imagesResouceId.size()];
            myArr = imagesResouceId.toArray(myArr);
            TouchImageAdapter touchImageAdapter = new TouchImageAdapter(getActivity(), myArr);
            mViewPager.setAdapter(touchImageAdapter);
        }
        else {
            TouchImageAdapter touchImageAdapter = new TouchImageAdapter(getActivity());
            mViewPager.setAdapter(touchImageAdapter);
            mViewPager.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Do something that differs the Activity's menu here
        getActivity().getMenuInflater().inflate(R.menu.menu_view_article,menu);
        bookmarkMenu = menu.findItem(R.id.action_bookmark);

        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        myDbHelper.openDatabase();
        Cursor d = myDbHelper.checkBookmark(mParam1);

        if (d != null) {
            int res = d.getCount();
            if (res > 0) {
                bookmarkMenu.setIcon(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                isBookmarked = true;
            }
            else {
                bookmarkMenu.setIcon(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                isBookmarked = false;
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        myDbHelper.openDatabase();
        myDbHelper.getWritableDatabase();

        switch (item.getItemId()) {
            case R.id.action_bookmark:
                if (isBookmarked) {
                    Toast.makeText(getActivity(), "Bookmark removed", Toast.LENGTH_SHORT).show();
                    myDbHelper.removeBookmark(mParam1);
                    Cursor d = myDbHelper.checkBookmark(mParam1);
                    if (d != null) {
                        int res = d.getCount();
                    }
                    bookmarkMenu.setIcon(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    isBookmarked = false;
                } else {
                    Toast.makeText(getActivity(), "Bookmark added", Toast.LENGTH_SHORT).show();
                    myDbHelper.addBookmark(mParam1);
                    Cursor d = myDbHelper.checkBookmark(mParam1);
                    if (d != null) {
                        int res = d.getCount();
                    }
                    bookmarkMenu.setIcon(android.support.v7.appcompat.R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    isBookmarked = true;
                }
                return true;
            default:
                break;
        }
        return false;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onArticleOpened();
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onArticleOpened ();
    }

    static class TouchImageAdapter extends PagerAdapter {

        private Activity act;
        private Integer[] myimages;
        public TouchImageAdapter(Activity activity) {
            act = activity;
        }

        public TouchImageAdapter(Activity activity, Integer[] img) {
            act = activity;
            myimages = img;
        }

        private static int[] images = { R.drawable.electric, R.drawable.bass, R.drawable.acoustic, R.drawable.classical };

        @Override
        public int getCount() {
            if(myimages != null) {
                return myimages.length;
            } else {
                return images.length;
            }
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            final int pos = position;
            final TouchImageView img = new TouchImageView(container.getContext());
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            if (myimages != null) {
                //Toast.makeText(act, "Has " + myimages.length + " images", Toast.LENGTH_SHORT).show();
                if(myimages.length > 0) {
                    img.setImageResource(myimages[position]);
                }
            }
            else {
                //Toast.makeText(act, "No images", Toast.LENGTH_SHORT).show();
            }
            return img;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
