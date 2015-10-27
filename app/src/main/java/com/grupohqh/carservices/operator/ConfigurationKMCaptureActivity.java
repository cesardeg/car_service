package com.grupohqh.carservices.operator;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Models.Reminder;


public class ConfigurationKMCaptureActivity extends ActionBarActivity {

    String LOAD_URL, REMIND_URL, UPDATEFREQUENCY_URL;
    Button btnChangeDate, btnChangeTime, btnUpdateNotifications, btnSheduledServices;
    ImageButton btnEditNotifications;
    TextView txtDate, txtTime, txtFrequency, txtTimeUnits;
    Spinner spFrequency, spTimeUnits;
    Switch swRemindCapture;
    View viewNotifications;
    boolean updating, edit;
    String date, time;
    int carId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_kmcapture);

        LOAD_URL = getString(R.string.base_url) + "getreminderkmcapture/";
        REMIND_URL = getString(R.string.base_url) + "remindkmcapture/";
        UPDATEFREQUENCY_URL = getString(R.string.base_url) + "updatereminderkmcapture/";

        if (getIntent().getExtras().containsKey("carId"))
            carId = getIntent().getExtras().getInt("carId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        btnChangeDate = (Button)findViewById(R.id.btnChangeDate);
        btnChangeTime = (Button)findViewById(R.id.btnChangeTime);
        btnEditNotifications = (ImageButton)findViewById(R.id.btnEditNotifications);
        btnUpdateNotifications = (Button)findViewById(R.id.btnUpdateNotifications);
        btnSheduledServices = (Button)findViewById(R.id.btnSheduledServices);
        txtDate = (TextView)findViewById(R.id.txtDate);
        txtTime = (TextView)findViewById(R.id.txtTime);
        txtFrequency = (TextView)findViewById(R.id.txtFrequency);
        txtTimeUnits = (TextView)findViewById(R.id.txtTimeUnits);
        spFrequency = (Spinner)findViewById(R.id.spFrequency);
        spTimeUnits = (Spinner)findViewById(R.id.spTimeUnits);
        swRemindCapture = (Switch)findViewById(R.id.swRemindCapture);
        viewNotifications = findViewById(R.id.viewNotifications);

        Integer[] items = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, items);
        spFrequency.setAdapter(adapter);
        updating = edit = false;


        swRemindCapture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!updating)
                    new RemindAsyncTask().execute(REMIND_URL);
            }
        });

        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ConfigurationKMCaptureActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(c.getTime()));
                        btnUpdateNotifications.setEnabled(true);
                    }
                }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

        btnChangeTime.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);
            @Override
            public void onClick(View v) {
                TimePickerDialog dpd = new TimePickerDialog(ConfigurationKMCaptureActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);
                        txtTime.setText(new SimpleDateFormat("HH:mm").format(c.getTime()));
                        btnUpdateNotifications.setEnabled(true);
                    }
                }, mHour, mMinute, false);
                dpd.show();
            }
        });


        btnUpdateNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateNotificationsAsyncTask().execute(UPDATEFREQUENCY_URL);
            }
        });

        btnEditNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = !edit;
                if (edit) {
                    btnUpdateNotifications.setVisibility(View.VISIBLE);
                    btnChangeTime.setVisibility(View.VISIBLE);
                    btnChangeDate.setVisibility(View.VISIBLE);
                    spFrequency.setVisibility(View.VISIBLE);
                    spTimeUnits.setVisibility(View.VISIBLE);
                    txtFrequency.setVisibility(View.GONE);
                    txtTimeUnits.setVisibility(View.GONE);
                    btnEditNotifications.setImageResource(R.drawable.ic_cancel);
                    date = txtDate.getText().toString();
                    time = txtTime.getText().toString();
                } else {
                    btnUpdateNotifications.setVisibility(View.INVISIBLE);
                    btnChangeTime.setVisibility(View.INVISIBLE);
                    btnChangeDate.setVisibility(View.INVISIBLE);
                    spFrequency.setVisibility(View.GONE);
                    spTimeUnits.setVisibility(View.GONE);
                    txtFrequency.setVisibility(View.VISIBLE);
                    txtTimeUnits.setVisibility(View.VISIBLE);
                    btnEditNotifications.setImageResource(R.drawable.ic_edit_black);
                    txtDate.setText(date);
                    txtTime.setText(time);
                }
            }
        });

        btnSheduledServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ConfigurationScheduledServicesActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("carId", carId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                ConfigurationKMCaptureActivity.this.finish();
            }
        });

        viewNotifications.setVisibility(View.GONE);

    }

    @Override
    public void onResume(){
        super.onResume();
        new LoadConfigurationKMCaptureAsyncTask().execute(LOAD_URL + userId + "/" + carId + "/");

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration_kmcapture, menu);
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

    class LoadConfigurationKMCaptureAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            updating = true;
            return HttpAux.httpGetRequest(urls[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    if (!jsonObject.getString("reminder").equals("null") ) {
                        Reminder reminder = objectMapper.readValue(jsonObject.getJSONObject("reminder").toString(), Reminder.class);
                        swRemindCapture.setChecked(reminder.remind);
                        txtFrequency.setText(reminder.frequency + "");
                        for (int i = 1; i <= 31; i++) {
                            if (i  == reminder.frequency)
                                spFrequency.setSelection(i - 1);
                        }
                        txtTimeUnits.setText(reminder.time_unit);
                        String[] units =  getResources().getStringArray(R.array.timeUnits);
                        for (int i = 0; i < units.length; i++) {
                            if (units[i].equals(reminder.time_unit))
                                spTimeUnits.setSelection(i);
                        }
                        if (reminder.getNext_reminder() != null) {
                            txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(reminder.getNext_reminder()));
                            txtTime.setText(new SimpleDateFormat("HH:mm").format(reminder.getNext_reminder()));
                        }
                        btnUpdateNotifications.setVisibility(View.GONE);
                        btnChangeTime.setVisibility(View.INVISIBLE);
                        btnChangeDate.setVisibility(View.INVISIBLE);
                        spFrequency.setVisibility(View.GONE);
                        spTimeUnits.setVisibility(View.GONE);
                        txtFrequency.setVisibility(View.VISIBLE);
                        txtTimeUnits.setVisibility(View.VISIBLE);
                        btnEditNotifications.setVisibility(View.VISIBLE);
                        viewNotifications.setVisibility(reminder.remind ? View.VISIBLE : View.GONE);
                    } else {
                        swRemindCapture.setChecked(false);
                    }

                }
                else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            updating = false;
        }
    }

    class RemindAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpAux.httpPostRequest(urls[0] + userId + "/" + carId + "/", new JSONObject().accumulate("remind", swRemindCapture.isChecked()));
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
                    viewNotifications.setVisibility(swRemindCapture.isChecked() ? View.VISIBLE : View.GONE);
                    if (swRemindCapture.isChecked())
                        new LoadConfigurationKMCaptureAsyncTask().execute(LOAD_URL + userId + "/" + carId + "/");
                }
                else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                    updating = true;
                    swRemindCapture.toggle();
                    updating = false;
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                updating = true;
                swRemindCapture.toggle();
                updating = false;
            }
        }
    }

    class UpdateNotificationsAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("frequency", spFrequency.getSelectedItem().toString());
                jsonObject.accumulate("time_unit", spTimeUnits.getSelectedItem().toString());
                jsonObject.accumulate("next_reminder", txtDate.getText() + " " + txtTime.getText());
                return HttpAux.httpPostRequest(urls[0] + userId + "/" + carId + "/", jsonObject);
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
                    btnUpdateNotifications.setVisibility(View.GONE);
                    btnChangeTime.setVisibility(View.INVISIBLE);
                    btnChangeDate.setVisibility(View.INVISIBLE);
                    spFrequency.setVisibility(View.GONE);
                    spTimeUnits.setVisibility(View.GONE);
                    txtFrequency.setVisibility(View.VISIBLE);
                    txtTimeUnits.setVisibility(View.VISIBLE);
                    btnEditNotifications.setVisibility(View.VISIBLE);
                    txtFrequency.setText(spFrequency.getSelectedItem().toString());
                    txtTimeUnits.setText(spTimeUnits.getSelectedItem().toString());
                    btnEditNotifications.setImageResource(R.drawable.ic_edit_black);
                    edit = false;
                } else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
