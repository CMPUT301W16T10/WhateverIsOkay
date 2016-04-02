package com.example.suhussai.gameshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
            this.image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            byte[] b = byteArrayOutputStream.toByteArray();
            imageBase64 = Base64.encodeToString(b, Base64.DEFAULT);
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

        int byteCount = image.getAllocationByteCount();
        int max_size = 65536;
        int width = image.getWidth();
        int height = image.getHeight();

        // since newWidth * newHeight = newByteCount <= max_size
        // originalWdith * originalHeight = byteCount
        // byteCount = max_size * Factor
        // newByteCount = byteCount / Factor
        // newWidth * newHeight = (originalWidth * originalHeight) / Factor
        // newWidth * newHeight = originalWidth / sqrt(Factor) * originalHeight / sqrt(Factor) to maintain the scale
        if(byteCount >= max_size) {
            double factor = byteCount/max_size;
            double factor_root = Math.sqrt(factor);
            width = (int) Math.floor(image.getWidth() / factor_root);
            height = (int) Math.floor(image.getHeight() / factor_root);
        }
        this.image = Bitmap.createScaledBitmap(image,width,height,true);
    }
}
