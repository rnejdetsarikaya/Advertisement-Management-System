package com.example.necoo.reklamyonetimsistemi;

import android.location.Location;
import android.support.design.widget.Snackbar;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NECOO on 15.05.2019.
 */

public class NodeAPI {

    private URL apiURL = null;


    public NodeAPI(URL url) {
        this.apiURL = url;
    }

    public String requestCompanies(String company) {
        String path = apiURL.toString();
        path += "/companies";
        if (!company.equalsIgnoreCase("all"))
            path += "?company=" + company;
        return getRequest(path);
    }

    public String requestCategories() {
        String path = apiURL.toString();
        path += "/category";
        return getRequest(path);
    }

    public String requestCampaigns(String category, String companyName) {
        String path = apiURL.toString();
        path += "/campaigns";

        if (!category.equalsIgnoreCase("all") && companyName.equalsIgnoreCase("all"))
            path += "?category=" + category;
        if (category.equalsIgnoreCase("all") && !companyName.equalsIgnoreCase("all"))
            path += "?company=" + companyName;
        if (!category.equalsIgnoreCase("all") && !companyName.equalsIgnoreCase("all"))
            path += "?category=" + category + "&company=" + companyName;

        //if(category.equalsIgnoreCase("all") && companyName.equalsIgnoreCase("all"))


        return getRequest(path);
    }

    private String getRequest(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<JSONObject> getJsonObjects(String jsonArray) throws Exception {
        jsonArray = jsonArray.substring(1, jsonArray.length() - 1);
        List<JSONObject> list = new ArrayList<>();
        String json;
        for (int i = 0; i < jsonArray.length(); i++) {
            int temp = i;
            i = jsonArray.indexOf("}", i);
            json = jsonArray.substring(temp, i + 1);
            list.add(new JSONObject(json));
            i += 1;
        }
        return list;
    }

    public List<Campaign> getCampaignsList(String jsonStr) {
        System.out.println(jsonStr);
        List<Campaign> list = new ArrayList<Campaign>();
        if (jsonStr == null) {
            Campaign camp = new Campaign();
            camp.setCompanyName("Failed to connect");
            camp.setCompanyID(-1);
            // news.setImage("https://kinsta.com/wp-content/uploads/2017/06/error-establishing-a-database-connection.png");
            camp.setCampaignInfo("Failed to connect to api");
            camp.setCampaignCategory(" ");
            camp.setCampaignDeadLine(" ");
            list.add(camp);
            return list;
        }

        try {

            List<JSONObject> jsonList = getJsonObjects(jsonStr);

            for (JSONObject json : jsonList) {
                Campaign camp = new Campaign();
                camp.setCompanyName(json.getString("FirmaAdi"));
                camp.setCompanyID(json.getInt("FirmaID"));
                camp.setCampaignInfo(json.getString("KampanyaIcerik"));
                camp.setCampaignCategory(json.getString("Kategori"));
                camp.setCampaignDeadLine(json.getString("KampanyaSuresi"));
                camp.setCompanyLocation(json.getString("FirmaLokasyon"));
                list.add(camp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean changePassword(String username, String newpassword) {
        JSONObject jsonParam = new JSONObject();
        HttpURLConnection conn = null;
        String line = "";
        try {
            jsonParam.put("UserName", username);
            jsonParam.put("Password", newpassword);
            System.out.println(jsonParam.toString());
            URL url = new URL(apiURL + "/changePassword"); //in the real code, there is an ip and a port
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            bufferedWriter.write("data=" + jsonParam);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

            line = bufferedReader.readLine();
            System.out.println(line);
            bufferedReader.close();
            inputStream.close();
            if (conn.getResponseCode() == 201) {
                return true;
            }

            conn.disconnect();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
