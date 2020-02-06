package com.che.demo.streaming.function;

import com.che.demo.streaming.domain.AlignedTrade;
import com.che.demo.streaming.domain.CounterpartyTrade;
import com.che.demo.streaming.domain.MyTrade;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class SerdeUtil {
    private static final Gson gson = new GsonBuilder().create();

    private SerdeUtil() {}

    public static Serde<CounterpartyTrade> counterpartyTradeSerde() {
        return createTypeSerde(CounterpartyTrade.class);
    }

    public static<T> Serde<ArrayList<T>> listSerde() {
        return createTypeSerde(new TypeToken<ArrayList<T>>(){}.getType());
    }

    public static Serde<MyTrade> myTradeSerde() {
        return createTypeSerde(MyTrade.class);
    }

    private static <T> Serdes.WrapperSerde<T> createTypeSerde (final Type type) {
        return new Serdes.WrapperSerde<T>(
                new Serializer<T>() {
                    @Override
                    public void configure(Map<String, ?> configs, boolean isKey) {

                    }

                    @Override
                    public byte[] serialize(String topic, T data) {
                        return gson.toJson(data).getBytes();
                    }

                    @Override
                    public void close() {

                    }
                },
                new Deserializer<T>() {
                    @Override
                    public void configure(Map<String, ?> configs, boolean isKey) {

                    }

                    @Override
                    public T deserialize(String topic, byte[] data) {
                        return gson.fromJson(new String(data), type);
                    }

                    @Override
                    public void close() {

                    }
                }
        );
    }

    public static Serde<AlignedTrade<CounterpartyTrade, MyTrade>> alignedSerde() {
        return createTypeSerde(new TypeToken<AlignedTrade<CounterpartyTrade, MyTrade>>(){}.getType());
    }
}
