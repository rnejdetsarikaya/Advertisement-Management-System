package com.example.necoo.reklamyonetimsistemi;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText username;
    EditText password;
    String apiURL;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.register1);
        username = findViewById(R.id.username1);
        password = findViewById(R.id.password1);

        apiURL = getResources().getString(R.string.api_url);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonParam = new JSONObject();
                HttpURLConnection conn=null;
                String line="";
                try{
                    jsonParam.put("UserName",username.getText());
                    jsonParam.put("Password",password.getText());
                    System.out.println(jsonParam.toString());
                    URL url = new URL(apiURL+"/addUser"); //in the real code, there is an ip and a port
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();
                    OutputStream outputStream = conn.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    bufferedWriter.write("data="+jsonParam);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                    line = bufferedReader.readLine();
                    bufferedReader.close();
                    inputStream.close();
                    if(conn.getResponseCode() == 201){
                        Snackbar.make(v, "Kayıt Başarılı."+"-"+conn.getResponseCode(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        finish();
                    }

                    conn.disconnect();
                }catch (Exception e){
                    System.out.println("hgv");
                    System.out.println(line);
                    try{
                        Snackbar.make(v, "Bu kullanıcı adı zaten mevcut.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
            }
        });

    }
}
