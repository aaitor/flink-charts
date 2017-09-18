package com.foreach.poc.charts.core;

import com.typesafe.config.Config;

public class PipelineChartsConf extends PipelineConf {

    public PipelineChartsConf(ArgsParser args) {
        super(args);
    }

    public PipelineChartsConf(Config config, ArgsParser args) {
        super(config, args);
    }

    public int getLimit()   {
        return args.getLimit();
    }

    public String getFilePath() {
        return config.getString("ingestion.file.path");
    }


}
