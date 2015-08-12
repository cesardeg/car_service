package com.example.cesar.carservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class LoginActivity extends ActionBarActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    User user;
    int client_id = 1;

    private static final String LOGIN_URL = "http://192.168.15.125/~Cesar/carservice/public/login/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText)findViewById(R.id.etUserName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new LoginAsyncTask().execute(LOGIN_URL);
                }
            }
        });
    }

    public boolean validate()
    {
        if (etUsername.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escribir el nombre de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escribir el nombre de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private class LoginAsyncTask extends AsyncTask<String, String, String>  {

        String username, password;
        JSONObject jsonObject;

        protected void onPreExecute() {
        }

        protected String doInBackground(String... params) {
            //obtnemos usr y pass
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("username", username);
                jsonObject.accumulate("password", password);
                jsonObject.accumulate("client_id", client_id);
            }catch (Exception e){
                e.getStackTrace();
            }
            return HttpAux.httpPostRequest(params[0], jsonObject);

        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.has("success")){
                    if (jsonResult.getInt("success") == 1){
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        user = objectMapper.readValue(jsonObject.toString(), User.class);
                        if (jsonResult.has("id")) user.id = jsonResult.getInt("id");
                        if (jsonResult.has("workshop_id")) user.workshop_id = jsonResult.getInt("workshop_id");
                        Intent i = new Intent(getBaseContext(), MenuActivity.class);
                        i.putExtra("user", user);
                        startActivity(i);
                    } else {
                        Toast.makeText(getBaseContext(), "Nombre de Usuario y/o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Exception e){
                Toast.makeText(getBaseContext(), "Fallo la conexión, vuelve a intentar", Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
