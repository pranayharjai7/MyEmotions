package com.pranayharjai7.myemotions.Utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.InputStream;

public class ImageUtils {

    public static Bitmap getImage(Uri selectedImageUri, ContentResolver contentResolver)
    {
        Bitmap bmp=null;
        try {
            InputStream ims = contentResolver.openInputStream(selectedImageUri);
            bmp= BitmapFactory.decodeStream(ims);
            ims.close();
            ims = contentResolver.openInputStream(selectedImageUri);
            ExifInterface exif = new ExifInterface(ims);//selectedImageUri.getPath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            int degreesForRotation=0;
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degreesForRotation=90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degreesForRotation=270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degreesForRotation=180;
                    break;
            }
            if(degreesForRotation!=0) {
                Matrix matrix = new Matrix();
                matrix.setRotate(degreesForRotation);
                bmp=Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        bmp.getHeight(), matrix, true);
            }

        } catch (Exception e) {
            Log.e("abc", "Exception thrown: " + e+" "+Log.getStackTraceString(e));
        }
        return bmp;
    }
}
