package com.grupohqh.carservices.operator;

import android.content.Intent;
import android.os.AsyncTask;
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

import Models.CarOwner;


public class ShowCarOwnerActivity extends ActionBarActivity {

    String URL;
    View layoutPerson, layoutBusiness;
    TextView txtBusinessName, txtRFC, txtFirstName, txtLastName, txtMotherMaidenName,
            txtStreet, txtNeighborhood, txtState, txtTown, txtPostalCode, txtEmail, txtTelephone, txtMovil;
    Button btnAddCar;
    int userId, carOwnerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car_owner);

        URL = getString(R.string.base_url) + getString(R.string.carowner_url);
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        if (getIntent().getExtras().containsKey("carOwnerId"))
            carOwnerId = getIntent().getExtras().getInt("carOwnerId");

        layoutPerson = findViewById(R.id.layoutPersonShowCarOwner);
        layoutBusiness = findViewById(R.id.layoutBusinessShowCarOwner);
        txtBusinessName = (TextView) findViewById(R.id.txtBusinessName);
        txtRFC = (TextView) findViewById(R.id.txtRFC);
        txtFirstName = (TextView) findViewById(R.id.txtFirstName);
        txtLastName = (TextView) findViewById(R.id.txtLastName);
        txtMotherMaidenName = (TextView) findViewById(R.id.txtMotherMaidenName);
        txtStreet = (TextView) findViewById(R.id.txtStreet);
        txtNeighborhood = (TextView) findViewById(R.id.txtNeighborhood);
        txtState = (TextView) findViewById(R.id.txtState);
        txtTown = (TextView) findViewById(R.id.txtTown);
        txtPostalCode = (TextView) findViewById(R.id.txtPostalCode);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtTelephone = (TextView) findViewById(R.id.txtTelephone);
        txtMovil = (TextView) findViewById(R.id.txtMovil);
        btnAddCar = (Button)findViewById(R.id.btnAddCar);

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ManipulateCarActivity.class);
                intent.putExtra("carOwnerId", carOwnerId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadCarOwnerAsyncTask().execute(URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_car_owner, menu);
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

    private class LoadCarOwnerAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + carOwnerId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    CarOwner carOwner = objectMapper.readValue(jsonResult.getJSONObject("car_owner").toString(), CarOwner.class);
                    if (carOwner.getType().equals("Person")) {
                        layoutBusiness.setVisibility(View.GONE);
                        txtFirstName.setText(carOwner.getFirst_name());
                        txtLastName.setText(carOwner.getLast_name());
                        txtMotherMaidenName.setText(carOwner.getMother_maiden_name());
                    }
                    else if (carOwner.getType().equals("Business")){
                        layoutPerson.setVisibility(View.GONE);
                        txtBusinessName.setText(carOwner.getBusiness_name());
                        txtRFC.setText(carOwner.getRfc());
                    }
                    txtStreet.setText(carOwner.getStreet());
                    txtNeighborhood.setText(carOwner.getNeighborhood());
                    txtTown.setText(carOwner.getTown());
                    txtState.setText(carOwner.getState());
                    txtPostalCode.setText(carOwner.getPostal_code());
                    txtEmail.setText(carOwner.getEmail());
                    txtTelephone.setText(carOwner.getPhone_number());
                    txtMovil.setText(carOwner.getMobile_phone_number());
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
