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
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import Models.User;


public class MenuActivity extends ActionBarActivity {

    String USER_URL;
    TextView txtName;
    Button btnCarOwners, btnScan;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        USER_URL = getString(R.string.base_url) + getString(R.string.user_url);
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        btnCarOwners = (Button)findViewById(R.id.btnCarOwners);
        btnScan = (Button)findViewById(R.id.btnReadTAG);
        txtName = (TextView)findViewById(R.id.txtName);

        btnCarOwners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ListCarOwnersActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnScan = (Button) findViewById(R.id.btnReadTAG);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReadTagActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new LoadUserAsyncTask().execute(USER_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
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

    private class LoadUserAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    User user = objectMapper.readValue(jsonResult.getJSONObject("user").toString(), User.class);
                    txtName.setText(user.name);

                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
}
