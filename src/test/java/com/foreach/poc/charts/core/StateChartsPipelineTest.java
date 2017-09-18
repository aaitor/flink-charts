package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.ChartsResult;
import com.foreach.poc.charts.model.TagEvent;
import com.foreach.poc.charts.model.TagEventUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple4;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StateChartsPipelineTest {

    private static Config config;
    private static ArgsParser argsParser;
    private static TagEventUtils tagUtils;

    @Before
    public void setUp() throws Exception {
        config= ConfigFactory.load();

    }

    /**
     * Test to validate the cleansing method.
     * We generate a DataSet with 10 TagEvents and modify 3 items to force bad data
     * The assertion checks that only are obtained the proper number of items after the
     * cleansing process.
     * @throws Exception
     */
    @Test
    public void cleansingTest() throws Exception {
        String args[]= {"-c", "state_chart", "-l", "3"};
        argsParser= ArgsParser.builder(args);

        PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);
        StateChartsPipeline pipeline= new StateChartsPipeline(pipelineConf);

        List<TagEvent> mockCollection= TagEventUtils.getMockData(10);
        mockCollection.set(0, new TagEvent(0l, "xxx", "yy","ZZ", "Locality", "United States"));
        mockCollection.set(2, new TagEvent(0l, "xxx", "yy","ZZ", "Locality", "UK"));
        mockCollection.set(4, new TagEvent(99l, "xxx", "yy","", "", ""));


        DataSet<TagEvent> mockDataset= pipeline.getEnv().fromCollection(mockCollection);

        DataSet<Tuple4<Long, Integer, String, TagEvent>> clean = pipeline.cleansing(mockDataset);
        assertEquals(7, clean.count());
    }

    /**
     * Validation of transformation logic.
     * A DataSet with 10 TagEvents is created. The mocked dataset should return after to
     * transform a Dataset with 3 items.
     * @throws Exception
     */
    @Test
    public void transformationTest() throws Exception {
        String args[]= {"-c", "state_chart", "-l", "3"};
        argsParser= ArgsParser.builder(args);

        PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);
        StateChartsPipeline pipeline= new StateChartsPipeline(pipelineConf);

        /**
         * S1 - 111 = 2
         * S1 - 222 = 1
         * S1 - 333 = 3
         *
         * S2 - 111 =
         * S2 - 222 = 3
         * S3 - 333 = 1
         */
        List<Tuple4<Long, Integer, String, TagEvent>> mockCollection= new ArrayList<>();
        mockCollection.add(new Tuple4<>(111l, 1, "S1",new TagEvent(111l, "Artist 1", "Title 1", "S1")));
        mockCollection.add(new Tuple4<>(222l, 1, "S2",new TagEvent(222l, "Artist 2", "Title 2", "S2")));
        mockCollection.add(new Tuple4<>(333l, 1, "S1",new TagEvent(333l, "Artist 3", "Title 3", "S1")));
        mockCollection.add(new Tuple4<>(111l, 1, "S1",new TagEvent(111l, "Artist 1", "Title 1", "S1")));
        mockCollection.add(new Tuple4<>(222l, 1, "S1",new TagEvent(222l, "Artist 2", "Title 2", "S1")));
        mockCollection.add(new Tuple4<>(333l, 1, "S1",new TagEvent(333l, "Artist 3", "Title 3", "S1")));
        mockCollection.add(new Tuple4<>(222l, 1, "S2",new TagEvent(222l, "Artist 2", "Title 2", "S2")));
        mockCollection.add(new Tuple4<>(222l, 1, "S2",new TagEvent(222l, "Artist 2", "Title 2", "S2")));
        mockCollection.add(new Tuple4<>(333l, 1, "S1",new TagEvent(333l, "Artist 3", "Title 3", "S1")));
        mockCollection.add(new Tuple4<>(333l, 1, "S2",new TagEvent(333l, "Artist 3", "Title 3", "S2")));

        DataSet<Tuple4<Long, Integer, String, TagEvent>> mockDataset= pipeline.getEnv().fromCollection(mockCollection);
        DataSet<ChartsResult> transformed= pipeline.transformation(mockDataset);

        List<ChartsResult> output= transformed.collect();
        // We expect 5 different items
        assertEquals(5, output.size());

        assertEquals(333l, output.get(0).getTrackId());
        assertEquals(3, output.get(0).getCounter());
        assertEquals("S1", output.get(0).getTagEvent().geoZone);

        assertEquals(111l, output.get(1).getTrackId());
        assertEquals(2, output.get(1).getCounter());
        assertEquals("S1", output.get(1).getTagEvent().geoZone);

        assertEquals(222l, output.get(2).getTrackId());
        assertEquals(1, output.get(2).getCounter());
        assertEquals("S1", output.get(2).getTagEvent().geoZone);

        assertEquals(222l, output.get(3).getTrackId());
        assertEquals(3, output.get(3).getCounter());
        assertEquals("S2", output.get(3).getTagEvent().geoZone);

        assertEquals(333l, output.get(4).getTrackId());
        assertEquals(1, output.get(4).getCounter());
        assertEquals("S2", output.get(4).getTagEvent().geoZone);
    }



}