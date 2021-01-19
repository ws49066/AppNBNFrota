package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";

    public  String user,pass, tipo;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                if (preferences.contains("user") && preferences.contains("pass")){
                    user = preferences.getString("user",null);
                    pass = preferences.getString("pass",null);
                    autenticado();
                }
                else{
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                    finish();
                }
            }
        }, 3000);
    }

    public void autenticado(){

        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("erro")) {
                            Intent intentEnviar = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intentEnviar);
                            finish();
                        }else{

                            Intent intentEnviar = new Intent(SplashActivity.this, MenuActivity.class);
                            startActivity(intentEnviar);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
                finish();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>  params = new HashMap<>();
                params.put("username",user);
                params.put("password",pass);
                return  params;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }
}