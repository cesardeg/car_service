package com.example.cesar.carservice;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import RFID_Data.CMD_AntPortOp;
import RFID_Data.CMD_Iso18k6cTagAccess;
import RFID_Data.CMD_PwrMgt;
import RFID_Data.MtiCmd;
import RFID_Data.UsbCommunication;


public class ReadTagActivity extends ActionBarActivity {

    private static final String URL = "http://192.168.15.125/~Cesar/carservice/public/carbyepc/";

    private static final String ACTION_USB_PERMISSION = "com.mti.rfid.minime.USB_PERMISSION";
    boolean DEBUG = false;
    private static final int PID = 49193;
    private static final int VID = 4901;
    UsbManager manager;
    UsbCommunication usbCommunication;

    EditText etEpc;
    Button btnRead;
    TextView txtStatus;

    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);

        manager = (UsbManager)getSystemService(Context.USB_SERVICE);
        usbCommunication = UsbCommunication.newInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED); // will intercept by system
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter);

        user = (User)getIntent().getExtras().get("user");
        etEpc = (EditText)findViewById(R.id.etEPCReadTag);
        btnRead = (Button)findViewById(R.id.btnRead);
        txtStatus = (TextView)findViewById(R.id.txtStatus);
        etEpc.setFocusable(false);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEpc.setText("");
                if (txtStatus.getText().toString().equals("conectado")) {
                    readTag();
                } else {
                    Toast.makeText(getBaseContext(), "dispositivo " + txtStatus.getText(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
                } else {
                    usbCommunication.setUsbInterface(manager, device);
                    setPowerLevel();
                    txtStatus.setText("conectado");
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
                            etEpc.setText(tagId);
                            break;
                        }
                    }
                }
                if (!etEpc.getText().toString().equals(""))
                    new HttpAsyncTask().execute(URL);
                else
                    etEpc.setHint("No se encontro ningun TAG");
            }
        });
    }

    private boolean validate() {
        if (etEpc.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escanear TAG", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_read_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
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

    private class HttpAsyncTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            //obtnemos usr y pass
            String epc = etEpc.getText().toString().replace(" ", "+");
            return HttpAux.httpGetRequest(params[0] + user.client_id + "/" + epc + "/");

        }

        /*Una vez terminado doInBackground segun lo que halla ocurrido
        pasamos a la sig. activity
        o mostramos error*/
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.has("car")) {
                    JSONObject jsonObject = jsonResult.getJSONObject("car");
                    if (jsonObject != null ) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        car = objectMapper.readValue(jsonObject.toString(), Car.class);
                        Intent i = new Intent(getBaseContext(), ShowCarActivity.class);
                        i.putExtra("car", car);
                        i.putExtra("user", user);
                        startActivity(i);
                    } else {
                        Toast.makeText(getBaseContext(), "No existe un Vehiculo asociado a este TAG", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Error: " + result, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Toast.makeText(getBaseContext(), "Fallo la conexión, vuelve a intentar", Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
