package com.example.cesar.carservice;

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

import org.json.JSONObject;

import Models.User;


public class LoginActivity extends ActionBarActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    User user;

    private String LOGIN_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LOGIN_URL = getString(R.string.base_url) + getString(R.string.login_url);
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

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    public boolean validate()
    {
        if (etUsername.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escribir el nombre de usuario", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escribir el password", Toast.LENGTH_SHORT).show();
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


        return super.onOptionsItemSelected(item);
    }

    private class LoginAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("username", username);
                jsonObject.accumulate("password", password);
            }catch (Exception e){
                e.getStackTrace();
            }
            return HttpAux.httpPostRequest(params[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")){
                    int userId = jsonResult.getInt("user_id");
                    Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
