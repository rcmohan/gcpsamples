
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

## Getting Started
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

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Eclipse](https://www.eclipse.org/) - Used to generate RSS Feeds


## Authors

* **rcmohan** - *Initial work*

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Thanks to [PurpleBooth](https://github.com/PurpleBooth) for the original readme teamplate 
