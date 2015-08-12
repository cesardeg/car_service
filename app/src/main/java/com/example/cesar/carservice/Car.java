package com.example.cesar.carservice;

import java.io.Serializable;

/**
 * Created by Cesar on 10/08/15.
 */
public class Car implements Serializable {
    public String epc, serial_number, color, ownerName, brandName, lineName;
    public int id, car_line_id, car_owner_id, model, notification_time, km;
}
