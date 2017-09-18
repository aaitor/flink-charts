package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.ChartsResult;
import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.operators.SortPartitionOperator;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.util.Collector;

import java.io.Serializable;
import java.util.List;

/**
 * StateChartsPipeline specialization. Extends Charts pipeline
 * and implements a custom cleansing and transformation logic.
 */
public class StateChartsPipeline extends ChartsPipeline implements Serializable {

    public StateChartsPipeline(PipelineConf conf) {
        super(conf);
        env.registerType(ChartsResult.class);
    }

    /**
     * Filtering invalid input data.
     * Removing documents without trackId and with a country distinct of the selected
     * in the pipeline configuration.
     * The cleansing logic can be encapsulated here
     * @param input
     * @return
     */
    public DataSet<Tuple4<Long, Integer, String, TagEvent>> cleansing(DataSet<TagEvent> input) {
        log.info("Cleansing Phase. Removing invalid TagEvent's");
        String country= pipelineConf.getConfig().getString("ingestion.stateChart.country");
        return input
                .filter( t ->
                        t.geoRegionCountry.equals(country)
                                && t.trackId > 0
                )
                .map( t -> new Tuple4<>(t.trackId, 1, t.geoZone, t))
                .returns(new TypeHint<Tuple4<Long, Integer, String, TagEvent>>(){});
    }

    /**
     * Data transformation.
     * The method group by trackId, sum the number of occurrences, sort the output
     * and get the top elements defined by the user.
     * @param input
     * @return
     */
    public DataSet<ChartsResult> transformation(DataSet<?> input) {
        final int limit= pipelineConf.getArgs().getLimit();

        log.info("Transformation Phase. Computing the tags");
        SortPartitionOperator<Tuple4<Long, Integer, String, TagEvent>> grouped = (SortPartitionOperator<Tuple4<Long, Integer, String, TagEvent>>) input
                .groupBy(2, 0) // Grouping by state & trackId
                .sum(1) // Sum the occurrences of each grouped item
                .sortPartition(2, Order.ASCENDING).setParallelism(1) // Sort by state
                .sortPartition(1, Order.DESCENDING).setParallelism(1);// Sort by count
                return grouped.reduceGroup(new ReduceLimit(limit, 2)); // Reducing groups applying the limit specified by user
    }

    public static void run(PipelineConf conf) throws Exception {
        StateChartsPipeline pipeline= new StateChartsPipeline(conf);

        DataSet<TagEvent> inputTags= pipeline.ingestion();
        DataSet<Tuple4<Long, Integer, String, TagEvent>> cleanTags = pipeline.cleansing(inputTags);
        DataSet<ChartsResult> topTags= pipeline.transformation(cleanTags);

        System.out.println("STATE, CHART POSITION , TRACK TITLE , ARTIST NAME , COUNT");


        List<ChartsResult> listTags= topTags.collect();
        String previousGroup= "";
        int counter= 1;
        for (ChartsResult out: listTags) {
            if (!out.getTagEvent().geoZone.equals(previousGroup)) {
                counter = 1;
                previousGroup= out.getTagEvent().geoZone;
                System.out.println("----- TOP Tracks: " + previousGroup);
            }
            System.out.println(out.getTagEvent().geoZone + ", #" + counter + ", " + out.toString());
            counter++;
        }

    }


    /**
     * This class reduces the input group given and generates a DataSet of ChartsResult
     * including only the number of items per group (limit) specified by the user
     */
    public class ReduceLimit implements
            GroupReduceFunction<Tuple4<Long, Integer, String, TagEvent>, ChartsResult> {
        int limit;
        int groupPosition;
        ReduceLimit(int limit, int groupPositionField)  {
            this.limit= limit;
            this.groupPosition= groupPositionField;
        }
        @Override
        public void reduce(Iterable<Tuple4<Long, Integer, String, TagEvent>> values, Collector<ChartsResult> out) throws Exception {
            int counter = 0;
            String group= "";
            log.info("Reducing Groups and applyting limit(" + limit + ") by field " + groupPosition);

            for (Tuple4<Long, Integer, String, TagEvent> t : values) {
                if (!t.getField(groupPosition).equals(group))    {
                    counter= 0;
                    group= t.f2;
                }
                if (counter < limit) {
                    out.collect(new ChartsResult(t.f0, t.f1, t.f3));
                }
                counter++;
            }
        }
    }

}