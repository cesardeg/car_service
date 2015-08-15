package com.example.cesar.carservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Cesar on 12/08/15.
 */
public class OrderService implements Serializable {
    int id;
    public int car_id, workshop_id, receiver_user, deliver_user;
    public String service_name, pick_up_address, delivery_address;
    private Date date, owneer_agree;
    public boolean owner_supplied_parts, owner_allow_used_parts;
    public float total;
    public List<QuoteItem> quoteItems;
    public OrderServiceDiagnostic diagnostic;

    public OrderService() {
        diagnostic = new OrderServiceDiagnostic();
        quoteItems = new ArrayList<QuoteItem>();
        total = 0;
    }

    public void addItem(QuoteItem item) {
        quoteItems.add(item);
        total += item.subtotal;
    }

    public  void  removeItem(int position){
        if (quoteItems.size() <= position)
            return;
        total -= quoteItems.get(position).subtotal;
        quoteItems.remove(position);
    }

    public void setId(int id){
        this.id = id;
    }

    public void setOwneer_agree(Date owneer_agree){
        this.owneer_agree = owneer_agree;

    }

}
