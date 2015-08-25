package Models;

import java.io.Serializable;

/**
 * Created by Cesar on 10/08/15.
 */
public class Car implements Serializable {
    public String epc, serial_number, color, ownerName, brand, model;
    public int id, car_owner_id, year, notification_time, km, service_status;
}
