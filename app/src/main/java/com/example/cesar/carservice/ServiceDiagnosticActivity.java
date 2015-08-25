package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import Models.QuoteItem;
import Models.ServiceDiagnostic;


public class ServiceDiagnosticActivity extends ActionBarActivity {

    final int LATER = 0, FINISH = 1;
    String SERVICEDIAGNOSTIC_URL, UPDATESERVICEDIAGNOSTIC_URL, FINISHSERVICEDIAGNOSTIC_URL;

    static ListView lvOrderServiceQuotes;
    RadioGroup rgTires, rgFrontShock, rgRearShock, rgFrontBrakes, rgRearBrakes, rgSuspension, rgBands;
    EditText etDiagnostic, etDescription, etAmount;
    Button btnAddQuoteItem, btnDiagnosticLater, btnFinishDiagnostic;
    ServiceDiagnostic serviceDiagnostic;
    int serviceDiagnosticId, userId, option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_diagnostic);

        if (getIntent().getExtras().containsKey("serviceDiagnosticId"))
            serviceDiagnosticId = getIntent().getExtras().getInt("serviceDiagnosticId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        SERVICEDIAGNOSTIC_URL       = getString(R.string.base_url) + getString(R.string.servicediagnostic_url);
        UPDATESERVICEDIAGNOSTIC_URL = getString(R.string.base_url) + getString(R.string.updateservicediagnostic_url);
        FINISHSERVICEDIAGNOSTIC_URL = getString(R.string.base_url) + getString(R.string.closeservicediagnostic_url);

        rgTires = (RadioGroup)findViewById(R.id.rgTires);
        rgFrontShock = (RadioGroup)findViewById(R.id.rgFrontShock);
        rgRearShock = (RadioGroup)findViewById(R.id.rgRearShock);
        rgFrontBrakes = (RadioGroup)findViewById(R.id.rgFrontBrakes);
        rgRearBrakes = (RadioGroup)findViewById(R.id.rgRearBrakes);
        rgSuspension = (RadioGroup)findViewById(R.id.rgSuspension);
        rgBands = (RadioGroup)findViewById(R.id.rgBands);
        etDiagnostic = (EditText)findViewById(R.id.etDiagnostic);
        lvOrderServiceQuotes = (ListView)findViewById(R.id.lvOrderServiceQuotes);
        etDescription = (EditText)findViewById(R.id.etDescription);
        etAmount = (EditText)findViewById(R.id.etAmount);
        btnAddQuoteItem = (Button)findViewById(R.id.btnAddQuoteItem);
        btnDiagnosticLater = (Button)findViewById(R.id.btnDiagnosticLater);
        btnFinishDiagnostic = (Button)findViewById(R.id.btnFinishDiagnostic);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        new LoadServiceDiagnosticAsyncTask().execute(SERVICEDIAGNOSTIC_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_diagnostic, menu);
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

    private boolean validate() {
        if (etAmount.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la cantidad", Toast.LENGTH_LONG).show();
            etAmount.requestFocus();
            return false;
        }
        if (!etAmount.getText().toString().trim().matches("[0-9]+(.[0-9]*)?")){
            Toast.makeText(getBaseContext(), "Favor de escribir una cantidad valida", Toast.LENGTH_LONG).show();
            etAmount.requestFocus();
            return false;
        }
        if (etDescription.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir una descripción del producto", Toast.LENGTH_LONG).show();
            etDescription.requestFocus();
            return false;
        }
        return true;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
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

    private class LoadServiceDiagnosticAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceDiagnosticId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    serviceDiagnostic = objectMapper.readValue(jsonResult.getJSONObject("service_diagnostic").toString(), ServiceDiagnostic.class);

                    final QuoteItemAdapter adapter = new QuoteItemAdapter(serviceDiagnostic, getBaseContext());
                    lvOrderServiceQuotes.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(lvOrderServiceQuotes);

                    etDiagnostic.setText(serviceDiagnostic.description);
                    if (serviceDiagnostic.front_brakes != null)
                        switch (serviceDiagnostic.front_brakes){
                            case "Bueno":
                                rgFrontBrakes.check(R.id.rbFrontBrakesGood);
                                break;
                            case "Regular":
                                rgFrontBrakes.check(R.id.rbFrontBrakesReg);
                                break;
                            case "Malo":
                                rgFrontBrakes.check(R.id.rbFrontBrakesBad);
                                break;
                        }
                    if (serviceDiagnostic.rear_brakes != null)
                        switch (serviceDiagnostic.rear_brakes){
                            case "Bueno":
                                rgRearBrakes.check(R.id.rbRearBrakesGood);
                                break;
                            case "Regular":
                                rgRearBrakes.check(R.id.rbRearBrakesReg);
                                break;
                            case "Malo":
                                rgRearBrakes.check(R.id.rbRearBrakesBad);
                                break;
                        }
                    if (serviceDiagnostic.front_shock_absorber != null)
                        switch (serviceDiagnostic.front_shock_absorber){
                            case "Bueno":
                                rgFrontShock.check(R.id.rbFrontShockGood);
                                break;
                            case "Regular":
                                rgFrontShock.check(R.id.rbFrontShockReg);
                                break;
                            case "Malo":
                                rgFrontShock.check(R.id.rbFrontShockBad);
                                break;
                        }
                    if (serviceDiagnostic.rear_shock_absorver != null)
                        switch (serviceDiagnostic.rear_shock_absorver){
                            case "Bueno":
                                rgRearShock.check(R.id.rbRearShockGood);
                                break;
                            case "Regular":
                                rgRearShock.check(R.id.rbRearShockReg);
                                break;
                            case "Malo":
                                rgRearShock.check(R.id.rbRearShockBad);
                                break;
                        }
                    if (serviceDiagnostic.tires != null)
                        switch (serviceDiagnostic.tires){
                            case "Bueno":
                                rgTires.check(R.id.rbTiresGood);
                                break;
                            case "Regular":
                                rgTires.check(R.id.rbTiresReg);
                                break;
                            case "Malo":
                                rgTires.check(R.id.rbTiresBad);
                                break;
                        }
                    if (serviceDiagnostic.suspension != null)
                        switch (serviceDiagnostic.suspension){
                            case "Bueno":
                                rgSuspension.check(R.id.rbSuspensionGood);
                                break;
                            case "Regular":
                                rgSuspension.check(R.id.rbSuspensionReg);
                                break;
                            case "Malo":
                                rgSuspension.check(R.id.rbSuspensionBad);
                                break;
                        }
                    if (serviceDiagnostic.bands != null)
                        switch (serviceDiagnostic.bands){
                            case "Bueno":
                                rgBands.check(R.id.rbBandsGood);
                                break;
                            case "Regular":
                                rgBands.check(R.id.rbBandsReg);
                                break;
                            case "Malo":
                                rgBands.check(R.id.rbBandsBad);
                                break;
                        }
                    btnAddQuoteItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (validate()) {
                                QuoteItem item = new QuoteItem();
                                item.amount = Float.parseFloat(etAmount.getText().toString());
                                item.description = etDescription.getText().toString().trim();
                                serviceDiagnostic.quote.add(item);
                                adapter.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(lvOrderServiceQuotes);
                                etAmount.setText("");
                                etDescription.setText("");
                                etAmount.requestFocus();
                            }
                        }
                    });
                    btnDiagnosticLater.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            option = LATER;
                            new UpdateServiceDiagnosticAsyncTask().execute(UPDATESERVICEDIAGNOSTIC_URL);
                        }
                    });
                    btnFinishDiagnostic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            option = FINISH;
                            new UpdateServiceDiagnosticAsyncTask().execute(UPDATESERVICEDIAGNOSTIC_URL);
                        }
                    });

                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }

    private class UpdateServiceDiagnosticAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            serviceDiagnostic.description           = etDiagnostic.getText().toString().trim();
            serviceDiagnostic.tires                 = ((RadioButton)findViewById(      rgTires.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.front_brakes          = ((RadioButton)findViewById(rgFrontBrakes.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.rear_brakes           = ((RadioButton)findViewById( rgRearBrakes.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.front_shock_absorber  = ((RadioButton)findViewById( rgFrontShock.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.rear_shock_absorver   = ((RadioButton)findViewById(  rgRearShock.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.suspension            = ((RadioButton)findViewById( rgSuspension.getCheckedRadioButtonId())).getText().toString();
            serviceDiagnostic.bands                 = ((RadioButton)findViewById(      rgBands.getCheckedRadioButtonId())).getText().toString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(serviceDiagnostic);
                return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceDiagnosticId + "/", new JSONObject(jsonString));
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
                            new FinishServiceDiagnosticAsyncTask().execute(FINISHSERVICEDIAGNOSTIC_URL);
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

    private class FinishServiceDiagnosticAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceDiagnosticId + "/");
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
