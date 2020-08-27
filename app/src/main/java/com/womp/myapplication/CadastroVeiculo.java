package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class CadastroVeiculo extends AppCompatActivity {

    RadioGroup groupTipoVeiculo,groupTipoComb;
    RadioButton buttonVeiculo,buttonCombustivel;
    String tipoVeiculo,tipoComb;
    EditText marca,modelo,cor,ano,placa,chassis,roda;

    Button btn_salvarVeiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_veiculo);

        groupTipoComb = findViewById(R.id.tipodecombustivel);
        groupTipoVeiculo =findViewById(R.id.tipodeveiculos);
        marca = findViewById(R.id.edit_marca);
        modelo = findViewById(R.id.edit_modelo);
        cor = findViewById(R.id.edit_cor);
        ano = findViewById(R.id.edit_ano);
        placa = findViewById(R.id.edit_placa);
        chassis = findViewById(R.id.edit_chassis);
        roda = findViewById(R.id.edit_roda);

        btn_salvarVeiculo = findViewById(R.id.btn_salvar_cadCarro);

        btn_salvarVeiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioIDCarro = groupTipoVeiculo.getCheckedRadioButtonId();
                int radioIDComb = groupTipoComb.getCheckedRadioButtonId();
                buttonVeiculo = findViewById(radioIDCarro);
                buttonCombustivel = findViewById(radioIDComb);
                tipoVeiculo = buttonVeiculo.getText().toString();
                tipoComb = buttonCombustivel.getText().toString();
                if (tipoComb.equals("Gas.")){
                    tipoComb = "Gasolina";
                }
                SalvarDadosCarro();
            }
        });
    }



    public void SalvarDadosCarro(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/controles/cadVeiculo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.contains("erro")){
                            Toast.makeText(getApplicationContext(),"Houve um erro",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Veículo cadastrado com Sucesso",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CadastroVeiculo.this,MenuActivity.class));
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
                Map<String, String> params = new HashMap<>();
                params.put("tipoVeiculo",tipoVeiculo);
                params.put("marca",marca.getText().toString());
                params.put("modelo",modelo.getText().toString());
                params.put("cor",cor.getText().toString());
                params.put("ano",ano.getText().toString());
                params.put("placa",placa.getText().toString());
                params.put("chassis",chassis.getText().toString());
                params.put("roda",roda.getText().toString());
                params.put("tipoComb",tipoComb);
                return params;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }

    public void radioCombustivel(View view){
        //get id
        int radioIDComb = groupTipoComb.getCheckedRadioButtonId();
        buttonCombustivel = findViewById(radioIDComb);
    }

    public void radioVeiculo(View view){
        //get id
        int radioIDCarro = groupTipoVeiculo.getCheckedRadioButtonId();
        buttonVeiculo = findViewById(radioIDCarro);

    }

}