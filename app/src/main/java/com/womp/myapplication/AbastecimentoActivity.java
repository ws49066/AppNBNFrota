package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AbastecimentoActivity extends AppCompatActivity {

    LinearLayout dinamicoLayout;


    RadioGroup groupTipoComb;
    RadioButton buttonCombustivel;

    TextView modelo,cor;
    EditText kmatual,litros,req;

    Button salvar;

    ArrayList<String> placas = new ArrayList<String>();

    String combustivel,idveiculo;

    ImageView tirafoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastecimento);

        getPlacas();
        groupTipoComb = findViewById(R.id.tipodecombustivel);

        dinamicoLayout = (LinearLayout) findViewById(R.id.LayoutDinamico);
        modelo = (TextView) findViewById(R.id.modelo);
        cor = (TextView) findViewById(R.id.cor);
        kmatual = (EditText) findViewById(R.id.kmatual);
        litros = (EditText) findViewById(R.id.litros);
        req = (EditText) findViewById(R.id.numeroRequisicao);
        tirafoto = (ImageView) findViewById(R.id.ImgBtn_foto);
        salvar = (Button) findViewById(R.id.btn_salvar);

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int radioIDComb = groupTipoComb.getCheckedRadioButtonId();
                buttonCombustivel = findViewById(radioIDComb);
                combustivel = buttonCombustivel.getText().toString();
                if (combustivel.equals("Gas.")){
                    combustivel = "Gasolina";
                }
                SalvarAbastecimento();
            }
        });

    }

    public void getPlacas() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/controles/getPlacas.php",
                new Response.Listener<String>() {
                    JSONArray arrayplacas = new JSONArray();
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            Toast.makeText(getApplicationContext(),"VAZIO", Toast.LENGTH_SHORT).show();
                        }else{
                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.isNull("placas")){

                                }else{
                                    arrayplacas = obj.getJSONArray("placas");
                                    for (int i=0; i< arrayplacas.length(); i++){
                                        placas.add(arrayplacas.getString(i));
                                    }
                                    Spinner spinner = (Spinner) findViewById(R.id.spinnerPlaca);

                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_text,placas);
                                    spinner.setAdapter(adapter);

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            Object item = parent.getItemAtPosition(position);
                                            if (position != 0){
                                                getDetailsVei(item.toString());
                                                modelo.setText("");
                                                cor.setText("");
                                            }else{
                                                dinamicoLayout.setVisibility(View.INVISIBLE);
                                            }

                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });



                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }

    private void getDetailsVei(String PlacaSelecionada){
            StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/controles/getDadosVeiculos.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.contains("erro")) {
                            }else{
                                try {
                                    JSONObject obj = new JSONObject(response);

                                    if (obj.isNull("modelo")){

                                    }
                                    else{
                                        idveiculo = obj.getString("id");
                                        modelo.setText(obj.getString("modelo"));
                                        cor.setText(obj.getString("cor"));
                                        dinamicoLayout.setVisibility(View.VISIBLE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
                    params.put("placa",PlacaSelecionada);
                    return  params;
                }
            };
            dinamicoLayout.setVisibility(View.INVISIBLE);
            RequestQueue fila = Volley.newRequestQueue(this);
            fila.add(request);
    }

    public void SalvarAbastecimento(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/controles/abastecimento.php",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("erro")){
                    Toast.makeText(getApplicationContext(),"Houve um erro", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Cadastro Realizado com Sucesso",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AbastecimentoActivity.this,MenuActivity.class));
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
                Map<String, String> param = new HashMap<>();
                param.put("kmatual",kmatual.getText().toString());
                param.put("combustivel",combustivel);
                param.put("litros",litros.getText().toString());
                param.put("requesicao",req.getText().toString());
                param.put("idveiculo",idveiculo.toString());
                return param;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }


    public void CombustivelRadio(View view){
        //get id
        int radioIDComb = groupTipoComb.getCheckedRadioButtonId();

        buttonCombustivel = findViewById(radioIDComb);
    }

}
