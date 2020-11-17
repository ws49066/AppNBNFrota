package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckList extends AppCompatActivity {

    private TextView modelo,cor;
    ScrollView scrollList1;
    ListView listScroll1;
    private ArrayList<String> list = new ArrayList<>() ;
    ArrayList<String> selectedItems = new ArrayList<>();
    ArrayList<EditModel> editModelArrayList;
    ArrayList<String> placas = new ArrayList<>();
    private ListView listView,listEdit;
    private CustomAdapter adapter;
    private CustomAdapterEdit adapterEdit;
    private Button buttonCheck,btn_finalizar;
    private LinearLayout dinamicoLayout, btn_layout;
    String idveiculo, tipo,iduser, tipoVeiculo;
    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";
    RadioGroup groupTipoComb;
    RadioButton buttonCombustivel;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
        }

        groupTipoComb = findViewById(R.id.tipo);
        scrollList1 = findViewById(R.id.scrollList1);
        btn_finalizar = findViewById(R.id.btn_finalizar);
        dinamicoLayout = findViewById(R.id.LayoutDinamico);
        buttonCheck = findViewById(R.id.btn_check);
        listView = findViewById(R.id.listview);
        listEdit = findViewById(R.id.listEdit);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listEdit.setChoiceMode(listEdit.CHOICE_MODE_MULTIPLE);
        btn_layout = findViewById(R.id.btn_layout);
        modelo = findViewById(R.id.modelo);
        cor = findViewById(R.id.cor);

        getPlacas();

        buttonCheck.setOnClickListener(view -> {
                setComment();
        });


        btn_finalizar.setOnClickListener(view -> {
            PreFinalizar();
        });
    }

    public void PreFinalizar(){
        ArrayList<String> editList = new ArrayList<>();
        for(int i =0; i< editModelArrayList.size(); i++){
            editList.add(editModelArrayList.get(i).getEditTextValue());
        }
        int radioIDComb = groupTipoComb.getCheckedRadioButtonId();
        buttonCombustivel = findViewById(radioIDComb);
        tipo = buttonCombustivel.getText().toString();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            progressDialog = new ProgressDialog(CheckList.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            SalvarCheckList(editList);
        }else {
            Toast.makeText(getApplicationContext(),"Dispositivo não está conectado á Internet",Toast.LENGTH_LONG).show();
        }
    }

    public void exibirConfirmacao(){
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setTitle("Excluindo....");
        msgbox.setIcon(android.R.drawable.ic_menu_delete);
        msgbox.setMessage("Tem certeza que deseja cancelar ?");

        msgbox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(CheckList.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Intent intentEnviar = new Intent(CheckList.this, MenuActivity.class);
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

    public void getItems(String tipo){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try{
            String url = "http://177.91.235.146/frota/mobileapp/items.json";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray items =  response.getJSONArray(tipo);
                        ArrayList<String> arrayList = new ArrayList<String>();

                        list.clear();
                        if(items != null){
                            for(int i=0; i< items.length();i++){
                                list.add(items.getString(i));
                            }
                            adapter = new CustomAdapter(CheckList.this, list);
                            listView.setAdapter(null);
                            selectedItems.clear();
                            listEdit.setAdapter(null);
                            listView.setAdapter(adapter);
                            LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) scrollList1.getLayoutParams();
                            LinearLayout.LayoutParams ll1 = (LinearLayout.LayoutParams) listView.getLayoutParams();
                            ll1.height = 72 * list.size();
                            lp1.height = 74 * list.size();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void setComment(){
        editModelArrayList = populateList();
        if (selectedItems.isEmpty()){
            System.out.println("Lista Vazia" + selectedItems.toString());
        }else{
            System.out.println("Lista Items" + selectedItems.toString());
            adapterEdit = new CustomAdapterEdit(CheckList.this, selectedItems,editModelArrayList);
            listEdit.setAdapter(null);
            listEdit.setAdapter(adapterEdit);
            btn_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            LinearLayout.LayoutParams ledit = (LinearLayout.LayoutParams) listEdit.getLayoutParams();
            ledit.height = 72 * selectedItems.size();
        }
    }

    public ArrayList<EditModel> populateList(){
        selectedItems.clear();
        ArrayList<EditModel> listEdit = new ArrayList<>();
        boolean isSelected[] = adapter.getSelectedFlags();


        for (int i=0; i < isSelected.length; i++){
            if(isSelected[i]){
                EditModel editModel = new EditModel();
                editModel.setEditTextValue(String.valueOf(i));
                listEdit.add(editModel);
                selectedItems.add(list.get(i));
            }
        }

        return listEdit;
    }

    public void getPlacas() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/getPlacas.php",
                new Response.Listener<String>() {
                    JSONArray arrayplacas = new JSONArray();
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            Toast.makeText(getApplicationContext(),"VAZIO", Toast.LENGTH_SHORT).show();
                        }else{
                            placas.add("");
                            try {
                                JSONObject obj = new JSONObject(response);

                                if (obj.isNull("placas")){

                                }else{
                                    arrayplacas = obj.getJSONArray("placas");
                                    for (int i=0; i< arrayplacas.length(); i++){
                                        JSONObject jsonObject = arrayplacas.getJSONObject(i);
                                        String placa = jsonObject.getString("placa");
                                        placas.add(placa);
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
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/getDadosVeiculos.php",
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
                                    JSONArray arrayplacas ;
                                    arrayplacas = obj.getJSONArray("modelo");
                                    JSONObject jsonObject = arrayplacas.getJSONObject(0);
                                    idveiculo = jsonObject.getString("idveiculo");
                                    tipoVeiculo = jsonObject.getString("tipo");
                                    getItems(tipoVeiculo);
                                    modelo.setText(jsonObject.getString("modelo"));
                                    cor.setText(jsonObject.getString("cor"));
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

    public void SalvarCheckList(ArrayList<String> listComments){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/checklist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Houve um erro", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Cadastro Realizado com Sucesso",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CheckList.this,MenuActivity.class));
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
                param.put("idveiculo",idveiculo);
                param.put("tipo",tipo);
                param.put("iduser",iduser);
                param.put("listItems",selectedItems.toString());
                param.put("listComments", listComments.toString());
                System.out.println("Lista Commentes: "+ listComments. toString());
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