package Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grupohqh.carservices.operator.ConfigurationScheduledServicesActivity;
import com.grupohqh.carservices.operator.HttpAux;
import com.grupohqh.carservices.operator.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import Models.ScheduledService;

/**
 * Created by Cesar on 11/09/15.
 */
public class ScheduledServicesAdapter extends BaseAdapter {
    String url;
    List<ScheduledService> scheduledServices;
    Context context;

    public ScheduledServicesAdapter(List<ScheduledService> scheduledServices, Context context, String url)
    {
        this.scheduledServices = scheduledServices;
        this.context = context;
        this.url = url;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return scheduledServices.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return scheduledServices.get(position);
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
            view = inflater.inflate(R.layout.sheduled_item, father, false);
        }

        TextView txtKm = (TextView)view.findViewById(R.id.txtKm);
        TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescription);
        ImageView btnRemove = (ImageView)view.findViewById(R.id.btnRemove);

        final ScheduledService scheduledService = scheduledServices.get(position);
        final int pos = position;

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RemoveSheduledServiceAsyncTask().execute(url);
            }

            class RemoveSheduledServiceAsyncTask extends AsyncTask<String, String, String> {
                @Override
                protected String doInBackground(String... urls) {
                    try {
                        return HttpAux.httpPostRequest(urls[0], new JSONObject().accumulate("scheduled_service_id", scheduledService.id));
                    } catch (Exception e) {
                        return e.getMessage();
                    }

                }

                @Override
                protected void onPostExecute(String result)
                {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getBoolean("success")) {
                            scheduledServices.remove(pos);
                            notifyDataSetChanged();
                            ConfigurationScheduledServicesActivity.setListViewHeightBasedOnChildren();
                        } else {
                            Toast.makeText(context, jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(context, result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }


        });

        DecimalFormat formatter = new DecimalFormat("#,###");
        txtKm.setText(formatter.format(scheduledService.km) + " KM");
        txtDate.setText(scheduledService.date);
        txtDescription.setText(scheduledService.description);
        return view;
    }


}
