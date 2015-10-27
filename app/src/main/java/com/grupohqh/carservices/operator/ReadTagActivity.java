package com.grupohqh.carservices.operator;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


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
    private String URL;
    boolean DEBUG = false, useMiniMe = false;
    int userId;
    String key = "tag";
    RadioGroup rgSearchBy;
    EditText etTag, etLicensePlate, etSerialNumber;
    View viewTag, viewPlate, viewSerial;
    Button btnSearch;

    /*------------------------Only available with true useMiniBand boolean------------------------*/
    private static final String ACTION_USB_PERMISSION = "com.mti.rfid.minime.USB_PERMISSION";     //
    private static final int PID = 49193, VID = 4901;                                             //
    UsbManager manager; UsbCommunication usbCommunication;                                        //
    EditText etEpc; Button btnRead; TextView txtStatus;                                           //
    /*--------------------------------------------------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_tag);

        URL = getString(R.string.base_url) + getString(R.string.findcarid_url);
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        rgSearchBy     = (RadioGroup)findViewById(R.id.rgSearchBy    );
        etTag          = ( EditText )findViewById(R.id.etTag         );
        etLicensePlate = ( EditText )findViewById(R.id.etLicensePlate);
        etSerialNumber = ( EditText )findViewById(R.id.etSerialNumber);
        btnSearch      = (  Button  )findViewById(R.id.btnSearch     );
        viewTag        =             findViewById(R.id.viewTag       );
        viewPlate      =             findViewById(R.id.viewPlate     );
        viewSerial     =             findViewById(R.id.viewSerial    );

        changeVisibility(rgSearchBy.getCheckedRadioButtonId());

        rgSearchBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                changeVisibility(checkedId);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new HttpAsyncTask().execute(URL);
                }
            }
        });



        if (useMiniMe) {
            manager = (UsbManager)getSystemService(Context.USB_SERVICE);
            usbCommunication = UsbCommunication.newInstance();

            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED); // will intercept by system
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            filter.addAction(ACTION_USB_PERMISSION);
            registerReceiver(usbReceiver, filter);
            etEpc     = (EditText)findViewById(R.id.etEPC);
            btnRead   = ( Button )findViewById(R.id.btnReadTAG);
            txtStatus = (TextView)findViewById(R.id.txtStatus);

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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        etSerialNumber.setText("");
        etLicensePlate.setText("");
        etTag.setText("");
        if (useMiniMe) {
            etEpc.setText("");
            txtStatus.setText("desconectado");
            try {
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
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
            } catch (Exception e) {
                Log.d("USB error", e.getMessage());
            }
        }
    }

    private boolean validate() {
        EditText editText = null;
        switch (key) {
            case "tag"          : editText = etTag;          break;
            case "license_plate": editText = etLicensePlate; break;
            case "serial_number": editText = etSerialNumber; break;
            default: return false;
        }
        if (editText.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escanear TAG", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void changeVisibility(int checkedId) {
        switch (checkedId) {
            case R.id.rbTag:
                key = "tag";
                viewTag.setVisibility(View.VISIBLE);
                viewPlate.setVisibility(View.GONE);
                viewSerial.setVisibility(View.GONE);
                break;
            case R.id.rbPlate:
                key = "license_plate";
                viewTag.setVisibility(View.GONE);
                viewPlate.setVisibility(View.VISIBLE);
                viewSerial.setVisibility(View.GONE);
                break;
            case R.id.rbSerie:
                key = "serial_number";
                viewTag.setVisibility(View.GONE);
                viewPlate.setVisibility(View.GONE);
                viewSerial.setVisibility(View.VISIBLE);
                break;
        }
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

        @Override
        protected String doInBackground(String... params) {
            String value = "none";
            switch (key) {
                case "tag":
                    value = URLEncoder.encode(etTag.getText().toString());
                    break;
                case "license_plate":
                    value = URLEncoder.encode(etLicensePlate.getText().toString());
                    break;
                case "serial_number":
                    value = URLEncoder.encode(etSerialNumber.getText().toString());
                    break;
            }
            return HttpAux.httpGetRequest(params[0] + userId + "/" + key + "/" + value +"/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("carId", jsonResult.getInt("id"));
                    startActivity(intent);
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
