package com.example.suhussai.gameshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Dave on 2016-04-02.
 */
public class Photo {
    /**
     * The decoded thumbnail image for the game, not saved via elastic controller (transient).
     */
    private transient Bitmap image = null;

    /**
     * The thumbnail base 64 image string to be decoded to produce the real image to display.
     */
    private String imageBase64 = "";

    public Photo(Bitmap image) {
        if (image != null) {

            resizeImage(image);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            this.image.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] b = byteArrayOutputStream.toByteArray();
            imageBase64 = Base64.encodeToString(b,Base64.DEFAULT);
        }
        // clear out the image if an empty image is "added"
        // in this way, a "save" will delete an image if it was deleted from the view
        // deleting an image will therefore be handled by the view directly.
        else {
            this.image = null;
            imageBase64 = "";
        }
    }

    /**
     * Returns the decoded bitmap image based on the base64 string associated
     * @return the decoded bitmap
     */
    public Bitmap getImage(){
        if (image == null && imageBase64 != "") {
            byte[] decodeString = Base64.decode(imageBase64, Base64.DEFAULT);
            image = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        }
        return image;
    }

    /**
     * Returns the base 64 string representing the photo
     * @return the string representation in base64.
     */
    public String getImageBase64() {
        return imageBase64;
    }

    private void resizeImage( Bitmap image ) {
        // http://developer.android.com/reference/android/graphics/Bitmap.html#getByteCount%28%29

        int max_size = 65536;
        int width = image.getWidth();
        int height = image.getHeight();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;

        while( image.getAllocationByteCount() >= max_size) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] b = byteArrayOutputStream.toByteArray();
            image = BitmapFactory.decodeByteArray(b, 0, b.length, opts);
        }

        this.image = image;
    }
}
