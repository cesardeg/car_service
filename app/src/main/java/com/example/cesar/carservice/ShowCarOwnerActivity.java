package com.example.cesar.carservice;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ShowCarOwnerActivity extends ActionBarActivity {

    View layoutPerson, layoutBusiness;
    TextView txtBusinessName, txtRFC, txtFirstName, txtLastName, txtMotherMaidenName,
            txtStreet, txtNeighborhood, txtState, txtTown, txtPostalCode, txtEmail, txtTelephone, txtMovil;
    Button btnAddCar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car_owner);

        final CarOwner carOwner = (CarOwner)getIntent().getExtras().get("carOwner");
        user = (User)getIntent().getExtras().get("user");
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
        //if (carOwner.getPostal_code() != null)
            txtPostalCode.setText(carOwner.getPostal_code());
        //if (carOwner.getEmail() != null)
            txtEmail.setText(carOwner.getEmail());
        //if (carOwner.getPhone_number() != null)
            txtTelephone.setText(carOwner.getPhone_number());
        //if (carOwner.getMobile_phone_number() != null)
            txtMovil.setText(carOwner.getMobile_phone_number());

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ManipulateCarActivity.class);
                String carOwnerName = carOwner.getType().equals("Person") ?
                        carOwner.getFirst_name() + " " + carOwner.getLast_name() + " " + carOwner.getMother_maiden_name() :
                        carOwner.getBusiness_name();
                intent.putExtra("carOwnerName", carOwnerName);
                intent.putExtra("carOwnerId", carOwner.getId());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
