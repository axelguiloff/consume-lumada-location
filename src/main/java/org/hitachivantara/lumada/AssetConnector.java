package org.hitachivantara.lumada;

import com.google.gson.JsonObject;

import java.io.IOException;

/**
 * Created by bmorrise on 12/14/17.
 */
public abstract class AssetConnector {
  public abstract void handleBody( JsonObject jsonObject );
  public abstract String getConsumerTag();
}
