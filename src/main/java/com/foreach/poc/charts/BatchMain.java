package com.foreach.poc.charts;

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.foreach.poc.charts.core.ArgsParser;
import com.foreach.poc.charts.core.PipelineChartsConf;
import com.foreach.poc.charts.core.SimpleChartsPipeline;
import com.foreach.poc.charts.core.StateChartsPipeline;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Flink Batch Charts jobs
 *
 * From the CLI you can run
 * 		 $FLINK_HOME/bin/flink run -c com.foreach.poc.charts.BatchMain target/charts-1.0-SNAPSHOT.jar -c chart -l 5
 *
 * For more information on the CLI see:
 *
 * http://flink.apache.org/docs/latest/apis/cli.html
 */
public class BatchMain {

	static final Logger log= LogManager.getLogger(BatchMain.class);
	static Config config;

	static final String CLI_CMD= "$FLINK_HOME/bin/flink run -c com.foreach.poc.charts.BatchMain target/charts-1.0-SNAPSHOT.jar -c chart -l 5";

	/**
	 * 	Flink-charts batch job. Giving the parameters:
	 *
	 * 	chart 3
	 *
	 * 	Should output the top 3 most tagged tracks irrespective of user location. The output should
	 * 	be in a columnar format and contain the following fields:
	 * 	CHART POSITION , TRACK TITLE , ARTIST NAME
	 *
	 * 	Thus, the output may be as follows:
	 * 		1 Shape Of You Ed Sheeran
	 * 		2 24k Magic Bruno Mars
	 * 		3 This Girl Kungs
	 *
	 *  Similarly, giving the parameters:
	 *
	 *  state_chart 3
	 *
	 *  Should output the top 3 tracks in each and every US state. The output format for the state
	 *  chart should be similar to the above, but you are free to define it.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ArgsParser argsParser= null;

		try {
			log.debug("Parsing input parameters ");
			argsParser= ArgsParser.builder(args);

			log.debug("Loading configuration");
			config= ConfigFactory.load();

		} catch (ParseException ex)	{
			log.error("Unable to parse arguments");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(CLI_CMD, ArgsParser.getDefaultOptions());
			System.exit(1);
		}

		log.debug("Initializing job: " + argsParser.toString());
		PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);

		if (argsParser.getChartType().equals(ArgsParser.chartTypeOptions.chart.toString())) {
			log.debug("Starting Simple Charts job. Getting top track chart!");
			SimpleChartsPipeline.run(pipelineConf);

		} else if (argsParser.getChartType().equals(ArgsParser.chartTypeOptions.state_chart.toString())) {
			log.debug("Starting State Charts job. Getting top track charts by State (country: " +
					config.getString("ingestion.stateChart.country")+ ").");
			StateChartsPipeline.run(pipelineConf);

		} else	{
			log.error("Unable to parse arguments");
			System.exit(1);
		}


	}
}
