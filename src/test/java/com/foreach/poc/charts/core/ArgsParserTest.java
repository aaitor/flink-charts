package com.foreach.poc.charts.core;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArgsParserTest {

    @Test
    public void simpleBuilderTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "3"};
        ArgsParser parser= ArgsParser.builder(args);

        assertEquals("chart", parser.getChartType());
        assertEquals(3, parser.getLimit());
    }

    @Test
    public void defaultLimitTest() throws Exception {
        String args[]= {"-c", "state_chart"};
        ArgsParser parser= ArgsParser.builder(args);

        assertEquals("state_chart", parser.getChartType());
        assertEquals(5, parser.getLimit());
    }


    @Test
    public void configFileTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "3", "-f", "myfile.conf"};
        ArgsParser parser= ArgsParser.builder(args);

        assertEquals("chart", parser.getChartType());
        assertEquals(3, parser.getLimit());
        assertEquals("myfile.conf", parser.getFileConfPath());

    }

    @Test(expected = ParseException.class)
    public void noValidChartOptionTest() throws Exception {
        String args[]= {"-c", "DOESNT-EXIST", "-l", "0"};
        ArgsParser parser= ArgsParser.builder(args);
    }

    @Test(expected = ParseException.class)
    public void badNumberLimitTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "0"};
        ArgsParser parser= ArgsParser.builder(args);
    }

    @Test(expected = ParseException.class)
    public void invalidLimitTest() throws Exception {
        String args[]= {"-c", "chart", "-l", "xxx"};
        ArgsParser parser= ArgsParser.builder(args);

    }
}