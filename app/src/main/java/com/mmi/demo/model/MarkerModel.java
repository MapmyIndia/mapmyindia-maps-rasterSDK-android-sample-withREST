package com.mmi.demo.model;

import com.mmi.util.GeoPoint;

/**
 * Created by Mohammad Akram on 03-04-2015.
 */
public class MarkerModel {

  private String title;
  private String imageUrl;
  private String description;
  private String subDescription;

  private GeoPoint geoPoint;


  public MarkerModel(String title, String imageUrl, String description, String subDescription, GeoPoint geoPoint) {
    this.title = title;
    this.imageUrl = imageUrl;
    this.description = description;
    this.subDescription = subDescription;
    this.geoPoint = geoPoint;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getSubDescription() {
    return subDescription;
  }

  public void setSubDescription(String subDescription) {
    this.subDescription = subDescription;
  }

  public GeoPoint getGeoPoint() {
    return geoPoint;
  }

  public void setGeoPoint(GeoPoint geoPoint) {
    this.geoPoint = geoPoint;
  }
}
