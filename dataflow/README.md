# Setting up Dataflows GCP Pub/Sub

Setting up data movement between any of GCP PaaS and SaaS offerings is possible through Google Dataflows. Dataflow is based on Apache Beam, and the code is available on git [here](https://github.com/GoogleCloudPlatform/DataflowTemplates)

Run the following commands to create a topic and corresponding pull subscription.
```
set PROJECT_ID=<GCP-project-name>
set BUCKET_NAME=<bucjet-name>
set PIPELINE_FOLDER=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery
set RUNNER=DataflowRunner

set TARGET_REGION=<region, say, us-central1>
set BQ_TBL_NAME=<big query table name in 'schema.table' format>
set SRC_TOPIC=<source topic>
SET SUBNET=<a dedicated subnet that is present in the target region>

@REM Set the runner
set RUNNER=DataflowRunner

@REM Build the template
c:/apps/maven/bin/mvn compile exec:java -Dexec.mainClass=com.google.cloud.teleport.templates.PubSubToBigQuery -Dexec.cleanupDaemonThreads=false -Dexec.args="--project=${PROJECT_ID} --stagingLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/staging --tempLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/temp --templateLocation=gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/template --runner=DataflowRunner --subnetwork=https://www.googleapis.com/compute/v1/projects/${PROJECT_ID}/regions/${TARGET_REGION}/subnetworks/{SUBNET} --usePublicIps=false"


gcloud dataflow jobs run workflowdrain-2 --gcs-location gs://${BUCKET_NAME}/dataflow/pipelines/pubsub-to-bigquery/template --parameters  inputTopic=projects/${PROJECT_ID}/topics/${SRC_TOPIC},outputTableSpec=${PROJECT_ID}:${BQ_TBL_NAME} --region ${TARGET_REGION}
```

