# Setting up Dataflows GCP Pub/Sub

Setting up data movement between any of GCP PaaS and SaaS offerings is possible through Google Dataflows. Dataflow is based on Apache Beam, and the code is available on git [here](https://github.com/GoogleCloudPlatform/DataflowTemplates). The basic idea here is to submit the dataflow to GCP with configurations enabled. 
Let us say your application needs a time series database of resource changes. The best way to achieve this is to ensure your REST endpoints publish the new state of the resource along with every `POST`/`PUT`/`PATCH` operations to a messaging channel, in case of GCP, a pub/sub topic. Next, it would be the right thing to store this time series data into BigQuery for analytics. This is where Dataflows help. Simply configure a dataflow, and dataflow ensure data is sinked into BQ from pub/sub, even matching JSON content to BQ table contents.The job of setting up a dataflow is as simple as `gcloud dataflow jobs run PubSubToBigQuery` command, with the topic name and BQ schema/table name as parameters.

##Prebuilt Templates
By default GCP provides a list of various dataflows, which can be found [here on gcp guides](https://cloud.google.com/dataflow/docs/guides/templates/provided-templates). However, all of these template assume certain predefined infrastructure, for instance, existence of a `default` subnet with certain settings, region locks, etc., As every enterprise sets up projects differently, it will be required to rebuild these templates and run the jobs.

###Customizing Templates
Run the following commands to create a topic and corresponding pull subscription.
```
set PROJECT_ID=<GCP-project-name>
set BUCKET_NAME=<bucket-name>
set PIPELINE_FOLDER=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery
set RUNNER=DataflowRunner

set TARGET_REGION=<region, say, us-central1>
set BQ_TBL_NAME=<big query table name in 'schema.table' format>
set SRC_TOPIC=<source topic>
set SUBNET=<a dedicated subnet that is present in the target region>
set JOBNAME=<a name for the streaming job created>

@REM Set the runner
set RUNNER=DataflowRunner

@REM Build the template
bin/mvn compile exec:java -Dexec.mainClass=com.google.cloud.teleport.templates.PubSubToBigQuery -Dexec.cleanupDaemonThreads=false -Dexec.args="--project=${PROJECT_ID} --stagingLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/staging --tempLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/temp --templateLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/PubSubToBigQueryTemplate --runner=DataflowRunner --subnetwork=https://www.googleapis.com/compute/v1/projects/${PROJECT_ID}/regions/${TARGET_REGION}/subnetworks/{SUBNET} --usePublicIps=false"


gcloud dataflow jobs run ${JOBNAME} --gcs-location gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/PubSubToBigQueryTemplate --parameters  inputTopic=projects/${PROJECT_ID}/topics/${SRC_TOPIC},outputTableSpec=${PROJECT_ID}:${BQ_TBL_NAME} --region ${TARGET_REGION}
```


The maven command creates a template, in this case `PubSubToBigQueryTemplate`, which will copy the contents of every message that appears on the topic `SRC_TOPIC` into the BQ table `BQ_TBL_NAME`. 

