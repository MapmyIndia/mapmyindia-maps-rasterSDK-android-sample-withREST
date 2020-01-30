package com.mmi.demo.util;

import com.mmi.util.BoundingBoxE6;
import com.mmi.util.GeoPoint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mohammad Akram on 28-11-2014.
 */

public class CoordinateValidator {
  static BoundingBoxE6 indianBoundingBox = new BoundingBoxE6(38.822591,
    99.887695, 3.688855, 65.566406);

  /**
   * This performs the validation of the coordinates based on the regex.
   *
   * @param latLongValue Coordinate parameters as string value separated by a Comma, like 28.9082,77.1224
   * @return A GeoPoint object if the asked coordinate is valid. Null, otherwise.
   */
  public static GeoPoint isCoordinateValid(String latLongValue) {

    if (latLongValue != null && latLongValue.trim().length() > 0) {
      String regex = "^([-+]?\\d{1,2}([.]\\d+)?),\\s*([-+]?\\d{1,3}([.]\\d+)?)$";
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(latLongValue);
      if (matcher.matches()) {
        /**
         * If it has reached this far, it means Coordinate is valid.
         * Now checking if it's a valid Indian Coordinate
         */
        String[] strings = latLongValue.split(",");
        double lat = Double.valueOf(strings[0]);
        double lng = Double.valueOf(strings[1]);
        GeoPoint geoPoint = new GeoPoint(lat, lng);
        return indianBoundingBox.contains(geoPoint) ? geoPoint : null;
      }
    }
    return null;
  }
}
