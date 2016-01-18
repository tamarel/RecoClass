package com.microsoft.projectoxford.face.samples;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RecoClassActivity extends ActionBarActivity {


    private Button signButton,registerNowButton;
    private TextView userName,password,helloGuest;
    private EditText passwordField,userNameField;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco_class);


    }


    private void  init (){
        signButton = (Button)findViewById(R.id.signButton);
        registerNowButton = (Button)findViewById(R.id.registerButton);
        userName = (TextView)findViewById(R.id.userNameLable);
        password = (TextView)findViewById(R.id.passwordLable);
        helloGuest =(TextView)findViewById(R.id.helloGuestLable);
        passwordField=(EditText)findViewById(R.id.passwordFieldText);

        userNameField=(EditText)findViewById(R.id.userNameFieldText);

        logo=(ImageView)findViewById(R.id.logo);



    }
}
