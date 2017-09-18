package com.foreach.poc.charts.core;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.*;

import java.io.File;
import java.util.Arrays;

/**
 * Arguments parser class. Implemented to validate the input parameters
 * given by the user.
 * More information in builder method.
 */
public class ArgsParser {

    // List of available chart types
    public enum chartTypeOptions {chart, state_chart};
    // Default Limit value
    private final static int DEFAULT_LIMIT= 5;

    // Chart type (simple chart by default)
    private String chartType= chartTypeOptions.chart.toString();
    // Limit number
    private int limit;
    // Path to config file
    private String fileConfPath= null;

    private Config config= null;

    public ArgsParser() {}

    public ArgsParser(String chartType, int limit) throws ParseException {
        setChartType(chartType);
        setLimit(limit);
    }
    public ArgsParser(String chartType, int limit, String fileConfPath) throws ParseException {
        this(chartType, limit);
        setFileConfPath(fileConfPath);
    }

    public Config getConfig()  {
        if (config!= null)
            return config;
        if (fileConfPath == null || fileConfPath.equals(""))
            config= ConfigFactory.load();
        else
            config= ConfigFactory.parseFile(new File(fileConfPath));
        return config;
    }

    public String getChartType() {
        return chartType;
    }

    public ArgsParser setChartType(String chartType) throws ParseException {
        // Checking if param is in available options
        if (!Arrays.stream(chartTypeOptions.values()).anyMatch(e -> e.name().equals(chartType)))
            throw new ParseException("Invalid option");
        this.chartType = chartType;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public ArgsParser setLimit(int limit) throws ParseException {
        if (limit < 1)
            throw new ParseException("Limit should be higher than 0");
        this.limit = limit;
        return this;
    }

    public String getFileConfPath() {
        return fileConfPath;
    }

    public ArgsParser setFileConfPath(String fileConfPath) {
        this.fileConfPath = fileConfPath;
        return this;
    }

    @Override
    public String toString() {
        return "ArgsParser{" +
                "chartType='" + chartType + '\'' +
                ", limit=" + limit +
                ", fileConfPath='" + fileConfPath + '\'' +
                '}';
    }

    /**
     * Command-line parser method. The application should accept two parameters:
     * 	1. A command, which will be either "chart" or "state_chart"
     * 	2. A limit, which will determine the number of tracks to output
     *
     * @param args
     * @throws ParseException
     */
    public static ArgsParser builder(String[] args) throws ParseException {
        Options options=getDefaultOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("c")) {
            String chartOption= cmd.getOptionValue("c", chartTypeOptions.chart.toString());
            int limit= DEFAULT_LIMIT;
            if (cmd.hasOption("l")) {
                try {
                    limit= Integer.parseInt(cmd.getOptionValue("l", String.valueOf(DEFAULT_LIMIT)));
                } catch (NumberFormatException ex)  {
                    throw new ParseException("Invalid limit passed as parameter");
                }
            }

            ArgsParser argsParser= new ArgsParser()
                    .setChartType(chartOption)
                    .setLimit(limit);

            if (cmd.hasOption("f")) {
                argsParser.setFileConfPath(cmd.getOptionValue("f"));
            }

            return argsParser;
        }

        throw new ParseException("Bad parameters used");
    }

    public static Options getDefaultOptions()   {
        Options options= new Options();

        options.addOption("c", "command", true, "chart type, can be chart or state_chart");
        options.addOption("l", "limit", true, "number of tracks to output");
        options.addOption("f", "config", true, "config file path");

        return options;
    }
}
