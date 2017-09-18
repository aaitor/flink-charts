package com.foreach.poc.charts.core;

import com.typesafe.config.Config;


public abstract class PipelineConf<T> {

    protected Config config;
    protected ArgsParser args;

    protected PipelineConf(ArgsParser args) {
        this.args= args;
        this.config= args.getConfig();
    }

    protected PipelineConf(Config config, ArgsParser args) {
        this.config= config;
        this.args= args;
    }

    public Config getConfig() {
        return config;
    }

    public ArgsParser getArgs() {
        return args;
    }
}
