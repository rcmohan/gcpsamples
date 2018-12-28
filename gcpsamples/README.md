# GCP Samples

I have always struggled to setup the initial 'boilerplate' code in every project, even after using Spring starter. Here are sample projects that help quickstart projects by having a basic wireframe of a project ready. These can be used as reference to understand how to leverage various GCP features.

## Getting Started

```
git clone
```

### Prerequisites

All projects are Spring Boot projects, and generate uberjars. The projects need maven installed, so mvn install can build the executable fatjar. It also assumes that you have a gcp project (with billing enabled) created and setup.
Run the following commands to ensure.
```
```

### Installing
```
mvn install

```


This builds the application as an uber jar.


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
