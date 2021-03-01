package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckList extends AppCompatActivity {

    private TextView modelo,marca,placa;

    private ArrayList<String> list = new ArrayList<>() ;
    ArrayList<String> selectedItems = new ArrayList<>();
    ArrayList<EditModel> editModelArrayList ;
    private ListView listEdit;
    private CustomAdapterEdit adapterEdit;
    private Button btn_finalizar;
    String idveiculo, tipo,iduser,tipovei;
    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";
    RadioGroup groupTipoComb;
    RadioButton buttonCombustivel;
    ProgressDialog progressDialog;
    EditText kmatual;
    Button mOrder;
    String[] listItems;
    boolean[] checkedItems;
    ArrayList<Integer> mUserItems = new ArrayList<>();
    ImageView tirafoto;
    Bitmap bitimagem ;
    Integer cont;
    List<String> ImagensStringList = new ArrayList<>(); // nome da foto tirada
    List<File> CaminhosFotos = new ArrayList<File>(); // caminho da foto na Raiz Celular
    String NomeFotoTirada,mCurrentPhotoPath,Nomefoto;
    int quantfotos = 0;
    private GridView imageGrid;
    private ArrayList<Bitmap> BitmapListmg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        placa = findViewById(R.id.placa);
        modelo = findViewById(R.id.modelo);
        marca = findViewById(R.id.marca);
        tirafoto = (ImageView) findViewById(R.id.ImgBtn_foto);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
            idveiculo = preferences.getString("idveiculo", null);
            placa.setText(preferences.getString("placa",null));
            modelo.setText(preferences.getString("modelo",null));
            marca.setText(preferences.getString("marca",null));
            tipovei = preferences.getString("tipovei", null);
        }


        groupTipoComb = findViewById(R.id.tipo);
        btn_finalizar = findViewById(R.id.btn_salvar);
        kmatual = (EditText) findViewById(R.id.kmatual);
        listEdit = findViewById(R.id.listEdit);
        listEdit.setChoiceMode(listEdit.CHOICE_MODE_MULTIPLE);

        mOrder = (Button) findViewById(R.id.btnOrder);

        getItems(tipovei);

        imageGrid = (GridView) findViewById(R.id.gridview);
        BitmapListmg = new ArrayList<Bitmap>();

        tirafoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiraFoto();
            }
        });

        //Permissão de CAMERA
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 0);
        }

        tirafoto = (ImageView) findViewById(R.id.ImgBtn_foto);



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
            SalvarDados();
        });
    }

    public void getItems(String tipovei){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try{
            String url = "http://177.91.234.230:8888/frota/src/pages/checklist/itens.json";
            System.out.println("tipo: "+tipovei);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {

                        JSONArray items =  response.getJSONArray(tipovei);
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
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.234.230:8888/frota/src/pages/checklist/checklist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Retorno "+response.toString());
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Houve um erro", Toast.LENGTH_SHORT).show();
                        }else{
                            for (int i=0; i<BitmapListmg.size(); i++){

                                SalvarFotos(BitmapListmg.get(i),ImagensStringList.get(i),response);
                            }
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
                System.out.println("Lista de Items: " + selectedItems.toString());
                System.out.println("Lista Commentes: "+ listComments. toString());
                return param;
            }
        };
        RequestQueue fila = Volley.newRequestQueue(this);
        fila.add(request);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        try{
            if (resultCode == RESULT_OK){
                quantfotos++;
                File file = new File(mCurrentPhotoPath);
                CaminhosFotos.add(file);
                bitimagem  = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));

                if(bitimagem  != null) {
                    //====== FAZER O AJUSTE NA ORIENTAÇAO DA TELA ===//
                    ImagensStringList.add(NomeFotoTirada);
                    System.out.println("Nome da foto = "+NomeFotoTirada);
                    float graus = 90;
                    Matrix matrix = new Matrix();
                    matrix.setRotate(graus);
                    Bitmap newBitmapRotate = Bitmap.createBitmap(bitimagem, 0,0, bitimagem.getWidth(),bitimagem.getHeight(),matrix,true);
                    BitmapListmg.add(newBitmapRotate);
                    imageGrid.setAdapter(new ImageAdapter(getApplicationContext(), this.BitmapListmg));
                    Nomefoto = NomeFotoTirada;
                    imageGrid.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception error){
            error.printStackTrace();
        }

        progressDialog.hide();
    }

    public void SalvarFotos(Bitmap bitImg, String nameimg,String idchecklist){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitImg.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        String photo = Base64.encodeToString(imgBytes,Base64.DEFAULT);
        System.out.println("foto "+ photo);
        System.out.println("id do checklist "+idchecklist);
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.234.230:8888/frota/src/pages/checklist/fotosChecklist.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Erro ao enviar foto posicao : " + cont,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("encoded_string",photo);
                map.put("image_name",nameimg);
                map.put("idchecklist",idchecklist);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public void tiraFoto(){
        File photoFile = null;
        if(quantfotos < 2){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
            if(intent.resolveActivity(getPackageManager())!= null) {
                try {
                    photoFile = criarImagem();
                } catch (IOException ex) {
                    //Error occurred while creating the file

                }
                if (photoFile != null) {
                    progressDialog = new ProgressDialog(CheckList.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.progress_dialog);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    Uri photoUri = FileProvider.getUriForFile(this, "com.womp.myapplication", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, 10);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Limite de fotos atingido",Toast.LENGTH_SHORT).show();
        }
    }

    public File criarImagem() throws IOException {
        // criar arquivo de imagem
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        NomeFotoTirada = timeStamp;
        File storagedir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(NomeFotoTirada,".jpg",storagedir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void SalvarDados(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            if(!BitmapListmg.isEmpty()){
                PreFinalizar();
            }else{
                Toast.makeText(getApplicationContext(),"Por favor tire as fotos",Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(getApplicationContext(),"Dispositivo não está conectado á Internet",Toast.LENGTH_LONG).show();
        }
    }


}