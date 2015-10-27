package com.grupohqh.carservices.operator;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;


public class ServiceOrderActivity extends ActionBarActivity {
    String CREATESEVICEORDER_URL;
    Spinner spServiceType;
    Switch swSuppliedParts, swAllowUsedParts, swPickUp, swDeliver;
    EditText etPickUpAddress, etDeliverAddress;
    Button btnCancel, btnGenerate;
    int userId, carId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order);


        CREATESEVICEORDER_URL = getString(R.string.base_url) + getString(R.string.createserviceorder_url);
        if (getIntent().getExtras().containsKey("carId"))
            carId = getIntent().getExtras().getInt("carId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        spServiceType    = ( Spinner)findViewById(R.id.spServiceType   );
        swSuppliedParts  = ( Switch )findViewById(R.id.swSuppliedParts );
        swAllowUsedParts = ( Switch )findViewById(R.id.swAllowUsedParts);
        swPickUp         = ( Switch )findViewById(R.id.swPickUp        );
        swDeliver        = ( Switch )findViewById(R.id.swDeliver       );
        etPickUpAddress  = (EditText)findViewById(R.id.etPickUpAddress );
        etDeliverAddress = (EditText)findViewById(R.id.etDeliverAddress);
        btnCancel        = ( Button )findViewById(R.id.btnCancel       );
        btnGenerate      = ( Button )findViewById(R.id.btnSave         );

        etPickUpAddress.setEnabled(false);
        etDeliverAddress.setEnabled(false);

        spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView) parent.getChildAt(position)).setTextColor(Color.GRAY);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPickUpAddress.setEnabled(isChecked);
                etPickUpAddress.requestFocus();
            }
        });
        swDeliver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etDeliverAddress.setEnabled(isChecked);
                etDeliverAddress.requestFocus();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceOrderActivity.this.finish();
            }
        });
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    new CreateServiceOrderAsyncTask().execute(CREATESEVICEORDER_URL);
                }
            }
        });
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean validate() {
        if (spServiceType.getSelectedItemPosition() == 0) {
            Toast.makeText(getBaseContext(), "Favor de seleccionar el tipo de servicio", Toast.LENGTH_LONG).show();
            spServiceType.requestFocus();
            return false;
        }
        if (swPickUp.isChecked() && etPickUpAddress.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la dirección donde se recogió el vehiculo", Toast.LENGTH_LONG).show();
            etPickUpAddress.requestFocus();
            return false;
        }
        if (swDeliver.isChecked() && etDeliverAddress.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la dirección de entrega", Toast.LENGTH_LONG).show();
            etDeliverAddress.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CreateServiceOrderAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("car_id", carId);
                jsonObject.accumulate("service_name", spServiceType.getSelectedItem().toString());
                jsonObject.accumulate("owner_supplied_parts", swSuppliedParts.isChecked());
                jsonObject.accumulate("owner_allow_used_parts", swAllowUsedParts.isChecked());
                jsonObject.accumulate("pick_up_address",  swPickUp.isChecked()  ?  etPickUpAddress.getText().toString() : null);
                jsonObject.accumulate("delivery_address", swDeliver.isChecked() ? etDeliverAddress.getText().toString() : null);
            } catch (Exception e) {
                e.getStackTrace();
            }
            return HttpAux.httpPostRequest(params[0] + userId + "/", jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ServiceOrderActivity.this.finish();
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
