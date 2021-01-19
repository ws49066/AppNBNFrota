package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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

public class CheckList extends AppCompatActivity {

    private TextView modelo,cor,placa;

    private ArrayList<String> list = new ArrayList<>() ;
    ArrayList<String> selectedItems = new ArrayList<>();
    ArrayList<EditModel> editModelArrayList ;
    private ListView listEdit;
    private CustomAdapterEdit adapterEdit;
    private Button btn_finalizar;
    String idveiculo, tipo,iduser, tipoVeiculo;
    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";
    RadioGroup groupTipoComb;
    RadioButton buttonCombustivel;
    ProgressDialog progressDialog;
    EditText kmatual;
    Button mOrder;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
            idveiculo = preferences.getString("idveiculo", null);
        }

        getDetailsVei();

        groupTipoComb = findViewById(R.id.tipo);
        btn_finalizar = findViewById(R.id.btn_salvar);
        placa = findViewById(R.id.placa);
        kmatual = (EditText) findViewById(R.id.kmatual);
        listEdit = findViewById(R.id.listEdit);
        listEdit.setChoiceMode(listEdit.CHOICE_MODE_MULTIPLE);
        modelo = findViewById(R.id.modelo);
        cor = findViewById(R.id.cor);
        mOrder = (Button) findViewById(R.id.btnOrder);

        getItems("carro");



        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(CheckList.this);
                mBuilder.setTitle("Lista de Chegagem");
                mBuilder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                        if(isChecked){
                            mUserItems.add(position);
                        }else{
                            mUserItems.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        selectedItems.clear();
                        for (int i = 0; i < mUserItems.size(); i++) {
                            selectedItems.add(listItems[mUserItems.get(i)]);
                        }
                        setComment();
                    }
                });

                mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Limpar tudo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mUserItems.clear();
                        }
                        selectedItems.clear();
                        listEdit.setAdapter(null);
                        setComment();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });

        btn_finalizar.setOnClickListener(view -> {
            PreFinalizar();
        });
    }

    public void getItems(String tipo){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try{
            String url = "http://177.91.235.146/frota/mobileapp/items.json";
            System.out.println("tipo: "+tipo);

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
                            listItems = list.toArray(new String[list.size()]);
                            checkedItems = new boolean[listItems.length];

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
    private void getDetailsVei(){
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
                                    System.out.println("Checklist Items tipo: "+tipoVeiculo);
                                    modelo.setText(jsonObject.getString("modelo"));
                                    cor.setText(jsonObject.getString("cor"));
                                    placa.setText(jsonObject.getString("placa"));
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
                params.put("idveiculo",idveiculo.toString());
                return  params;
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
    public void setComment(){

        editModelArrayList = populateList();
        if (selectedItems.isEmpty()){
            System.out.println("Lista Vazia" + selectedItems.toString());
        }else{
            System.out.println("Lista Items" + selectedItems.toString());
            adapterEdit = new CustomAdapterEdit(CheckList.this, selectedItems,editModelArrayList);
            listEdit.setAdapter(null);
            listEdit.setDivider(null);
            listEdit.setAdapter(adapterEdit);
            ConstraintLayout.LayoutParams ledit = (ConstraintLayout.LayoutParams) listEdit.getLayoutParams();
            ledit.height = 70 * selectedItems.size();
        }
    }
    public ArrayList<EditModel> populateList(){

        ArrayList<EditModel> listEdit = new ArrayList<>();


        for (int i=0; i < selectedItems.size() ; i++){
                EditModel editModel = new EditModel();
                editModel.setEditTextValue(String.valueOf(i));
                listEdit.add(editModel);
        }

        return listEdit;
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

    public void PreFinalizar(){
        ArrayList<String> editList = new ArrayList<>();
        if(!selectedItems.isEmpty()){
            for(int i =0; i< editModelArrayList.size(); i++){
                editList.add(editModelArrayList.get(i).getEditTextValue());
            }
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
            if(kmatual.getText().toString().matches("") ){
                Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                progressDialog.hide();
            }else{
                SalvarCheckList(editList);
            }

        }else {
            Toast.makeText(getApplicationContext(),"Dispositivo não está conectado á Internet",Toast.LENGTH_LONG).show();
        }
    }

    public void SalvarCheckList(ArrayList<String> listComments){
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/checklist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Retorno "+response.toString());
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Houve um erro", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
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
                param.put("kmatual",kmatual.getText().toString());
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



}