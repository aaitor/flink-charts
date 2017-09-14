package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.java.DataSet;

public class ChartsBatchPipeline extends ChartsPipeline implements DataPipeline {

    private PipelineChartsConf conf;

    public ChartsBatchPipeline(PipelineChartsConf conf) {
        this.conf= conf;
    }

    @Override
    public DataSet<TagEvent> ingestion() {
        log.info("Ingestion Phase. Parsing JSON file: " + conf.getFilePath());
        return getEnv()
                .readTextFile(conf.getFilePath())
                .map(line -> TagEvent.builder(line));
    }

    @Override
    public DataSet<?> cleansing(DataSet<?> input) {
        return null;
    }

    @Override
    public DataSet<?> normalization(DataSet<?> input) {
        return null;
    }

    @Override
    public DataSet<?> transformation(DataSet<?> input) {
        return null;
    }

    @Override
    public boolean persistence(DataSet<?> input) {
        return false;
    }
}
