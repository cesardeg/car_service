package com.example.cesar.carservice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;


public class OrderServiceQuotesActivity extends ActionBarActivity {

    private static final String STORE_URL = "http://192.168.15.125/~Cesar/carservice/public/storeserviceorder/";

    OrderService orderService;
    EditText etDescription, etAmount, etPrice;
    Button btnAddQuoteItem, btnFinishOrder;
    TextView txtQuoteTotal;
    static ListView lvOrderServiceQuotes;
    Car car;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_service_quotes);

        orderService = (OrderService)getIntent().getExtras().get("orderService");
        car = (Car)getIntent().getExtras().get("car");
        user = (User)getIntent().getExtras().get("user");
        lvOrderServiceQuotes = (ListView)findViewById(R.id.lvOrderServiceQuotes);
        etDescription = (EditText)findViewById(R.id.etDescription);
        etAmount = (EditText)findViewById(R.id.etAmount);
        etPrice = (EditText)findViewById(R.id.etPrice);
        btnAddQuoteItem = (Button)findViewById(R.id.btnAddQuoteItem);
        btnFinishOrder = (Button)findViewById(R.id.btnFinishOrder);
        txtQuoteTotal = (TextView)findViewById(R.id.txtQuoteTotal);

        final QuoteItemAdapter adapter = new QuoteItemAdapter(orderService, getBaseContext(), txtQuoteTotal);
        lvOrderServiceQuotes.setAdapter(adapter);


        btnAddQuoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    QuoteItem item = new QuoteItem();
                    item.amount = Float.parseFloat(etAmount.getText().toString());
                    item.description = etDescription.getText().toString().trim();
                    item.subtotal = item.amount * Float.parseFloat(etPrice.getText().toString());
                    orderService.addItem(item);
                    txtQuoteTotal.setText("$ " + String.format("%1$,.2f", orderService.total));
                    adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(lvOrderServiceQuotes);
                    etAmount.setText("");
                    etDescription.setText("");
                    etPrice.setText("");
                    etDescription.requestFocus();
                }
            }
        });

        btnFinishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpAsyncTask().execute(STORE_URL);
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private boolean validate() {
        if (etAmount.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir la cantidad", Toast.LENGTH_LONG).show();
            etAmount.requestFocus();
            return false;
        }
        if (!etAmount.getText().toString().trim().matches("[0-9]+(.[0-9]*)?")){
            Toast.makeText(getBaseContext(), "Favor de escribir una cantidad valida", Toast.LENGTH_LONG).show();
            etAmount.requestFocus();
            return false;
        }
        if (etDescription.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir una descripción del producto", Toast.LENGTH_LONG).show();
            etDescription.requestFocus();
            return false;
        }
        if (etPrice.getText().toString().trim().equals("")){
            Toast.makeText(getBaseContext(), "Favor de escribir el precio", Toast.LENGTH_LONG).show();
            etPrice.requestFocus();
            return false;
        }
        if (!etPrice.getText().toString().trim().matches("[0-9]+(.[0-9]{1,2})?")){
            Toast.makeText(getBaseContext(), "Favor de escribir un precio valido", Toast.LENGTH_LONG).show();
            etPrice.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_service_quotes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(orderService);
                return HttpAux.httpPostRequest(urls[0], new JSONObject(jsonString));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
                    Toast.makeText(getBaseContext(), "Orden de servicio levantada con exito!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), ShowCarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return;
                }
                else if (jsonObject.has("msj")) {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msj"), Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), "¡Fallo alta, favor de volver a intentar!", Toast.LENGTH_LONG).show();
        }
    }
}
