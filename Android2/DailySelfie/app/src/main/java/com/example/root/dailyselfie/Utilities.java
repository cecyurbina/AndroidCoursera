package com.example.root.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 3/05/15.
 */
public class Utilities {

    public static Bitmap getThumbnailFromFile(String picturePath) {
        int thumbSize = 100;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bm = BitmapFactory.decodeFile(picturePath,options);
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bm,
                thumbSize, thumbSize);
        return ThumbImage;
    }


    public static Boolean saveBitmapToFile(Bitmap aBitmap, String aPictureName){

        Boolean isImageSavedOk = false;

        File folder =  new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DailySelfieThumb");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File sdDir = folder;

        String fileName = sdDir + File.separator + aPictureName;
        File pictureFile = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pictureFile);
            aBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            isImageSavedOk = true;
        } catch(Exception ex){
            ex.printStackTrace();
            isImageSavedOk = false;
        }
        return isImageSavedOk;
    }


}
