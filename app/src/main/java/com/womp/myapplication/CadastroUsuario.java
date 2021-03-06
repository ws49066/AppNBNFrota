package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class CadastroUsuario extends AppCompatActivity {

    RadioGroup radioGroup;
    RadioButton radioButton;
    EditText nome,cpf,cnh,funcao,login,senha;
    String tipo;
    Button salvar_cadUser;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_usuario);

        radioGroup = findViewById(R.id.radio_tipoUser);
        nome = (EditText) findViewById(R.id.edit_senha);
        cpf = (EditText) findViewById(R.id.edit_cpf);
        cnh = (EditText) findViewById(R.id.edit_cnh);
        funcao = (EditText) findViewById(R.id.edit_funcao);
        login = (EditText) findViewById(R.id.edit_login);
        senha = (EditText) findViewById(R.id.edit_senha);


        salvar_cadUser = (Button) findViewById(R.id.btn_salvar_cadUser);

        salvar_cadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioID = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(radioID);
                tipo = radioButton.getText().toString();
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                    progressDialog = new ProgressDialog(CadastroUsuario.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    SalvarDadosUsuario();
                }else {
                    Toast.makeText(getApplicationContext(),"Dispositivo não está conectado á Internet",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void SalvarDadosUsuario() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/cadUsuario.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.contains("erro")){
                    Toast.makeText(getApplicationContext(),"Não foi possivel cadastrar usuário",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CadastroUsuario.this, "Usuário cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CadastroUsuario.this,MenuActivity.class));
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
                Map<String,String> params = new HashMap<>();
                params.put("nome",nome.getText().toString());
                params.put("cpf",cpf.getText().toString());
                params.put("cnh",cnh.getText().toString());
                params.put("funcao",funcao.getText().toString());
                params.put("login",login.getText().toString());
                params.put("senha",senha.getText().toString());
                params.put("tipousuario",tipo);
                return params;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }

    public void onRadioButtonClicked(View view) {
       int radioID = radioGroup.getCheckedRadioButtonId();
       radioButton = findViewById(radioID);

    }
    public void exibirConfirmacao(){
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setTitle("Excluindo....");
        msgbox.setIcon(android.R.drawable.ic_menu_delete);
        msgbox.setMessage("Tem certeza que deseja cancelar ?");

        msgbox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(CadastroUsuario.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Intent intentEnviar = new Intent(CadastroUsuario.this, MenuActivity.class);
                startActivity(intentEnviar);
                finish();
            }

        });

        msgbox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        msgbox.show();
    }

    public void onBackPressed(){

        exibirConfirmacao();
    }
}