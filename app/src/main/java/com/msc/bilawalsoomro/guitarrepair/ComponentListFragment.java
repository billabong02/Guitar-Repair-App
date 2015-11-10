package com.msc.bilawalsoomro.guitarrepair;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import com.msc.bilawalsoomro.guitarrepair.helpers.DatabaseHelper;

public class ComponentListFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private onGuitarComponentSelected mListener;

    public static ComponentListFragment newInstance(String param1, String param2) {
        ComponentListFragment fragment = new ComponentListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ComponentListFragment newInstance(String type) {
        ComponentListFragment fragment = new ComponentListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, type);
        fragment.setArguments(args);
        return fragment;
    }

    public ComponentListFragment newInstance(Boolean showall) {
        ComponentListFragment fragment = new ComponentListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, "showAll");
        fragment.setArguments(args);
        return fragment;
    }

    public ComponentListFragment() {
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
        final View v = inflater.inflate(R.layout.fragment_component_list, container, false);
        final ListView listview = (ListView) v.findViewById(R.id.componentlistview);
        final ArrayList <String> components = new ArrayList<String>();
        final ArrayList <String> images = new ArrayList<String>();
        final ArrayList <String> ids = new ArrayList<String>();

        DatabaseHelper myDbHelper = new DatabaseHelper(getActivity());
        try {
            myDbHelper.openDatabase();
            if (mParam2 == "showAll") {
                Cursor c = myDbHelper.listComponentsCursor();
                if (c != null) {
                    if (c.moveToFirst()) {
                        do {
                            ids.add(c.getString(0));
                            components.add(c.getString(c.getColumnIndex("guitar_type")) + " guitar " + c.getString(1));
                            images.add(c.getString(3));
                        } while (c.moveToNext());
                    }
                }
            }
            else {
                Cursor c = myDbHelper.listComponentsByTypeCursor(mParam1);
                if (c != null) {
                    if (c.moveToFirst()) {
                        do {
                            ids.add(c.getString(0));
                            components.add(c.getString(1));
                            images.add(c.getString(3));
                            Log.d("images", c.getString(3));
                        } while (c.moveToNext());
                    }
                }
            }
        }
        catch(SQLException sqle){
            throw sqle;
        }
        finally {
            myDbHelper.close();
        }

        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), components, images);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                String componentId = ids.get(position);
                mListener.onComponentSelected(Integer.parseInt(componentId));
            }

        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onGuitarComponentSelected) activity;
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

    public interface onGuitarComponentSelected {
        // TODO: Update argument type and name
        public void onComponentSelected(int id);
    }

    static class ViewHolder {
        TextView text;
        ImageView icon;
    }

    public class MySimpleArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> components;
        private final ArrayList<String> images;

        public MySimpleArrayAdapter(Context context, ArrayList<String> components, ArrayList<String> images) {
            super(context, -1, components);
            this.context = context;
            this.components = components;
            this.images = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_row_components, parent, false);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.componentName);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(components.get(position));
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier(images.get(position), "drawable", context.getPackageName());
            holder.icon.setImageResource(resourceId);
            return convertView;
        }
    }
}
