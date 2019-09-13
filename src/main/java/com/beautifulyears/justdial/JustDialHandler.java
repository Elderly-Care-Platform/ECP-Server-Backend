package com.beautifulyears.justdial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beautifulyears.domain.JustdialToken;
import com.beautifulyears.util.LoggerUtil;

public class JustDialHandler {

    private static final Logger logger = Logger.getLogger(JustDialHandler.class);

    private String baseUrl = "http://win.justdial.com/tata-v1";
    private String createToken = "/createToken.php";
    private String justDialsearch = "/searchziva.php?";
    private String categories= "/catid_list.php";
    private String city = "Hyderabad";

    private String JDcase = "spcall";
    private String JDdetailCase = "detail";
    private int JDversion = 9;
    private String stype = "category_list";
    private String searchstype = "company_list";
    private int wap = 2;

    private String createToken() {
        LoggerUtil.logEntry();
        String response = null;
        try {

            String postUrl = this.baseUrl + this.createToken;

            logger.debug(postUrl);

            URL u = new URL(postUrl);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            response = b.toString();
            logger.debug(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in getting token from Justdial. " + e);
        }
        return response;
    }

    public JustdialToken getNewToken() throws IOException {
        LoggerUtil.logEntry();
        String tokenResponse = this.createToken();
        JustdialToken JDtoken = new JustdialToken();
        try {
            JSONObject json = new JSONObject(tokenResponse);

            /*
             * { "token": "lkjsdlfkjlas", "expires": 1000 }
             */
            JDtoken.setToken(json.getString("token"));
            JDtoken.setExpires(json.getLong("expires"));
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in parsing token data. " + e);
        }
        return JDtoken;

    }

    public JSONObject getServiceList(String token, String category, Integer catID, int max, int pageNo) {
        LoggerUtil.logEntry();
        String response = null;
        JSONObject resultsObject = new JSONObject();
        try {
            String postUrl = this.baseUrl + this.justDialsearch;

            // Prepare parameter string
            StringBuilder sbPostData = new StringBuilder(postUrl);
            sbPostData.append("city=" + this.city);
            sbPostData.append("&case=" + this.JDcase);
            sbPostData.append("&stype=" + this.stype);
            sbPostData.append("&search=" + category);
            sbPostData.append("&national_catid=" + catID);
            sbPostData.append("&max=" + max);
            sbPostData.append("&pg_no=" + pageNo);
            sbPostData.append("&wap=" + this.wap);

            // final string
            postUrl = sbPostData.toString();

            logger.debug(postUrl);

            URL u = new URL(postUrl);
            String type = "application/json";
            String Bearertoken = "Bearer " + token;
            String encodedData = sbPostData.toString();
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", type);
            conn.setRequestProperty("Authorization", Bearertoken);
            // conn.setRequestProperty("Content-Length",
            // String.valueOf(encodedData.length()));
            OutputStream os = conn.getOutputStream();
            os.write(encodedData.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            response = b.toString();
            JSONObject json = new JSONObject(response);
            resultsObject = json;

            // result = resultsObject.toString();

            logger.debug(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in search from Justdial. " + e);
        }
        return resultsObject;

    }

    public String getServiceDetail(String token, String service, String docID) {
        LoggerUtil.logEntry();
        String response = null;
        String result = null;
        try {
            String postUrl = this.baseUrl + this.justDialsearch;

            // Prepare parameter string
            StringBuilder sbPostData = new StringBuilder(postUrl);
            sbPostData.append("search=" + URLEncoder.encode(service, "UTF-8"));
            sbPostData.append("&docid=" + docID);
            sbPostData.append("&case=" + this.JDdetailCase);
            sbPostData.append("&city=" + this.city);
            sbPostData.append("&wap=" + this.wap);
            sbPostData.append("&version=" + this.JDversion);

            // final string
            postUrl = sbPostData.toString();

            logger.debug(postUrl);

            URL u = new URL(postUrl);
            String type = "application/json";
            String Bearertoken = "Bearer " + token;
            String encodedData = sbPostData.toString();
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", type);
            conn.setRequestProperty("Authorization", Bearertoken);
            // conn.setRequestProperty("Content-Length",
            // String.valueOf(encodedData.length()));
            OutputStream os = conn.getOutputStream();
            os.write(encodedData.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            response = b.toString();
            JSONObject json = new JSONObject(response);
            JSONObject resultsObject = json.getJSONObject("results");

            result = resultsObject.toString();

            logger.debug(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in search from Justdial. " + e);
        }
        return result;

    }

    public JSONObject getSearchServiceList(String token, String search, int max, int pageNo) {
        LoggerUtil.logEntry();
        String response = null;
        JSONObject resultsObject = new JSONObject();
        try {
            String postUrl = this.baseUrl + this.justDialsearch;

            // Prepare parameter string
            StringBuilder sbPostData = new StringBuilder(postUrl);
            sbPostData.append("city=" + this.city);
            sbPostData.append("&case=" + this.JDcase);
            sbPostData.append("&stype=" + this.searchstype);
            sbPostData.append("&search=" + URLEncoder.encode(search, "UTF-8"));
            sbPostData.append("&max=" + max);
            sbPostData.append("&pg_no=" + pageNo);
            sbPostData.append("&wap=" + this.wap);

            // final string
            postUrl = sbPostData.toString();

            logger.debug(postUrl);

            URL u = new URL(postUrl);
            String type = "application/json";
            String Bearertoken = "Bearer " + token;
            String encodedData = sbPostData.toString();
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", type);
            conn.setRequestProperty("Authorization", Bearertoken);
            // conn.setRequestProperty("Content-Length",
            // String.valueOf(encodedData.length()));
            OutputStream os = conn.getOutputStream();
            os.write(encodedData.getBytes());
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            response = b.toString();
            JSONObject json = new JSONObject(response);
            resultsObject = json;

            // result = resultsObject.toString();

            logger.debug(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in search from Justdial. " + e);
        }
        return resultsObject;

    }

    public String getServiceCategories(String token) {
        LoggerUtil.logEntry();
        String response = null;
        String result = null;
        try {
            String postUrl = this.baseUrl + this.categories;

            // final string

            URL u = new URL(postUrl);
            String type = "application/json";
            String Bearertoken = "Bearer " + token;
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", type);
            conn.setRequestProperty("Authorization", Bearertoken);
            // conn.setRequestProperty("Content-Length",
            // String.valueOf(encodedData.length()));
            // OutputStream os = conn.getOutputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            response = b.toString();
            JSONObject json = new JSONObject(response);
            JSONArray resultsObject = json.getJSONArray("results");

            result = resultsObject.toString();

            logger.debug(response);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ERROR in search from Justdial. " + e);
        }
        return result;

    }

}