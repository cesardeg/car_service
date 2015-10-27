package Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Cesar on 23/09/15.
 */
public class ServicePhoto {
    public int id;
    private Bitmap photo;

    public void setPhoto(String photo) {
        byte[] byte_arr = Base64.decode(photo, Base64.DEFAULT);
        this.photo = BitmapFactory.decodeByteArray(byte_arr, 0, byte_arr.length);
    }

    public void setBitmapPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getStringPhoto() {
        if (photo != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byte_arr = stream.toByteArray();
            return Base64.encodeToString(byte_arr, Base64.DEFAULT);
        }
        return "";
    }

    public Bitmap getPhoto() {
        return photo;
    }
}
