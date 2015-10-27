package com.grupohqh.carservices.operator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.ServicePhoto;


public class GalleryActivity extends AppCompatActivity {

    public static final String EXTRA_USERID = "userId", EXTRA_SERVICEORDERID = "serviceOrderId", EXTRA_TYPE = "type";
    private static final int CAPTURE_IMAGE = 100;
    String LOAD_URL, REMOVE_URL, ADD_URL;
    int userId, serviceOrderId; String type;
    ViewPager pager; LinearLayout thumbnails;
    ImageView btnBack, btnAddPhoto, btnDeletePhoto;
    List<ServicePhoto> gallery; ServicePhoto photo;
    GalleryPagerAdapter adapter;
    TextView txtMessage, txtTitle;
    ProgressBar bar;

    private static final Map<String, String> translate = new HashMap<String, String>() {
        { put("outside", "Exterior"); put("inside", "Interior"); put("motor", "Motor"); put("trunk", "Porta-equipaje"); }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (getIntent().getExtras().containsKey(               EXTRA_USERID        ))
            userId         = getIntent().getExtras().getInt(   EXTRA_USERID        );
        if (getIntent().getExtras().containsKey(               EXTRA_SERVICEORDERID))
            serviceOrderId = getIntent().getExtras().getInt(   EXTRA_SERVICEORDERID);
        if (getIntent().getExtras().containsKey(               EXTRA_TYPE          ))
            type           = getIntent().getExtras().getString(EXTRA_TYPE          );

        LOAD_URL   = getString(R.string.base_url) + "listphotos/" ;
        ADD_URL    = getString(R.string.base_url) + "addphoto/"   ;
        REMOVE_URL = getString(R.string.base_url) + "removephoto/";

        thumbnails     = (LinearLayout)findViewById(R.id.thumbnails    );
        pager          = (  ViewPager )findViewById(R.id.pager         );
        txtMessage     = (  TextView  )findViewById(R.id.txtMessage    );
        txtTitle       = (  TextView  )findViewById(R.id.txtTitle      );
        btnBack        = (  ImageView )findViewById(R.id.btnBack       );
        btnAddPhoto    = (  ImageView )findViewById(R.id.btnAddPhoto   );
        btnDeletePhoto = (  ImageView )findViewById(R.id.btnDeletePhoto);
        bar            = ( ProgressBar)findViewById(R.id.progressBar   );

        if (translate.containsKey(type)) txtTitle.setText(translate.get(type));
        txtMessage.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(GalleryActivity.this)));
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });
        btnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gallery.size() == 0){
                    Toast.makeText(GalleryActivity.this, "Nada que eliminar", Toast.LENGTH_SHORT).show();
                    return;
                }
                new RemovePhotosAsyncTask().execute(REMOVE_URL);
            }
        });

        gallery = new ArrayList<>();
        adapter = new GalleryPagerAdapter(getBaseContext(), gallery);
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(6);
        new LoadPhotosAsyncTask().execute(LOAD_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private File getTempFile(Context context){
        //it will return /sdcard/image.tmp
        final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );
        if(!path.exists()){
            path.mkdir();
        }
        return new File(path, "image.tmp");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch(requestCode){
                case CAPTURE_IMAGE:
                    final File file = getTempFile(this);
                    try {
                        Bitmap bp = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                        photo = new ServicePhoto();
                        photo.setBitmapPhoto(bp);
                        new AddPhotosAsyncTask().execute(ADD_URL);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    class GalleryPagerAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;
        List<ServicePhoto> gallery;


        public GalleryPagerAdapter(Context context, List<ServicePhoto> gallery) {
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.gallery = gallery;
        }

        @Override
        public int getCount() {
            return gallery.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = inflater.inflate(R.layout.pager_gallery_item, container, false);
            container.addView(itemView);

            // Get the border size to show around each image
            int borderSize = thumbnails.getPaddingTop();

            // Get the size of the actual thumbnail image
            int thumbnailSize = ((FrameLayout.LayoutParams)
                    pager.getLayoutParams()).bottomMargin - (borderSize*2);

            // Set the thumbnail layout parameters. Adjust as required
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(thumbnailSize, thumbnailSize);
            params.setMargins(0, 0, borderSize, 0);

            // You could also set like so to remove borders
            //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
            //        ViewGroup.LayoutParams.WRAP_CONTENT,
            //        ViewGroup.LayoutParams.WRAP_CONTENT);

            ImageView thumbView = new ImageView(context);
            thumbView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbView.setLayoutParams(params);
            thumbView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("GalleryActivity", "Thumbnail clicked");

                    // Set the pager position when thumbnail clicked
                    pager.setCurrentItem(position);
                }
            });
            thumbnails.addView(thumbView);

            SubsamplingScaleImageView imageView = (SubsamplingScaleImageView)itemView.findViewById(R.id.image);

            imageView.setImage(ImageSource.bitmap(gallery.get(position).getPhoto()));
            thumbView.setImageBitmap(gallery.get(position).getPhoto());

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            int pos = collection.indexOfChild((LinearLayout) view);
            ((ViewPager) collection).removeView((View) view);
            thumbnails.removeViewAt(pos);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }

    private class LoadPhotosAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return HttpAux.httpGetRequest(params[0] + userId + "/" + serviceOrderId + "/" + type + "/");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JSONArray jsonArray = jsonResult.getJSONArray("photos");
                    gallery.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        gallery.add(objectMapper.readValue(jsonArray.get(i).toString(), ServicePhoto.class));
                    }
                    txtMessage.setVisibility(gallery.size() > 0? View.GONE: View.VISIBLE );
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            bar.setVisibility(View.GONE);
        }
    }

    private class AddPhotosAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            txtMessage.setVisibility(View.GONE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("photo", photo.getStringPhoto());
                return HttpAux.httpPostRequest(params[0] + userId + "/" + serviceOrderId + "/" + type + "/", jsonObject);
            } catch (Exception e){
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    photo.id = jsonResult.getInt("photo_id");
                    gallery.add(gallery.size(), photo);
                    adapter.notifyDataSetChanged();
                    pager.setCurrentItem(gallery.size() - 1);
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            txtMessage.setVisibility(gallery.size() > 0? View.GONE: View.VISIBLE );
            bar.setVisibility(View.GONE);
        }
    }

    private class RemovePhotosAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject jsonObject = new JSONObject();
                int photoId = gallery.get(pager.getCurrentItem()).id;
                return HttpAux.httpPostRequest(params[0] + userId + "/" + photoId + "/", jsonObject);
            } catch (Exception e){
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);
                if (jsonResult.getBoolean("success")) {
                    gallery.remove(pager.getCurrentItem());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), jsonResult.getString("msj"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.getStackTrace();
                Toast.makeText(getBaseContext(), result + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            bar.setVisibility(View.GONE);
        }
    }
}
