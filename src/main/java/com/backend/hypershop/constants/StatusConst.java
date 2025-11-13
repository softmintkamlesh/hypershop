// StatusConst.java
package com.backend.hypershop.constants;

public interface StatusConst {
    // Application Status Codes (Sequential)
    int FAILED = 0;
    int SUCCESS = 1;
    int PENDING = 2;
    int PROCESSING = 3;
    int COMPLETED = 4;
    int CANCELLED = 5;
    int REJECTED = 6;
    int EXPIRED = 7;
    int REFUNDED = 8;
    int ON_HOLD = 9;

    // Order Specific Status (10-19)
    int ORDER_PLACED = 10;
    int ORDER_CONFIRMED = 11;
    int ORDER_PREPARING = 12;
    int ORDER_READY = 13;
    int ORDER_OUT_FOR_DELIVERY = 14;
    int ORDER_DELIVERED = 15;
    int ORDER_CANCELLED = 16;
    int ORDER_RETURNED = 17;

    // Payment Status (20-29)
    int PAYMENT_PENDING = 20;
    int PAYMENT_SUCCESS = 21;
    int PAYMENT_FAILED = 22;
    int PAYMENT_REFUND_INITIATED = 23;
    int PAYMENT_REFUNDED = 24;

    // Delivery Status (30-39)
    int DELIVERY_ASSIGNED = 30;
    int DELIVERY_PICKED_UP = 31;
    int DELIVERY_IN_TRANSIT = 32;
    int DELIVERY_REACHED = 33;
    int DELIVERY_COMPLETED = 34;

    // Rider Status (40-49)
    int RIDER_AVAILABLE = 40;
    int RIDER_BUSY = 41;
    int RIDER_OFFLINE = 42;

    // Product Status (50-59)
    int PRODUCT_IN_STOCK = 50;
    int PRODUCT_OUT_OF_STOCK = 51;
    int PRODUCT_LOW_STOCK = 52;
    int PRODUCT_DISCONTINUED = 53;

    // HTTP Status Codes (for reference only)
    int HTTP_OK = 200;
    int HTTP_CREATED = 201;
    int HTTP_BAD_REQUEST = 400;
    int HTTP_UNAUTHORIZED = 401;
    int HTTP_FORBIDDEN = 403;
    int HTTP_NOT_FOUND = 404;
    int HTTP_CONFLICT = 409;
    int HTTP_INTERNAL_SERVER_ERROR = 500;
}
