package jhm.ufam.br.epulum.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Classes.CustomVolleyRequest;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Database.ReceitaDAO;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

public class ActivityListarPesquisaReceitas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "PermissionDemo";
    private final String server="https://epulum.000webhostapp.com";
    private final String url_base_get="/epulumDev/getController.php?acao=";
    private final String url_base_post="/epulumDev/getController.php?acao=";
    private final String url_get_receitas=server+url_base_get+"readReceitas";
    private final String url_create_user=server+url_base_get+"createUsuario";
    private final String url_server_login=server+url_base_post+"login";
    private final String url_criar_receita=server+url_base_post+"createReceita";
    private final String url_pegar_categorias=server+url_base_get+"readCategorias";
    private final String em_login="mateus.lucena";
    private final String em_nome="m";
    private final String em_senha="123";
    private String user_id;
    private List<Receita> receitas;
    private RecyclerView rv;
    private RVAdapter adapter;
    private SpeechWrapper sh;
    private GoogleApiClient mGoogleApiClient;
    private ImageLoader imageLoader;
    private SearchView sv_procura_receita;
    private ImageView synchronize;
    private int RC_SIGN_IN = 100;
    private ImageView imgvPerfil;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        imgvPerfil = (ImageView) findViewById(R.id.imgvPerfilPhoto);
        /*txtEmailBar = (TextView) findViewById(R.id.txtBarEmail);
        txtNomeBar = (TextView) findViewById(R.id.txtBarNome);*/

        sh = new SpeechWrapper(getApplicationContext());
        Intent in= getIntent();
        receitas=new ArrayList<>();
        int n=in.getIntExtra("tamanho",0);
        for(int i=0;i<n;i++){
            receitas.add((Receita) in.getSerializableExtra("rs"+i));
        }


        doRecyclerView();
        initializeAdapter();

    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
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
            Intent intentNewActivity = new Intent(ActivityListarPesquisaReceitas.this,
                    ActivityMain.class);
            ActivityListarPesquisaReceitas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityListarPesquisaReceitas.this,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityListarPesquisaReceitas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityListarPesquisaReceitas.this,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityListarPesquisaReceitas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityListarPesquisaReceitas.this,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityListarPesquisaReceitas.this.startActivity(intentNewActivity);

        } else if (id == R.id.nav_site) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityListarPesquisaReceitas.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityListarPesquisaReceitas.this.startActivity(intentNewActivity);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeAdapter() {
        adapter = new RVAdapter(receitas);
        rv.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void doRecyclerView(){
        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Intent intentMain = new Intent(ActivityListarPesquisaReceitas.this,
                        ActivityReceita.class);
                intentMain.putExtra("receita", receitas.get(position));
                intentMain.putExtra("nome", nome);
                intentMain.putExtra("email", email);
                ActivityListarPesquisaReceitas.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });
    }
}