package com.grupohqh.carservices.operator;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.ScheduledServicesAdapter;
import Models.ScheduledService;


public class ConfigurationScheduledServicesActivity extends ActionBarActivity {

    String LOAD_URL, ADD_URL, REMOVE_URL, txt = "Ajustar fecha";
    EditText etDescription, etKm;
    TextView txtDate;
    Button btnAdd, btnKmCapture, btnSetDate;
    List<ScheduledService> scheduledServices;
    static ListView listView;
    int carId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_scheduled_services);
        scheduledServices = new ArrayList<>();

        LOAD_URL   = getString(R.string.base_url) + "scheduledservices/";
        ADD_URL    = getString(R.string.base_url) + "addscheduledservice/";


        if (getIntent().getExtras().containsKey("carId"))
            carId = getIntent().getExtras().getInt("carId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        REMOVE_URL = getString(R.string.base_url) + "removescheduledservice/" + userId + "/";

        etDescription = (EditText)findViewById(R.id.etDescription);
        etKm = (EditText)findViewById(R.id.etKm);
        btnAdd = (Button)findViewById(R.id.btnAdd);
        txtDate = (TextView)findViewById(R.id.txtDate);
        btnKmCapture = (Button)findViewById(R.id.btnKmCapture);
        btnSetDate = (Button)findViewById(R.id.btnSetDate);
        listView = (ListView)findViewById(R.id.lvScheduledServices);

        final ScheduledServicesAdapter scheduledServicesAdapter = new ScheduledServicesAdapter(scheduledServices, getBaseContext(), REMOVE_URL);
        listView.setAdapter(scheduledServicesAdapter);

        txtDate.setText(txt);
        txtDate.setTextColor(0xff888888);

        etKm.addTextChangedListener(new NumberTextWatcher(etKm));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new AddScheduledServiceAsyncTask().execute(ADD_URL);
                }
            }
        });

        btnKmCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ConfigurationKMCaptureActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("carId", carId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                ConfigurationScheduledServicesActivity.this.finish();
            }
        });

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ConfigurationScheduledServicesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        c.set(year, monthOfYear, dayOfMonth);
                        txtDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(c.getTime()));
                        txtDate.setTextColor(0xff111111);
                    }
                }, mYear, mMonth, mDay);
                dpd.show();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        new LoadConfigurationScheduledAsyncTask().execute(LOAD_URL);
    }

    boolean validate(){
        if (etDescription.getText().toString().trim().equals("")) {
            Toast.makeText(getBaseContext(), "Favor de escribir una descripción", Toast.LENGTH_LONG).show();
            return false;
        }
        if (txtDate.getText().toString().equals(txt)) {
            Toast.makeText(getBaseContext(), "Favor de ajustar fecha", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etKm.getText().toString().trim().matches("^\\d{1,3}(,\\d{3})*$")) {
            Toast.makeText(getBaseContext(), "Favor de escribir un kilometraje válido", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public static void setListViewHeightBasedOnChildren() {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.EXACTLY);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration_scheduled_services, menu);
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

    class LoadConfigurationScheduledAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            return HttpAux.httpGetRequest(urls[0] + userId + "/" + carId + "/");
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    scheduledServices.clear();
                    JSONArray jsonArray = jsonObject.getJSONArray("scheduled_services");
                    for (int i = 0; i < jsonArray.length(); i++){
                        scheduledServices.add(objectMapper.readValue(jsonArray.get(i).toString(), ScheduledService.class));
                    }
                    Collections.sort(scheduledServices, new Comparator<ScheduledService>() {
                        @Override
                        public int compare(ScheduledService lhs, ScheduledService rhs) {
                            return lhs.km - rhs.km;
                        }
                    });
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    setListViewHeightBasedOnChildren();
                }
                else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    class AddScheduledServiceAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("km", etKm.getText().toString().replace(",",""));
                jsonObject.accumulate("description", etDescription.getText().toString().trim());
                jsonObject.accumulate("date", txtDate.getText().toString());
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
                    ScheduledService scheduledService = new ScheduledService();
                    scheduledService.id = jsonObject.getInt("id");
                    scheduledService.km = Integer.parseInt(etKm.getText().toString().replace(",",""));
                    scheduledService.date = txtDate.getText().toString().trim();
                    scheduledService.description = etDescription.getText().toString().trim();
                    scheduledServices.add(scheduledService);
                    Collections.sort(scheduledServices, new Comparator<ScheduledService>() {
                        @Override
                        public int compare(ScheduledService lhs, ScheduledService rhs) {
                            return lhs.km - rhs.km;
                        }
                    });
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    setListViewHeightBasedOnChildren();
                    etDescription.setText("");
                    etDescription.requestFocus();
                    etKm.setText("");
                    txtDate.setText(txt);
                    txtDate.setTextColor(0xff888888);
                } else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
