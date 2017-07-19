package jhm.ufam.br.epulum.Activities;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;

import jhm.ufam.br.epulum.Classes.Categoria;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
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

        sh=new SpeechWrapper(getApplicationContext());
        receita = new Receita();
        acr=this;

        txtEmailBar=(TextView)findViewById(R.id.txtBarEmail);
        txtNomeBar=(TextView)findViewById(R.id.txtBarNome);
        Intent in= getIntent();
        nome=in.getStringExtra("nome");
        email=in.getStringExtra("email");
        //txtEmailBar.setText(email);
        //txtNomeBar.setText(nome);
        criarReceita=null;
        cr=null;
        //criarReceita.setContext(this);

        doButtons();
        doRecyclerViews();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
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

        Button salva = (Button) findViewById(R.id.btn_salva);
        salva.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ReceitaSalvaDAO receitaSalvaDAO = new ReceitaSalvaDAO(ActivityCriarReceita.this);
                final Dialog dialog = new Dialog(ActivityCriarReceita.this);
                dialog.setContentView(R.layout.dialog_salvar_receita);
                dialog.setTitle("Salvar Receita");
                ArrayList<String> categorias = new Receita().getCategorias();

                final EditText text = (EditText) dialog.findViewById(R.id.edtNomeReceita);
                final Spinner spnCategoria   = (Spinner) dialog.findViewById(R.id.spn_categoria);
                Button salvaReceita = (Button) dialog.findViewById(R.id.btn_salva_receita);
                Button cancela      = (Button) dialog.findViewById(R.id.btn_cancela);
                final ImageView img       = (ImageView) dialog.findViewById(R.id.img_receita);

                /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityCriarReceita.this,
                        R.array.lista_categorias, R.layout.support_simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);*/
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCriarReceita.this,
                        R.layout.support_simple_spinner_dropdown_item, categorias);

                spnCategoria.setAdapter(adapter);

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
                        receita.set_idcategoria(spnCategoria.getSelectedItemPosition());
                        try {
                            receitaSalvaDAO.addReceita(receita);
                        } catch (Exception e){
                        }
                    dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
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
}
