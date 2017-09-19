package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.ChartsResult;
import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple3;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SimpleChartsPipeline specialization. Extends Charts pipeline
 * and implements a custom cleansing and transformation logic.
 */
public class SimpleChartsPipeline extends ChartsPipeline implements DataPipeline<TagEvent>, Serializable {

    public SimpleChartsPipeline(PipelineConf conf) {
        super(conf);
        env.registerType(ChartsResult.class);
    }

    /**
     * Filtering invalid input data.
     * Initially removing TagEvents without trackId
     * The cleansing logic can be encapsulated here
     * @param input
     * @return
     */
    @Override
    public DataSet<Tuple3<Long, Integer, TagEvent>> cleansing(DataSet<?> input) {
        log.info("Cleansing Phase. Removing invalid TagEvent's");

        return ((DataSet<TagEvent>) input)
                .filter(t -> t.trackId > 0) // Removing all the events with invalid trackids
                .map( t -> new Tuple3<>(t.trackId, 1, t)) // getting tuples
                .returns(new TypeHint<Tuple3<Long, Integer, TagEvent>>(){});
    }

    /**
     * Data transformation.
     * The method group by trackId, sum the number of occurrences, sort the output
     * and get the top elements defined by the user.
     * @param input
     * @return
     */
    @Override
    public DataSet<ChartsResult> transformation(DataSet<?> input) {
        log.info("Transformation Phase. Computing the tags");
        return input
                .groupBy(0) // Grouping by trackId
                .sum(1) // Sum the occurrences of each grouped item
                .sortPartition(1, Order.DESCENDING).setParallelism(1) // Sort by count
                .first(pipelineConf.args.getLimit())
                .map( t -> {
                        Tuple3<Long, Integer, TagEvent> tuple= (Tuple3<Long, Integer, TagEvent>) t;
                        return new ChartsResult(tuple.f0, tuple.f1, tuple.f2);
                })
                .returns(new TypeHint<ChartsResult>(){});
    }

    @Override
    public boolean persistence(DataSet<?> input) {
        return false;
    }

    /**
     * Pipeline runner. This static method orchestrates the pipeline execution stages.
     * @param conf
     * @throws Exception
     */
    public static void run(PipelineConf conf) throws Exception {
        SimpleChartsPipeline pipeline= new SimpleChartsPipeline(conf);

        DataSet<TagEvent> inputTags= pipeline.ingestion();
        DataSet<Tuple3<Long, Integer, TagEvent>> cleanTags = pipeline.cleansing(inputTags);
        DataSet<ChartsResult> topTags= pipeline.transformation(cleanTags);

        System.out.println("CHART POSITION , TRACK TITLE , ARTIST NAME , COUNT");
        AtomicInteger position= new AtomicInteger(0);
        topTags.collect().forEach( t ->
                System.out.println("#" + position.incrementAndGet() + ", " + t.toString())
        );
    }

}
