package com.stationmarket.api.vendor.exception;

public class MarketplaceNotFoundException extends RuntimeException {
    public MarketplaceNotFoundException(String message) {
        super(message);
    }
}