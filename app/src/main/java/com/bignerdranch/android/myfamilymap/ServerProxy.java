package com.bignerdranch.android.myfamilymap;

import com.google.gson.Gson;

import java.io.*;
import java.net.*;

import requestandresult.*;



public class ServerProxy {

    public ServerProxy(){};
    static String serverHost;
    static String serverPort;
    String authToken = "";
    String personId = "";

    public ServerProxy(String serverHost, String serverPort){
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
    }
    public RegisterResult register(RegisterRequest request){
        RegisterResult result = null;
        Gson gson = new Gson();

        String json = gson.toJson(request,RegisterRequest.class);

        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.setRequestProperty("Accept","application/json");
            http.connect();

            OutputStream outputStream = http.getOutputStream();

            writeString(json,outputStream);

            outputStream.close();

            InputStream resBody = null;

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Route successfully claimed.");
                resBody = http.getInputStream();

            }else {
                System.out.println(http.getResponseMessage());
                resBody = http.getErrorStream();

            }

            String resData = readString(resBody);
            result = gson.fromJson(resData, RegisterResult.class);

            authToken = result.getAuthToken();
            personId = result.getPersonID();

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public LoginResult login(LoginRequest request){
        LoginResult result = null;
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);

            http.setRequestProperty("Accept","application/json");

            http.connect();

            Gson gson = new Gson();
            String reqData = gson.toJson(request);
            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();

            InputStream resBody = null;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Route successfully claimed.");
                resBody = http.getInputStream();

            }else {
                System.out.println(http.getResponseMessage());
                resBody = http.getErrorStream();

            }
            String resData = readString(resBody);
            result = gson.fromJson(resData, LoginResult.class);

            authToken = result.getAuthToken();
            personId = result.getPersonID();


        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            e.getLocalizedMessage();
        }
        return result;
    }

    public PersonResult getPerson() throws IOException {
        
        HttpURLConnection http = null;
        PersonResult result = null;
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person" + "/" +personId);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.connect();

            Gson gson = new Gson();

            InputStream resBody = null;
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                resBody = http.getInputStream();

            }else{
                System.out.println(http.getResponseMessage());
                resBody = http.getErrorStream();
            }

            String resData = readString(resBody);
            result = gson.fromJson(resData, PersonResult.class);

        }catch (MalformedURLException e){
            System.out.println(http.getResponseMessage());
            InputStream respBody = http.getErrorStream();
            String respData = readString(respBody);
            System.out.println(respData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return result;
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
