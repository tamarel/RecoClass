package com.microsoft.projectoxford.face.samples; /**
 * Created by אמנון on 20/01/2016.
 */

import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseUser;

import android.app.Application;
public class ParseHelper extends Application  {

        @Override
        public void onCreate() {
                super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();


        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}