package com.che.demo.streaming.domain;

import java.util.Objects;

public class CounterpartyTrade {
    private String tradeId;
    private String name;
    private String securityId;
    private long quantity;
    private double value;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String toString() {
        return "CounterpartyTrade{" +
                "tradeId='" + tradeId + '\'' +
                ", name='" + name + '\'' +
                ", securityId='" + securityId + '\'' +
                ", quantity=" + quantity +
                ", value=" + value +
                '}';
    }

    public CounterpartyTrade(String tradeId, String name, String securityId, long quantity, double value) {
        this.tradeId = tradeId;
        this.name = name;
        this.securityId = securityId;
        this.quantity = quantity;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CounterpartyTrade that = (CounterpartyTrade) o;
        return quantity == that.quantity &&
                Double.compare(that.value, value) == 0 &&
                Objects.equals(tradeId, that.tradeId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(securityId, that.securityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId, name, securityId, quantity, value);
    }
}
