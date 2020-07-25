# Telegram-Project
This is a simple pure-java-based project similar to [Telgram](https://www.telegram.org), a social media and messaging application which is used to send messages with no charge.  
The project has two modules: GUI and service, the former is the Graphical User Interface which is based on javafx, and the latter is the back bone of the project which handles server-side queries.

## Getting Started
It is so easy and fast to run Telegram project, all it needs are some tools and 5 minutes of your time.

### Prerequisites
Before using Telegram, you have to setup the following tools:
* [jdk11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* [maven](https://maven.apache.org/)

### Installing
* Download and install [jdk11+](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) and [maven](https://maven.apache.org/download.cgi).
* You'd better have an IDE which supports maven, intellij IDEA is a preference.
* In **server** module, run **clean** and **compile** goals or simply run **compile** lifecycle and then run **exec:java** goal of the [exec](https://www.mojohaus.org/exec-maven-plugin) plugin, which is defined in the parent pom.xml file. Another option for you is to run it manually with the help of IDE.
* In **GUI** module run **javafx:run** goal, which belongs to [javafx](https://github.com/openjfx/javafx-maven-plugin) plugin and is defined in GUI module's pom.xml.  

## Built With
* [Maven 3.6.3](https://maven.apache.org/) - Dependency Management
* [jdk11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
* intellij IDEA 2020.1.4
    
## Authors
* **Alireza Asadi**
