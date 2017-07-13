package jhm.ufam.br.epulum.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityReadingReceita extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
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
    private ActivityReadingReceita acrr;
    private Thread fazReceita;

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
        txt_nome_receita.setText(receita.getName());

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

        Button lerReceita = (Button) findViewById(R.id.btn_nova_lista);
        sh = new SpeechWrapper(getApplicationContext());
        lr = new LeitorReceita(receita, sh);
        Button pararLeitura = (Button) findViewById(R.id.btn_parar_leitura);
        pararLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tfr.para=true;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_procurar_receita) {
            // Handle the camera action
            Intent intentNewActivity = new Intent(ActivityReadingReceita.this,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReadingReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityReadingReceita.this,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReadingReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityReadingReceita.this,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReadingReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityReadingReceita.this,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReadingReceita.this.startActivity(intentNewActivity);

        } else if (id == R.id.nav_site) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityReadingReceita.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReadingReceita.this.startActivity(intentNewActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void promptSpeechInput() {
        tfr.newResult=false;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String languagePref = "pt-BR";//or, whatever iso code...
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tfr.setResult(result.get(0));
                    tfr.newResult=true;
                    Log.v("result","new result "+result.get(0));
                }
                break;
            }

        }
        if(fazReceita.isAlive()){
            Log.v("result","thread is alive");
        }
    }


}
