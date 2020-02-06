# kafka-streams-matching
## Description
Basic demo to show how a basic trade matching algorithm could be implemented using kafka streams.
<br>
Breakdown of logic:
<ul>
<li>aggregation of trades on same key (counterparty & security)
<li>compare quantity and value (could be easily expanded to tolerance)
</ul>

## Steps to run the demo

<ol>
<li>Start local zookeeper & kafka cluster on standard port (2181, 9092)
<li>Create input data Kafka topics
    <ol>
    <li> source1: 
    <pre>
    $ bin/kafka-topics.sh --create --topic source1 --replication-factor 2 --partitions 2 --zookeeper localhost:2181
    Created topic source1.
    </pre>
    <li> source2
    <pre>
    $ bin/kafka-topics.sh --create --topic source2 --replication-factor 2 --partitions 2 --zookeeper localhost:2181
    Created topic source1.
    </pre>
    </ol>
<li>Publish demo data to input Kafka topics
    <ol>
    <li> source1
    <pre>
    $ bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093 --topic source1 --property parse.key=true --property key.separator=!
    >EXT55551!{"tradeId" : "EXT55551", "name": "CPTY1", "securityId": "SECURITY1", "quantity" : 1500, "value" : 3000.0}
    >EXT55552!{"tradeId" : "EXT55552", "name": "CPTY1", "securityId": "SECURITY1", "quantity" : 2500, "value" : 4000.0}
    >EXT55553!{"tradeId" : "EXT55553", "name": "CPTY1", "securityId": "SECURITY2", "quantity" : 3000, "value" : 6000.0}
    </pre>
    <li> source2
    <pre>
    $ bin/kafka-console-producer.sh --broker-list localhost:9092,localhost:9093 --topic source2 --property parse.key=true --property key.separator=!
    >MY99991!{"bookingId" : "MY99991","cptyId": "EXT1","securityId": "SECURITY1","quantity" : 4000, "value" : 7000.0}
    >MY99992!{"bookingId" : "MY99992","cptyId": "EXT1","securityId": "SECURITY2","quantity" : 3000, "value" : 6000.0}
    </pre>
    </ol>    
