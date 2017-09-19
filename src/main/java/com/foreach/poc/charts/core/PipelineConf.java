package com.foreach.poc.charts.core;

import com.typesafe.config.Config;

/**
 * Provide the commons PipelineConf common capabilities
 */
public abstract class PipelineConf<T> {

    // Typesafe conf file
    protected Config config;
    // Args parser
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
