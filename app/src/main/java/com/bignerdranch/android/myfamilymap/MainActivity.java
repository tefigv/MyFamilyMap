package com.bignerdranch.android.myfamilymap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import requestandresult.*;

public class MainActivity extends AppCompatActivity {
    String gender = "";
    static String serverHost = "";
    static String serverPort = "";String username = "";
    String password = "";String email = "";String firstName = "";String lastName = "";
    EditText p;

    Button registerButton; Button loginButton;

    LoginRequest lRequest;
    LoginResult lResult;
    RegisterRequest rRequest;
    RegisterResult rResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RadioButton female = findViewById(R.id.femaleField);
        RadioButton male = findViewById(R.id.maleField);



        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        registerButton.setEnabled(false);
        loginButton.setEnabled(false);

        enableButtons();

        female.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(female);
            }
        });

        male.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(male);
            }
        });




        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TextView serverhost =  findViewById(R.id.serverHostField);
                serverHost = serverhost.getText().toString();

                TextView serverport = findViewById(R.id.serverPortField);
                serverPort = serverport.getText().toString();

                TextView userName = findViewById(R.id.usernameField);
                username = userName.getText().toString();
               TextView passWord = findViewById(R.id.passwordField);
               password = passWord.getText().toString();


                    Handler uiThreadMessageHandlerLogin = new Handler(Looper.getMainLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            if(bundle.containsKey("error")) {
                                String value = bundle.getString("error", "Key not found");
                                Toast.makeText(MainActivity.this, getString(R.string.wrong_message, value), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String value = bundle.getString("login", "Nothing happened");
                                Toast.makeText(MainActivity.this, getString(R.string.message, value), Toast.LENGTH_SHORT).show();
                            }

                            }
                    };

                    LoginTask login = new LoginTask(uiThreadMessageHandlerLogin);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(login);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                TextView serverhost =  findViewById(R.id.serverHostField);
                serverHost = serverhost.getText().toString();

                TextView serverport = findViewById(R.id.serverPortField);
                serverPort = serverport.getText().toString();

                TextView userName = findViewById(R.id.usernameField);
                username = userName.getText().toString();
                TextView passWord = findViewById(R.id.passwordField);
                password = passWord.getText().toString();

                TextView mail = findViewById(R.id.emailField);
                 email = mail.getText().toString();

                TextView nombre = findViewById(R.id.firstNameField);
                firstName = nombre.getText().toString();
                TextView apellido = findViewById(R.id.lastNameField);
                lastName = apellido.getText().toString();



                Handler uiThreadHandlerRegister = new Handler(Looper.getMainLooper()) {

                @Override
                public void handleMessage (Message message){

                    Bundle bundle = message.getData();
                    if(bundle.containsKey("error")) {
                        String value = bundle.getString("error", "Key not found");
                        Toast.makeText(MainActivity.this, getString(R.string.wrong_message, value), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String value = bundle.getString("register", "Nothing happened");
                        Toast.makeText(MainActivity.this, getString(R.string.message, value), Toast.LENGTH_SHORT).show();
                    }

                }



                };

                RegisterTask register = new RegisterTask(uiThreadHandlerRegister);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(register);
            };
        });

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if(checked){
            registerButton.setEnabled(true);
        }
        switch(view.getId()) {
            case R.id.femaleField:
                if (checked) {gender = "f";}
                break;
            case R.id.maleField:
                if (checked) {gender = "m";}
                break;
        }
    }

    public String storeInput(View view){
       return view.toString();
    }


    public void enableButtons(){

        p = findViewById(R.id.passwordField);

        p.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().trim().length() == 0){
                    loginButton.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    //rRequest = new RegisterRequest(username,password,email,firstName,lastName,gender);




    private class RegisterTask implements Runnable{

        private Handler handler;

        public RegisterTask(Handler handler){
            this.handler = handler;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy(serverHost, serverPort);

            PersonResult personResult = null;
            try {
                rRequest = new RegisterRequest(username,password,email,firstName,lastName,gender);
                rResult = proxy.register(rRequest);

                if(!rResult.isSuccess())
                {
                    throw new Exception();
                }

                personResult = proxy.getPerson();

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("register",(personResult.getFirstName()+ " " + personResult.getLastName()));
                message.setData(bundle);
                handler.sendMessage(message);



            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("error","Invalid");
                message.setData(bundle);
                handler.sendMessage(message);

            }


        }
    }





    private class LoginTask implements Runnable{
        
        private Handler handler;

        public LoginTask(Handler handler){
            this.handler = handler;
        }

        @Override
        public void run() {
            ServerProxy proxy = new ServerProxy(serverHost, serverPort);

            PersonResult personResult = null;
            try {
                lRequest = new LoginRequest(username,password);
                lResult = proxy.login(lRequest);

                if(!lResult.isSuccess())
                {
                    throw new Exception();
                }

                personResult = proxy.getPerson();

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("login",(personResult.getFirstName()+ " " + personResult.getLastName()));
                message.setData(bundle);
                handler.sendMessage(message);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {

                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("error","Invalid");
                message.setData(bundle);
                handler.sendMessage(message);

            }


        }
    }






}