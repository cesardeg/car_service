package com.example.cesar.carservice;

import java.io.Serializable;

/**
 * Created by Cesar on 10/08/15.
 */
public class Car implements Serializable{
    public String epc, model, serial_number, color, km;
    public int id, car_line_id, car_owner_id, notification_time;
}
