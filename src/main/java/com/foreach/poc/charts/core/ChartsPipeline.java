package com.foreach.poc.charts.core;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class ChartsPipeline {

    static final Logger log= LogManager.getLogger(ChartsPipeline.class);
    protected final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
    protected PipelineConf pipelineConf;

    public ChartsPipeline(PipelineConf conf) {
        log.info("Initializing Pipeline");
        this.pipelineConf= conf;
    }

    protected ChartsPipeline() {
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
