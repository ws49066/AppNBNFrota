package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {

    ImageView full,manutencao,cadUser,cadCarro,logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        full = (ImageView) findViewById(R.id.full);
        manutencao = (ImageView) findViewById(R.id.manutencao);
        cadUser = (ImageView) findViewById(R.id.cadUser);
        cadCarro = (ImageView) findViewById(R.id.cadCarro);
        logout = (ImageView) findViewById(R.id.logout) ;

        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,AbastecimentoActivity.class));
            }
        });

        manutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Manutenção",Toast.LENGTH_SHORT).show();
            }
        });

        cadUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this,CadastroUsuario.class));
            }
        });

        cadCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Cadastro Veiculo",Toast.LENGTH_SHORT).show();
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