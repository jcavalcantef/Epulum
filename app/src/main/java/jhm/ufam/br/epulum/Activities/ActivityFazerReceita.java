package jhm.ufam.br.epulum.Activities;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;

import jhm.ufam.br.epulum.Classes.LeitorReceita;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;
import jhm.ufam.br.epulum.Classes.*;
import jhm.ufam.br.epulum.Threads.ThreadFazerReceita;

/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityFazerReceita extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecognitionListener, SensorEventListener {
    private Receita receita;
    private RecyclerView rv_ingredientes;
    private RecyclerView rv_passos;
    private SpeechWrapper sh;
    private TextView txNomeBar;
    private TextView txEmailBar;
    private String nome;
    private String email;
    private LeitorReceita lr;
    private boolean leitorOn;
    private Thread leitor;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ThreadFazerReceita tfr;
    private ActivityFazerReceita acrr;
    private Thread fazReceita;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mSpeechRecognizerIntent;
    private boolean mIslistening;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private final String languagePref = "pt-BR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading_receita);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        acrr=this;

        Intent in = getIntent();
        receita = (Receita) in.getSerializableExtra("receita");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView txt_nome_receita = (TextView) findViewById(R.id.txt_lista_compras);
        txt_nome_receita.setText(receita.getNome());

        doRecyclerView();
        doButton();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_procurar_receita) {
            // Handle the camera action
            Intent intentNewActivity = new Intent(ActivityFazerReceita.this,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityFazerReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityFazerReceita.this,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityFazerReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityFazerReceita.this,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityFazerReceita.this.startActivity(intentNewActivity);

        } else if (id == R.id.nav_site) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityFazerReceita.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityFazerReceita.this.startActivity(intentNewActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void promptSpeechInput() {
        tfr.setNewResult(false);
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
            tfr.setResult(matches.get(0));
            tfr.setNewResult(true);
            Log.i("listening", "new result " + tfr.getResult());
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
            if (tfr != null)
                if (!mIslistening && tfr.isAskedResult()) {
                    mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
                }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void doRecyclerView(){
        rv_ingredientes = (RecyclerView) findViewById(R.id.rv_ingrediente);
        rv_passos = (RecyclerView) findViewById(R.id.rv_passos);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_ingredientes.setLayoutManager(llm);
        rv_ingredientes.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_passos.setLayoutManager(llm2);
        rv_passos.setHasFixedSize(true);

        RVIngredienteAdapter RVingradapter = new RVIngredienteAdapter(receita.getIngredientes());
        rv_ingredientes.setAdapter(RVingradapter);
        RVPassosAdapter RVPassAdapter = new RVPassosAdapter(receita.getPassos());
        rv_passos.setAdapter(RVPassAdapter);
    }
    private void doButton(){
        Button lerReceita = (Button) findViewById(R.id.btn_nova_lista);
        sh = new SpeechWrapper(getApplicationContext());
        lr = new LeitorReceita(receita, sh);
        Button pararLeitura = (Button) findViewById(R.id.btn_parar_leitura);
        pararLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tfr.setPara(true);
                sh.Stop();
                fazReceita.interrupt();
                if (leitorOn) {
                    lr.setCanRead(false);
                    leitorOn = false;
                }
            }
        });

        lerReceita.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tfr= new ThreadFazerReceita(receita, getApplicationContext(), sh, acrr);
                fazReceita= new Thread(tfr);
                fazReceita.start();
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
