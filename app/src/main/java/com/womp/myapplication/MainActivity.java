package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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


public class MainActivity extends AppCompatActivity {


    EditText edt_user,edit_password;
    Button btn_login;
    CheckBox salvarAutenticacao;

    public  String user,pass;

    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);

        if (preferences.contains("user") && preferences.contains("pass")){
            user = preferences.getString("user",null);
            pass = preferences.getString("pass",null);
        }

        setContentView(R.layout.activity_main);

        btn_login = findViewById(R.id.btn_login);
        edt_user = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);

        salvarAutenticacao = findViewById(R.id.salvarAutenticacao);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_user.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Login está vazio",Toast.LENGTH_LONG).show();
                    return;
                }
                if(edit_password.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Senha está vazio",Toast.LENGTH_LONG).show();
                    return;
                }

                login();
            }
        });

    }


    // Função sera chamado caso o Sqlite não retorne usuario True
    public void login(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.contains("erro")) {
                            Toast.makeText(getApplicationContext(),"Erro usuario ou senha"+response, Toast.LENGTH_SHORT).show();

                        }else{
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progress_dialog);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            String Array[] = new String[2];
                            Array = response.split(",");
                            String var_name_user = Array[0];
                            String id_login_user = Array[1];
                            String idveiculo = Array[2];
                            System.out.println("Tipo: "+ idveiculo);
                            String usuario = edt_user.getText().toString();
                            String senha = edit_password.getText().toString();
                            SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("nome",var_name_user);
                            editor.putString("idveiculo",idveiculo);
                            if(salvarAutenticacao.isChecked()){
                                editor.putString("user",usuario);
                                editor.putString("pass",senha);
                            }
                            editor.putString("id",id_login_user);
                            editor.commit();
                            Intent intentEnviar = new Intent(MainActivity.this, MenuActivity.class);
                            startActivity(intentEnviar);
                            finish();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>  params = new HashMap<>();
                params.put("username",edt_user.getText().toString());
                params.put("password",edit_password.getText().toString());
                return  params;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }

}