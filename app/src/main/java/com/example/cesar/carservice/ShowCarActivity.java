package com.example.cesar.carservice;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ShowCarActivity extends ActionBarActivity {

    ImageView imgCar;
    TextView txtOwnerName, txtEPC, txtCarBrand, txtCarLine, txtModel, txtSerialNumber, txtColor, txtKM;
    Button btnHistorial, btnOrderService, btnInputKM;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car);

        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");

        txtOwnerName = (TextView)findViewById(R.id.txtOwnerName);
        txtEPC = (TextView)findViewById(R.id.txtEPC);
        txtCarBrand = (TextView)findViewById(R.id.txtCarBrand);
        txtCarLine = (TextView)findViewById(R.id.txtCarLine);
        txtModel = (TextView)findViewById(R.id.txtCarModel);
        txtSerialNumber = (TextView)findViewById(R.id.txtSerialNumber);
        txtColor = (TextView)findViewById(R.id.txtColor);
        txtKM = (TextView)findViewById(R.id.txtKM);

        txtOwnerName.setText(car.ownerName);
        txtEPC.setText(car.epc);
        txtCarBrand.setText(car.brandName);
        txtCarLine.setText(car.lineName);
        txtModel.setText(car.model + "");
        txtSerialNumber.setText(car.serial_number);
        txtColor.setText(car.color);
        txtKM.setText(car.km + " KM");

        btnHistorial = (Button)findViewById(R.id.btnHistorial);
        btnOrderService = (Button)findViewById(R.id.btnOrderService);
        btnInputKM = (Button) findViewById(R.id.btnInputKM);

        btnOrderService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), OrderService1Activity.class);
                intent.putExtra("car", car);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_car, menu);
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

    public static String httpGetRequest(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. create POST request to the given URL
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpclient.execute(httpGet);
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
}
