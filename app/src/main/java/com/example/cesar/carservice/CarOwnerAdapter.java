package com.example.cesar.carservice;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Models.CarOwnerItem;

/**
 * Created by Cesar on 10/08/15.
 */
public class CarOwnerAdapter extends BaseAdapter {

    List<CarOwnerItem> carOwners;
    Context context;

    public CarOwnerAdapter(List<CarOwnerItem> list, Context context)
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

        CarOwnerItem carOwner = carOwners.get(position);

        int imgId = carOwner.type.equals("Person") ?
                R.drawable.ic_person: R.drawable.ic_business ;

        carowner_name.setText(carOwner.name);
        carowner_address.setText(carOwner.address);
        carownerImg.setImageResource(imgId);

        return view;
    }

}
