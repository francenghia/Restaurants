package com.example.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.common.Shared.DISHES_PATH;
import static com.example.common.Shared.RESERVATION_PATH;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;

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

    public static String getStringDateFromTimestamp(Long timestamp){
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

        int am_pm = c.get(Calendar.AM_PM);
        String ampm = "ampm";
        if (am_pm == 0) {
            ampm = "AM";
        } else {
            ampm = "PM";
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day =c.get(Calendar.DAY_OF_MONTH);

        return hourString + ":" + minString+" "+ampm + " "+day+"/"+(month+1)+"/"+year;
    }


    public static Long getDate(int hour, int min, int mode, Long prev) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date date = cal.getTime();

        if (mode==1 && date.getTime()<prev){
            cal.set(Calendar.DATE,cal.get(Calendar.DATE)+1);
            date = cal.getTime();
        }
        return date.getTime();
    }

    public static String formatCurrency(String tempPrice){
        double amount = Double.parseDouble(tempPrice);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static void updateInfoDish(final HashMap<String, Integer> dishes){
        Query getDishes = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO + "/" + ROOT_UID
                + "/" + RESERVATION_PATH);

        getDishes.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        final DishItem dishItem = d.getValue(DishItem.class);

                        if(dishes.containsKey(dishItem.getName())){
                            String keyDish = d.getKey();

                            Query updateDish = FirebaseDatabase.getInstance().getReference(RESTAURATEUR_INFO + "/" + ROOT_UID
                                    + "/" + RESERVATION_PATH).child(keyDish);
                            updateDish.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        DishItem newDishItem = dataSnapshot.getValue(DishItem.class);

                                        newDishItem.setQuantity(newDishItem.getQuantity() - dishes.get(dishItem.getName()));
                                        newDishItem.setFrequency(newDishItem.getFrequency() + dishes.get(dishItem.getName()));

                                        Map<String, Object> dishMap = new HashMap<>();
                                        DatabaseReference dishRef = FirebaseDatabase.getInstance().getReference(
                                                RESTAURATEUR_INFO + "/" + ROOT_UID + "/" + DISHES_PATH);
                                        dishMap.put(dataSnapshot.getKey(), newDishItem);
                                        dishRef.updateChildren(dishMap);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
