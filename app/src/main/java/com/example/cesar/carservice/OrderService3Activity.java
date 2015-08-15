package com.example.cesar.carservice;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;


public class OrderService3Activity extends ActionBarActivity {

    RadioGroup rgTires, rgFrontShock, rgRearShock, rgFrontBrakes, rgRearBrakes, rgSuspension, rgBands,
            rgBrakesFluid, rgWipers, rgAntifreeze, rgOil, rgDirection;
    Button btnOrderService3Next;
    OrderService orderService;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service3);

        orderService = (OrderService)getIntent().getExtras().get("orderService");
        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");
        rgTires = (RadioGroup)findViewById(R.id.rgTires);
        rgFrontShock = (RadioGroup)findViewById(R.id.rgFrontShock);
        rgRearShock = (RadioGroup)findViewById(R.id.rgRearShock);
        rgFrontBrakes = (RadioGroup)findViewById(R.id.rgFrontBrakes);
        rgRearBrakes = (RadioGroup)findViewById(R.id.rgRearBrakes);
        rgSuspension = (RadioGroup)findViewById(R.id.rgSuspension);
        rgBands = (RadioGroup)findViewById(R.id.rgBands);
        rgBrakesFluid = (RadioGroup)findViewById(R.id.rgBrakesFluid);
        rgWipers = (RadioGroup)findViewById(R.id.rgWipers);
        rgAntifreeze = (RadioGroup)findViewById(R.id.rgAntifreeze);
        rgOil = (RadioGroup)findViewById(R.id.rgOil);
        rgDirection = (RadioGroup)findViewById(R.id.rgDirection);
        btnOrderService3Next = (Button)findViewById(R.id.btnOrderService3Next);

        btnOrderService3Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (rgTires.getCheckedRadioButtonId())
                {
                    case R.id.rbTiresGood:
                        orderService.diagnostic.tires = "Bueno";
                        break;
                    case R.id.rbTiresReg:
                        orderService.diagnostic.tires = "Regular";
                        break;
                    case R.id.rbTiresBad:
                        orderService.diagnostic.tires = "Malo";
                        break;
                }
                switch (rgFrontBrakes.getCheckedRadioButtonId())
                {
                    case R.id.rbFrontBrakesGood:
                        orderService.diagnostic.front_brakes = "Bueno";
                        break;
                    case R.id.rbFrontBrakesReg:
                        orderService.diagnostic.front_brakes = "Regular";
                        break;
                    case R.id.rbFrontBrakesBad:
                        orderService.diagnostic.front_brakes = "Malo";
                        break;
                }
                switch (rgRearBrakes.getCheckedRadioButtonId())
                {
                    case R.id.rbRearBrakesGood:
                        orderService.diagnostic.rear_brakes = "Bueno";
                        break;
                    case R.id.rbRearBrakesReg:
                        orderService.diagnostic.rear_brakes = "Regular";
                        break;
                    case R.id.rbRearBrakesBad:
                        orderService.diagnostic.rear_brakes = "Malo";
                        break;
                }
                switch (rgFrontShock.getCheckedRadioButtonId())
                {
                    case R.id.rbFrontShockGood:
                        orderService.diagnostic.front_shock_absorber = "Bueno";
                        break;
                    case R.id.rbFrontShockReg:
                        orderService.diagnostic.front_shock_absorber = "Regular";
                        break;
                    case R.id.rbFrontShockBad:
                        orderService.diagnostic.front_shock_absorber = "Malo";
                        break;
                }
                switch (rgRearShock.getCheckedRadioButtonId())
                {
                    case R.id.rbRearShockGood:
                        orderService.diagnostic.rear_shock_absorver = "Bueno";
                        break;
                    case R.id.rbRearShockReg:
                        orderService.diagnostic.rear_shock_absorver = "Regular";
                        break;
                    case R.id.rbRearShockBad:
                        orderService.diagnostic.rear_shock_absorver = "Malo";
                        break;
                }
                switch (rgSuspension.getCheckedRadioButtonId())
                {
                    case R.id.rbSuspensionGood:
                        orderService.diagnostic.suspension = "Bueno";
                        break;
                    case R.id.rbSuspensionReg:
                        orderService.diagnostic.suspension = "Regular";
                        break;
                    case R.id.rbSuspensionBad:
                        orderService.diagnostic.suspension = "Malo";
                        break;
                }
                switch (rgBands.getCheckedRadioButtonId())
                {
                    case R.id.rbBandsGood:
                        orderService.diagnostic.bands = "Bueno";
                        break;
                    case R.id.rbBandsReg:
                        orderService.diagnostic.bands = "Regular";
                        break;
                    case R.id.rbBandsBad:
                        orderService.diagnostic.bands = "Malo";
                        break;
                }
                switch (rgBrakesFluid.getCheckedRadioButtonId())
                {
                    case R.id.rbBrakesFluidOk:
                        orderService.diagnostic.brake_fluid = "A nivel";
                        break;
                    case R.id.rbBrakesFluidLack:
                        orderService.diagnostic.brake_fluid = "Falta";
                        break;
                }
                switch (rgOil.getCheckedRadioButtonId())
                {
                    case R.id.rbOilOk:
                        orderService.diagnostic.oil = "A nivel";
                        break;
                    case R.id.rbOilLack:
                        orderService.diagnostic.oil = "Falta";
                        break;
                }
                switch (rgWipers.getCheckedRadioButtonId())
                {
                    case R.id.rbWipersOk:
                        orderService.diagnostic.wiper_fluid = "A nivel";
                        break;
                    case R.id.rbWipersLack:
                        orderService.diagnostic.wiper_fluid = "Falta";
                        break;
                }
                switch (rgAntifreeze.getCheckedRadioButtonId())
                {
                    case R.id.rbAntifreezeOk:
                        orderService.diagnostic.antifreeze = "A nivel";
                        break;
                    case R.id.rbAntifreezeLack:
                        orderService.diagnostic.antifreeze = "Falta";
                        break;
                }
                switch (rgDirection.getCheckedRadioButtonId())
                {
                    case R.id.rbDirectionOk:
                        orderService.diagnostic.power_steering_fluid = "A nivel";
                        break;
                    case R.id.rbDirectionLack:
                        orderService.diagnostic.power_steering_fluid = "Falta";
                        break;
                }
                Intent intent = new Intent(getBaseContext(), OrderService4Activity.class);
                intent.putExtra("orderService", orderService);
                intent.putExtra("car", car);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_service3, menu);
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
