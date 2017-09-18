package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.ChartsCliOutput;
import com.foreach.poc.charts.model.TagEvent;
import com.foreach.poc.charts.model.TagEventUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.tuple.Tuple3;
import org.junit.Before;
import org.junit.Test;

import javax.swing.text.html.HTML;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SimpleChartsPipelineTest {

    private static Config config;
    private static ArgsParser argsParser;
    private static TagEventUtils tagUtils;

    @Before
    public void setUp() throws Exception {
        config= ConfigFactory.load();

    }

    /**
     * Test to validate the cleansing method.
     * We generate a DataSet with 10 TagEvents and modify 2 items to force bad data
     * The assertion checks that only are obtained the proper number of items after the
     * cleansing process.
     * @throws Exception
     */
    @Test
    public void cleansingTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "3"};
        argsParser= ArgsParser.builder(args);

        PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);
        SimpleChartsPipeline pipeline= new SimpleChartsPipeline(pipelineConf);

        List<TagEvent> mockCollection= TagEventUtils.getMockData(10);
        mockCollection.set(0, new TagEvent(0l, "xxx", "yy","zz"));
        mockCollection.set(4, new TagEvent(99l, "xxx", "yy",""));

        DataSet<TagEvent> mockDataset= pipeline.getEnv().fromCollection(mockCollection);

        DataSet<Tuple3<Long, Integer, TagEvent>> clean = pipeline.cleansing(mockDataset);
        assertEquals(9, clean.count());
    }

    /**
     * Validation of transformation logic.
     * A DataSet with 10 TagEvents is created. The mocked dataset should return after to
     * transform a Dataset with 3 items.
     * @throws Exception
     */
    @Test
    public void transformationTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "5"};
        argsParser= ArgsParser.builder(args);

        PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);
        SimpleChartsPipeline pipeline= new SimpleChartsPipeline(pipelineConf);

        List<Tuple3<Long, Integer, TagEvent>> mockCollection= new ArrayList<>();
        mockCollection.add(new Tuple3<>(111l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(222l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(333l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(111l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(222l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(333l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(222l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(222l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(333l, 1, new TagEvent()));
        mockCollection.add(new Tuple3<>(222l, 1, new TagEvent()));

        DataSet<Tuple3<Long, Integer, TagEvent>> mockDataset= pipeline.getEnv().fromCollection(mockCollection);
        DataSet<ChartsCliOutput> transformed= pipeline.transformation(mockDataset);

        List<ChartsCliOutput> output= transformed.collect();
        // We only expect 3 different items
        assertEquals(3, output.size());

        assertEquals(222l, output.get(0).getTrackId());
        assertEquals(5, output.get(0).getCounter());

        assertEquals(333l, output.get(1).getTrackId());
        assertEquals(3, output.get(1).getCounter());

        assertEquals(111l, output.get(2).getTrackId());
        assertEquals(2, output.get(2).getCounter());
    }



}