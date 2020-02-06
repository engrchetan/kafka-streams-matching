package com.che.demo.streaming.domain;

import java.util.List;
import java.util.Objects;

public class AlignedTrade<T1, T2> {
    private String key;
    private List<T1> data1;
    private List<T2> data2;

    public AlignedTrade(String key, List<T1> data1, List<T2> data2) {
        this.key = key;
        this.data1 = data1;
        this.data2 = data2;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<T1> getData1() {
        return data1;
    }

    public void setData1(List<T1> data1) {
        this.data1 = data1;
    }

    public List<T2> getData2() {
        return data2;
    }

    public void setData2(List<T2> data2) {
        this.data2 = data2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlignedTrade<?, ?> that = (AlignedTrade<?, ?>) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(data1, that.data1) &&
                Objects.equals(data2, that.data2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, data1, data2);
    }

    @Override
    public String toString() {
        return "AlignedTrade{" +
                "key='" + key + '\'' +
                ", data1=" + data1 +
                ", data2=" + data2 +
                '}';
    }
}
