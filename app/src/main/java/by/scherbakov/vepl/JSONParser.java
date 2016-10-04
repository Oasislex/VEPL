package by.scherbakov.vepl;

import android.util.Log;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by User on 20.04.2016.
 */
public class JSONParser {


    static JSONObject jObj = null;


    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response responses = null;

            try {
                responses = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonData = responses.body().string();
            jObj = new JSONObject(jsonData);

        } catch (JSONException e) {
            Log.d("JSON Parser", e.getMessage());
        } catch (IOException e) {
            Log.d("JSON Parser", e.getMessage());
        }
        // return JSON String
        return jObj;

    }
}
