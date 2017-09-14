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

import com.foreach.poc.charts.core.*;
import com.foreach.poc.charts.model.TagEvent;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.cli.*;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import static org.apache.flink.api.java.aggregation.Aggregations.SUM;

/**
 * Skeleton for a Flink Batch Job.
 *
 * For a full example of a Flink Batch Job, see the WordCountJob.java file in the
 * same package/directory or have a look at the website.
 *
 * You can also generate a .jar file that you can submit on your Flink
 * cluster.
 * Just type
 * 		mvn clean package
 * in the projects root directory.
 * You will find the jar in
 * 		target/charts-1.0-SNAPSHOT.jar
 * From the CLI you can then run
 * 		./bin/flink run -c com.foreach.poc.BatchJob target/charts-1.0-SNAPSHOT.jar
 *
 * For more information on the CLI see:
 *
 * http://flink.apache.org/docs/latest/apis/cli.html
 */
public class BatchMain {

	static final Logger log= LogManager.getLogger(BatchMain.class);
	static Config config;

	/**
	 * 	Flink-charts batch job. Giving the parameters:
	 * 	chart 3
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
	 *  state_chart 3
	 *  Should output the top 3 tracks in each and every US state. The output format for the state
	 *  chart should be similar to the above, but you are free to define it.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		ArgsParser argsParser= null;

		try {
			argsParser= ArgsParser.builder(args);
			config= ConfigFactory.load();
		} catch (ParseException ex)	{
			log.error("Unable to parse arguments");
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar xxx.jar -c com.foreach.poc.charts.BatchMain", ArgsParser.getDefaultOptions());
			System.exit(1);
		}

		log.info("Initializing job: " + argsParser.toString());

//		log.info("Initializing Flink engine");
//		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		PipelineChartsConf pipelineConf= new PipelineChartsConf(config, argsParser);
		ChartsBatchPipeline pipeline= new ChartsBatchPipeline(pipelineConf);
		DataSet<TagEvent> inputTags= pipeline.ingestion();
		DataSet<TagEvent> cleanTags= pipeline.cleansing(inputTags);
		pipeline.transformation();

		log.info("Ingestion Phase. Parsing JSON file: " + config.getString("ingestion.file.path"));
		DataSet<String> jsonTags= env.readTextFile(config.getString("ingestion.file.path"));

		log.info("Ingestion Phase. Mapping the string lines to TagEvent bean");
		DataSet<TagEvent> rawTags= jsonTags.map(line -> TagEvent.builder(line));

		log.info("Cleansing Phase. Removing the invalid documents");
		DataSet<Tuple3<Long, Integer, TagEvent>> tagsTuple= rawTags
				.filter( t -> !t.geoZone.isEmpty() && t.trackId > 0)
				.map( t -> new Tuple3<>(t.trackId, 1, t))
				.returns(new TypeHint<Tuple3<Long, Integer, TagEvent>>(){});

		log.info("Transformation Phase. Computing the tags");
		tagsTuple.groupBy(0) // Grouping by trackId
				 .sum(1) // Sum the occurrences of each grouped item
				 .sortPartition(1, Order.DESCENDING).setParallelism(1) // Sort by count
				 .first(argsParser.getLimit()) // Get only the number of results defined by the user
				 .collect()
				 .forEach( t -> log.info("trackId: " + t.f0 + ", count: " + t.f1 + ", " + t.toString())); // Print the results



		/**
		 * new PipelineConf()
		 * 		.setConfig("file.path.yaml")
		 * new ChartsPipeline(pipelineConf)
		 * pipeline.ingest()
		 * 			.calculateTopTags(5)
		 * 			.print()
 		 */



		// execute program
		//env.execute("Flink Batch Java API Skeleton");
	}
}
