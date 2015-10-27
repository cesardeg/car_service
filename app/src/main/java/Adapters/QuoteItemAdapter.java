package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupohqh.carservices.operator.R;
import com.grupohqh.carservices.operator.ServiceDiagnosticActivity;

import java.util.List;

import Models.QuoteItem;
import Models.ServiceDiagnostic;

/**
 * Created by Cesar on 12/08/15.
 */
public class QuoteItemAdapter extends BaseAdapter {
    List<QuoteItem> required_material;
    Context context;

    public QuoteItemAdapter(List<QuoteItem> required_material, Context context)
    {
        this.required_material = required_material;
        this.context = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.required_material.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return this.required_material.get(position);
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
        ImageView btnRemove = (ImageView)view.findViewById(R.id.btnRemoveItem);


        QuoteItem quoteItem = this.required_material.get(position);
        final int pos = position;
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                required_material.remove(pos);
                notifyDataSetChanged();
                ServiceDiagnosticActivity.setListViewHeightBasedOnChildren();
            }
        });
        txtAmount.setText(quoteItem.amount + "");
        txtDescription.setText(quoteItem.description);
        return view;
    }
}
