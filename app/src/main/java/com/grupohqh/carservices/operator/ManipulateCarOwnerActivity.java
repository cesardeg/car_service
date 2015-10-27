package com.grupohqh.carservices.operator;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
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
    EditText etBusinessName, etRFC, etFirstName, etLastName, etMotherMaidenName, etUsername, etPassword, etConfirmPass,
            etStreet, etNeighborhood, etPostalCode, etEmail, etTelephone, etMovil;
    Spinner spState, spTown, spType;
    View layoutPerson, layoutBusiness;
    Button btnSave;
    
    List<String> states, towns;
    Map<String, Integer> mapStates, mapTowns;
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
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etMotherMaidenName = (EditText)findViewById(R.id.etMotherMaidenName);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etPassword = (EditText)findViewById(R.id.etPassword);
        etConfirmPass = (EditText)findViewById(R.id.etConfirmPass);
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

        etMovil.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etTelephone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

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
        new LoadStatesAsyncTask().execute(getString(R.string.base_url) + getString(R.string.states_url));
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                }
                else if (typeCarOwner.equals("Person")) {
                    jsonObject.accumulate("first_name", etFirstName.getText().toString().trim());
                    jsonObject.accumulate("last_name", etLastName.getText().toString().trim());
                    jsonObject.accumulate("mother_maiden_name", etMotherMaidenName.getText().toString().trim());
                }
                jsonObject.accumulate("username", etUsername.getText().toString().trim());
                jsonObject.accumulate("password", etPassword.getText().toString());
                if (!etStreet.getText().toString().trim().equals("")){
                    jsonObject.accumulate("street", etStreet.getText().toString().trim());
                }
                if (!etNeighborhood.getText().toString().trim().equals("")){
                    jsonObject.accumulate("neighborhood", etNeighborhood.getText().toString().trim());
                }
                if (spState.getSelectedItemPosition() > 0) {
                    jsonObject.accumulate("state_id", mapStates.get(spState.getSelectedItem().toString() ));
                }
                if (spTown.getSelectedItemPosition() > 0) {
                    jsonObject.accumulate("town_id", mapTowns.get( spTown.getSelectedItem().toString() ));
                }
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
                Toast.makeText(getBaseContext(), "escribir razón social", Toast.LENGTH_LONG).show();
                return false;
            }
            if (etRFC.getText().toString().trim().equals("")) {
                etRFC.requestFocus();
                Toast.makeText(getBaseContext(), "escribir RFC", Toast.LENGTH_LONG).show();
                return false;
            }
            if (!etRFC.getText().toString().trim().matches("[[A-Z|Ñ]|&]{3,4}[0-9]{2}[0-1][0-9][0-3][0-9][A-Z|0-9]?[A-Z|0-9]?[0-9|A-Z]?")) {
                etRFC.requestFocus();
                Toast.makeText(getBaseContext(), "escribir RFC válido", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else if (typeCarOwner.equals("Person")) {
            if(etFirstName.getText().toString().trim().equals("")) {
                etFirstName.requestFocus();
                Toast.makeText(getBaseContext(), "escribir nombre", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etLastName.getText().toString().trim().equals("")) {
                etLastName.requestFocus();
                Toast.makeText(getBaseContext(), "escribir apellido paterno", Toast.LENGTH_LONG).show();
                return false;
            }
            if(etMotherMaidenName.getText().toString().trim().equals("")) {
                etMotherMaidenName.requestFocus();
                Toast.makeText(getBaseContext(), "escribir apellido materno", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        else {
            spType.requestFocus();
            Toast.makeText(getBaseContext(), "seleccionar tipo de cliente", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etUsername.getText().toString().trim().equals("")) {
            etUsername.requestFocus();
            Toast.makeText(getBaseContext(), "escribir nombre de usuario", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etPassword.getText().toString().trim().equals("")){
            etPassword.requestFocus();
            Toast.makeText(getBaseContext(), "escribir contraseña", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etPassword.getText().toString().matches("^(?=.*[0-9])(?=.*[a-zA-Z])[0-9A-Za-z@#\\-_$%^&+=!\\?]{8,20}$")){
            etPassword.requestFocus();
            Toast.makeText(getBaseContext(), "Contraseña debe ser:\n. 8 a 20 carácteres\n. al menos un número y una letra", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etConfirmPass.getText().toString().equals("")) {
            etConfirmPass.requestFocus();
            Toast.makeText(getBaseContext(), "confirmar contraseña", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etPostalCode.getText().toString().trim().equals("") && !etPostalCode.getText().toString().trim().matches("[0-9]{5}"))
        {
            etPostalCode.requestFocus();
            Toast.makeText(getBaseContext(), "código postal 5 digitos", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etEmail.getText().toString().trim().equals("") && !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()){
            etEmail.requestFocus();
            Toast.makeText(getBaseContext(), "escribir email válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etMovil.getText().toString().trim().equals("") && !etMovil.getText().toString().trim().matches("\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})([ .-]?)([0-9]{4})"))
        {
            etMovil.requestFocus();
            Toast.makeText(getBaseContext(), "escribir celular válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etTelephone.getText().toString().trim().equals("") && !etTelephone.getText().toString().trim().matches("\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})([ .-]?)([0-9]{4})"))
        {
            etTelephone.requestFocus();
            Toast.makeText(getBaseContext(), "escribir un teléfono válido", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etMovil.getText().toString().trim().equals("") && etTelephone.getText().toString().trim().equals("")){
            etMovil.requestFocus();
            Toast.makeText(getBaseContext(), "escribir al menos un teléfono de contacto", Toast.LENGTH_LONG).show();
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
                mapTowns = new HashMap<String, Integer>();
                towns.add("Selecciona municipio");
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    towns.add(jsonObject.getString("town"));
                    mapTowns.put(jsonObject.getString("town"), jsonObject.getInt("id"));
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
