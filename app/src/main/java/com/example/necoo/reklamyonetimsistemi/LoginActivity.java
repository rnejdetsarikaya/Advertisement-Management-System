package com.example.necoo.reklamyonetimsistemi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    Button login;
    Button register;
    EditText username;
    EditText password;
    Context ctx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        String apiURL = getResources().getString(R.string.api_url);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url;
                HttpURLConnection urlConnection=null;
                String name = username.getText().toString();
                String pass = password.getText().toString();
                String path = apiURL+"/User?userName="+name+"&password="+pass;
                try {
                    url = new URL(path);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line = bufferedReader.readLine();
                    if(line.length() != 2)
                    {
                        startActivity(new Intent(LoginActivity.this,ApplicationActivity.class));
                        finish();
                    }
                    else
                    {
                        Snackbar.make(v, "Kullanıcı adı veya şifre hatalı..", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    bufferedReader.close();
                } catch (Exception e){
                    Snackbar.make(v, "Giriş başarısız!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    e.printStackTrace();
                }finally {
                    urlConnection.disconnect();
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }
}
