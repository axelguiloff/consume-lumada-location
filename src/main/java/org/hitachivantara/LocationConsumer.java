package org.hitachivantara;

import com.google.gson.JsonObject;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.hitachivantara.lumada.Asset;
import org.hitachivantara.lumada.AssetConnector;
import org.hitachivantara.lumada.LumadaConnector;

/**
 * Initiates AMQP connection to Lumada and listens for incoming
 * location data messages. Based on bmorrise's AMQPConsumer used
 * for the Lumada demo.
 */

public class LocationConsumer {
  private static final String LUMADA_HOST = "localhost";
  private static final int LUMADA_PORT = 5671;
  private static final String CONSUMER = "consumer";
  private static final String LUMADA_USERNAME = "";
  private static final String LUMADA_PASSWORD = "";
  private static final boolean USE_SSL = true;
  private static final String GOOGLE_API_KEY = "";

  private static Logger logger = Logger.getLogger( LocationConsumer.class );
  private Properties properties = new Properties();

  public static void main( String[] args ) throws Exception {
    System.out.println( "Starting consumer" );

    BasicConfigurator.configure();

    LocationConsumer locationConsumer = new LocationConsumer();
    locationConsumer.connect( args[0] );
  }

  private void connect( String assetID ) throws Exception {
    System.out.println( "Starting connect" );
    loadProperties();

    String lumadaHost = properties.getProperty( "LUMADA_HOST", LUMADA_HOST );
    System.out.println( "Connecting to " + lumadaHost );
    logger.info( "Connecting to " + lumadaHost );

    LumadaConnector lumadaConnector = new LumadaConnector.Builder()
            .host( lumadaHost )
            .port( Integer.valueOf( properties.getProperty( "LUMADA_PORT", String.valueOf( LUMADA_PORT ) ) ) )
            .virtualHost( CONSUMER )
            .username( properties.getProperty( "LUMADA_USERNAME", LUMADA_USERNAME ) )
            .password( properties.getProperty( "LUMADA_PASSWORD", LUMADA_PASSWORD ) )
            .ssl( Boolean.valueOf( properties.getProperty( "USE_SSL", String.valueOf( USE_SSL ) ) ) ).build();
    lumadaConnector.init();

    Asset asset = new Asset( assetID );

    lumadaConnector.connect( asset, new AssetConnector() {
      @Override
      public void handleBody( JsonObject jsonObject ) {
        System.out.println( "====================" );
        System.out.println( "Message received" );
        System.out.println( "Location: " + jsonObject.get( "latitude" ) + "," + jsonObject.get( "longitude" ) );

        // Retrieve POI suggestions for received location data
        PoiClient poiClient = new PoiClient( properties.getProperty( "GOOGLE_API_KEY", GOOGLE_API_KEY ) );
        try {
          poiClient.retrievePoiInformation( jsonObject );
        } catch ( URISyntaxException exception ) {
          exception.printStackTrace();
        }

      }

      public String getConsumerTag() {
        return "lumadademo";
      }

    } );

  }

  private void loadProperties() {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream( "connection.properties" );
      properties.load( inputStream );
    } catch ( IOException ieo ) {
      ieo.printStackTrace();
    } finally {
      if ( inputStream != null ) {
        try {
          inputStream.close();
        } catch ( IOException ioe ) {
          ioe.printStackTrace();
        }
      }
    }
  }
}
