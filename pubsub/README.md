# GCP Pubsub

A sample Spring Boot project to read and write Google Pubsub using Google's core libraries. 

## Getting Started

```
git clone
```

### Prerequisites

All projects are Spring Boot projects, and generate uberjars. The projects need maven installed, so mvn install can build the executable fatjar. It also assumes that you have a gcp project (with billing enabled) created and setup.
Run the following commands to create a topic and corresponding pull subscription. 

```
 gcloud pubsub topics create <topic name>
 gcloud pubsub subscriptions create <subscription name> --topic <topic name from before>
```

### Installing

```
mvn install

```

This builds the application as a runnable uberjar `pubsub-1.0-SNAPSHOT.jar`.


## Deployment

All GCP libraries look for project name and credentials. To enable this, set the following environment variables

```
export GOOGLE_CLOUD_PROJECT=<your project name>
export GOOGLE_APPLICATION_CREDENTIALS=<your certificate file> 
```

Or, in Windows,

```
set GOOGLE_CLOUD_PROJECT=<your project name>
set GOOGLE_APPLICATION_CREDENTIALS=<your certificate file> 
```

Make sure the credentials are for a service account with proper read/write access to pub/sub.

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
