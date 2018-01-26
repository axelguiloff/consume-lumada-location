## Demo for Java AMQP Consumer for Lumada Events

This project is a test for subscribing to event messages from [Lumada](https://www.hitachivantara.com/en-us/products/internet-of-things/lumada.html)

Events or state changes created in Lumada trigger a message event that can be
subsribed to via the AMPQ or MQTT protocols.

#### Build
Install your lumada sdk into your local dependencies:
```

mvn install:install-file -Dfile=/path/to/java-device-sdk-1.1.0.56.jar -DgroupId=com.hds.lumada -DartifactId=java-device-sdk -Dversion=1.1.0.56 -Dpackaging=jar
```
Then run an install
```
mvn clean install
```

#### Running
```
java -cp /path/to/jar org.hitachivantara.LocationConsumer <assetId>

```

#### Acknowledgement

Based on Lumada package written by Benjamin Morrise
