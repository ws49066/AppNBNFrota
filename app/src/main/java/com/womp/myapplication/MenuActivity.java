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
    String iduser;

    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";

    ProgressDialog progressDialogMenuActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        full = (ImageView) findViewById(R.id.full);
        manutencao = (ImageView) findViewById(R.id.manutencao);
        logout = (ImageView) findViewById(R.id.logout) ;
        cadChecklist = (ImageView) findViewById(R.id.cadChecklist) ;

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
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
        progressDialogMenuActivity = new ProgressDialog(MenuActivity.this);
        progressDialogMenuActivity.show();
        progressDialogMenuActivity.setContentView(R.layout.progress_dialog);
        progressDialogMenuActivity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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