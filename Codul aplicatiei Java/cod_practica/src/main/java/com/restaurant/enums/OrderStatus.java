package com.restaurant.enums;

public enum OrderStatus {
    NOUA("Noua"),
    IN_PREPARARE("In Preparare"),
    GATA("Gata"),
    SERVITA("Servita"),
    ANULATA("Anulata");

    private final String value;

    OrderStatus(String value) { this.value = value; }

    public String getValue() { return value; }

    @Override
    public String toString() { return value; }

    public static OrderStatus from(String value) {
        for (OrderStatus s : values())
            if (s.value.equalsIgnoreCase(value)) return s;
        throw new IllegalArgumentException("Status necunoscut: " + value);
    }
}