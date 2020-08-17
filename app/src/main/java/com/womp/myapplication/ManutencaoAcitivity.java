package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.fabiomsr.moneytextview.MoneyTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManutencaoAcitivity extends AppCompatActivity {

    LinearLayout dinamicoLayout;

    TextView modelo,cor;
    EditText kmatual,pecas,servicos,editvalor;

    Button salvar;


    ArrayList<String> placas = new ArrayList<String>();

    String idveiculo;

    ImageView tirafoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao_acitivity);

        getPlacas();

        dinamicoLayout = (LinearLayout) findViewById(R.id.LayoutDinamicoManutencao);
        modelo = (TextView) findViewById(R.id.modeloManu);
        cor = (TextView) findViewById(R.id.corManu);
        kmatual = (EditText) findViewById(R.id.kmatualManu);
        pecas = (EditText) findViewById(R.id.pecas);
        servicos= (EditText) findViewById(R.id.servicos);
        editvalor = findViewById(R.id.editvalor);
        tirafoto = (ImageView) findViewById(R.id.ImgBtn_foto_Manutencao);
        salvar = (Button) findViewById(R.id.btn_salvar_Manutencao);

        pecas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        servicos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalvarManutencao();
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
                                    Spinner spinner = (Spinner) findViewById(R.id.spinnerPlacaManu);

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

    public void SalvarManutencao(){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/controles/manutencao.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Houve um erro", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Cadastro Realizado com Sucesso",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ManutencaoAcitivity.this,MenuActivity.class));
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
                param.put("pecas",pecas.getText().toString());
                param.put("servicos",servicos.getText().toString());
                param.put("valor",editvalor.getText().toString());
                param.put("idveiculo",idveiculo.toString());
                return param;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }


}