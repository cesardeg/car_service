package com.grupohqh.carservices.operator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import Models.Car;


public class ShowCarActivity extends ActionBarActivity {

    final int DOES_NOT_APPLY = -1;
    final int NO_AVAILABLE   =  0;
    final int IN_PROCESS     =  1;
    final int WAITING_AGREE  =  2;
    final int AGREED         =  3;
    final int DISAGREED      =  4;


    View serviceButtons;
    ImageView imgCar, imgBlur;
    TextView txtOwnerName, txtTag, txtCarBrand, txtModel, txtYear, txtSerialNumber, txtColor, txtLicensePlate, txtKM;
    Button btnOrderService, btnConfigurationCar, btnInventory, btnDiagnostic, btnQuote, btnCompletion;
    int carId, userId, serviceOrderId;
    private String CAR_URL, COMPLETESEVICEORDER_URL;
    Drawable defaultBg;

    final HashMap<Integer, Integer > colorBg = new HashMap<Integer, Integer>(){{
        put(DOES_NOT_APPLY, 0xFFD6D7D7); put(NO_AVAILABLE, 0xFFD6D7D7); put(IN_PROCESS, 0xFF2196F3); put(WAITING_AGREE, 0xFFFFEB3B); put(AGREED, 0xFF4CAF50); put(DISAGREED, 0xFFF44336);
    }};
    final HashMap<Integer, Integer > colorText = new HashMap<Integer, Integer>(){{
        put(DOES_NOT_APPLY, 0xFF000000); put(NO_AVAILABLE, 0xFF888888); put(IN_PROCESS, 0xFFFFFFFF); put(WAITING_AGREE, 0xFF000000); put(AGREED, 0xFF000000); put(DISAGREED, 0xFFFFFFFF);
    }};
    final HashMap<Integer, Boolean > enable = new HashMap<Integer, Boolean>(){{
        put(DOES_NOT_APPLY, false); put(NO_AVAILABLE, false); put(IN_PROCESS, true); put(WAITING_AGREE, false); put(AGREED, false); put(DISAGREED, false);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car);

        if (getIntent().getExtras().containsKey("carId"))
            carId  = getIntent().getExtras().getInt("carId");
        if (getIntent().getExtras().containsKey("userId"))
            userId = getIntent().getExtras().getInt("userId");
        CAR_URL = getString(R.string.base_url) + getString(R.string.car_url);
        COMPLETESEVICEORDER_URL = getString(R.string.base_url) + "completeserviceorder/";

        serviceButtons = findViewById(R.id.serviceButtons);
        txtOwnerName = (TextView)findViewById(R.id.txtOwnerName);
        txtTag = (TextView)findViewById(R.id.txtTag);
        txtCarBrand = (TextView)findViewById(R.id.txtCarBrand);
        txtModel = (TextView)findViewById(R.id.txtCarModel);
        txtYear = (TextView)findViewById(R.id.txtCarYear);
        txtSerialNumber = (TextView)findViewById(R.id.txtSerialNumber);
        txtLicensePlate = (TextView)findViewById(R.id.txtLicensePlate);
        txtColor = (TextView)findViewById(R.id.txtColor);
        txtKM = (TextView)findViewById(R.id.txtKM);
        imgCar = (ImageView)findViewById(R.id.imgCar);
        imgBlur = (ImageView)findViewById(R.id.imgBlur);
        btnOrderService = (Button)findViewById(R.id.btnOrderService);
        btnConfigurationCar = (Button)findViewById(R.id.btnConfigurationCar);
        btnInventory = (Button)findViewById(R.id.btnInventory);
        btnDiagnostic = (Button)findViewById(R.id.btnDiagnostic);
        btnQuote = (Button)findViewById(R.id.btnQuote);
        btnCompletion = (Button)findViewById(R.id.btnCompletion);

        defaultBg = btnConfigurationCar.getBackground().getConstantState().newDrawable();

        btnOrderService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ServiceOrderActivity.class);
                intent.putExtra("carId", carId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnConfigurationCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ConfigurationKMCaptureActivity.class);
                intent.putExtra("carId", carId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ServiceOrderLevelsActivity.class);
                intent.putExtra("serviceOrderId",serviceOrderId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnDiagnostic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ServiceDiagnosticActivity.class);
                intent.putExtra("serviceOrderId", serviceOrderId);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        btnCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new CompleteServiceAsyncTask().execute(COMPLETESEVICEORDER_URL);
                                dialog.dismiss();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.dismiss();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowCarActivity.this);
                builder.setMessage("¿Finalizar el servicio técnico?").setPositiveButton("Si", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        new LoadCarAsyncTask().execute(CAR_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_car, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_refresh_car:
                new LoadCarAsyncTask().execute(CAR_URL);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoadCarAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + carId + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    Car car = objectMapper.readValue(jsonResult.getJSONObject("car").toString(), Car.class);
                    if (!car.photo.equals("")) {
                        byte[] byte_arr = Base64.decode(car.photo, Base64.DEFAULT);
                        Bitmap bm = BitmapFactory.decodeByteArray(byte_arr, 0, byte_arr.length);
                        imgCar.setImageBitmap(bm);
                        imgBlur.setImageBitmap(blur(ShowCarActivity.this, bm));
                    }
                    txtOwnerName.setText(car.owner);
                    txtTag.setText(car.tag);
                    txtCarBrand.setText(car.brand);
                    txtModel.setText(car.model);
                    txtYear.setText(car.year + "");
                    txtSerialNumber.setText(car.serial_number);
                    txtLicensePlate.setText(car.license_plate);
                    txtColor.setText(car.color);
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    txtKM.setText(formatter.format(car.km) + " KM");

                    if (jsonResult.getBoolean("in_workshop")) {
                        serviceButtons.setVisibility(View.VISIBLE);
                        btnOrderService.setVisibility(View.GONE);
                        serviceOrderId = car.service_order_id;
                        int inventoryStatus = jsonResult.getInt("inventory_status");
                        int diagnosticStatus = jsonResult.getInt("diagnostic_status");
                        int quoteStatus = jsonResult.getInt("quote_status");

                        Drawable bgInventory  = defaultBg.getConstantState().newDrawable();
                        Drawable bgDiagnostic = defaultBg.getConstantState().newDrawable();
                        Drawable bgQuote      = defaultBg.getConstantState().newDrawable();
                        Drawable bgCompletion = defaultBg.getConstantState().newDrawable();

                        bgInventory.setColorFilter(colorBg.get(inventoryStatus), PorterDuff.Mode.MULTIPLY);
                        btnInventory.setBackground(bgInventory);
                        btnInventory.setTextColor(colorText.get(inventoryStatus));
                        btnInventory.setEnabled(enable.get(inventoryStatus));

                        bgDiagnostic.setColorFilter(colorBg.get(diagnosticStatus), PorterDuff.Mode.MULTIPLY);
                        btnDiagnostic.setBackground(bgDiagnostic);
                        btnDiagnostic.setTextColor(colorText.get(diagnosticStatus));
                        btnDiagnostic.setEnabled(enable.get(diagnosticStatus));

                        bgQuote.setColorFilter(colorBg.get(quoteStatus), PorterDuff.Mode.MULTIPLY);
                        btnQuote.setBackground(bgQuote);
                        btnQuote.setTextColor(colorText.get(quoteStatus));
                        btnQuote.setEnabled(false);

                        if (inventoryStatus == AGREED && diagnosticStatus == AGREED && quoteStatus == AGREED) {
                            if (jsonResult.getBoolean("completed")) {
                                bgCompletion.setColorFilter(colorBg.get(AGREED), PorterDuff.Mode.MULTIPLY);
                                btnCompletion.setTextColor(colorText.get(AGREED));
                                btnCompletion.setEnabled(enable.get(AGREED));
                            } else {
                                bgCompletion.setColorFilter(colorBg.get(IN_PROCESS), PorterDuff.Mode.MULTIPLY);
                                btnCompletion.setTextColor(colorText.get(IN_PROCESS));
                                btnCompletion.setEnabled(enable.get(IN_PROCESS));
                            }

                        } else {
                            bgCompletion.setColorFilter(colorBg.get(NO_AVAILABLE), PorterDuff.Mode.MULTIPLY);
                            btnCompletion.setTextColor(colorText.get(NO_AVAILABLE));
                            btnCompletion.setEnabled(enable.get(NO_AVAILABLE));
                        }
                        btnCompletion.setBackground(bgCompletion);
                    } else {
                        serviceButtons.setVisibility(View.GONE);
                        btnOrderService.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getBaseContext(), e.getMessage() + "\n" + result, Toast.LENGTH_LONG).show();
            }

        }
    }

    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 3.5f;

    public static Bitmap blur(Context context, Bitmap image) {
        int width  = Math.round(image.getWidth()  * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    private class CompleteServiceAsyncTask extends AsyncTask<String, String, String>  {
        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceOrderId + "+");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    new LoadCarAsyncTask().execute(CAR_URL);
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                e.getStackTrace();
            }
        }
    }
}
