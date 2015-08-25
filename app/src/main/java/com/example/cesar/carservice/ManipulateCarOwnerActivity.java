package com.example.cesar.carservice;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManipulateCarOwnerActivity extends ActionBarActivity {

    String STORE_URL;
    EditText etBusinessName, etRFC, etBusinessUserName, etFirstName, etLastName, etMotherMaidenName, etPersonUsername,
            etStreet, etNeighborhood, etPostalCode, etEmail, etTelephone, etMovil;
    Spinner spState, spTown, spType;
    View layoutPerson, layoutBusiness;
    Button btnSave;
    
    List<String> states, towns;
    Map<String, Integer> mapStates;
    int userId;
    String typeCarOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manipulate_car_owner);

        STORE_URL = getString(R.string.base_url) + "createowner/";

        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");

        spType = (Spinner)findViewById(R.id.spCarOwnerType);
        etBusinessName = (EditText)findViewById(R.id.etBusinessName);
        etRFC = (EditText)findViewById(R.id.etRFC);
        etBusinessUserName = (EditText)findViewById(R.id.etBusinessUserName);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etMotherMaidenName = (EditText)findViewById(R.id.etMotherMaidenName);
        etPersonUsername = (EditText)findViewById(R.id.etPersonUsername);
        etStreet = (EditText)findViewById(R.id.etStreet);
        etNeighborhood = (EditText)findViewById(R.id.etNeighborhood);
        spState = (Spinner)findViewById(R.id.spState);
        spTown = (Spinner)findViewById(R.id.spTown);
        etPostalCode = (EditText)findViewById(R.id.etPostalCode);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etTelephone = (EditText)findViewById(R.id.etTelephone);
        etMovil = (EditText)findViewById(R.id.etMovil);
        btnSave = (Button)findViewById(R.id.btnSaveCarOwner);
        layoutPerson = findViewById(R.id.layoutPersonManipulateCarOwner);
        layoutBusiness = findViewById(R.id.layoutBusinessManipulateCarOwner);

        layoutPerson.setVisibility(View.GONE);
        layoutBusiness.setVisibility(View.GONE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    new HttpAsyncTask().execute(STORE_URL);
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.typeCarOwner_arrays, R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spType.setAdapter(arrayAdapter);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView) parent.getChildAt(position)).setTextColor(Color.GRAY);
                switch (parent.getItemAtPosition(position).toString()) {
                    case "Persona":
                        layoutPerson.setVisibility(View.VISIBLE);
                        layoutBusiness.setVisibility(View.GONE);
                        typeCarOwner = "Person";
                        etFirstName.requestFocus();
                        break;
                    case "Empresa":
                        layoutPerson.setVisibility(View.GONE);
                        layoutBusiness.setVisibility(View.VISIBLE);
                        typeCarOwner = "Business";
                        etBusinessName.requestFocus();
                        break;
                    default:
                        layoutPerson.setVisibility(View.GONE);
                        layoutBusiness.setVisibility(View.GONE);
                        typeCarOwner = "None";
                        etStreet.requestFocus();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView)parent.getChildAt(position)).setTextColor(Color.GRAY);
                String url = getString(R.string.base_url) + getString(R.string.towns_url);
                new LoadTownsAsyncTask().execute(url + mapStates.get(spState.getSelectedItem().toString()) + "/");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    ((TextView)parent.getChildAt(position)).setTextColor(Color.GRAY);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = getString(R.string.base_url) + getString(R.string.states_url);
        new LoadStatesAsyncTask().execute(url);
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("type", typeCarOwner);
                if (typeCarOwner.equals("Business")) {
                    jsonObject.accumulate("business_name", etBusinessName.getText().toString().trim());
                    jsonObject.accumulate("rfc", etRFC.getText().toString().trim());
                    jsonObject.accumulate("username", etBusinessUserName.getText().toString().trim());
                }
                else if (typeCarOwner.equals("Person")) {
                    jsonObject.accumulate("first_name", etFirstName.getText().toString().trim());
                    jsonObject.accumulate("last_name", etLastName.getText().toString().trim());
                    jsonObject.accumulate("mother_maiden_name", etMotherMaidenName.getText().toString().trim());
                    jsonObject.accumulate("username", etPersonUsername.getText().toString().trim());
                }
                jsonObject.accumulate("street", etStreet.getText().toString().trim());
                jsonObject.accumulate("neighborhood", etNeighborhood.getText().toString().trim());
                jsonObject.accumulate("state", spState.getSelectedItem().toString());
                jsonObject.accumulate("town", spTown.getSelectedItem().toString());

                if (!etPostalCode.getText().toString().trim().equals(""))
                    jsonObject.accumulate("postal_code", etPostalCode.getText().toString().trim());
                if (!etEmail.getText().toString().trim().equals(""))
                    jsonObject.accumulate("email", etEmail.getText().toString().trim());
                if (!etTelephone.getText().toString().trim().equals(""))
                    jsonObject.accumulate("phone_number", etTelephone.getText().toString().trim());
                if (!etMovil.getText().toString().trim().equals(""))
                    jsonObject.accumulate("mobile_phone_number", etMovil.getText().toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return HttpAux.httpPostRequest(urls[0] + userId + "/", jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    Intent intent = new Intent(getBaseContext(), ShowCarOwnerActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("carOwnerId", jsonObject.getInt("car_owner_id"));
                    startActivity(intent);
                    ManipulateCarOwnerActivity.this.finish();
                }
                else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getBaseContext(), e.getMessage() + "\n" + result, Toast.LENGTH_LONG).show();
            }

        }
    }

    private boolean validate() {
        if (typeCarOwner.equals("Business")) {
            if (etBusinessName.getText().toString().trim().equals("")) {
                etBusinessName.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir la Razón Social", Toast.LENGTH_LONG).show();
                return false;
            }
            if (etRFC.getText().toString().trim().equals("")) {
                etRFC.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el RFC", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!etRFC.getText().toString().trim().matches("[[A-Z|Ñ]|&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z|0-9]?[A-Z|0-9]?[0-9|A-Z]?")){
                etRFC.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir un RFC válido", Toast.LENGTH_LONG).show();
                return false;
            }
            if (etBusinessUserName.getText().toString().trim().equals("")) {
                etBusinessName.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el Nombre de Usuario", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else if (typeCarOwner.equals("Person")) {
            if(etFirstName.getText().toString().trim().equals("")) {
                etFirstName.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el Nombre", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etLastName.getText().toString().trim().equals("")) {
                etLastName.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el Apellido Paterno", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etMotherMaidenName.getText().toString().trim().equals("")) {
                etMotherMaidenName.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el Apellido Materno", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etPersonUsername.getText().toString().trim().equals("")) {
                etPersonUsername.requestFocus();
                Toast.makeText(getBaseContext(), "Favor de escribir el Nombre de Usuario", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else {
            spType.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de seleccionar el tipo de Cliente", Toast.LENGTH_LONG).show();
            return false;
        }
        if(etStreet.getText().toString().trim().equals("")) {
            etStreet.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir la Calle y el Número", Toast.LENGTH_LONG).show();
            return false;
        }
        if(etNeighborhood.getText().toString().trim().equals("")) {
            etNeighborhood.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir la Colonia", Toast.LENGTH_LONG).show();
            return false;
        }
        if (spState.getSelectedItemPosition() == 0) {
            Toast.makeText(getBaseContext(), "Favor de seleccionar el estado", Toast.LENGTH_LONG).show();
            return false;
        }
        if (spTown.getSelectedItemPosition() == 0) {
            Toast.makeText(getBaseContext(), "Favor de seleccionar el municipio", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etPostalCode.getText().toString().trim().equals("") && !etPostalCode.getText().toString().trim().matches("[0-9]{5}"))
        {
            etPostalCode.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir un CP válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etEmail.getText().toString().trim().equals("") && !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()){
            etEmail.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir un email válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etMovil.getText().toString().trim().equals("") && !etMovil.getText().toString().trim().matches("[1-9][0-9]{6,9}"))
        {
            etMovil.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir un número de celular válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etTelephone.getText().toString().trim().equals("") && !etTelephone.getText().toString().trim().matches("[1-9][0-9]{6,9}"))
        {
            etTelephone.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir un número de teléfono válido", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manipulate_car_owner, menu);
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

    class LoadStatesAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                states = new ArrayList<String>();
                towns = new ArrayList<String>();
                mapStates = new HashMap<String, Integer>();
                states.add("Selecciona estado");
                mapStates.put("Selecciona estado", 0);
                towns.add("Selecciona municipio");
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    states.add(jsonObject.getString("state"));
                    mapStates.put(jsonObject.getString("state"), jsonObject.getInt("id"));
                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, states);
                stateAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spState.setAdapter(stateAdapter);

                ArrayAdapter<String> townAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, towns);
                townAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spTown.setAdapter(townAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class LoadTownsAsyncTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0]);
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                towns = new ArrayList<String>();
                towns.add("Selecciona municipio");
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    towns.add(jsonObject.getString("town"));
                }
                ArrayAdapter<String> townAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.support_simple_spinner_dropdown_item, towns);
                townAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spTown.setAdapter(townAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
