package com.che.demo.streaming.kafkastream;

import com.che.demo.streaming.domain.AlignedTrade;
import com.che.demo.streaming.domain.CounterpartyTrade;
import com.che.demo.streaming.domain.MyTrade;
import com.che.demo.streaming.function.SerdeUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KafkaStreamMain {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "match-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, CounterpartyTrade> source1Trades = builder.stream("source1",
                                                    Consumed.with(Serdes.String(), SerdeUtil.counterpartyTradeSerde(),
                                                            null, Topology.AutoOffsetReset.EARLIEST));

        KStream<String, MyTrade> source2Trades = builder.stream("source2",
                Consumed.with(Serdes.String(), SerdeUtil.myTradeSerde(),
                        null, Topology.AutoOffsetReset.EARLIEST));

        KTable<String, ArrayList<MyTrade>> source2Agg = source2Trades.groupBy((k, v) -> "CPTY1" + v.getSecurityId())
                .aggregate(() -> new ArrayList<MyTrade>(),
                        (k, v, a) -> {
                            System.out.println("Aggregating " + v + " to " + a);
                            a.add(v);
                            return a;
                        }, Materialized.with(Serdes.String(), SerdeUtil.listSerde()));

        KTable<String, AlignedTrade<CounterpartyTrade, MyTrade>> alignedTrades = source1Trades.groupBy((k, v) -> v.getName() + v.getSecurityId())
                .aggregate(() -> new ArrayList<CounterpartyTrade>(),
                        (k, v, a) -> {
                            System.out.println("Aggregating " + v + " to " + a);
                            a.add(v);
                            return a;
                        },Materialized.with(Serdes.String(), SerdeUtil.listSerde()))
                .outerJoin(source2Agg,
                        (t1, t2) -> new AlignedTrade<>(null, t1, t2),
                        Materialized.with(Serdes.String(), SerdeUtil.alignedSerde()));

        alignedTrades.toStream((k, v) -> "Aligned Trade Value " + v).print(Printed.toSysOut());

        Topology topology = builder.build();
        System.out.println(topology.describe());
        final KafkaStreams streams = new KafkaStreams(topology, config);
        final CountDownLatch latch = new CountDownLatch(1);
        try {
            streams.start();
            latch.await();
            System.exit(0);
        }catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
        }));
    }
}
