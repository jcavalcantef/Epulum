package jhm.ufam.br.epulum.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Classes.DividerItemDecoration;
import jhm.ufam.br.epulum.Classes.Ingrediente;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.LeitorReceita;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Database.ReceitaDAO;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

public class ActivityReceitasSalvas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Receita> receitas;
    private RecyclerView rv;
    private SpeechWrapper sh;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;

    public ReceitaDAO receitaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        receitaDAO = new ReceitaDAO(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);


        sh = new SpeechWrapper(getApplicationContext());
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Intent intentMain = new Intent(ActivityReceitasSalvas.this,
                        ActivityReceita.class);
                intentMain.putExtra("receita", receitas.get(position));
                intentMain.putExtra("nome", nome);
                intentMain.putExtra("email", email);
                ActivityReceitasSalvas.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });
        initializeData();
        initializeAdapter();
        txtEmailBar = (TextView) findViewById(R.id.txtBarEmail);
        txtNomeBar = (TextView) findViewById(R.id.txtBarNome);
        Intent in = getIntent();
        nome = in.getStringExtra("nome");
        email = in.getStringExtra("email");
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
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);

        } else if (id == R.id.nav_site) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeData() {
        long idUsuario = 1;
        receitas = receitaDAO.getReceitasByUsuario(idUsuario);


    }

    private void initializeAdapter() {
        RVAdapter adapter = new RVAdapter(receitas);
        rv.setAdapter(adapter);
    }
}
