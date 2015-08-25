package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.text.DecimalFormat;

import Models.Car;


public class ShowCarActivity extends ActionBarActivity {

    ImageView imgCar;
    TextView txtOwnerName, txtEPC, txtCarBrand, txtModel, txtYear, txtSerialNumber, txtColor, txtKM;
    Button btnHistorial, btnOrderService, btnInputKM;
    int carId, userId, serviceOrderId, serviceDiagnosticId;
    private String CAR_URL, CREATESEVICEORDER_URL, CREATESEVICEDIAGNOSTIC_URL, CREATESERVICEDELIVERY_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car);

        if (getIntent().getExtras().containsKey("carId"))
            carId  = getIntent().getExtras().getInt("carId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        CAR_URL = getString(R.string.base_url) + getString(R.string.car_url);
        CREATESEVICEORDER_URL = getString(R.string.base_url) + getString(R.string.createserviceorder_url);
        CREATESEVICEDIAGNOSTIC_URL = getString(R.string.base_url) + getString(R.string.createservicediagnostic_url);
        CREATESERVICEDELIVERY_URL = getString(R.string.base_url) + getString(R.string.createservicedelivery_url);

        txtOwnerName = (TextView)findViewById(R.id.txtOwnerName);
        txtEPC = (TextView)findViewById(R.id.txtEPC);
        txtCarBrand = (TextView)findViewById(R.id.txtCarBrand);
        txtModel = (TextView)findViewById(R.id.txtCarModel);
        txtYear = (TextView)findViewById(R.id.txtCarYear);
        txtSerialNumber = (TextView)findViewById(R.id.txtSerialNumber);
        txtColor = (TextView)findViewById(R.id.txtColor);
        txtKM = (TextView)findViewById(R.id.txtKM);

        btnHistorial = (Button)findViewById(R.id.btnHistorial);
        btnOrderService = (Button)findViewById(R.id.btnOrderService);
        btnInputKM = (Button) findViewById(R.id.btnInputKM);

    }
    @Override
    protected void onResume() {
        super.onResume();
        btnOrderService.setText("Sin información de servicios");
        btnOrderService.setEnabled(false);
        new LoadCarAsyncTask().execute(CAR_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_car, menu);
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
            case R.id.action_refresh_car:
                new LoadCarAsyncTask().execute(CAR_URL);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadCarAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + carId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                final JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    Car car = objectMapper.readValue(jsonResult.getJSONObject("car").toString(), Car.class);
                    txtOwnerName.setText(car.ownerName);
                    txtEPC.setText(car.epc);
                    txtCarBrand.setText(car.brand);
                    txtModel.setText(car.model);
                    txtYear.setText(car.year + "");
                    txtSerialNumber.setText(car.serial_number);
                    txtColor.setText(car.color);
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    txtKM.setText(formatter.format(car.km) + " KM");
                    if (jsonResult.has("service_order_id"))
                        serviceOrderId = jsonResult.getInt("service_order_id");
                    if (jsonResult.has("service_diagnostic_id"))
                        serviceDiagnosticId = jsonResult.getInt("service_diagnostic_id");
                    switch (jsonResult.getInt("service_status")) {
                        case 0:
                            btnOrderService.setText("Generar orden de servicio");
                            btnOrderService.setEnabled(true);
                            btnOrderService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new CreateServiceOrderAsyncTask().execute(CREATESEVICEORDER_URL);
                                }
                            });
                            break;
                        case 1:
                            btnOrderService.setText("Continuar orden de servicio");
                            btnOrderService.setEnabled(true);
                            btnOrderService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), ServiceOrderActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("serviceOrderId", serviceOrderId);
                                    startActivity(intent);
                                }
                            });
                            break;
                        case 2:
                            btnOrderService.setText("Esperando cliente autorice orden de servicio");
                            btnOrderService.setEnabled(false);
                            break;
                        case 3:
                            btnOrderService.setText("Realizar diagnóstico");
                            btnOrderService.setEnabled(true);
                            btnOrderService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new CreateServiceDiagnosticAsyncTask().execute(CREATESEVICEDIAGNOSTIC_URL);
                                }
                            });
                            break;
                        case 4:
                            btnOrderService.setText("Continuar diagnóstico");
                            btnOrderService.setEnabled(true);
                            btnOrderService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), ServiceDiagnosticActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("serviceDiagnosticId", serviceDiagnosticId);
                                    startActivity(intent);
                                }
                            });
                            break;
                        case 5:
                            btnOrderService.setText("Esperando cliente autorice diagnóstico");
                            btnOrderService.setEnabled(false);
                            break;
                        case 6:
                            btnOrderService.setText("Registrar salida de vehículo");
                            btnOrderService.setEnabled(true);
                            btnOrderService.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new CreateServiceDeliveryAsyncTask().execute(CREATESERVICEDELIVERY_URL);
                                }
                            });
                            break;
                        case 7:
                            btnOrderService.setText("Esperando cliente autorice salida de vehículo");
                            btnOrderService.setEnabled(false);
                            break;
                    }
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    private class CreateServiceOrderAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.accumulate("car_id", carId);
            } catch (Exception e) {
                e.getStackTrace();
            }
            return HttpAux.httpPostRequest(params[0] + userId + "/", jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    Intent intent = new Intent(getBaseContext(), ServiceOrderActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("serviceOrderId", jsonResult.getInt("service_order_id"));
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

    private class CreateServiceDiagnosticAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceOrderId + "/", jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    Intent intent = new Intent(getBaseContext(), ServiceDiagnosticActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("serviceDiagnosticId", jsonResult.getInt("service_diagnostic_id"));
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

    private class CreateServiceDeliveryAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceDiagnosticId + "/", jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    new LoadCarAsyncTask().execute(CAR_URL);
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
