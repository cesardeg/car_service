package com.example.cesar.carservice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import RFID_Data.CMD_AntPortOp;
import RFID_Data.CMD_Iso18k6cTagAccess;
import RFID_Data.CMD_PwrMgt;
import RFID_Data.MtiCmd;
import RFID_Data.UsbCommunication;


public class ManipulateCarActivity extends ActionBarActivity {

    private static final String ACTION_USB_PERMISSION = "com.mti.rfid.minime.USB_PERMISSION";
    boolean DEBUG = false;
    private static final int PID = 49193;
    private static final int VID = 4901;
    UsbManager manager;
    UsbCommunication usbCommunication;

    String CARBRAND_URL, CARLINE_URL, SAVECAR_URL;
    int carOwnerId, userId, selectId = -1, otherId = 0;;
    List<String> brands, models;
    Map<String, Integer> mapBrands, mapModels;
    String selecBrandText = "Selecciona la marca del vehículo", selecModelText  = "Selecciona el modelo del vehículo", otherText = "Otro";

    Spinner spCarBrand, spCarModel;
    EditText etEPC, etCarYear, etColor, etSerialNumber, etKM, etCarBrand, etCarModel;
    Button btnChangeImageCar, btnReadTAG, btnSaveCar;
    TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manipulate_car);

        manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        usbCommunication = UsbCommunication.newInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED); // will intercept by system
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        if (getIntent().getExtras().containsKey("carOwnerId"))
            carOwnerId = getIntent().getExtras().getInt("carOwnerId");

        CARBRAND_URL = getString(R.string.base_url) + getString(R.string.carbrands_url);
        CARLINE_URL  = getString(R.string.base_url) + getString(R.string.carmodels_url);
        SAVECAR_URL  = getString(R.string.base_url) + "createcar/";

        etEPC          = (EditText)findViewById(R.id.etEPC);
        etCarBrand     = (EditText)findViewById(R.id.etCarBrand);
        etCarModel     = (EditText)findViewById(R.id.etCarModel);
        etCarYear      = (EditText)findViewById(R.id.etCarYear);
        etColor        = (EditText)findViewById(R.id.etCarColor);
        etSerialNumber = (EditText)findViewById(R.id.etSerialNumber);
        etKM           = (EditText)findViewById(R.id.etKm);
        spCarBrand     = (Spinner) findViewById(R.id.spCarBrand);
        spCarModel     = (Spinner) findViewById(R.id.spCarModel);
        btnSaveCar     = (Button)  findViewById(R.id.btnSaveCar);
        btnReadTAG     = (Button)  findViewById(R.id.btnReadTAGManipulateCar);
        txtStatus      = (TextView)findViewById(R.id.txtStatus);

        etCarBrand.setVisibility(View.GONE);
        etCarModel.setVisibility(View.GONE);

        //etEPC.setFocusable(false);
        btnReadTAG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEPC.setText("");
                if (txtStatus.getText().toString().equals("conectado")) {
                    readTag();
                } else {
                    Toast.makeText(getBaseContext(), "dispositivo " + txtStatus.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    new SaveCarAsyncTask().execute(SAVECAR_URL);
            }
        });

        spCarBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView)parent.getChildAt(position)).setTextColor(Color.GRAY);
                if (mapBrands.get(brands.get(position)) == otherId){
                    etCarBrand.setVisibility(View.VISIBLE);
                    etCarModel.setVisibility(View.VISIBLE);
                    spCarModel.setVisibility(View.GONE);
                    etCarBrand.requestFocus();
                } else {
                    etCarBrand.setVisibility(View.GONE);
                    etCarModel.setVisibility(View.GONE);
                    spCarModel.setVisibility(View.VISIBLE);
                    new LoadCarModelsAsyncTask().execute(CARLINE_URL + mapBrands.get(brands.get(position)) + "/");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCarModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView)parent.getChildAt(position)).setTextColor(Color.GRAY);
                if (mapModels.get(models.get(position)) == otherId){
                    etCarModel.setVisibility(View.VISIBLE);
                    etCarModel.requestFocus();
                } else {
                    etCarModel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        new LoadCarBrandsAsyncTask().execute(CARBRAND_URL);

        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        txtStatus.setText("desconectado");
        while(deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if (device.getProductId() == PID && device.getVendorId() == VID)
                if(!manager.hasPermission(device)) {
                    txtStatus.setText("sin permisos");
                    manager.requestPermission(device, PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0));
                    break;
                } else {
                    txtStatus.setText("conectado");
                }
        }
    }

    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if(DEBUG) Toast.makeText(getBaseContext(), "USB Attached", Toast.LENGTH_SHORT).show();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(!manager.hasPermission(device)) {
                    manager.requestPermission(device, PendingIntent.getBroadcast(getBaseContext(), 0, new Intent(ACTION_USB_PERMISSION), 0));
                    txtStatus.setText("sin permisos");
                }
            }
            else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if(DEBUG) Toast.makeText(getBaseContext(), "USB Detached", Toast.LENGTH_SHORT).show();
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if(manager.hasPermission(device))
                    usbCommunication.setUsbInterface(null, null);
                txtStatus.setText("desconectado");
            }
            else if(ACTION_USB_PERMISSION.equals(action)) {
                if(DEBUG) Toast.makeText(context, "USB Permission", Toast.LENGTH_SHORT).show();
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    synchronized(this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (usbCommunication.getUsbDevice() != null)
                                usbCommunication.setUsbInterface(null, null);
                            usbCommunication.setUsbInterface(manager, device);
                            txtStatus.setText("conectado");
                            setPowerLevel();
                            setPowerState(CMD_PwrMgt.PowerState.Sleep);
                        } else {
                            txtStatus.setText("sin permisos");
                        }
                    }
                }
            }
        }
    };

    private void readTag() {
        runOnUiThread(new Runnable() {
            int scantimes = 25;
            String tagId;
            ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

            @Override
            public void run() {
                for (int i = 0; i < scantimes; i++) {
                    MtiCmd mtiCmd = new CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory(usbCommunication);
                    CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory finalCmd = (CMD_Iso18k6cTagAccess.RFID_18K6CTagInventory) mtiCmd;
                    if (finalCmd.setCmd(CMD_Iso18k6cTagAccess.Action.StartInventory)) {
                        tagId = finalCmd.getTagId();
                        if (finalCmd.getTagNumber() > 0) {
                            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                            etEPC.setText(tagId);
                            break;
                        }
                    }
                }
                if (etEPC.getText().toString().equals(""))
                    etEPC.setHint("No se encontro ningun TAG");
            }
        });
    }

    public boolean validate() {
        if (etEPC.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de leer el TAG", Toast.LENGTH_LONG).show();
            etEPC.requestFocus();
            return false;
        }
        if (mapBrands.get(spCarBrand.getSelectedItem().toString()) == selectId) {
            Toast.makeText(getBaseContext(), "favor de seleccionar la marca del vehículo", Toast.LENGTH_LONG).show();
            return false;
        }
        if (mapBrands.get(spCarBrand.getSelectedItem().toString()) == otherId && etCarBrand.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "favor de escribir la marca del vehículo", Toast.LENGTH_LONG).show();
            etCarBrand.requestFocus();
            return false;
        }
        if (mapModels.get(spCarModel.getSelectedItem().toString()) == selectId && mapBrands.get(spCarBrand.getSelectedItem().toString()) != otherId) {
            Toast.makeText(getBaseContext(), "favor de seleccionar el modelo del vehículo", Toast.LENGTH_LONG).show();
            return false;
        }
        if ((mapModels.get(spCarModel.getSelectedItem().toString()) == otherId || mapBrands.get(spCarBrand.getSelectedItem().toString()) == otherId)
                && etCarModel.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "favor de escribir el modelo del vehículo", Toast.LENGTH_LONG).show();
            etCarModel.requestFocus();
            return false;
        }
        if (etCarYear.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "favor de escribir el año del vehículo", Toast.LENGTH_LONG).show();
            etCarYear.requestFocus();
            return false;
        }
        if (!etCarYear.getText().toString().trim().matches("19[0-9]{2}|20[0-9]{2}")) {
            Toast.makeText(getBaseContext(), "favor de escribir un año entre 1900 y 2099", Toast.LENGTH_LONG).show();
            etCarYear.requestFocus();
            return false;
        }
        if (etColor.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "favor de escribir el color del vehículo", Toast.LENGTH_LONG).show();
            etColor.requestFocus();
            return false;
        }
        if (etSerialNumber.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "favor de escribir el número de serie del vehículo", Toast.LENGTH_LONG).show();
            etSerialNumber.requestFocus();
            return false;
        }
        if (etKM.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "favor de escribir el kilometraje del vehículo", Toast.LENGTH_LONG).show();
            etKM.requestFocus();
            return false;
        }
        if (!etKM.getText().toString().trim().matches("[0-9]+")){
            Toast.makeText(getBaseContext(), "favor de escribir un kilometraje válido", Toast.LENGTH_LONG).show();
            etKM.requestFocus();
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manipulate_car, menu);
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

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private void setPowerLevel() {
        MtiCmd mMtiCmd = new CMD_AntPortOp.RFID_AntennaPortSetPowerLevel(usbCommunication);
        CMD_AntPortOp.RFID_AntennaPortSetPowerLevel finalCmd = (CMD_AntPortOp.RFID_AntennaPortSetPowerLevel) mMtiCmd;
        finalCmd.setCmd((byte) 18);
    }

    private void setPowerState(CMD_PwrMgt.PowerState state) {
        try {
            MtiCmd mMtiCmd = new CMD_PwrMgt.RFID_PowerEnterPowerState(usbCommunication);
            CMD_PwrMgt.RFID_PowerEnterPowerState finalCmd = (CMD_PwrMgt.RFID_PowerEnterPowerState) mMtiCmd;
            finalCmd.setCmd(state);
            Thread.sleep(200);
        } catch (Exception e) {
            Toast.makeText(this, "Error en setPowerState: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    class LoadCarBrandsAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                brands = new ArrayList<String>();
                models = new ArrayList<String>();
                mapBrands = new HashMap<String, Integer>();
                mapModels = new HashMap<String, Integer>();
                brands.add(selecBrandText);
                models.add(selecModelText);
                mapBrands.put(selecBrandText, selectId);
                mapModels.put(selecModelText, selectId);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    brands.add(jsonObject.getString("name"));
                    mapBrands.put(jsonObject.getString("name"), jsonObject.getInt("id"));
                }
                brands.add(otherText);
                models.add(otherText);
                mapBrands.put(otherText, otherId);
                mapModels.put(otherText, otherId);
                ArrayAdapter<String> brandAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, brands);
                brandAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarBrand.setAdapter(brandAdapter);

                ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models);
                lineAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarModel.setAdapter(lineAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class LoadCarModelsAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                models = new ArrayList<String>();
                mapModels = new HashMap<String, Integer>();
                models.add(selecModelText);
                mapModels.put(selecModelText, selectId);
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    models.add(jsonObject.getString("name"));
                    mapModels.put(jsonObject.getString("name"), jsonObject.getInt("id"));
                }
                models.add(otherText);
                mapModels.put(otherText, otherId);
                ArrayAdapter<String> lineAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, models);
                lineAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spCarModel.setAdapter(lineAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SaveCarAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("epc", etEPC.getText().toString().trim());
                if (mapBrands.get(spCarBrand.getSelectedItem().toString()) != otherId) {
                    jsonObject.accumulate("brand", spCarBrand.getSelectedItem().toString());
                } else {
                    jsonObject.accumulate("brand", etCarBrand.getText().toString().trim());
                    jsonObject.accumulate("model", etCarModel.getText().toString().trim());
                }
                if (mapModels.get(spCarModel.getSelectedItem().toString()) != otherId) {
                    if (mapBrands.get(spCarBrand.getSelectedItem().toString()) != otherId)
                        jsonObject.accumulate("model", spCarModel.getSelectedItem().toString());
                } else {
                    jsonObject.accumulate("model", etCarModel.getText().toString().trim());
                }
                jsonObject.accumulate("year", etCarYear.getText().toString().trim());
                jsonObject.accumulate("serial_number", etSerialNumber.getText().toString().trim());
                jsonObject.accumulate("color", etColor.getText().toString().trim());
                jsonObject.accumulate("km", etKM.getText().toString().trim());
                jsonObject.accumulate("car_owner_id", carOwnerId);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return HttpAux.httpPostRequest(urls[0] + userId + "/", jsonObject);
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    int carId = jsonObject.getInt("id");
                    Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                    intent.putExtra("carId", carId);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    ManipulateCarActivity.this.finish();
                }
                else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
