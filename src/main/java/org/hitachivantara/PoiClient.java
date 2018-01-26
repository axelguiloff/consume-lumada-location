package org.hitachivantara;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Retrieves POI data from Google Places
 */
public class PoiClient {
  private String apiKey;

  public PoiClient( String apiKey ) {
    this.apiKey = apiKey;
  }

  public void retrievePoiInformation( JsonObject jsonObject ) throws URISyntaxException {
    try {
      // Connect to Google Place API and request POI for location
      URL urlConnection = new URL( buildRequestUrl( jsonObject ) );
      HttpURLConnection request = (HttpURLConnection) urlConnection.openConnection();
      System.out.println( "Retrieving POI Information" );
      request.connect();

      // TODO: Error handling for invalid response

      // Parse and display API results
      displayResults( parseResponse( request ) );
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
    }
  }

  private JsonArray parseResponse( HttpURLConnection request ) throws IOException {
    JsonParser jp = new JsonParser();
    JsonElement root = jp.parse( new InputStreamReader( (InputStream) request.getContent() ) );
    JsonObject rootObj = root.getAsJsonObject();
    return rootObj.getAsJsonArray( "results" );
  }

  private String buildRequestUrl( JsonObject jsonObject ) throws URISyntaxException {
    URIBuilder b = new URIBuilder( "https://maps.googleapis.com/maps/api/place/nearbysearch/json" );
    b.addParameter( "key", this.apiKey );
    b.addParameter( "location", jsonObject.get( "latitude" ) + "," + jsonObject.get( "longitude" ) );
    b.addParameter( "rankby", "distance" );
    b.addParameter( "type", "natural_feature" );

    return b.build().toString();
  }

  private void displayResults( JsonArray results ) {
    System.out.println( "-------------------\nPoints of Interest\n-------------------" );

    if ( results.size() == 0 ) {
      System.out.println( "No locations found" );
      return;
    }

    int i = 0;
    for ( JsonElement loc : results ) {
      i++;
      System.out.println( loc.getAsJsonObject().get( "name" ).toString().replaceAll( "\"", "" ) );
      if ( i > 4 ) {
        break;
      }
    }
  }
}
