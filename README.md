
# GCP Samples
An architect who is responsible to move an enterprise / organization / product to a cloud, has to be aware of many aspects of the target cloud. It is important to look at it from the aspects of tooling, non-functional requirements, As I started moving our applications to GCP, I started documenting my learning. I hope this is found helpful.
 
### Application Tooling
Every cloud provides various options to help design and run applications. Here are the important questions an architect has to pose towards tooling:
- What options do I have to store data? 
- How do I run my applications? 
- How do I move data between applications and data stores? 
### Non functional requirements
- How do I ensure scalability and availability?
- How do I achieve monitoring, logging and metrics collection?
#### Security
- How do I manage access control?
- How do I manage, control and secure network traffic?

Once an architect answers these questions, next step would be to understand it at a code level. I have always struggled to setup the initial 'boilerplate' code in every project, many a times even after using Spring starter. Here are sample projects that help quickstart application development that demonstrate a cloud aspect with a simple wireframe of a project. These can be used as reference to understand how to leverage various GCP features.

# GCP Starter Cheat Sheet

## Compatible Tech Stack
### Application Tech Stack
#### Application Development and Connectivity:
 * Prefer OpenJDK 8 over Oracle JDK unless enterprise has worked out licensing requirements
 * All applications are Spring Boot, running as uberjars
     * Use Spring Jersey for REST endpoints
     * Use Spring Data for DB connections
     * Use Spring Data JPA + Hibernate for RDBMS
     * Use Spring Data Redis + Redis for far-caching
      * Use Hikari for connection pool
 * Use Google Pub/Sub for messaging, use native Pub/Sub libraries or spring-cloud-messaging
#### Application Builds and Deployments
 * Build applications as Docker images
 * Prefer Alpine JDK Docker image as root (lightest)
 * Docker containers are versioned and pushed to GCP container registry
 * All docker are run on GKE. Prefer manual scaling of cluster size over auto scaling.
 * All application's deployment yamls to contain placeholders for parameters like db connection, db credentials, endpoints, etc.,
 * Secrets are stored in Kubernetes key store and injected at the time of pod creation


## GCP Infrastructure Stack
### Data Stores - RDBMS
 * Choose CloudSQL managed MySQL or PostgresQL instances.
    * Choose HA version only
    * Cheaper thank other SQL options
    * Not vertically or horizontally scalable . (okay for microservices, manage multiple schema)
    * Not multi-region, need a DR strategy for multi-region
  * Choose CloudSpanner for multi-region, autoscalable,  ACID level transactions
     * Very expensive $$$$
     * Charged by usage, a PaaS solution - size for number of compute nodes ($$$) and storage ($)
### Data Stores - BigQuery
 * Maintain different schema for different business groups
 * Best used for reporting, deep analytics, ML and possibly dashboarding
 * Best place to create a [Materialize View](https://docs.microsoft.com/en-us/azure/architecture/patterns/materialized-view) for joining microservice domain
 * Create dataflow to move data from other data stores to BQ (CloudSQL to BQ, pub/sub to BQ, etc.,)


## Handling Non Functional Requirements
### Monitoring, Tracing, Logging
#### Logging
 * Favor logging to sysout and allow stackdriver to collect logs
 * Ensure trace-id (as explained under tracing) is attached in logs
#### Monitoring applications
 * All applications should enable /metrics endpoints to allow Promoetheus to pull metrics
 * Use `micrometer` (via spring actuator) Spring Boot 2.0 has abandoned its own metrics collection and adopted micrometer as standard: https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector
 * Prometheus installed in every GKE cluster
#### Tracing applications
 * Stackdriver Trace (different product from Stackdriver) is the best place to collect metrics
 * All applications need to allow tracing. Easiest way to achieve is via using `spring-cloud-gcp-starter-trace` library. Only adding to maven, and adding a few configuration parameters will start tracing to Stackdriver Trace
 * Under the wraps `spring-cloud-gcp-starter-trace` uses zipkin/sleuth for tracing
 * GCP conforms to opencensus, (similar to Azure) and not opentracing
 * `spring-cloud-gcp-starter-trace` also generates traceid and spanid, which can be embedded in logs for debugging

### Cutting Edge
 * Istio is available as addon to GKE, but in Beta stage (as of Q1 2019)
 * Envoy sidecar is Google's preferred sidecar


# Getting Started
```
git clone
```
### Prerequisites
All projects are Spring Boot projects, and generate uberjars. The projects need maven installed, so mvn install can build the executable fatjar. It also assumes that you have a gcp project (with billing enabled) created and setup. 
Create a new service account with enough privileges required to work with GCP features. Generate a private key from this account,  download it, store it in your local machine (or on the GCE instance) securely, and set it in an environment variable.
```
export GOOGLE_APPLICATION_CREDENTIALS=/opt/secrets/pubsubkey.json
```
or
```
set GOOGLE_APPLICATION_CREDENTIALS=C:\secret\pubsubkey.json
```

### Installing
```
mvn install
```
This builds the application as a runnable uberjar `pubsub-1.0-SNAPSHOT.jar`.


## Deployment

The application needs GCP credentials with proper read/write access set as environment variable.
```
set GOOGLE_APPLICATION_CREDENTIALS=<your certificate file> 
```
Start the application to create a REST end point
```
java -DPROJECT_ID=<project name> -DTOPIC_NAME=<name of pubsub topic to read and write from> -DSUBSCRIPTION_ID=<pull subscription name against the topic> pubsub-1.0-SNAPSHOT.jar
```

# Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Eclipse](https://www.eclipse.org/) - Used to generate RSS Feeds


# Authors

* **rcmohan** - *Initial work*

# License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

# Acknowledgments

* Thanks to [PurpleBooth](https://github.com/PurpleBooth) for the original readme teamplate 
