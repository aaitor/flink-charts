# Flink Charts Proof of Concept

Flink PoC project using as use case some Tag Events where a user has successfully indentified a song. 
Those events are in JSON format. You can see an example in: **src/test/resources/data/one-tag.json** 

Using as input multiple tags the software generate 2 kind of chart's:

* Simple Chart. Returning the tracks more times identified by the users
* State Chart. Returning the top tracks more times identified by state

## Requirements 

To compile and run this project you need:

* Java 8
* Apache Flink 1.3 (scala 2.11)
* Maven 3

## How to compile?

The following command should compile the application, execute the tests and build the application fat jar:

`mvn clean package -Pbuild-jar`

The fat jar should be generated in the target folder:

`ls -la target/charts-*.jar`

## How to run the application?

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
United States in the state chart scenario.
* Normalization. This phase could implement some basic data normalizations. Not implemented initially.
* Transformation. After clean and/or normalize this phase compute the calculations.
* Persistence. This phase is not implemented initially.

The **ChartsPipeline** abstract class define the generalization of this Data Pipeline implementation. 
The different classes implementing the different charts behaviours are:
* **SimpleChartsPipeline**. Provides the logic to return the top tracks identified by the users.
* **StateChartsPipeline**. Provides the logic to return the top tracks identified by the users per state.



