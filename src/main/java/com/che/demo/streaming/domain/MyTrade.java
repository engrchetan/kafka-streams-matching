package com.che.demo.streaming.domain;

import java.util.Objects;

public class MyTrade {
    private String bookingId;
    private String cptyId;
    private String securityId;
    private long quantity;
    private double value;

    public MyTrade(String bookingId, String cptyId, String securityId, long quantity, double value) {
        this.bookingId = bookingId;
        this.cptyId = cptyId;
        this.securityId = securityId;
        this.quantity = quantity;
        this.value = value;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getCptyId() {
        return cptyId;
    }

    public void setCptyId(String cptyId) {
        this.cptyId = cptyId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyTrade myTrade = (MyTrade) o;
        return quantity == myTrade.quantity &&
                Double.compare(myTrade.value, value) == 0 &&
                Objects.equals(bookingId, myTrade.bookingId) &&
                Objects.equals(cptyId, myTrade.cptyId) &&
                Objects.equals(securityId, myTrade.securityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, cptyId, securityId, quantity, value);
    }

    @Override
    public String toString() {
        return "MyTrade{" +
                "bookingId='" + bookingId + '\'' +
                ", cptyId='" + cptyId + '\'' +
                ", securityId='" + securityId + '\'' +
                ", quantity=" + quantity +
                ", value=" + value +
                '}';
    }
}
