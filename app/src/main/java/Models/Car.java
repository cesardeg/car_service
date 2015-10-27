package Models;

import java.io.Serializable;

/**
 * Created by Cesar on 10/08/15.
 */
public class Car implements Serializable {
    public String tag, serial_number, color, brand, model, photo, owner, license_plate;
    public int id, year, km, service_order_id;
}
