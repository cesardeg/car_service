package com.example.cesar.carservice;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;


public class OrderService1Activity extends ActionBarActivity {

    OrderService orderService;
    Spinner spServiceType;
    SeekBar skFuelLevel;
    EditText etOrderServiceKM;
    Button btnOrderService1Next;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service1);

        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");
        orderService = new OrderService();

        spServiceType = (Spinner)findViewById(R.id.spServiceType);
        skFuelLevel = (SeekBar)findViewById(R.id.skFuelLevel);
        etOrderServiceKM = (EditText)findViewById(R.id.etOrderServiceKM);
        btnOrderService1Next = (Button)findViewById(R.id.btnOrderService1Next);

        btnOrderService1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    orderService.service_name = spServiceType.getSelectedItem().toString();
                    orderService.diagnostic.fuel_level = skFuelLevel.getProgress();
                    orderService.diagnostic.km =  Integer.parseInt(etOrderServiceKM.getText().toString());
                    orderService.car_id = car.id;
                    orderService.receiver_user = user.id;
                    orderService.workshop_id = user.workshop_id;
                    Intent intent = new Intent(getBaseContext(), OrderService2Activity.class);
                    intent.putExtra("orderService", orderService);
                    intent.putExtra("car", car);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean validate() {
        if (spServiceType.getSelectedItemPosition() == 0) {
            Toast.makeText(getBaseContext(), "Favor de seleccionar el tipo de servicio", Toast.LENGTH_LONG).show();
            spServiceType.requestFocus();
            return false;
        }
        if (etOrderServiceKM.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el kilometraje", Toast.LENGTH_LONG).show();
            etOrderServiceKM.requestFocus();
            return false;
        }
        if (!etOrderServiceKM.getText().toString().trim().matches("[0-9]+")){
            Toast.makeText(getBaseContext(), "Solo se aceptan valores numericos para el kilometraje", Toast.LENGTH_LONG).show();
            etOrderServiceKM.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_service1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
