package com.example.necoo.reklamyonetimsistemi;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PasswordChangeActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText newpassword;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        username = findViewById(R.id.username2);
        password = findViewById(R.id.password2);
        newpassword = findViewById(R.id.newpassword);
        save = findViewById(R.id.save);
        String apiURL = getResources().getString(R.string.api_url);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL url;
                HttpURLConnection urlConnection = null;
                String name = username.getText().toString();
                String pass = password.getText().toString();
                String newpass = newpassword.getText().toString();
                String path = apiURL + "/User?userName=" + name + "&password=" + pass;
                try {
                    url = new URL(path);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line = bufferedReader.readLine();
                    if (line.length() != 2) {
                        changePassword(name,newpass);
                        finish();
                    } else {
                        Snackbar.make(v, "Kullanıcı adı veya şifre hatalı..", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    bufferedReader.close();
                } catch (Exception e) {
                    Snackbar.make(v, "İşlem başarısız!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
            }
        });
    }

    public void changePassword(String username, String newpassword) {
        try {
            NodeAPI nodeAPI = new NodeAPI(new URL(getString(R.string.api_url)));
            if(nodeAPI.changePassword(username, newpassword))
                Toast.makeText(getApplicationContext(), "İşlem Başarılı.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "İşlem Başarısız.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
