package com.example.restaurants.OrderActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.common.OrderItem;
import com.example.common.OrderShipperItem;
import com.example.common.Restaurateur;
import com.example.restaurants.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.common.Shared.ACCEPTED_ORDER_PATH;
import static com.example.common.Shared.CUSTOMER_PATH;
import static com.example.common.Shared.RESERVATION_PATH;
import static com.example.common.Shared.RESTAURATEUR_INFO;
import static com.example.common.Shared.ROOT_UID;
import static com.example.common.Shared.SHIPPERS_ORDER;
import static com.example.common.Shared.SHIPPERS_PATH;
import static com.example.common.Shared.SHIPPER_INFO;
import static com.example.common.Shared.STATUS_DELIVERING;
import static com.example.common.Utilities.updateInfoDish;

public class SelectionShipperCommon {

    public static void selectShipper(String shipperId, String orderId, String customerId, Context context ,String TAG){
        AlertDialog reservationDialog = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.reservation_dialog, null);

        view.findViewById(R.id.button_confirm).setOnClickListener(e -> reservationConfirm(shipperId,orderId,customerId,reservationDialog,context,TAG));

        view.findViewById(R.id.button_cancel).setOnClickListener(e -> reservationDialog.dismiss());

        reservationDialog.setView(view);
        reservationDialog.setTitle("Are you sure to select this shipper?\n");

        reservationDialog.show();
    }

    public static void reservationConfirm(String shipperId, String orderId, String customerId, AlertDialog reservationDialog, Context context, String TAG) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query queryDel = database.getReference().child(RESTAURATEUR_INFO + "/" + ROOT_UID
                + "/" + RESERVATION_PATH).child(orderId);

        queryDel.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    DatabaseReference acceptOrder = database.getReference(RESTAURATEUR_INFO + "/" + ROOT_UID
                            + "/" + ACCEPTED_ORDER_PATH);

                    Map<String, Object> orderMap = new HashMap<>();
                    OrderItem orderItem = dataSnapshot.getValue(OrderItem.class);

                    updateInfoDish(orderItem.getDishes());

                    //removing order from RESERVATION_PATH and storing it into ACCEPTED_ORDER_PATH
                    orderMap.put(Objects.requireNonNull(acceptOrder.push().getKey()), orderItem);
                    dataSnapshot.getRef().removeValue();
                    acceptOrder.updateChildren(orderMap);

                    // choosing the selected shipper (shipperId)
                    Query queryShipper = database.getReference(SHIPPERS_PATH);
                    queryShipper.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                String keyShipper = "", name = "";

                                for(DataSnapshot d : dataSnapshot.getChildren()){
                                    if(d.getKey().equals(shipperId)){
                                        keyShipper = d.getKey();
                                        name = d.child(SHIPPER_INFO).child("name").getValue(String.class);
                                        break;
                                    }
                                }
                                //getting address of restaurant to fill OrderShipperItem class
                                DatabaseReference getAddrRestaurant = database.getReference(RESTAURATEUR_INFO + "/" + ROOT_UID
                                        + "/info");

                                String finalKeyShipper = keyShipper;
                                String finalName = name;

                                getAddrRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Restaurateur restaurateur = dataSnapshot.getValue(Restaurateur.class);
                                            orderMap.clear();
                                            orderMap.put(orderId,new OrderShipperItem(ROOT_UID,
                                                    customerId,
                                                    orderItem.getAddrCustomer(),
                                                    restaurateur.getAddr(),
                                                    orderItem.getTime(),
                                                    orderItem.getTotPrice()));

                                            DatabaseReference addOrderToShipper = database.getReference(SHIPPERS_PATH + "/" + finalKeyShipper + SHIPPERS_ORDER);
                                            addOrderToShipper.updateChildren(orderMap);

                                            //setting to 'false' boolean variable of shipper
                                            DatabaseReference setFalse = database.getReference(SHIPPERS_PATH + "/" + finalKeyShipper + "/available");
                                            setFalse.setValue(false);

                                            //setting status delivering of the order to customer
                                            DatabaseReference refCustomerOrder = FirebaseDatabase.getInstance()
                                                    .getReference().child(CUSTOMER_PATH + "/" + customerId).child("orders").child(orderId);
                                            HashMap<String, Object> order = new HashMap<>();
                                            order.put("status", STATUS_DELIVERING);

                                            // TODO: 4/16/2020
                                            order.put("shipper", finalKeyShipper);

                                            refCustomerOrder.updateChildren(order);

                                            reservationDialog.dismiss();
                                            Toast.makeText(context, "Order assigned to shipper " + finalName, Toast.LENGTH_LONG).show();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.w(TAG, "Failed to read value.", databaseError.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
