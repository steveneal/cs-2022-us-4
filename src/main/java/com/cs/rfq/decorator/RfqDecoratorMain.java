package com.cs.rfq.decorator;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class RfqDecoratorMain {

    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir", "C:\\Java\\hadoop-2.9.2");
        System.setProperty("spark.master", "local[4]");

        //TODO: create a Spark configuration and set a sensible app name
        SparkConf conf = new SparkConf().setAppName("RfqDecorator");

        //TODO: create a Spark streaming context
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, Durations.seconds(1));

        //TODO: create a Spark session
        SparkSession session = SparkSession.builder().config(conf).getOrCreate();

        //TODO: create a new RfqProcessor and set it listening for incoming RFQs
        KafkaRfqProcessor processor = new KafkaRfqProcessor(session, streamingContext);

        JavaDStream<String> stream = processor.initRfqStream();

        //TODO: convert each incoming line to a Rfq object and call processRfq method with it
        stream.foreachRDD(rdd -> {
            rdd.collect().stream().map(Rfq::fromJson).forEach(System.out::println);
        });

        //TODO: start the streaming context
        System.out.println("Listening for RFQs");
        streamingContext.start();
        streamingContext.awaitTermination();
    }

}
