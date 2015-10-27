package com.grupohqh.carservices.operator;
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

import Models.ServiceInventory;

public class ServiceOrderLevelsActivity extends ActionBarActivity {

    final int LATER = 1, CLOSE = 2;
    int userId, serviceOrderId, option;
    String INVENTORY_URL, FINISHINVENTORY_URL;

    ServiceInventory serviceInventory;
    SeekBar skFuelLevel;
    EditText etServiceOrderKM;
    RadioGroup rgBrakesFluid, rgWipers, rgAntifreeze, rgOil, rgPowerSteering;
    TextView txtFuelLevel;
    Button btnQueueInventory, btnCloseInventory;
    Button btnOutsidePhotos, btnInsidePhotos, btnMotorPhotos, btnTrunkPhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_levels);

        if (getIntent().getExtras().containsKey("serviceOrderId"))
            serviceOrderId = getIntent().getExtras().getInt("serviceOrderId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        INVENTORY_URL       = getString(R.string.base_url) + getString(R.string.inventory_url);
        FINISHINVENTORY_URL = getString(R.string.base_url) + getString(R.string.closeinventory_url);

        skFuelLevel       = ( SeekBar  )findViewById(R.id.skFuelLevel      );
        etServiceOrderKM  = ( EditText )findViewById(R.id.etServiceOrderKM );
        rgBrakesFluid     = (RadioGroup)findViewById(R.id.rgBrakesFluid    );
        rgWipers          = (RadioGroup)findViewById(R.id.rgWipers         );
        rgAntifreeze      = (RadioGroup)findViewById(R.id.rgAntifreeze     );
        rgOil             = (RadioGroup)findViewById(R.id.rgOil            );
        rgPowerSteering   = (RadioGroup)findViewById(R.id.rgPowerSteering  );
        txtFuelLevel      = ( TextView )findViewById(R.id.txtFuelLevel     );
        btnOutsidePhotos  = (  Button  )findViewById(R.id.btnOutsidePhotos );
        btnInsidePhotos   = (  Button  )findViewById(R.id.btnInsidePhotos  );
        btnMotorPhotos    = (  Button  )findViewById(R.id.btnMotorPhotos   );
        btnTrunkPhotos    = (  Button  )findViewById(R.id.btnTrunkPhotos   );
        btnQueueInventory = (  Button  )findViewById(R.id.btnQueueInventory);
        btnCloseInventory = (  Button  )findViewById(R.id.btnCloseInventory);

        etServiceOrderKM.addTextChangedListener(new NumberTextWatcher(etServiceOrderKM));
        btnOutsidePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_SERVICEORDERID, serviceOrderId);
                intent.putExtra(GalleryActivity.EXTRA_USERID, userId);
                intent.putExtra(GalleryActivity.EXTRA_TYPE, "outside");
                startActivity(intent);
            }
        });
        btnInsidePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_SERVICEORDERID, serviceOrderId);
                intent.putExtra(GalleryActivity.EXTRA_USERID, userId);
                intent.putExtra(GalleryActivity.EXTRA_TYPE, "inside");
                startActivity(intent);
            }
        });
        btnMotorPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_SERVICEORDERID, serviceOrderId);
                intent.putExtra(GalleryActivity.EXTRA_USERID, userId);
                intent.putExtra(GalleryActivity.EXTRA_TYPE, "motor");
                startActivity(intent);
            }
        });
        btnTrunkPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
                intent.putExtra(GalleryActivity.EXTRA_SERVICEORDERID, serviceOrderId);
                intent.putExtra(GalleryActivity.EXTRA_USERID, userId);
                intent.putExtra(GalleryActivity.EXTRA_TYPE, "trunk");
                startActivity(intent);
            }
        });

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

        btnQueueInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option = LATER;
                new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
            }
        });

        btnCloseInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    option = CLOSE;
                    new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                }
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new LoadInventoryAsyncTask().execute(INVENTORY_URL);
    }

    private boolean validate()
    {
        if (etServiceOrderKM.getText().toString().trim().equals(""))
        {
            Toast.makeText(getBaseContext(), "escribir kilometraje", Toast.LENGTH_LONG).show();
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
                    new UpdateInventoryAsyncTask().execute(INVENTORY_URL);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadInventoryAsyncTask extends AsyncTask<String, String, String> {
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
                    serviceInventory = objectMapper.readValue(jsonResult.getJSONObject("service_inventory").toString(), ServiceInventory.class);
                    etServiceOrderKM.setText(serviceInventory.km == 0? "" : serviceInventory.km + "");
                    skFuelLevel.setProgress(serviceInventory.fuel_level);
                    if (serviceInventory.brake_fluid != null)
                        switch (serviceInventory.brake_fluid){
                            case "Falta":
                                rgBrakesFluid.check(R.id.rbBrakesFluidLack);
                                break;
                            case "A Nivel":
                                rgBrakesFluid.check(R.id.rbBrakesFluidOk);
                                break;
                        }
                    if (serviceInventory.wiper_fluid != null)
                        switch (serviceInventory.wiper_fluid){
                            case "Falta":
                                rgWipers.check(R.id.rbWipersLack);
                                break;
                            case "A Nivel":
                                rgWipers.check(R.id.rbWipersOk);
                                break;
                        }
                    if (serviceInventory.antifreeze != null)
                        switch (serviceInventory.antifreeze){
                            case "Falta":
                                rgAntifreeze.check(R.id.rbAntifreezeLack);
                                break;
                            case "A Nivel":
                                rgAntifreeze.check(R.id.rbAntifreezeOk);
                                break;
                        }
                    if (serviceInventory.oil != null)
                        switch (serviceInventory.oil){
                            case "Falta":
                                rgOil.check(R.id.rbOilLack);
                                break;
                            case "A Nivel":
                                rgOil.check(R.id.rbOilOk);
                                break;
                        }
                    if (serviceInventory.power_steering_fluid != null)
                        switch (serviceInventory.power_steering_fluid){
                            case "Falta":
                                rgPowerSteering.check(R.id.rbPowerSteeringLack);
                                break;
                            case "A Nivel":
                                rgPowerSteering.check(R.id.rbPowerSteeringOk);
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

    private class UpdateInventoryAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("km",                   etServiceOrderKM.getText().toString().replace(",",""));
                jsonObject.accumulate("fuel_level",           skFuelLevel.getProgress());
                jsonObject.accumulate("brake_fluid",          ((RadioButton)findViewById(  rgBrakesFluid.getCheckedRadioButtonId())).getText().toString());
                jsonObject.accumulate("wiper_fluid",          ((RadioButton)findViewById(       rgWipers.getCheckedRadioButtonId())).getText().toString());
                jsonObject.accumulate("antifreeze",           ((RadioButton)findViewById(   rgAntifreeze.getCheckedRadioButtonId())).getText().toString());
                jsonObject.accumulate("oil",                  ((RadioButton)findViewById(          rgOil.getCheckedRadioButtonId())).getText().toString());
                jsonObject.accumulate("power_steering_fluid", ((RadioButton)findViewById(rgPowerSteering.getCheckedRadioButtonId())).getText().toString());
                return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceOrderId + "/", jsonObject);
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
                        case CLOSE: {
                            new CloseInventoryAsyncTask().execute(FINISHINVENTORY_URL);
                        }
                        break;
                        case LATER: {
                            Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
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

    private class CloseInventoryAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceOrderId + "/", new JSONObject());
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
