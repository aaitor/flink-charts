# Flink Charts Proof of Concept

Flink PoC project using as use case some Tag Events where a user has successfully indentified a song. 
Those events are in JSON format. You can see an example in: **src/test/resources/data/one-tag.json** 

Using as input multiple tags the software generate 2 kind of chart's:

* Simple Chart. Returning the tracks more times identified by the users
* State Chart. Returning the top tracks more times identified by state

## Requirements 

To compile and run this project you need:

* Java 8
* Maven 3
* Apache Flink 1.3 (scala 2.11)


## How to compile?

The following command should compile the application, execute the tests and build the application fat jar:

`mvn clean package -Pbuild-jar`

The fat jar should be generated in the target folder:

`ls -la target/charts-*.jar`

Also using maven assembly the application generates a zip file in target folder including the **application.conf**
 and **log4j.properties** file. 

## How to run the application?

Properties file are packaged in an independent zip file, if the -f option is not provided the software will use the 
properties by default packaged in the jar file (TODO: Uncoment in the pom.xml the options to remove those files).
This package doesn't include the sample data set, so you should copy your own dataset and setup the file in the application.conf file.

After to modify the application.conf and compile the application, you can run using the following command:
* To generate the general top tags use the -c chart parameter:
`$FLINK_HOME/bin/flink run -c com.foreach.poc.charts.BatchMain target/charts-1.0-SNAPSHOT.jar -c chart -l 5 -f c://Users//yourpath//application.conf`

* To generate the top tags per state use the -c state_chart parameter:
`$FLINK_HOME/bin/flink run -c com.foreach.poc.charts.BatchMain target/charts-1.0-SNAPSHOT.jar -c state_chart -l 5 -f c://Users//yourpath//application.conf`

Depending if you are running in Windows, MacOS or Linux you will need to run the flink.sh or flink.bat script. Also the paths 
to the config files should be adapted depending of the environment. 

### Running using Docker

If you don't have flink in your local environment but have Docker, you can run the code using the following commands.

First of all you need to access the docker folder start the flink docker instances (jobmanager & taskmanager)
`cd docker && docker-compose up`

Copy the jar file packaging the application and the config files to the jobmanager:

`docker cp ../target/charts-1.0-SNAPSHOT.jar docker_jobmanager_1:/flink`
`docker cp ../target/charts-1.0-SNAPSHOT.zip docker_jobmanager_1:/flink`

Copy your sample dataset to the docker image (in the /flink folder):

`docker cp ../src/test/resources/data/tag-sample.json docker_jobmanager_1:/flink/`

Unzip the properties files:

`docker exec -it docker_jobmanager_1 sh -c "cd /flink &&  unzip -u /flink/*.zip"`

Replace the path to the dataset in the application.conf file. Update this command if you are using a dataset in a different path:

`docker exec -it docker_jobmanager_1 sh -c  "sed -i 's/src\/test\/resources/\/flink/g' /flink/config/application.conf"`

Run the command:

`docker exec -it docker_jobmanager_1 sh -c "flink run -c com.foreach.poc.charts.BatchMain /flink/charts-1.0-SNAPSHOT.jar -c state_chart -l 5 -f /flink/config/application.conf"`

After to run the flink application you should be able to see the execution details in the Flink console:

http://localhost:8081/

## Application Architecture

### Data Models

The application uses as ingestion model the **TagEvent** class. This bean implements a simplified 
version of the of the tag event document and is populated automatically using Jackson.
The **TagEvent** class implements the interface **FromJsonToModel**, this interface define the 
behaviour of given a Json document, instantiate and return the model bean.
At the same time this class extends from **TagModel** abstract class. **TagModel** provides the 
Jackson ObjectMapper instantiation capabilities.

Using this architecture would be possible to extend and support additional JSON document events 
adding additional beans defining the models required. 

As output model the application uses the **ChartsResult** bean. The different instances of results
are generated during the application execution populating the multiple instances of this bean.   

### Data Pipeline

The application implements a **ETL** pipeline with the following phases:
* Ingestion. The data is ingested from the source as a DataSet of **TagEvent** beans
* Cleansing. The input data is cleansed in a simple way. The application filter the invalid 
TagEvent's. In this case only the documents without trackid or with a country distinct of the 
United States in the state chart scenario. Could be possible to add more advance rules to remove other 
kind of invalid input data.
* Normalization. This phase could implement some basic data normalizations. Not implemented initially.
* Transformation. After clean and/or normalize this phase compute the calculations.
* Persistence. This phase is not implemented initially.

The **ChartsPipeline** abstract class define the generalization of this Data Pipeline implementation. 
The different classes implementing the different charts behaviours are:
* **SimpleChartsPipeline**. Provides the logic to return the top tracks identified by the users.
* **StateChartsPipeline**. Provides the logic to return the top tracks identified by the users per state.


## TODO

* Add new use cases using Flink Streaming
* New use cases using Flink SQL
* Integrate the CEP


