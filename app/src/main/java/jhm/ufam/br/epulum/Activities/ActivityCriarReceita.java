package jhm.ufam.br.epulum.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.solovyev.android.views.llm.LinearLayoutManager;

import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityCriarReceita extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Receita receita;
    private RecyclerView rv_ingredientes;
    private RecyclerView rv_passos;
    private SpeechWrapper sh;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;

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


        rv_ingredientes=(RecyclerView)findViewById(R.id.rv_ingrediente);
        rv_passos=(RecyclerView)findViewById(R.id.rv_passos);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_ingredientes.setLayoutManager(llm);
        rv_ingredientes.setHasFixedSize(true);
        LinearLayoutManager llm2=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_passos.setLayoutManager(llm2);
        rv_passos.setHasFixedSize(true);

        RVIngredienteAdapter RVingradapter = new RVIngredienteAdapter(receita.getIngredientes());
        rv_ingredientes.setAdapter(RVingradapter);
        RVPassosAdapter RVPassAdapter = new RVPassosAdapter(receita.getPassos());
        rv_passos.setAdapter(RVPassAdapter);

        txtEmailBar=(TextView)findViewById(R.id.txtBarEmail);
        txtNomeBar=(TextView)findViewById(R.id.txtBarNome);
        Intent in= getIntent();
        nome=in.getStringExtra("nome");
        email=in.getStringExtra("email");
        //txtEmailBar.setText(email);
        //txtNomeBar.setText(nome);


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
            sh.Speak("Procurar receita");
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            sh.Speak("Criar receita");
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            sh.Speak("Receitas salvas");
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);
        }  else if (id == R.id.nav_perfil) {
            sh.Speak("Perfil");
            Intent intentNewActivity = new Intent(ActivityCriarReceita.this ,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarReceita.this.startActivity(intentNewActivity);

        }else if(id == R.id.nav_site){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
