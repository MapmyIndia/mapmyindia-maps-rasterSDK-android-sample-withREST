package com.mmi.demo;

import android.app.Application;

import com.mmi.services.account.MapmyIndiaAccountManager;

/**
 * Created by CE on 29/09/15.
 */
public class DemoApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    MapmyIndiaAccountManager.getInstance().setRestAPIKey(getRestAPIKey());
    MapmyIndiaAccountManager.getInstance().setMapSDKKey(getMapSDKKey());
    MapmyIndiaAccountManager.getInstance().setAtlasClientId(getAtlasClientId());
    MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(getAtlasClientSecret());
    MapmyIndiaAccountManager.getInstance().setAtlasGrantType(getAtlasGrantType());


  }


}
