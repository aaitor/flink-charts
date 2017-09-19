package com.foreach.poc.charts.core;

import com.foreach.poc.charts.model.ChartsResult;
import com.foreach.poc.charts.model.TagEvent;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * ChartsPipeline generalization class. Provides the common Charts pipeline capabilities.
 * Different kind of charts extends from here.
 */
public abstract class ChartsPipeline {

    static final Logger log= LogManager.getLogger(ChartsPipeline.class);

    protected final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    protected PipelineConf pipelineConf;

    protected ChartsPipeline() {}

    public ChartsPipeline(PipelineConf conf) {
        log.info("Initializing Pipeline");
        this.pipelineConf= conf;
    }

//    abstract protected DataSet<Tuple3<Long, Integer, TagEvent>> cleansing(DataSet<TagEvent> input);
//
//    abstract protected DataSet<Tuple4<Long, Integer, String, TagEvent>> cleansingState(DataSet<TagEvent> input);

    abstract protected DataSet<ChartsResult> transformation(DataSet<?> input);

    public DataSet<TagEvent> ingestion() {
        log.info("Ingestion Phase. Parsing JSON file: " + pipelineConf.config.getString("ingestion.file.path"));
        return getEnv()
                .readTextFile( pipelineConf.config.getString("ingestion.file.path"))
                .map(line -> TagEvent.builder(line));
    }

    // Normalization not required for this use case
    public DataSet<?> normalization(DataSet<?> input) {
        log.info("Normalization Phase");
        return input;
    }

    public ExecutionEnvironment getEnv() {
        return env;
    }

    public PipelineConf getPipelineConf() {
        return pipelineConf;
    }

    public void setPipelineConf(PipelineConf pipelineConf) {
        this.pipelineConf = pipelineConf;
    }

}
