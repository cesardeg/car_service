package com.grupohqh.carservices.operator;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Adapters.CarOwnerAdapter;
import Models.CarOwnerItem;


public class ListCarOwnersActivity extends ActionBarActivity {

    String URL;
    ListView lv;
    List<CarOwnerItem> carOwners;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_car_owners);

        URL = getString(R.string.base_url) + "listcarowners/";
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        lv = (ListView)findViewById(R.id.lvCarOwners);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ShowCarOwnerActivity.class);
                intent.putExtra("carOwnerId", carOwners.get(position).id);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        carOwners = new ArrayList<CarOwnerItem>();
        new HttpAsyncTask().execute(URL + "/" + userId + "/");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_car_owners, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_add_carowner:
                Intent intent = new Intent(getBaseContext(), ManipulateCarOwnerActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                break;
            case R.id.action_search_carowner:
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return HttpAux.httpGetRequest(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jsonArray = new JSONArray(result);
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CarOwnerItem carOwner = objectMapper.readValue(jsonObject.toString(), CarOwnerItem.class);
                    carOwners.add(carOwner);
                }
                CarOwnerAdapter adapter = new CarOwnerAdapter(carOwners, getBaseContext());
                lv.setAdapter(adapter);
            }catch (Exception e){
                e.getStackTrace();
            }

        }
    }
}
