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
import android.widget.Toast;


public class OrderService4Activity extends ActionBarActivity {

    OrderService orderService;
    EditText etMechanic, etDiagnostic;
    Button btnOrderService4Next;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service4);

        orderService = (OrderService)getIntent().getExtras().get("orderService");
        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");
        etMechanic = (EditText)findViewById(R.id.etMechanic);
        etDiagnostic = (EditText)findViewById(R.id.etDiagnostic);
        btnOrderService4Next = (Button)findViewById(R.id.btnOrderService4Next);

        btnOrderService4Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    orderService.diagnostic.mechanic_in_charge = etMechanic.getText().toString().trim();
                    orderService.diagnostic.description = etDiagnostic.getText().toString().trim();
                    Intent intent = new Intent(getBaseContext(), OrderServiceQuotesActivity.class);
                    intent.putExtra("orderService", orderService);
                    intent.putExtra("car", car);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validate() {
        if (etMechanic.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el nombre del mecánico a cargo", Toast.LENGTH_LONG).show();
            etMechanic.requestFocus();
            return false;
        }
        if (etDiagnostic.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el diagnóstico", Toast.LENGTH_LONG).show();
            etDiagnostic.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_service4, menu);
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
