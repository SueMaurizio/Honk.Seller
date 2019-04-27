package org.honk.seller.model;

public enum AuthenticationType {
    facebook(0),
    google(1);

    private int numericValue;

    AuthenticationType(int numericValue) {
        this.numericValue = numericValue;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
