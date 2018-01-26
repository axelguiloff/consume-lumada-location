package org.hitachivantara.lumada;

/**
 * Created by bmorrise on 12/14/17.
 */
public class Asset {
  private String hash;

  public Asset( String hash ) {
    this.hash = hash;
  }

  public String getHash() {
    return hash;
  }

  public void setHash( String hash ) {
    this.hash = hash;
  }

  public String getTopic() {
    return "assets." + hash + ".*";
  }
}
