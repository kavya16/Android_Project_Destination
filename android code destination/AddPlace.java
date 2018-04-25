package com.vnr.smartcitytraveller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AddPlace extends AppCompatActivity {

    AutoCompleteTextView search;
    String [] types;
    ArrayList<Place> places = new ArrayList<>();
    ListView listView;
    CustomAdapter customAdapter;
    String location;
    Double latitude;
    Double longitude;
    int pos;
    String radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        setTitle("Search Place");
        Intent i = getIntent();
        latitude = i.getDoubleExtra((Util.lat),0.0);
        longitude = i.getDoubleExtra(Util.lng,0.0);
        pos = i.getIntExtra(Util.lastPosition,-1);
        location = String.valueOf(latitude)+","+String.valueOf(longitude);
        radius = getSharedPreferences(Util.preferences, Context.MODE_PRIVATE).getString(Util.radius,"2");
        radius = String.valueOf(Integer.parseInt(radius)*1000);
        search = (AutoCompleteTextView)findViewById(R.id.search);
        types = Util.types;
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,types);
        search.setAdapter(adapter);
        listView = (ListView)findViewById(R.id.list);
        final EditText searchTerm = (EditText)findViewById(R.id.search_term);
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        Button button = (Button)findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkType(search.getText().toString()))
                {
                    if(check(searchTerm.getText().toString()))
                    {
                        final String search_term = searchTerm.getText().toString();
                        String n = search_term.replace(" ","+");
                        final ProgressDialog pd = new ProgressDialog(AddPlace.this);
                        pd.setCancelable(false);
                        pd.setMessage("fetching...");
                        pd.show();
                        places.clear();
                        final String s = search.getText().toString();
                        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST,"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&types="+s+"&radius="+radius+"&key=AIzaSyCVVaJKeI8iRd68Chmg3IdsDxh90gH4CrM&keyword="+n
                                , new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("EM VACHINDHI ",response);
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray(Util.result);
                                    for(int i =0;i<jsonArray.length();i++)
                                    {
                                        JSONObject j = jsonArray.getJSONObject(i);
                                        String name = j.getString(Util.placename);
                                        String neighbourhood = j.getString(Util.neighbourhood);
                                        String id = j.getString(Util.id);
                                        String rating;
                                        boolean open =false;
                                        if(j.has("opening_hours"))
                                        {
                                            if(j.has("open_now")) {
                                                open = j.getJSONObject("opening_hours").getBoolean("open_now");
                                            }
                                        }
                                        if(j.has(Util.rating))
                                            rating = j.getString(Util.rating);
                                        else
                                            rating = "0";
                                        String icon = j.getString(Util.icon);
                                        LatLng latLng = new LatLng(Double.parseDouble(j.getJSONObject(Util.geometry).getJSONObject(Util.location).getString(Util.lat)),Double.parseDouble(j.getJSONObject(Util.geometry).getJSONObject(Util.location).getString(Util.lng)));
                                        double distance = distance(latLng.latitude,latLng.longitude,latitude,longitude);
                                        Place place = new Place(name,rating,latLng,icon,distance,neighbourhood,j.toString(),id,open);
                                        places.add(place);
                                        adapter.notifyDataSetChanged();
                                    }

                                    if(pd.isShowing())
                                        pd.dismiss();
                                }
                                catch (JSONException e) {
                                    if(pd.isShowing())
                                        pd.dismiss();

                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(AddPlace.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                                if(pd.isShowing())
                                    pd.dismiss();

                            }
                        })
                        {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<>();
                                params.put(Util.location,String.valueOf(latitude)+","+String.valueOf(longitude));
                                params.put(Util.key,Util.apiKey);
                                params.put(Util.type,s);
                                return params;
                            }
                        };
                        VolleyHelper.getInstance(AddPlace.this).addToRequestQueue(request);
                    }
                    else
                    {
                        searchTerm.setError("required");
                        searchTerm.requestFocus();
                    }
                }
                else
                {
                    search.setError("Please choose from the list");
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddPlace.this);
                    builder.setTitle("Available Types");
                    StringBuilder builder1 = new StringBuilder();
                    for (String type : types) {
                        builder1.append(type).append("\n");
                    }
                    builder.setMessage(builder1.toString());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    search.requestFocus();
                }
            }
        });
        /*search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                places.clear();
                final ProgressDialog pd = new ProgressDialog(AddPlace.this);
                pd.setCancelable(false);
                pd.setMessage("fetching...");
                pd.show();
                final String s = search.getText().toString();
                //Log.i("request","https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&types="+s+"&rankby=distance&key=AIzaSyCVVaJKeI8iRd68Chmg3IdsDxh90gH4CrM");
                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST,"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&types="+s+"&radius=2000&key=AIzaSyCVVaJKeI8iRd68Chmg3IdsDxh90gH4CrM"
, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("EM VACHINDHI ",response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(Util.result);
                            for(int i =0;i<jsonArray.length();i++)
                            {
                                JSONObject j = jsonArray.getJSONObject(i);
                                String name = j.getString(Util.placename);
                                String neighbourhood = j.getString(Util.neighbourhood);
                                String rating="";
                                if(j.has(Util.rating))
                                    rating = j.getString(Util.rating);
                                else
                                    rating = "--NA--";
                                String icon = j.getString(Util.icon);
                                LatLng latLng = new LatLng(Double.parseDouble(j.getJSONObject(Util.geometry).getJSONObject(Util.location).getString(Util.lat)),Double.parseDouble(j.getJSONObject(Util.geometry).getJSONObject(Util.location).getString(Util.lng)));
                                double distance = distance(latLng.latitude,latLng.longitude,latitude,longitude);
                                Place place = new Place(name,rating,latLng,icon,distance,neighbourhood);
                                places.add(place);
                                adapter.notifyDataSetChanged();
                            }

                            if(pd.isShowing())
                                pd.dismiss();
                        }
                        catch (JSONException e) {
                            if(pd.isShowing())
                                pd.dismiss();

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPlace.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                        if(pd.isShowing())
                            pd.dismiss();

                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put(Util.location,"-33.8670522,151.1957362");
                        params.put(Util.key,Util.apiKey);
                        params.put(Util.type,s);
                        return params;
                    }
                };
                VolleyHelper.getInstance(AddPlace.this).addToRequestQueue(request);
            }
        });*/
    }

    private boolean check(String s) {
        return s != null && !(s.contentEquals(""));

    }

    private boolean checkType(String s) {
        int flag=0;
        for (String type : types) {
            if (type.equals(s)) {
                flag = 1;
                break;
            }
        }
        return flag != 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.addplace, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if(android.R.id.home==id)
        {
            onBackPressed();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddPlace.this);
            builder.setMessage("How would you like to sort the list?");
            builder.setPositiveButton("Rating", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sortRating();
                }
            }).setCancelable(true).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).setNeutralButton("Distance", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sortDistance();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sortDistance() {
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                if(o1.getDistance()<o2.getDistance())
                    return -1;
                else if(o1.getDistance()<o2.getDistance())
                    return 0;
                else
                    return 1;
            }
        });
    }

    private void sortRating() {
        Collections.sort(places, new Comparator<Place>() {
            @Override
            public int compare(Place o1, Place o2) {
                if(Double.parseDouble(o1.getRating())<Double.parseDouble(o2.getRating()))
                    return 1;
                else if(Double.parseDouble(o1.getRating())==Double.parseDouble(o2.getRating()))
                    return 0;
                else
                    return -1;
            }
        });
    }

    private class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return places.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView = getLayoutInflater().inflate(R.layout.place_item,parent,false);
            }
            final Place place = places.get(position);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            TextView name = (TextView)convertView.findViewById(R.id.name);
            TextView neighbourhood = (TextView)convertView.findViewById(R.id.neighbourhood);
            TextView distance = (TextView)convertView.findViewById(R.id.distance);
            TextView rating = (TextView)convertView.findViewById(R.id.rating);
            {
                name.setText(place.getName());
                Glide.with(AddPlace.this).load(place.getIcon())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                neighbourhood.setText(place.getNeighbourhood());
                distance.setText("Distance : "+place.getDistance()+" KM");
                rating.setText("Rating : "+place.getRating());
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    Bundle extras = new Bundle();
                    extras.putString(Util.placename,place.getName());
                    extras.putString(Util.rating,place.getRating());
                    extras.putDouble(Util.lat,place.getLocation().latitude);
                    extras.putDouble(Util.lng,place.getLocation().longitude);
                    extras.putDouble(Util.distance,place.getDistance());
                    extras.putString(Util.icon,place.getIcon());
                    extras.putString(Util.neighbourhood,place.getNeighbourhood());
                    extras.putString(Util.description,place.getDescription());
                    extras.putString(Util.id,place.getId());
                    extras.putBoolean(Util.open,place.isOpen());
                    extras.putInt(Util.lastPosition,pos);
                    i.putExtras(extras);
                    setResult(RESULT_OK,i);
                    finish();
                }
            });
            return convertView;
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}