package com.example.common;

public class Shared {

    /**
     * KEY values
     */
    public static final String Name = "keyName";
    public static final String Password = "keyPassword";
    public static final String Description = "keyDescription";
    public static final String Address = "keyAddress";
    public static final String Mail = "keyMail";
    public static final String Price = "keyEuroPrice";
    public static final String Photo = "keyPhoto";
    public static final String Phone = "keyPhone";
    public static final String Time = "keyTime";
    public static final String Quantity = "keyQuantity";
    public static final String CameraOpen = "keyCameraDialog";
    public static final String PriceOpen = "keyPriceDialog";
    public static final String QuantOpen = "keyQuantityDialog";
    public static final String TimeClose = "keyTimeClose";
    public static final String TimeOpen = "keyTimeOpen";

    /**
     * Permission values
     */
    public static final int PERMISSION_GALLERY_REQUEST = 1;
    public static final int GOOGLE_SIGIN = 101;
    public static final int SIGNUP = 102;

    /**
     * Firebase paths
     */
    public static String ROOT_UID = "";
    public static User user;
    public static final String RESTAURATEUR_INFO = "/restaurants";
    public static final String DISHES_PATH =  "/dishes";
    public static final String RESERVATION_PATH = "/reservation";
    public static final String ACCEPTED_ORDER_PATH = "/order";
    public static final String RESTAURATEUR_REVIEW = "/reviews";
    public static final String SHIPPERS_PATH = "/shippers";
    public static final String RIDERS_ORDER = "/pending";
    public static final String CUSTOMER_PATH = "/customers";
    public static final String CUSTOMER_FAVOURITE_RESTAURANT_PATH = "/favourites";


}
