package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManipulateCarActivity extends ActionBarActivity {

    private static final String CARBRAND_URL = "http://192.168.15.125/~Cesar/carservice/public/carbrands/";
    private static final String CARLINE_URL  = "http://192.168.15.125/~Cesar/carservice/public/carlines/";
    private static final String SAVECAR_URL  = "http://192.168.15.125/~Cesar/carservice/public/createcar/";

    int carOwnerId, carLineId;
    List<String> brands, lines;
    Map<String, Integer> mapBrands, mapLines;
    String noBrand = "Selecciona la marca del vehiculo", noLine  = "Selecciona la linea del vehiculo", carOwnerName;
    int nothing = 0;
    Car car;
    User user;

    Spinner spCarBrand, spCarLine;
    EditText etEPC, etModel, etColor, etSerialNumber, etKM;
    Button btnChangeImageCar, btnReadTAGManipulateCar, btnSaveCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manipulate_car);

        carOwnerId = getIntent().getExtras().getInt("carOwnerId");
        carOwnerName = getIntent().getExtras().getString("carOwnerName");
        user = (User)getIntent().getExtras().get("user");

        etEPC = (EditText)findViewById(R.id.etEPC);
        etModel = (EditText)findViewById(R.id.etCarModel);
        etColor = (EditText)findViewById(R.id.etCarColor);
        etSerialNumber = (EditText)findViewById(R.id.etSerialNumber);
        etKM = (EditText)findViewById(R.id.etKm);
        spCarBrand = (Spinner)findViewById(R.id.spCarBrand);
        spCarLine = (Spinner)findViewById(R.id.spCarLine);
        btnSaveCar = (Button)findViewById(R.id.btnSaveCar);

        btnSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    new HttpSaveCarAsyncTask().execute(SAVECAR_URL);
            }
        });

        new HttpSpinnerBrandAsyncTask().execute(CARBRAND_URL);
        spCarBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new HttpSpinnerLineAsyncTask().execute(CARLINE_URL + mapBrands.get(brands.get(position)) + "/");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCarLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carLineId = mapLines.get(lines.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public boolean validate() {
        if (etEPC.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de leer el TAG", Toast.LENGTH_LONG).show();
            etEPC.requestFocus();
            return false;
        }
        if (etModel.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el Modelo", Toast.LENGTH_LONG).show();
            etModel.requestFocus();
            return false;
        }
        if (etColor.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el color", Toast.LENGTH_LONG).show();
            etColor.requestFocus();
            return false;
        }
        if (etSerialNumber.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el Número de Serie", Toast.LENGTH_LONG).show();
            etSerialNumber.requestFocus();
            return false;
        }
        if (etKM.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el Kilometraje", Toast.LENGTH_LONG).show();
            etKM.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manipulate_car, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String httpGetRequest(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. create POST request to the given URL
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
            // 8. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 9. convert inputstream to string
            result = convertStreamToString(inputStream);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

    public static String httpPostRequest(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. create POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            // 3. convert JSONObject to JSON to String
            String json = jsonObject.toString();
            // 4. set json to StringEntity
            StringEntity se = new StringEntity(json);
            // 5. set httpPost Entity
            httpPost.setEntity(se);
            // 6. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 7. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 8. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 9. convert inputstream to string
            result = convertStreamToString(inputStream);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    class HttpSpinnerBrandAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                brands = new ArrayList<String>();
                lines = new ArrayList<String>();
                mapBrands = new HashMap<String, Integer>();
                mapLines = new HashMap<String, Integer>();
                brands.add(noBrand);
                mapBrands.put(noBrand, nothing);
                lines.add(noLine);
                mapLines.put(noLine, nothing);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    brands.add(jsonObject.getString("name"));
                    mapBrands.put(jsonObject.getString("name"), jsonObject.getInt("id"));
                }

                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, brands);
                brandAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarBrand.setAdapter(brandAdapter);

                ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, lines);
                lineAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarLine.setAdapter(lineAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HttpSpinnerLineAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            return httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                lines = new ArrayList<String>();
                mapLines = new HashMap<String, Integer>();
                lines.add(noLine);
                mapLines.put(noLine, nothing);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    lines.add(jsonObject.getString("name"));
                    mapLines.put(jsonObject.getString("name"), jsonObject.getInt("id"));
                }
                ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, lines);
                lineAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarLine.setAdapter(lineAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HttpSaveCarAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... urls) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("epc", etEPC.getText().toString().trim());
                jsonObject.accumulate("model", etModel.getText().toString().trim());
                jsonObject.accumulate("serial_number", etSerialNumber.getText().toString().trim());
                jsonObject.accumulate("color", etColor.getText().toString().trim());
                jsonObject.accumulate("km", etKM.getText().toString().trim());
                jsonObject.accumulate("car_line_id", carLineId);
                jsonObject.accumulate("car_owner_id", carOwnerId);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                car = objectMapper.readValue(jsonObject.toString(), Car.class);
                car.brandName = spCarBrand.getSelectedItem().toString();
                car.lineName = spCarLine.getSelectedItem().toString();
                car.ownerName = carOwnerName;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return httpPostRequest(urls[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
                    car.id = jsonObject.getInt("id");
                    Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                    intent.putExtra("car", car);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    return;
                }
                else if (jsonObject.has("msj")) {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "¡Fallo alta, favor de volver a intentar!", Toast.LENGTH_LONG).show();
        }
    }
}
