package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;


public class ReadTagActivity extends ActionBarActivity {

    private static final String URL = "http://192.168.15.125/~Cesar/carservice/public/carbyepc/";

    EditText etEpc;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);

        user = (User)getIntent().getExtras().get("user");
        etEpc = (EditText)findViewById(R.id.etEPCReadTag);
        Button btnRead = (Button)findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    new HttpAsyncTask().execute(URL);
                }
            }
        });
    }

    private boolean validate() {
        if (etEpc.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escanear TAG", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpAsyncTask extends AsyncTask<String, String, String> {


        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            //obtnemos usr y pass

            String epc = etEpc.getText().toString().trim();
            return HttpAux.httpGetRequest(params[0] + "/" + user.client_id + "/" + epc + "/");

        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.has("car")){
                    JSONObject jsonObject = jsonResult.getJSONObject("car");
                    if (jsonObject != null ) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        car = objectMapper.readValue(jsonObject.toString(), Car.class);
                        Intent i = new Intent(getBaseContext(), ShowCarActivity.class);
                        i.putExtra("car", car);
                        i.putExtra("user", user);
                        startActivity(i);
                    } else {
                        Toast.makeText(getBaseContext(), "No existe un Vehiculo asociado a este TAG", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Error: " + result, Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Fallo la conexión, vuelve a intentar", Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
