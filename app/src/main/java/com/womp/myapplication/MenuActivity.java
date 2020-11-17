package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    ImageView full,manutencao,cadUser,cadCarro,logout,cadChecklist;
    LinearLayout tipouserlayout;
    String iduser, tipouser;

    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        full = (ImageView) findViewById(R.id.full);
        manutencao = (ImageView) findViewById(R.id.manutencao);
        cadUser = (ImageView) findViewById(R.id.cadUser);
        cadCarro = (ImageView) findViewById(R.id.cadCarro);
        logout = (ImageView) findViewById(R.id.logout) ;
        cadChecklist = (ImageView) findViewById(R.id.cadChecklist) ;
        tipouserlayout = findViewById(R.id.tipouser);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
            tipouser = preferences.getString("tipo", null);
        }



        if(tipouser.equals("admin")){
            System.out.println("Tipo de usuario: " + tipouser);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tipouserlayout.getLayoutParams();
            lp.height = 170;
        }

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressseDialog();
                startActivity(new Intent(MenuActivity.this,AbastecimentoActivity.class));
                finish();
            }
        });

        manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressseDialog();
                startActivity(new Intent(MenuActivity.this,ManutencaoAcitivity.class));
                finish();
            }
        });

        cadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressseDialog();
                startActivity(new Intent(MenuActivity.this,CadastroUsuario.class));
                finish();
            }
        });

        cadCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressseDialog();
                startActivity(new Intent(MenuActivity.this,CadastroVeiculo.class));
                finish();
            }
        });

        cadChecklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressseDialog();
                startActivity(new Intent(MenuActivity.this, CheckList.class));
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder msgbox = new AlertDialog.Builder(MenuActivity.this);
                msgbox.setTitle("Logout...");
                msgbox.setIcon(android.R.drawable.ic_lock_idle_alarm);
                msgbox.setMessage("Tem certeza que deseja Deslogar do aplicativo?");
                msgbox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { SharedPreferences preferences = getSharedPreferences("ArquivoAutentica",0);
                        progressseDialog();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.commit();
                        Intent loginview = new Intent(MenuActivity.this,MainActivity.class);
                        startActivity(loginview);
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
        });


    }

    public void progressseDialog(){
        progressDialog = new ProgressDialog(MenuActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void exibirConfirmacao(){
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setTitle("Sair da aplicação....");
        msgbox.setIcon(android.R.drawable.ic_lock_power_off);
        msgbox.setMessage("Tem certeza que deseja sair do aplicativo?");

        msgbox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
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