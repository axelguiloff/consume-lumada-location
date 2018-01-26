package org.hitachivantara.lumada;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * Created by bmorrise on 12/14/17.
 */
public class LumadaConnector {

  public static final String EXCHANGE = "lumada";
  private String host;
  private int port;
  private String virtualHost;
  private String username;
  private String password;
  private boolean ssl;
  private Channel channel;

  protected LumadaConnector( String host, int port, String virtualHost, String username, String password, boolean ssl ) {
    this.host = host;
    this.port = port;
    this.virtualHost = virtualHost;
    this.username = username;
    this.password = password;
    this.ssl = ssl;
  }

  public void init() throws NoSuchAlgorithmException, KeyManagementException, IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost( host );
    factory.setPort( port );
    factory.setVirtualHost( virtualHost );
    factory.setUsername( username );
    factory.setPassword( password );
    if ( this.ssl ) {
      factory.useSslProtocol( "TLSv1.2" );
    }
    Connection connection = factory.newConnection();
    channel = connection.createChannel();
  }

  public static class Builder {
    private String host;
    private int port;
    private String virtualHost;
    private String username;
    private String password;
    private boolean ssl = false;

    public Builder host( String host ) {
      this.host = host;
      return this;
    }

    public Builder port( int port ) {
      this.port = port;
      return this;
    }

    public Builder virtualHost( String virtualHost ) {
      this.virtualHost = virtualHost;
      return this;
    }

    public Builder username( String username ) {
      this.username = username;
      return this;
    }

    public Builder password( String password ) {
      this.password = password;
      return this;
    }

    public Builder ssl( boolean ssl ) {
      this.ssl = ssl;
      return this;
    }

    public LumadaConnector build() {
      return new LumadaConnector( host, port, virtualHost, username, password, ssl );
    }
  }

  public void connect( Asset asset, final AssetConnector assetConnector ) throws Exception {
    if ( channel == null ) {
      throw new Exception( "Channel has not been created yet!" );
    }

    channel.basicConsume( asset.getTopic(), true, assetConnector.getConsumerTag(), new DefaultConsumer( channel ) {
      @Override
      public void handleDelivery( String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body
      ) throws IOException {
        String message = new String( body );
        try {
          JsonParser jsonParser = new JsonParser();
          JsonObject jsonObject = jsonParser.parse( message ).getAsJsonObject();
          assetConnector.handleBody( jsonObject.get( "data" ).getAsJsonObject() );
        } catch ( Exception e ) {
          e.printStackTrace();
        }
      }

      @Override
      public void handleConsumeOk(String consumerTag) {
        System.out.println( "Subscribed to topic: " + asset.getTopic() );
      }
    } );
  }
}
