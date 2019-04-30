package com.example.mp5football;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    /** Default logging tag for messages from the main activity. */
    private static final String TAG = "MP5 Main";



//    public void setPolicy(StrictMode.ThreadPolicy policy) {
//        this.policy = policy;
//    }


    /** Request queue for our API requests. */
    private static RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);




        final EditText yes = findViewById(R.id.editText);
//        final TextView no = findViewById(R.id.TeamID);
        final Button update = findViewById(R.id.button);
        update.setOnClickListener(v -> {
            String x = yes.getText().toString();
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("Value", x);
//            editor.apply();
            /** Tests the editText.*/
            Log.d(TAG, "Update Button Clicked");
            Log.d(TAG, "hello = " + x);
            startAPICall(x);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(yes.getWindowToken(),0);

        });
    }

    void startAPICall(final String teamName) {
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET,
                    "https://www.thesportsdb.com/api/v1/json/1/searchteams.php?t=" + teamName,
                    null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            Log.e(TAG,"success = " + response.toString());
                            apiCallDone(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    Log.e(TAG, "error = " + error.toString());
                }
            });
            jsonObjectRequest.setShouldCache(false);
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Drawable LoadImageFromWebOperations(String url) {
        try {
            System.out.println("url = " + url);
            InputStream is = (InputStream) new URL(url).getContent();
//            System.out.println(" is = " + is);
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    void apiCallDone(final JSONObject response) {
        try {
            Log.d(TAG, "first one = " + response.toString(2));
            // Example of how to pull a field off the returned JSON object
//            Log.i(TAG, "second one = "
//                    + response.getJSONObject(0).getJSONObject("gms").getJSONArray("g").getJSONObject(0).getJSONObject("@attributes").get("h"));
            Log.i(TAG, "second one = " + response.getJSONArray("teams").getJSONObject(0).get("strTeamBadge"));
            final TextView output = findViewById(R.id.TeamID);
            output.setText(response.getJSONArray("teams").getJSONObject(0).get("strDescriptionEN").toString());
            output.setMovementMethod(new ScrollingMovementMethod());
            final ImageView image = findViewById(R.id.imageView3);
//            Drawable x = LoadImageFromWebOperations(response.getJSONArray("teams").getJSONObject(0).getString("strTeamBadge"));
            try {
                Bitmap x = BitmapFactory.decodeStream((InputStream) new URL(response.getJSONArray("teams").getJSONObject(0).getString("strTeamBadge")).getContent());
                image.setImageBitmap(x);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println(x);
//            image.setImageDrawable(x);
        } catch (JSONException ignored) { }
    }
}
