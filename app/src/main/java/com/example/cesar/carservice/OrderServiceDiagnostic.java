package com.example.cesar.carservice;

import java.io.Serializable;

/**
 * Created by Cesar on 12/08/15.
 */
public class OrderServiceDiagnostic implements Serializable{
    public int fuel_level, km;
    public String tires, front_shock_absorber, rear_shock_absorver, front_brakes, rear_brakes, suspension, bands;
    public String brake_fluid, wiper_fluid, antifreeze, oil, power_steering_fluid, description, mechanic_in_charge;
}
