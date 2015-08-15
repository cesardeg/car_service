package com.example.cesar.carservice;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {

    TextView txtName;
    Button btnCarOwners;
    Button btnScan;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        user = (User)getIntent().getExtras().get("user");
        btnCarOwners = (Button)findViewById(R.id.btnCarOwners);
        btnScan = (Button)findViewById(R.id.btnReadTAG);
        txtName = (TextView)findViewById(R.id.txtName);

        txtName.setText(user.first_name + " " + user.last_name);

        btnCarOwners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ListCarOwnersActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        btnScan = (Button) findViewById(R.id.btnReadTAG);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ReadTagActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
