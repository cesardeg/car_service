package com.example.cesar.carservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import Models.QuoteItem;
import Models.ServiceDiagnostic;

/**
 * Created by Cesar on 12/08/15.
 */
public class QuoteItemAdapter extends BaseAdapter {
    ServiceDiagnostic serviceDiagnostic;
    Context context;

    public QuoteItemAdapter(ServiceDiagnostic serviceDiagnostic, Context context)
    {
        this.serviceDiagnostic = serviceDiagnostic;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return serviceDiagnostic.quote.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return serviceDiagnostic.quote.get(position);
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
            view = inflater.inflate(R.layout.quote_item, father, false);
        }

        TextView txtAmount = (TextView)view.findViewById(R.id.txtAmountItem);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescriptionItem);
        ImageButton btnRemove = (ImageButton)view.findViewById(R.id.btnRemoveItem);


        QuoteItem quoteItem = serviceDiagnostic.quote.get(position);
        final int pos = position;
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceDiagnostic.quote.remove(pos);
                notifyDataSetChanged();
                ServiceDiagnosticActivity.setListViewHeightBasedOnChildren(ServiceDiagnosticActivity.lvOrderServiceQuotes);
            }
        });
        txtAmount.setText(quoteItem.amount + "");
        txtDescription.setText(quoteItem.description);
        return view;
    }
}