<li>Run matching application main class KafkaStreamMain, output ->
    <pre>
    Topologies:
       Sub-topology: 0
        Source: KSTREAM-SOURCE-0000000000 (topics: [source1])
          --> KSTREAM-KEY-SELECT-0000000008
        Processor: KSTREAM-KEY-SELECT-0000000008 (stores: [])
          --> KSTREAM-FILTER-0000000012
          <-- KSTREAM-SOURCE-0000000000
        Processor: KSTREAM-FILTER-0000000012 (stores: [])
          --> KSTREAM-SINK-0000000011
          <-- KSTREAM-KEY-SELECT-0000000008
        Sink: KSTREAM-SINK-0000000011 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000009-repartition)
          <-- KSTREAM-FILTER-0000000012
    
      Sub-topology: 1
        Source: KSTREAM-SOURCE-0000000001 (topics: [source2])
          --> KSTREAM-KEY-SELECT-0000000002
        Processor: KSTREAM-KEY-SELECT-0000000002 (stores: [])
          --> KSTREAM-FILTER-0000000006
          <-- KSTREAM-SOURCE-0000000001
        Processor: KSTREAM-FILTER-0000000006 (stores: [])
          --> KSTREAM-SINK-0000000005
          <-- KSTREAM-KEY-SELECT-0000000002
        Sink: KSTREAM-SINK-0000000005 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000003-repartition)
          <-- KSTREAM-FILTER-0000000006
    
      Sub-topology: 2
        Source: KSTREAM-SOURCE-0000000007 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000003-repartition])
          --> KSTREAM-AGGREGATE-0000000004
        Source: KSTREAM-SOURCE-0000000013 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000009-repartition])
          --> KSTREAM-AGGREGATE-0000000010
        Processor: KSTREAM-AGGREGATE-0000000004 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000003])
          --> KTABLE-JOINOTHER-0000000017
          <-- KSTREAM-SOURCE-0000000007
        Processor: KSTREAM-AGGREGATE-0000000010 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000009])
          --> KTABLE-JOINTHIS-0000000016
          <-- KSTREAM-SOURCE-0000000013
        Processor: KTABLE-JOINOTHER-0000000017 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000009])
          --> KTABLE-MERGE-0000000015
          <-- KSTREAM-AGGREGATE-0000000004
        Processor: KTABLE-JOINTHIS-0000000016 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000003])
          --> KTABLE-MERGE-0000000015
          <-- KSTREAM-AGGREGATE-0000000010
        Processor: KTABLE-MERGE-0000000015 (stores: [KTABLE-MERGE-STATE-STORE-0000000014])
          --> KTABLE-TOSTREAM-0000000018
          <-- KTABLE-JOINTHIS-0000000016, KTABLE-JOINOTHER-0000000017
        Processor: KTABLE-TOSTREAM-0000000018 (stores: [])
          --> KSTREAM-KEY-SELECT-0000000019
          <-- KTABLE-MERGE-0000000015
        Processor: KSTREAM-KEY-SELECT-0000000019 (stores: [])
          --> KSTREAM-PRINTER-0000000020
          <-- KTABLE-TOSTREAM-0000000018
        Processor: KSTREAM-PRINTER-0000000020 (stores: [])
          --> none
          <-- KSTREAM-KEY-SELECT-0000000019
    
    
    Aggregating MyTrade{bookingId='MY99992', cptyId='EXT1', securityId='SECURITY2', quantity=3000, value=6000.0} to []
    Aggregating CounterpartyTrade{tradeId='EXT55553', name='CPTY1', securityId='SECURITY2', quantity=3000, value=6000.0} to []
    Aggregating MyTrade{bookingId='MY99991', cptyId='EXT1', securityId='SECURITY1', quantity=4000, value=7000.0} to []
    Aggregating CounterpartyTrade{tradeId='EXT55551', name='CPTY1', securityId='SECURITY1', quantity=1500, value=3000.0} to []
    Aggregating CounterpartyTrade{tradeId='EXT55552', name='CPTY1', securityId='SECURITY1', quantity=2500, value=4000.0} to [{tradeId=EXT55551, name=CPTY1, securityId=SECURITY1, quantity=1500.0, value=3000.0}]

    [KSTREAM-KEY-SELECT-0000000019]: Aligned Trade Value AlignedTrade{key='null', data1=[CounterpartyTrade{tradeId='EXT55553', name='CPTY1', securityId='SECURITY2', quantity=3000, value=6000.0}], data2=[MyTrade{bookingId='MY99992', cptyId='EXT1', securityId='SECURITY2', quantity=3000, value=6000.0}]}, AlignedTrade{key='null', data1=[CounterpartyTrade{tradeId='EXT55553', name='CPTY1', securityId='SECURITY2', quantity=3000, value=6000.0}], data2=[MyTrade{bookingId='MY99992', cptyId='EXT1', securityId='SECURITY2', quantity=3000, value=6000.0}]}

    [KSTREAM-KEY-SELECT-0000000019]: Aligned Trade Value AlignedTrade{key='null', data1=[CounterpartyTrade{tradeId='EXT55551', name='CPTY1', securityId='SECURITY1', quantity=1500, value=3000.0}, CounterpartyTrade{tradeId='EXT55552', name='CPTY1', securityId='SECURITY1', quantity=2500, value=4000.0}], data2=[MyTrade{bookingId='MY99991', cptyId='EXT1', securityId='SECURITY1', quantity=4000, value=7000.0}]}, AlignedTrade{key='null', data1=[CounterpartyTrade{tradeId='EXT55551', name='CPTY1', securityId='SECURITY1', quantity=1500, value=3000.0}, CounterpartyTrade{tradeId='EXT55552', name='CPTY1', securityId='SECURITY1', quantity=2500, value=4000.0}], data2=[MyTrade{bookingId='MY99991', cptyId='EXT1', securityId='SECURITY1', quantity=4000, value=7000.0}]}

    </pre>
</ol>
