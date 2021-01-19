package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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


public class AbastecimentoActivity extends AppCompatActivity {

    LinearLayout dinamicoLayout;
    RadioGroup groupTipoComb;
    RadioButton buttonCombustivel;
    TextView modelo,cor,placa;
    EditText kmatual,litros,requisicao;
    Button salvar;
    String combustivel,idveiculo;
    ImageView tirafoto;

    ProgressDialog progressDialog;

    Bitmap bitimagem ;
    Integer cont;
    List<String> ImagensStringList = new ArrayList<>(); // nome da foto tirada
    List<File> CaminhosFotos = new ArrayList<File>(); // caminho da foto na Raiz Celular
    String NomeFotoTirada,mCurrentPhotoPath,Nomefoto,iduser;

    private   final String ARQUIVO_AUTENTICACAO = "ArquivoAutentica";
    int quantfotos = 0;
    private GridView imageGrid;
    private ArrayList<Bitmap> BitmapListmg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastecimento);

        SharedPreferences preferences = getSharedPreferences(ARQUIVO_AUTENTICACAO,0);
        if (preferences.contains("id")){
            iduser = preferences.getString("id",null);
            idveiculo = preferences.getString("idveiculo", null);

        }

        System.out.println("Id veiculo "+idveiculo);

        getDetailsVei();


        imageGrid = (GridView) findViewById(R.id.gridview);
        BitmapListmg = new ArrayList<Bitmap>();

        //Permissão de CAMERA
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 0);
        }

        groupTipoComb = findViewById(R.id.tipodecombustivel);

        dinamicoLayout = (LinearLayout) findViewById(R.id.LayoutDinamico);
        modelo = (TextView) findViewById(R.id.modelo);
        cor = (TextView) findViewById(R.id.cor);
        placa = (TextView) findViewById(R.id.placa);
        kmatual = (EditText) findViewById(R.id.kmatual);
        litros = (EditText) findViewById(R.id.litros);
        requisicao = (EditText) findViewById(R.id.numeroRequisicao);
        tirafoto = (ImageView) findViewById(R.id.ImgBtn_foto);
        salvar = (Button) findViewById(R.id.btn_salvar);

        salvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                int radioIDComb = groupTipoComb.getCheckedRadioButtonId();
                buttonCombustivel = findViewById(radioIDComb);
                combustivel = buttonCombustivel.getText().toString();
                if (combustivel.equals("Gas.")){
                    combustivel = "Gasolina";
                }
                if(kmatual.getText().toString().matches("") || litros.getText().toString().matches("") || requisicao.getText().toString().matches("")){
                    Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }else{
                        SalvarDados();
                }
            }
        });

        tirafoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tiraFoto();
            }
        });

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

    public void SalvarFotos(Bitmap bitImg, String nameimg){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitImg.compress(Bitmap.CompressFormat.JPEG,8,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        String photo = Base64.encodeToString(imgBytes,Base64.DEFAULT);
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/fotos.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("erro")){
                            Toast.makeText(getApplicationContext(),"Erro ao enviar foto posicao : " + cont,Toast.LENGTH_SHORT).show();
                        }else{

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
                    progressDialog = new ProgressDialog(AbastecimentoActivity.this);
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
        if(!BitmapListmg.isEmpty()){
            for (int i=0; i<BitmapListmg.size(); i++){
                System.out.println("Loop "+i);
                SalvarFotos(BitmapListmg.get(i),ImagensStringList.get(i));
            }

        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

            SalvarAbastecimento();
        }else {
            Toast.makeText(getApplicationContext(),"Dispositivo não está conectado á Internet",Toast.LENGTH_LONG).show();
        }

    }

    private void getDetailsVei(){

            StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/getDadosVeiculos.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog = new ProgressDialog(AbastecimentoActivity.this);
                            progressDialog.show();
                            progressDialog.setContentView(R.layout.progress_dialog);
                            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

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
                                        modelo.setText(jsonObject.getString("modelo"));
                                        cor.setText(jsonObject.getString("cor"));
                                        placa.setText(jsonObject.getString("placa"));

                                    }
                                } catch (JSONException e) {
                                    System.out.println("eroo json é tal333333");
                                    e.printStackTrace();
                                }
                            }
                            progressDialog.hide();
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

    public void SalvarAbastecimento(){

        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/abastecimento.php",
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
                param.put("requisicao",requisicao.getText().toString());
                param.put("nomedasfotos",ImagensStringList.toString());
                param.put("idveiculo",idveiculo.toString());
                param.put("iduser",iduser);
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

    public void exibirConfirmacao(){
        AlertDialog.Builder msgbox = new AlertDialog.Builder(this);
        msgbox.setTitle("Excluindo....");
        msgbox.setIcon(android.R.drawable.ic_menu_delete);
        msgbox.setMessage("Tem certeza que deseja cancelar ?");

        msgbox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(AbastecimentoActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Intent intentEnviar = new Intent(AbastecimentoActivity.this, MenuActivity.class);
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
