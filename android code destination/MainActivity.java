package com.vnr.smartcitytraveller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        pd = new ProgressDialog(this);
        pd.setMessage("Logging in");
        pd.setCancelable(false);
    }

    public static class PlaceholderFragment extends Fragment
    {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private DatabaseReference databaseReference;

        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final int pos =  getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            if(pos==1)
            {
                rootView = inflater.inflate(R.layout.login_fragment,container,false);
                final TextView username = (TextView) rootView.findViewById(R.id.username);
                final TextView password = (TextView)rootView.findViewById(R.id.password);
                Button login = (Button)rootView.findViewById(R.id.login);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String u =  username.getText().toString();
                        final String p = password.getText().toString();
                        if(check(u))
                        {
                            if(check(p))
                            {
                                /*Intent i = new Intent(getContext(),MainActivity.class);
                                startActivity(i);*/
                                pd.show();
                                databaseReference = FirebaseDatabase.getInstance().getReference().child(Util.users);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean exists = dataSnapshot.child(u).exists();
                                        if(exists)
                                        {
                                            if(p.equals(dataSnapshot.child(u).getValue()))
                                            {
                                                pd.dismiss();
                                                getActivity().getSharedPreferences(Util.preferences,Context.MODE_PRIVATE).edit().putString(Util.username,u).apply();
                                                Intent i = new Intent(getContext(),PlaceFinder.class);
                                                getActivity().startActivity(i);
                                                getActivity().finish();
                                            }
                                            else {
                                                pd.dismiss();
                                                Toast.makeText(getContext(), "wrong password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "no user", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        pd.dismiss();
                                        Toast.makeText(getContext(),databaseError.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else {
                                password.setError("password required");
                                password.requestFocus();
                            }
                        }
                        else {
                            username.setError("username required");
                            username.requestFocus();
                        }
                    }
                });
            }
            else
            {
                rootView = inflater.inflate(R.layout.signup_fragment,container,false);
                final TextView name = (TextView) rootView.findViewById(R.id.name);
                final TextView username = (TextView) rootView.findViewById(R.id.username);
                final TextView password = (TextView)rootView.findViewById(R.id.password);
                Button signup = (Button)rootView.findViewById(R.id.signup);
                signup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String u = username.getText().toString();
                        final String p = password.getText().toString();
                        final String n = name.getText().toString();
                        if (check(n))
                        {
                            if (check(u))
                            {
                                if (check(p))
                                {
                                    pd.show();
                                    databaseReference = FirebaseDatabase.getInstance().getReference().child(Util.users);
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.child(u).exists()) {
                                                pd.dismiss();
                                                Toast.makeText(getContext(), "user already exists", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                databaseReference.child(u).setValue(p);
                                                pd.dismiss();
                                                getActivity().getSharedPreferences(Util.preferences,Context.MODE_PRIVATE).edit().putString(Util.username,u).apply();
                                                Intent i = new Intent(getContext(),PlanActivity.class);
                                                getActivity().startActivity(i);
                                                getActivity().finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(),databaseError.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    password.setError("password required");
                                    password.requestFocus();
                                }
                            } else {
                                username.setError("username required");
                                username.requestFocus();
                            }
                        }
                        else
                        {
                            name.setError("name required");
                            name.requestFocus();
                        }
                    }
                });
            }

            return rootView;
        }

        private boolean check(String u) {
            return !u.equals("");
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "LOGIN";
                case 1:
                    return "SIGNUP";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

}
