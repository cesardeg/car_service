package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ManipulateCarOwnerActivity extends ActionBarActivity {

    EditText etBusinessName, etRFC, etBusinessUserName, etFirstName, etLastName, etMotherMaidenName, etPersonUsername,
            etStreet, etNeighborhood, etState, etTown, etPostalCode, etEmail, etTelephone, etMovil;
    View layoutPerson, layoutBusiness;
    CarOwner carOwner;

    private static final String STORE_URL = "http://192.168.15.125/~Cesar/carservice/public/createowner/";

    Spinner spType;
    Button btnSave;
    String typeCarOwner;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manipulate_car_owner);

        user = (User)getIntent().getExtras().get("user");
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
        etState = (EditText)findViewById(R.id.etState);
        etTown = (EditText)findViewById(R.id.etTown);
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
                switch (parent.getItemAtPosition(position).toString())
                {
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
    }

    public static String POST(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. create POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            // 3. convert JSONObject to JSON to String
            json = jsonObject.toString();
            // 4. set json to StringEntity
            StringEntity se = new StringEntity(json);
            // 5. set httpPost Entity
            httpPost.setEntity(se);
            // 6. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            // 7. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);
            // 8. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 9. convert inputstream to string
            result = convertStreamToString(inputStream);

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
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
                jsonObject.accumulate("state", etState.getText().toString().trim());
                jsonObject.accumulate("town", etTown.getText().toString().trim());

                if (!etPostalCode.getText().toString().trim().equals(""))
                    jsonObject.accumulate("postal_code", etPostalCode.getText().toString().trim());
                if (!etEmail.getText().toString().trim().equals(""))
                    jsonObject.accumulate("email", etEmail.getText().toString().trim());
                if (!etTelephone.getText().toString().trim().equals(""))
                    jsonObject.accumulate("phone_number", etTelephone.getText().toString().trim());
                if (!etMovil.getText().toString().trim().equals(""))
                    jsonObject.accumulate("mobile_phone_number", etMovil.getText().toString().trim());

                jsonObject.accumulate("client_id", user.client_id);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                carOwner = objectMapper.readValue(jsonObject.toString(), CarOwner.class);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return POST(urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
                    carOwner.setId(jsonObject.getInt("id"));
                    Intent intent = new Intent(getBaseContext(), ShowCarOwnerActivity.class);
                    intent.putExtra("carOwner", carOwner);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    return;
                }
                else if (jsonObject.has("msj")) {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "¡Fallo alta, favor de volver a intentar!", Toast.LENGTH_LONG).show();
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
        if(etState.getText().toString().trim().equals("")) {
            etState.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir el Estado", Toast.LENGTH_LONG).show();
            return false;
        }
        if(etTown.getText().toString().trim().equals("")) {
            etTown.requestFocus();
            Toast.makeText(getBaseContext(), "Favor de escribir el Municipio", Toast.LENGTH_LONG).show();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
