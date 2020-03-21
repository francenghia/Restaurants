package com.example.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Utilities {

    public static File reizeImageFileWithGlide(String path) throws ExecutionException, InterruptedException, IOException {
        File imgFile = new File(path);
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        Bitmap resized = Bitmap.createScaledBitmap(myBitmap,
                (int) (myBitmap.getWidth() * 0.8),
                (int)(myBitmap.getHeight()*0.8),
                true);

        File file = new File("prova.png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileOutputStream fos = new FileOutputStream(file);
        resized.compress(Bitmap.CompressFormat.PNG, 10,bos);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();

        return file;
    }

    public static String getDateFromTimestamp(Long timestamp){
        Date d = new Date(timestamp);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int hourValue = c.get(Calendar.HOUR);
        int minValue =c.get(Calendar.MINUTE);
        String hourString = Integer.toString(hourValue), minString = Integer.toString(minValue);

        if(hourValue < 10)
            hourString = "0" + hourValue;
        if(minValue < 10)
            minString = "0" + minValue;

        return hourString + ":" + minString;
    }
}
