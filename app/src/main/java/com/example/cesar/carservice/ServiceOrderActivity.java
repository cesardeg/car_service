package com.example.cesar.carservice;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import Models.ServiceOrder;


public class ServiceOrderActivity extends ActionBarActivity {

    final int LATER = 0, NEXT = 1, PREVIOUS = 2;
    String SERVICEORDER_URL, UPDATESERVICEORDER_URL;
    Spinner spServiceType;
    Switch swSuppliedParts, swAllowUsedParts, swPickUp, swDeliver;
    EditText etPickUpAddress, etDeliverAddress;
    Button btnLaterServiceOrder, btnServiceOrderNext;
    int userId, serviceOrderId, option;
    ServiceOrder serviceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order);

        SERVICEORDER_URL = getString(R.string.base_url) + getString(R.string.serviceorder_url);
        UPDATESERVICEORDER_URL = getString(R.string.base_url) + getString(R.string.updateserviceorder_url);
        if (getIntent().getExtras().containsKey("serviceOrderId"))
            serviceOrderId = getIntent().getExtras().getInt("serviceOrderId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        spServiceType = (Spinner)findViewById(R.id.spServiceType);
        swSuppliedParts = (Switch)findViewById(R.id.swSuppliedParts);
        swAllowUsedParts = (Switch)findViewById(R.id.swAllowUsedParts);
        swPickUp = (Switch)findViewById(R.id.swPickUp);
        swDeliver = (Switch)findViewById(R.id.swDeliver);
        etPickUpAddress = (EditText)findViewById(R.id.etPickUpAddress);
        etDeliverAddress = (EditText)findViewById(R.id.etDeliverAddress);
        btnLaterServiceOrder = (Button)findViewById(R.id.btnLaterServiceOrder);
        btnServiceOrderNext = (Button)findViewById(R.id.btnServiceOrderNext);

        etPickUpAddress.setEnabled(false);
        etDeliverAddress.setEnabled(false);

        spServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView)parent.getChildAt(position)).setTextColor(Color.GRAY);
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

        btnLaterServiceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    option = LATER;
                    new UpdateServiceOrderAsyncTask().execute(UPDATESERVICEORDER_URL);
                }
            }
        });
        btnServiceOrderNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                {
                    option = NEXT;
                    new UpdateServiceOrderAsyncTask().execute(UPDATESERVICEORDER_URL);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new LoadServiceOrderAsyncTask().execute(SERVICEORDER_URL);
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
                if (validate()) {
                    option = PREVIOUS;
                    new UpdateServiceOrderAsyncTask().execute(UPDATESERVICEORDER_URL);
                }
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadServiceOrderAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceOrderId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    serviceOrder = objectMapper.readValue(jsonResult.getJSONObject("service_oder").toString(), ServiceOrder.class);
                    String[] types =  getResources().getStringArray(R.array.typeOrders);
                    for (int i = 0; i < types.length; i++) {
                        if (types[i].equals(serviceOrder.service_name))
                            spServiceType.setSelection(i);
                    }
                    swSuppliedParts.setChecked(serviceOrder.owner_supplied_parts);
                    swAllowUsedParts.setChecked(serviceOrder.owner_allow_used_parts);
                    swPickUp.setChecked(serviceOrder.pick_up_address != null);
                    swDeliver.setChecked(serviceOrder.delivery_address != null);
                    etPickUpAddress.setText(serviceOrder.pick_up_address);
                    etDeliverAddress.setText(serviceOrder.delivery_address);

                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }

    private class UpdateServiceOrderAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            serviceOrder.service_name = spServiceType.getSelectedItem().toString();
            serviceOrder.owner_supplied_parts = swSuppliedParts.isChecked();
            serviceOrder.owner_allow_used_parts = swAllowUsedParts.isChecked();
            if (swPickUp.isChecked()) {
                serviceOrder.pick_up_address = etPickUpAddress.getText().toString().trim();
            }
            else {
                serviceOrder.pick_up_address = null;
            }
            if (swDeliver.isChecked()) {
                serviceOrder.delivery_address = etDeliverAddress.getText().toString().trim();
            } else {
                serviceOrder.delivery_address = null;
            }
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(serviceOrder);
                return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceOrderId + "/", new JSONObject(jsonString));
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    switch (option) {
                        case NEXT: {
                            Intent intent = new Intent(getBaseContext(), ServiceOrderLevelsActivity.class);
                            intent.putExtra("serviceOrderId", serviceOrderId);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                        }
                        break;
                        case LATER: {
                            Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        break;
                        case PREVIOUS:
                            NavUtils.navigateUpFromSameTask(ServiceOrderActivity.this);
                            break;
                    }

                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
