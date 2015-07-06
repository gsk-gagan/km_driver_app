package com.knowmiles.www.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity {

    EditText rName, rMobile, rCabInfo, rCabProvider, rPassword1, rPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rName = (EditText) findViewById(R.id.edtTxtRegName);
        rMobile = (EditText) findViewById(R.id.edtTxtRegMobile);
        rCabInfo = (EditText) findViewById(R.id.edtTxtRegCabInfo);
        rCabProvider = (EditText) findViewById(R.id.edtTxtRegServProv);
        rPassword1 = (EditText) findViewById(R.id.edtTxtRegPass1);
        rPassword2 = (EditText) findViewById(R.id.edtTxtRegPass2);
    }


    public void registerNewUser(View v) {
        switch (v.getId()) {
            case (R.id.btn_register): {
                String name = rName.getText().toString();
                String mobile = rMobile.getText().toString();
                String cabInfo = rCabInfo.getText().toString();
                String servProv = rCabProvider.getText().toString();
                String passwrd1 = rPassword1.getText().toString();
                String passwrd2 = rPassword2.getText().toString();

                boolean valid = true;

                if(!passwrd1.matches(passwrd2))
                {
                    Toast.makeText(getApplicationContext(),"Passwords don't match, Re-Enter",Toast.LENGTH_LONG).show();
                    valid = false;
                }
                if((name.length()==0)||(mobile.length()==0)||(cabInfo.length()==0)||(servProv.length()==0)||(passwrd1.length()==0))
                {
                    Toast.makeText(getApplicationContext(),"Some of the fields are empty, Re-Enter",Toast.LENGTH_LONG).show();
                    valid = false;
                }

                if(valid)
                {
                    User user = new User(name, mobile,cabInfo,servProv,passwrd1);

                    registerUser(user);
                }
                break;
            }
        }
    }

    private void registerUser(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

}
