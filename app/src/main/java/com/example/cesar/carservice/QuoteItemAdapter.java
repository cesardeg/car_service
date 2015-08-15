package com.example.cesar.carservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

/**
 * Created by Cesar on 12/08/15.
 */
public class QuoteItemAdapter extends BaseAdapter {
    OrderService orderService;
    Context context;
    TextView txtTotal;

    public QuoteItemAdapter(OrderService order, Context context, TextView txtTotal)
    {
        orderService = order;
        this.context = context;
        this.txtTotal = txtTotal;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return orderService.quoteItems.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return orderService.quoteItems.get(position);
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
            view = inflater.inflate(R.layout.product_item, father, false);
        }

        TextView txtAmount = (TextView)view.findViewById(R.id.txtAmountItem);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescriptionItem);
        TextView txtSTotal = (TextView) view.findViewById(R.id.txtSubtotalItem);
        ImageButton btnRemove = (ImageButton)view.findViewById(R.id.btnRemoveItem);


        QuoteItem quoteItem = orderService.quoteItems.get(position);
        final int pos = position;
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderService.removeItem(pos);
                txtTotal.setText("$ " + String.format("%1$,.2f", orderService.total));
                notifyDataSetChanged();
                OrderServiceQuotesActivity.setListViewHeightBasedOnChildren(OrderServiceQuotesActivity.lvOrderServiceQuotes);
            }
        });
        txtAmount.setText(quoteItem.amount + "");
        txtDescription.setText(quoteItem.description);
        txtSTotal.setText("$ " + String.format(Locale.US, "%1$,.2f", quoteItem.subtotal));
        return view;
    }


}
