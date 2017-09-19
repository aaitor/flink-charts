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
public abstract class ChartsPipeline implements DataPipeline<TagEvent> {

    static final Logger log= LogManager.getLogger(ChartsPipeline.class);

    // Flink execution environment
    protected final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    protected PipelineConf pipelineConf;

    protected ChartsPipeline() {}

    public ChartsPipeline(PipelineConf conf) {
        log.info("Initializing Pipeline");
        this.pipelineConf= conf;
    }

    /**
     * Ingestion common phase
     * @return
     */
    public DataSet<TagEvent> ingestion() {
        log.info("Ingestion Phase. Parsing JSON file: " + pipelineConf.config.getString("ingestion.file.path"));
        return getEnv()
                .readTextFile( pipelineConf.config.getString("ingestion.file.path"))
                .map(line -> TagEvent.builder(line));
    }

    /**
     * Normalization common phase. Not implemented initially
     * @param input
     * @return
     */
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
