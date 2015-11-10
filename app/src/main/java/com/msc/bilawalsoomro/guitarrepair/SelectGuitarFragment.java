package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class SelectGuitarFragment extends Fragment {

    private ViewPager mViewPager;
    private onGuitarSelectedListener mCallback;

    public static SelectGuitarFragment newInstance() {
        SelectGuitarFragment fragment = new SelectGuitarFragment();
        return fragment;
    }

    public SelectGuitarFragment() {
    }

    public interface onGuitarSelectedListener {
        public void onGuitarSelected(int position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select, container, false);
        mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);
        TouchImageAdapter touchImageAdapter = new TouchImageAdapter(getActivity());
        mViewPager.setAdapter(touchImageAdapter);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onGuitarSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private void chosenGuitarType(int position) {
        mCallback.onGuitarSelected(position);
    }

    static class TouchImageAdapter extends PagerAdapter {

        private Activity act;
        public TouchImageAdapter(Activity activity) {
            act = activity;
        }

        private static int[] images = { R.drawable.electric, R.drawable.bass, R.drawable.acoustic, R.drawable.classical };

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            final int pos = position;
            final TouchImageView img = new TouchImageView(container.getContext());
            img.setImageResource(images[position]);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectGuitarFragment fg =  (SelectGuitarFragment) act.getFragmentManager().findFragmentByTag("guitarSelectorFragment");
                    fg.chosenGuitarType(position);
                }
            });
            container.addView(img, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
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