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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import Models.ServiceOrder;


public class ServiceOrderLevelsActivity extends ActionBarActivity {

    final int LATER = 0, FINISH = 1, PREVIOUS = 2;
    int userId, serviceOrderId, option;
    String SERVICEORDER_URL, UPDATESERVICEORDER_URL, FINISHSERVICEORDER_URL;

    ServiceOrder serviceOrder;
    SeekBar skFuelLevel;
    EditText etServiceOrderKM;
    RadioGroup rgBrakesFluid, rgWipers, rgAntifreeze, rgOil, rgPowerSteering;
    TextView txtFuelLevel;
    Button btnServiceOrderLater, btnServiceOrderFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_levels);

        if (getIntent().getExtras().containsKey("serviceOrderId"))
            serviceOrderId = getIntent().getExtras().getInt("serviceOrderId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        SERVICEORDER_URL       = getString(R.string.base_url) + getString(R.string.serviceorder_url);
        UPDATESERVICEORDER_URL = getString(R.string.base_url) + getString(R.string.updateserviceorder_url);
        FINISHSERVICEORDER_URL = getString(R.string.base_url) + getString(R.string.closeserviceorder_url);

        skFuelLevel           = (SeekBar)findViewById(R.id.skFuelLevel);
        etServiceOrderKM      = (EditText)findViewById(R.id.etServiceOrderKM);
        rgBrakesFluid         = (RadioGroup)findViewById(R.id.rgBrakesFluid);
        rgWipers              = (RadioGroup)findViewById(R.id.rgWipers);
        rgAntifreeze          = (RadioGroup)findViewById(R.id.rgAntifreeze);
        rgOil                 = (RadioGroup)findViewById(R.id.rgOil);
        rgPowerSteering       = (RadioGroup)findViewById(R.id.rgPowerSteering);
        txtFuelLevel          = (TextView)findViewById(R.id.txtFuelLevel);
        btnServiceOrderLater  = (Button)findViewById(R.id.btnServiceOrderLater);
        btnServiceOrderFinish = (Button)findViewById(R.id.btnServiceOrderFinish);

        skFuelLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtFuelLevel.setText(progress + "/4");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnServiceOrderLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    option = LATER;
                    new UpdateServiceOrderAsyncTask().execute(UPDATESERVICEORDER_URL);
                }
            }
        });

        btnServiceOrderFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    option = FINISH;
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

    private boolean validate()
    {
        if (etServiceOrderKM.getText().toString().trim().equals(""))
        {
            Toast.makeText(getBaseContext(), "favor de escribir el kilometraje", Toast.LENGTH_LONG).show();
            etServiceOrderKM.requestFocus();
            return false;
        }
        if (!etServiceOrderKM.getText().toString().trim().matches("[0-9]+"))
        {
            Toast.makeText(getBaseContext(), "favor de escribir un kilometraje válido", Toast.LENGTH_LONG).show();
            etServiceOrderKM.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_order_levels, menu);
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

    private class LoadServiceOrderAsyncTask extends AsyncTask<String, String, String> {
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

                    etServiceOrderKM.setText(serviceOrder.km == 0? "" : serviceOrder.km + "");
                    skFuelLevel.setProgress(serviceOrder.fuel_level);
                    if (serviceOrder.brake_fluid != null)
                        switch (serviceOrder.brake_fluid){
                            case "Falta":
                                rgBrakesFluid.check(R.id.rbBrakesFluidLack);
                                break;
                            case "A Nivel":
                                rgBrakesFluid.check(R.id.rbBrakesFluidOk);
                                break;
                        }
                    if (serviceOrder.wiper_fluid != null)
                        switch (serviceOrder.wiper_fluid){
                            case "Falta":
                                rgWipers.check(R.id.rbWipersLack);
                                break;
                            case "A Nivel":
                                rgWipers.check(R.id.rbWipersOk);
                                break;
                        }
                    if (serviceOrder.antifreeze != null)
                        switch (serviceOrder.antifreeze){
                            case "Falta":
                                rgAntifreeze.check(R.id.rbAntifreezeLack);
                                break;
                            case "A Nivel":
                                rgAntifreeze.check(R.id.rbAntifreezeOk);
                                break;
                        }
                    if (serviceOrder.oil != null)
                        switch (serviceOrder.oil){
                            case "Falta":
                                rgOil.check(R.id.rbOilLack);
                                break;
                            case "A Nivel":
                                rgOil.check(R.id.rbOilOk);
                                break;
                        }
                    if (serviceOrder.power_steering_fluid != null)
                        switch (serviceOrder.power_steering_fluid){
                            case "Falta":
                                rgPowerSteering.check(R.id.rbPowerSteeringOk);
                                break;
                            case "A Nivel":
                                rgPowerSteering.check(R.id.rbPowerSteeringLack);
                                break;
                        }

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
            serviceOrder.km = Integer.parseInt(etServiceOrderKM.getText().toString().trim());
            serviceOrder.fuel_level           = skFuelLevel.getProgress();
            serviceOrder.brake_fluid          = ((RadioButton)findViewById(  rgBrakesFluid.getCheckedRadioButtonId())).getText().toString();
            serviceOrder.wiper_fluid          = ((RadioButton)findViewById(       rgWipers.getCheckedRadioButtonId())).getText().toString();
            serviceOrder.antifreeze           = ((RadioButton)findViewById(   rgAntifreeze.getCheckedRadioButtonId())).getText().toString();
            serviceOrder.oil                  = ((RadioButton)findViewById(          rgOil.getCheckedRadioButtonId())).getText().toString();
            serviceOrder.power_steering_fluid = ((RadioButton)findViewById(rgPowerSteering.getCheckedRadioButtonId())).getText().toString();
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
                        case FINISH: {
                            new FinishServiceOrderAsyncTask().execute(FINISHSERVICEORDER_URL);
                        }
                        break;
                        case LATER: {
                            Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        break;
                        case PREVIOUS:
                            NavUtils.navigateUpFromSameTask(ServiceOrderLevelsActivity.this);
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

    private class FinishServiceOrderAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceOrderId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
