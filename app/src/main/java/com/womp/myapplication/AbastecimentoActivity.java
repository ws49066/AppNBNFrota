package com.womp.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.slice.Slice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
    TextView modelo,cor;
    EditText kmatual,litros,requisicao;
    Button salvar;
    ArrayList<String> placas = new ArrayList<String>();
    String combustivel,idveiculo;
    ImageView tirafoto;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    Bitmap bitimagem ;
    Integer cont;
    List<String> ImagensStringList = new ArrayList<>(); // nome da foto tirada
    List<File> CaminhosFotos = new ArrayList<File>(); // caminho da foto na Raiz Celular
    LinearLayout layoutgridimg;
    String NomeFotoTirada,mCurrentPhotoPath,Nomefoto;
    List<String> encoded_string = new ArrayList<String>();

    int quantfotos = 0;
    private GridView imageGrid;
    private ArrayList<Bitmap> BitmapListmg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abastecimento);
        getPlacas();
        imageGrid = (GridView) findViewById(R.id.gridview);
        BitmapListmg = new ArrayList<Bitmap>();
        layoutgridimg = (LinearLayout) findViewById(R.id.layout_gridmig);

        //Permissão de CAMERA
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, 0);
        }

        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File caminho = CaminhosFotos.get(position);
                Intent intent = new Intent(AbastecimentoActivity.this,FullView.class);
                intent.putExtra("caminho", caminho);
                startActivity(intent);
            }
        });

        groupTipoComb = findViewById(R.id.tipodecombustivel);

        dinamicoLayout = (LinearLayout) findViewById(R.id.LayoutDinamico);
        modelo = (TextView) findViewById(R.id.modelo);
        cor = (TextView) findViewById(R.id.cor);
        kmatual = (EditText) findViewById(R.id.kmatual);
        litros = (EditText) findViewById(R.id.litros);
        requisicao = (EditText) findViewById(R.id.numeroRequisicao);
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
                SalvarDados();
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
                    //===== FAZER O AJUSTE DA VIEW
                    if(quantfotos == 1){
                        layoutgridimg.setVisibility(View.VISIBLE);
                        layoutgridimg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,300));
                    }
                    if(quantfotos == 4){
                        layoutgridimg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,600));
                    }
                }
            }
        }catch (Exception error){
            error.printStackTrace();
        }
    }

    public void SalvarFotos(int i){
        BitmapListmg.get(i).compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
                    String photo = Base64.encodeToString(imgBytes,Base64.DEFAULT);
                    String name = ImagensStringList.get(i);
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
                map.put("image_name",name);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    public void tiraFoto(){
        File photoFile = null;
        if(quantfotos < 3){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
            if(intent.resolveActivity(getPackageManager())!= null) {
                try {
                    photoFile = criarImagem();
                } catch (IOException ex) {
                    //Error occurred while creating the file
                }

                if (photoFile != null) {
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


    public void getPlacas() {
        StringRequest request = new StringRequest(Request.Method.POST, "http://177.91.235.146/frota/mobileapp/getPlacas.php",
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

    private void SalvarDados(){
        System.out.println(requisicao.getText().toString());
        SalvarAbastecimento();
        if(!BitmapListmg.isEmpty()){
            for (int i=0; i<BitmapListmg.size(); i++){
                System.out.println("Loop "+i);
                SalvarFotos(i);
            }
        }

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
