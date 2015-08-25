package Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cesar on 20/08/15.
 */
public class ServiceOrder {
    public int user_id, workshop_id,  car_id, car_owner_id, km, fuel_level;
    public String service_name, pick_up_address, delivery_address;
    public String brake_fluid, wiper_fluid, antifreeze, oil, power_steering_fluid;
    public String date, owneer_agree, is_closed;
    public boolean owner_supplied_parts, owner_allow_used_parts;

    public ServiceOrder() {

    }
}
