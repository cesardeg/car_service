package com.example.cesar.carservice;


import android.content.Context;
import android.support.v7.internal.widget.TintImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cesar on 10/08/15.
 */
public class CarOwnerAdapter extends BaseAdapter {

    List<CarOwner> carOwners;
    Context context;

    public CarOwnerAdapter(List<CarOwner> list, Context context)
    {
        carOwners = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return carOwners.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return carOwners.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup father) {
        // TODO Auto-generated method stub
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_carowner, father, false);
        }

        TextView carowner_name = (TextView)view.findViewById(R.id.txtCarOwnerNameItem);
        TextView carowner_address = (TextView)view.findViewById(R.id.txtCarOwnerAddressItem);
        ImageView carownerImg = (ImageView) view.findViewById(R.id.imgCarOwnerItem);

        CarOwner carOwner = carOwners.get(position);

        int imgId = carOwner.getType().equals("Person") ?
                R.drawable.ic_person: R.drawable.ic_business ;
        String name = carOwner.getType().equals("Person") ?
                carOwner.getFirst_name() + " " + carOwner.getLast_name(): carOwner.getBusiness_name();

        String address = carOwner.getNeighborhood() + ", " + carOwner.getTown();

        carowner_name.setText(name);
        carowner_address.setText(address);
        carownerImg.setImageResource(imgId);

        return view;
    }

}
