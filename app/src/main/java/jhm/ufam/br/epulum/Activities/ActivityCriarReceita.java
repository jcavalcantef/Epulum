package jhm.ufam.br.epulum.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.views.llm.LinearLayoutManager;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.io.IOException;

import jhm.ufam.br.epulum.Classes.Categoria;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.MultipartUtility;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Database.CategoriaDAO;
import jhm.ufam.br.epulum.Database.ReceitaDAO;
import jhm.ufam.br.epulum.Database.ReceitaSalvaDAO;
import jhm.ufam.br.epulum.Threads.ThreadCriarReceita;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;


/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityCriarReceita extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecognitionListener, SensorEventListener {
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "PermissionDemo";
    private final String server="https://epulum.000webhostapp.com";
    private final String url_base_get="/epulumDev/getController.php?acao=";
    private final String url_base_post="/epulumDev/mainController.php";
    private final String url_base="/epulumDev/mainController.php?acao=";
    private final String url_get_receitas=server+url_base_get+"readReceitas";
    private final String url_create_user=server+url_base_get+"createUsuario";
    private final String url_server_login=server+url_base_post+"login";

    private final String url_criar_receita = server + url_base_post;

    private final String url_pegar_categorias=server+url_base_get+"readCategorias";
    private final String url_campo_nome = "nome";
    private final String url_campo_tempo = "tempo";
    private final String url_campo_descricao = "descricao";
    private final String url_campo_ingredientes = "ingredientes";
    private final String url_campo_passos = "passos";
    private final String url_campo_categoria = "idCategoria";
    private final String url_campo_usuario = "idUser";
    private final String url_campo_foto = "fileToUpload";
    private final String em_login="mateus.lucena.work@gmail.com";
    private final String em_nome="Mateus";
    private final String em_senha="123";
/*
    private String returnUrl(Receita rec, String path)
    {
        return url_criar_receita + "&" + url_nome + "=" + rec.getNome() +
                                   "&" + url_tempo + "=" + rec.getTempopreparo() +
                                   "&" + url_descricao + "=" + rec.getDescricao() +
                                   "&" + url_ingredientes + "=" + rec.getIngredientesString() +
                "&" + url_passos + "=" + rec.getPassosString() +
                "&" + url_descricao + "=" + rec.getDescricao() +"";
    }
*/
    private final String languagePref = "pt-BR";
    private Receita receita;
    private RecyclerView rv_ingredientes;
    private RecyclerView rv_passos;
    private SpeechWrapper sh;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;
    private ThreadCriarReceita criarReceita;
    private Thread cr;
    private ActivityCriarReceita acr;
    private RVIngredienteAdapter RVingradapter;
    private RVPassosAdapter RVPassAdapter;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private String user_id;
    private List<String> categorias;
    private List<Categoria> categorias_db;
    private Dialog dialog;
    private ImageView imagem;
    public Button salva;
    private EditText edt_descricao;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_receita);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        edt_descricao =(EditText) findViewById(R.id.edt_descricao);

        Intent in= getIntent();
        sh=new SpeechWrapper(getApplicationContext());
        if(in.hasExtra("receita")){
            receita = (Receita) in.getSerializableExtra("receita");
        }
        else receita = new Receita();
        acr=this;

        txtEmailBar=(TextView)findViewById(R.id.txtBarEmail);
        txtNomeBar=(TextView)findViewById(R.id.txtBarNome);

        nome=in.getStringExtra("nome");
        email=in.getStringExtra("email");
        //txtEmailBar.setText(email);
        //txtNomeBar.setText(nome);
        criarReceita=null;
        cr=null;
        //criarReceita.setContext(this);
        //imageView = (ImageView) findViewById(R.id.img_receita);
        doButtons();
        doRecyclerViews();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        serverLogin();
        pegarCategorias();
        requestStoragePermission();
    }

    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

   public void uploadMultipart(String name, Uri caminho_arquivo) {
        //getting name for the image
        //String name = editText.getText().toString().trim();

        //getting the actual path of the image
        String path = getPath(filePath);
       String up_url=url_criar_receita+
               "&nome="+receita.getNome()+
               "&tempopreparo="+receita.getTempopreparo()+
               "&descricao="+receita.getDescricao()+
               "&ingredientes="+receita.getIngredientes().toString()+
               "&passos="+receita.getPassos().toString()+
               "&categoria="+receita.get_idcategoria()+
               "&idUser="+user_id;

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, up_url)
                    .addFileToUpload(path, "image") //Adding file
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_procurar_receita) {
            // Handle the camera action
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);

            ActivityCriarReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);
        }  else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);

        }else if(id == R.id.nav_site){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        }else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void promptSpeechInput() {
        criarReceita.setNewResult(false);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        try {
            mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            Log.i("listening", "asked to listen");
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        mIslistening = true;
        Log.i("listening", "isListening set");
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {
        Log.i("listening", "Listening error");
        mIslistening = false;
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches == null) {
            mIslistening = false;
            Log.i("listening", "matches is null");
        } else {
            criarReceita.setResult(matches.get(0));
            criarReceita.setNewResult(true);
            Log.i("listening", "new result " + criarReceita.getResult());
            mIslistening = false;
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];
        Log.v("sensor", "" + distance);
        if (event.values[0] == 0.0f) {
            if (criarReceita != null)
                if (!mIslistening && criarReceita.isAskedResult()) {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void doRecyclerViews() {
        rv_ingredientes = (RecyclerView) findViewById(R.id.rv_ingrediente);
        rv_passos = (RecyclerView) findViewById(R.id.rv_passos);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_ingredientes.setLayoutManager(llm);
        //rv_ingredientes.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_passos.setLayoutManager(llm2);
        //rv_passos.setHasFixedSize(true);

        RVingradapter = new RVIngredienteAdapter(receita.getIngredientes());
        rv_ingredientes.setAdapter(RVingradapter);
        RVPassAdapter = new RVPassosAdapter(receita.getPassos());
        rv_passos.setAdapter(RVPassAdapter);

        ItemClickSupport.addTo(rv_ingredientes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                final Dialog alteraReceitaDialog = new Dialog(ActivityCriarReceita.this);
                alteraReceitaDialog.setContentView(R.layout.dialog_alter_text);
                alteraReceitaDialog.setTitle("Ingrediente");
                TextView title = (TextView) alteraReceitaDialog.findViewById(R.id.txt_dialog_title);
                title.setText("Ingrediente");
                final EditText ingr = (EditText) alteraReceitaDialog.findViewById(R.id.et_item);
                ingr.setText(receita.getIngredientes().get(position));

                Button altera = (Button) alteraReceitaDialog.findViewById(R.id.btn_alterar);
                altera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.modifyIngrediente(position, ingr.getText().toString());
                        RVingradapter.notifyDataSetChanged();
                        alteraReceitaDialog.dismiss();
                    }
                });

                Button remover = (Button) alteraReceitaDialog.findViewById(R.id.btn_remover);
                remover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.removeIngrediente(position);
                        RVingradapter.notifyDataSetChanged();
                        alteraReceitaDialog.dismiss();
                    }
                });

                Button cancelar = (Button) alteraReceitaDialog.findViewById(R.id.btn_cancelar);
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alteraReceitaDialog.dismiss();
                    }
                });
                alteraReceitaDialog.show();
            }
        });

        ItemClickSupport.addTo(rv_passos).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                final Dialog alteraReceitaDialog = new Dialog(ActivityCriarReceita.this);
                alteraReceitaDialog.setContentView(R.layout.dialog_alter_text);
                alteraReceitaDialog.setTitle("Passo");
                TextView title = (TextView) alteraReceitaDialog.findViewById(R.id.txt_dialog_title);
                title.setText("Passo");
                final EditText ingr = (EditText) alteraReceitaDialog.findViewById(R.id.et_item);
                ingr.setText(receita.getPassos().get(position));

                Button altera = (Button) alteraReceitaDialog.findViewById(R.id.btn_alterar);
                altera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.modifyPasso(position, ingr.getText().toString());
                        RVPassAdapter.notifyDataSetChanged();
                        alteraReceitaDialog.dismiss();
                    }
                });

                Button remover = (Button) alteraReceitaDialog.findViewById(R.id.btn_remover);
                remover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.removePasso(position);
                        RVPassAdapter.notifyDataSetChanged();
                        alteraReceitaDialog.dismiss();
                    }
                });

                Button cancelar = (Button) alteraReceitaDialog.findViewById(R.id.btn_cancelar);
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alteraReceitaDialog.dismiss();
                    }
                });
                alteraReceitaDialog.show();
            }
        });
    }

    private void doButtons() {
        Button comecar = (Button) findViewById(R.id.btn_comecar);
        comecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cr != null)
                    cr.interrupt();
                criarReceita = new ThreadCriarReceita(receita, getApplicationContext(), sh, acr, RVingradapter, RVPassAdapter);
                cr = new Thread(criarReceita);
                if (!criarReceita.isPara()) {
                    criarReceita.setPara(false);
                    cr.start();
                }
            }
        });

        Button pararLeitura = (Button) findViewById(R.id.btn_para);
        pararLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarReceita.setPara(true);
                sh.Stop();
                cr.interrupt();

            }
        });

        salva = (Button) findViewById(R.id.btn_salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(categorias!=null) {
                    final ReceitaSalvaDAO receitaSalvaDAO = new ReceitaSalvaDAO(ActivityCriarReceita.this);
                    dialog = new Dialog(ActivityCriarReceita.this);

                    boolean flag = false;
                    dialog.setContentView(R.layout.dialog_salvar_receita);
                    dialog.setTitle("Salvar Receita");
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    dialog.getWindow().setAttributes(lp);
                    final EditText text = (EditText) dialog.findViewById(R.id.edtNomeReceita);
                    final Spinner spnCategoria = (Spinner) dialog.findViewById(R.id.spn_categoria);
                    if(receita.getNome()!=null) text.setText(receita.getNome());
                    Button tiraFoto = (Button) dialog.findViewById(R.id.btn_tirafoto);
                    Button salvaReceita = (Button) dialog.findViewById(R.id.btn_salva_receita);
                    Button cancela = (Button) dialog.findViewById(R.id.btn_cancela);
                    //final ImageView img = (ImageView) dialog.findViewById(R.id.img_receita);
                    final Switch compartilhar = (Switch) dialog.findViewById(R.id.swt_compartilhar);
                    imagem = (ImageView) dialog.findViewById(R.id.img_receita);
                    if(!edt_descricao.getText().equals("Descriçao")){
                        receita.setDescricao(edt_descricao.getText().toString());
                    }

                /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityCriarReceita.this,
                        R.array.lista_categorias, R.layout.support_simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);*/
                    ArrayAdapter<String> aa = new ArrayAdapter<>(ActivityCriarReceita.this,
                            R.layout.support_simple_spinner_dropdown_item, ActivityCriarReceita.this.categorias);
                    Log.v("+++++++", (categorias == null) + "");

                    spnCategoria.setAdapter(aa);

                    tiraFoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFileChooser();
                        }
                    });

                    cancela.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    salvaReceita.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            receita.setNome(text.getText().toString());
                            receita.setPhotoId(R.drawable.torta_de_maca);
                            receita.set_idcategoria(categorias_db.get(spnCategoria.getSelectedItemPosition()).getTipo());
                            if (compartilhar.isChecked()){
                                criarReceitaServer(receita);
                                //uploadMultipart("img",filePath);
                            }
                            else Log.v("comp", "receita nao compartilhada");
                            try {
                                receitaSalvaDAO.addReceita(receita);
                            } catch (Exception e) {
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //imageView = (ImageView) findViewById(R.id.img_receita);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagem.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void criarReceitaServer(final Receita receita){
        /*RequestQueue queue = Volley.newRequestQueue(this);
        Log.v("adicionar","Tentou adicionar receita");
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_criar_receita+
                    "&nome="+receita.getNome()+
                    "&tempopreparo="+receita.getTempopreparo()+
                    "&descricao="+receita.getDescricao()+
                    "&ingredientes="+receita.getIngredientes().toString()+
                    "&passos="+receita.getPassos().toString()+
                    "&categoria="+receita.get_idcategoria()+
                    "&idUser="+user_id,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.v("receita","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            Log.v("adicionar",response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("volley","That didn't work!");
                }
            });
            queue.add(stringRequest);
        }catch(NullPointerException e){
            Log.v("volley",e.toString());
        }*/

        Log.v("post","strating thread");
        Thread b= new Thread(){
            @Override
            public void run (){
                try {
                    Log.v("post","entered thread");
                    MultipartUtility multipart = new MultipartUtility(url_criar_receita, "UTF-8");
                    multipart.addFormField("acao","createReceita");
                    multipart.addFormField(url_campo_nome, receita.getNome());
                    multipart.addFormField(url_campo_tempo,receita.getTempopreparo());
                    multipart.addFormField(url_campo_descricao,receita.getDescricao());

                    multipart.addFormField(url_campo_ingredientes,receita.getIngredientesString());
                    multipart.addFormField(url_campo_passos,receita.getPassosString());
                    multipart.addFormField(url_campo_categoria,receita.get_idcategoria()+"");
                    multipart.addFormField(url_campo_usuario,"0");

                    multipart.addFilePart(url_campo_foto,
                            new File(getRealPathFromUri(getApplicationContext(),filePath)));

                    List<String> response = multipart.finish();

                    Log.v("post", "SERVER REPLIED:");
                    for (String line : response) {
                        Log.v("post", "Upload Files Response:::" + line);
                    }
                }catch( Exception e){
                    e.printStackTrace();
                    Log.v("post","woops");
                }
            }
        };
        b.start();
    }

    private void createServerUser(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_create_user+"&email="+email+"&nome="+nome+"&senha="+em_senha,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("volley","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            Log.v("server",url_create_user+"&email="+em_login+"$nome="+em_nome+"&senha="+em_senha);

                            JSONDealCreateUser(response);
                            Log.v("server",response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("volley","That didn't work!");
                }
            });
            queue.add(stringRequest);
        }catch(NullPointerException e){
            Log.v("volley",e.toString());
        }
        serverLogin();
    }

    private void serverLogin(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_server_login+"&email="+email+"&senha="+em_senha,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("volley","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            //Log.v("server",url_create_user+"&email="+em_login+"$nome="+em_nome+"&senha="+em_senha);

                            JSONdealServerLogin(response);
                            Log.v("server",response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("volley","That didn't work!");
                }
            });
            queue.add(stringRequest);
        }catch(NullPointerException e){
            Log.v("volley",e.toString());
        }
    }

    private void pegarCategorias(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_pegar_categorias,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("volley","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            //Log.v("server",url_create_user+"&email="+em_login+"$nome="+em_nome+"&senha="+em_senha);
                            Log.v("categorias",response);
                            JSONDealCategorias(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("volley","That didn't work!");
                }
            });
            queue.add(stringRequest);
        }catch(NullPointerException e){
            Log.v("volley",e.toString());
        }
    }

    private void JSONDealCategorias(String jstring){
        try{
            JSONObject j = new JSONObject(jstring);
            if(j.getInt("Sucess")==1){
                categorias_db=new ArrayList<>();
                categorias=new ArrayList<>();
                JSONArray ja=j.getJSONArray("Categorias");
                int i=0;
                while(i<ja.length()){
                    j=ja.getJSONObject(i);
                    categorias.add(j.getString("Nome"));
                    categorias_db.add(new Categoria(j.getString("Nome"),j.getLong("Id")));
                    i++;
                }
            }
        }catch(JSONException e){
            Log.v("categorias","failed");
            e.printStackTrace();

        }
    }

    private void JSONDealCreateUser(String jstring){
        try{
            JSONObject j= new JSONObject(jstring);
            if(j.getInt("Sucess")==1){
                Toast.makeText(this,j.getString("Mensagem"),Toast.LENGTH_SHORT);

            }else{
                Toast.makeText(this,j.getString("Mensagem"),Toast.LENGTH_SHORT);
            }

        }catch (JSONException e){

        }
    }

    private void JSONdealServerLogin(String jstring){
        try{
            JSONObject j= new JSONObject(jstring);
            if(j.getInt("Sucess")==1){
                Toast.makeText(this,j.getString("Mensagem"),Toast.LENGTH_SHORT);
                JSONObject m=j.getJSONObject("Usuario");
                user_id=m.getString("Id");

            }else{
                Toast.makeText(this,j.getString("Mensagem"),Toast.LENGTH_SHORT);
            }

        }catch (JSONException e){

        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
