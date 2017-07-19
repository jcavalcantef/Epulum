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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Classes.DividerItemDecoration;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.ListaCompras;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Threads.ThreadCriarReceita;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVListaComprasAdapter;

/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityListaCompras extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<ListaCompras> lista;
    private RecyclerView rv_ingredientes;
    private SpeechWrapper sh;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;
    private ThreadCriarReceita criarReceita;
    private Thread cr;
    private ActivityListaCompras acr;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private RVListaComprasAdapter RVingradapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
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
        acr=this;
        lista= new ArrayList<>();
        populateListas();


        rv_ingredientes=(RecyclerView)findViewById(R.id.rv_listas_compras);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_ingredientes.setLayoutManager(llm);
        rv_ingredientes.setHasFixedSize(true);


        RVingradapter = new RVListaComprasAdapter(lista);
        rv_ingredientes.setAdapter(RVingradapter);

        rv_ingredientes.addItemDecoration(new DividerItemDecoration(this, android.support.v7.widget.LinearLayoutManager.VERTICAL));
        ItemClickSupport.addTo(rv_ingredientes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Intent intentMain = new Intent(ActivityListaCompras.this,
                        ActivityCriarListaCompras.class);
                intentMain.putExtra("receita", lista.get(position));
                intentMain.putExtra("nome", nome);
                intentMain.putExtra("email", email);
                ActivityListaCompras.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });

        Button criarLista = (Button) findViewById(R.id.btn_nova_lista);
        criarLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMain = new Intent(ActivityListaCompras.this,
                        ActivityCriarListaCompras.class);
                intentMain.putExtra("nome", nome);
                intentMain.putExtra("email", email);
                ActivityListaCompras.this.startActivity(intentMain);
            }
        });

        txtEmailBar=(TextView)findViewById(R.id.txtBarEmail);
        txtNomeBar=(TextView)findViewById(R.id.txtBarNome);
        Intent in= getIntent();
        nome=in.getStringExtra("nome");
        email=in.getStringExtra("email");
        //txtEmailBar.setText(email);
        //txtNomeBar.setText(nome);
        criarReceita=null;
        cr=null;

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_procurar_receita) {
            // Handle the camera action
            Intent intentNewActivity = new Intent(ActivityListaCompras.this ,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityListaCompras.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityListaCompras.this ,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityListaCompras.this.startActivity(intentNewActivity);
        }  else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityListaCompras.this ,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityListaCompras.this.startActivity(intentNewActivity);

        }else if(id == R.id.nav_site){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void populateListas(){
        ListaCompras a= new ListaCompras();
        a.setNome("Mercado semanal");
        a.addItem("Batata");
        a.addItem("cebola");
        a.addItem("banana");
        a.addItem("repolho roxo");
        ListaCompras b= new ListaCompras();
        b.setNome("Farmacia");
        b.addItem("tilenol");
        b.addItem("dipirona");
        b.addItem("leite de rosas");
        ListaCompras c= new ListaCompras();
        c.setNome("Churrascada");
        c.addItem("Batata palha");
        c.addItem("Picanha");
        c.addItem("Maminha");
        c.addItem("Amendoim");
        lista.add(a);
        lista.add(b);
        lista.add(c);

    }
}

