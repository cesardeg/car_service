package com.example.cesar.carservice;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


public class OrderService2Activity extends ActionBarActivity {

    OrderService orderService;
    Switch swSuppliedParts, swAllowUsedParts, swPickUp, swDeliver;
    EditText etPickUpAddress, etDeliverAddress;
    Button btnOrderService2Next;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service2);

        orderService = (OrderService)getIntent().getExtras().get("orderService");
        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");
        swSuppliedParts = (Switch)findViewById(R.id.swSuppliedParts);
        swAllowUsedParts = (Switch)findViewById(R.id.swAllowUsedParts);
        swPickUp = (Switch)findViewById(R.id.swPickUp);
        swDeliver = (Switch)findViewById(R.id.swDeliver);
        etPickUpAddress = (EditText)findViewById(R.id.etPickUpAddress);
        etDeliverAddress = (EditText)findViewById(R.id.etDeliverAddress);
        btnOrderService2Next = (Button)findViewById(R.id.btnOrderService2Next);

        etPickUpAddress.setEnabled(false);
        etDeliverAddress.setEnabled(false);

        swPickUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPickUpAddress.setEnabled(isChecked);
                etPickUpAddress.requestFocus();
            }
        });
        swDeliver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etDeliverAddress.setEnabled(isChecked);
                etDeliverAddress.requestFocus();
            }
        });

        btnOrderService2Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    orderService.owner_supplied_parts = swSuppliedParts.isChecked();
                    orderService.owner_allow_used_parts = swAllowUsedParts.isChecked();
                    if (swPickUp.isChecked()) {
                        orderService.pick_up_address = etPickUpAddress.getText().toString().trim();
                    }
                    if (swDeliver.isChecked()) {
                        orderService.delivery_address = etDeliverAddress.getText().toString().trim();
                    }
                    Intent intent = new Intent(getBaseContext(), OrderService3Activity.class);
                    intent.putExtra("orderService", orderService);
                    intent.putExtra("car", car);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        if (swPickUp.isChecked() && etPickUpAddress.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la dirección donde se recogió el vehiculo", Toast.LENGTH_LONG).show();
            etPickUpAddress.requestFocus();
            return false;
        }
        if (swDeliver.isChecked() && etDeliverAddress.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la dirección de entrega", Toast.LENGTH_LONG).show();
            etDeliverAddress.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_service2, menu);
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
