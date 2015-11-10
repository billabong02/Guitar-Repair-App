package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ImageView img;
    private TextView myText;
    private onStandClicked mListener;
    private getComponents mListener2;
    private changeGuitar mListener3;
    private Button changeGuitarBtn;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 1);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        img = (ImageView) rootView.findViewById(R.id.guitarStand);
        myText = (TextView) rootView.findViewById(R.id.textView2);
        changeGuitarBtn = (Button) rootView.findViewById(R.id.button);
        changeGuitarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener3.changeSelectedGuitar();
            }
        });

        SharedPreferences sharedPref = getActivity().getSharedPreferences("com.msc.bilawalsoomro.guitarrepair", Context.MODE_PRIVATE);
        String guitarSelected = "com.msc.bilawalsoomro.guitarrepair.guitar";
        int guitarChosen = sharedPref.getInt(guitarSelected, 0);

        switch (guitarChosen) {
            case 0:
                img.setImageResource(R.drawable.stand);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onStandClick();
                    }
                });
                changeGuitarBtn.setVisibility(View.GONE);
                break;
            case 1:
                myText.setText("Welcome to Billy's Guitar Repair and Maintenance app. Tap on your selected guitar or click the button below to select a different guitar.");
                img.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stand4, 400, 400));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener2.viewComponents(0);
                    }
                });
                break;
            case 2:
                myText.setText("Welcome to Billy's Guitar Repair and Maintenance app. Tap on your selected guitar or click the button below to select a different guitar.");
                img.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stand2, 400, 400));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener2.viewComponents(1);
                    }
                });
                break;
            case 3:
                myText.setText("Welcome to Billy's Guitar Repair and Maintenance app. Tap on your selected guitar or click the button below to select a different guitar.");
                img.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stand1, 400, 400));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener2.viewComponents(2);
                    }
                });
                break;
            case 4:
                myText.setText("Welcome to Billy's Guitar Repair and Maintenance app. Tap on your selected guitar or click the button below to select a different guitar.");
                img.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.stand3, 400, 400));
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener2.viewComponents(3);
                    }
                });
                break;
        }



        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onStandClicked) activity;
            mListener2 = (getComponents) activity;
            mListener3 = (changeGuitar) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface onStandClicked {
        // TODO: Update argument type and name
        public void onStandClick();
    }

    public interface getComponents {
        public void viewComponents(int id);
    }

    public interface changeGuitar {
        public void changeSelectedGuitar();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}