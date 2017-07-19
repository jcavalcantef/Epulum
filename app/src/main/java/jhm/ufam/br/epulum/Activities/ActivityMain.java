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
import jhm.ufam.br.epulum.Classes.DividerItemDecoration;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "PermissionDemo";
    private final String server="http://10.208.2.190:8888";
    private final String url_base="/epulumDev/mainController.php?acao=";
    private final String url_get_receitas=server+url_base+"readReceitas";
    private final String url_create_user=server+url_base+"createUsuario";
    private final String url_server_login=server+url_base+"login";
    private final String url_criar_receita=server+url_base+"createReceita";
    private final String em_login="mateus.lucena.work@gmail.com";
    private final String em_nome="Mateus";
    private final String em_senha="123";
    private List<Receita> receitas;
    private RecyclerView rv;
    private RVAdapter adapter;
    private SpeechWrapper sh;
    private GoogleApiClient mGoogleApiClient;
    private ImageLoader imageLoader;
    private SearchView sv_procura_receita;
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
        txtEmailBar = (TextView) findViewById(R.id.txtBarEmail);
        txtNomeBar = (TextView) findViewById(R.id.txtBarNome);

        sh = new SpeechWrapper(getApplicationContext());
        doRecyclerView();
        initializeData();
        initializeAdapter();
        doPermissions();
        doGoogle();

        signIn();
        getReceitasFromServer();

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
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityMain.class);
            ActivityMain.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityMain.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityMain.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityMain.this.startActivity(intentNewActivity);

        } else if (id == R.id.nav_site) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        } else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityMain.this.startActivity(intentNewActivity);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeData() {
        receitas = new ArrayList<>();
        Receita torta = new Receita("Torta de Maçã", "Uma Torta de Maçã muito gostosa e simples.", R.drawable.torta_de_maca);
        /*torta.addIngrediente("100 gramas de manteiga");
        torta.addIngrediente("2 gemas");
        torta.addIngrediente("4 colheres de açúcar refinado");
        torta.addIngrediente("200 gramas de farinha de trigo");
        torta.addIngrediente("500 ml de leite");
        torta.addIngrediente("1 lata de leite condensado");
        torta.addIngrediente("2 colheres de sopa de amido de milho");
        torta.addIngrediente("3 maçãs");*/
        torta.addPasso("misture a manteiga as gemas e o açúcar");
        torta.addPasso("Junte a farinha aos poucos até formar uma massa que não grude nas mãos.");
        torta.addPasso("Forre com a massa uma forma de torta redonda untada levemente com manteiga e fure toda a superfície com um garfo e leve ao forno pré-aquecido em temperatura média ou baixa para a massa dourar, aproximadamente 15 minutos");
        torta.addPasso("numa panela coloque a água e o açúcar e leve ao fogo");
        torta.addPasso("Ao ferver junte as fatias de maçãs para cozinhar levemente sem deixar desmanchar, apenas uns 2 minutos");
        torta.addPasso("Retire as maçãs com uma escumadeira e acrescente a gelatina à água que sobrou na panela, mexendo bem");
        torta.addPasso("Deixe esfriar e leve a geladeira por 10 minutos");
        torta.setAllIngredientes("[100 gramas de manteiga, 2 gemas, 4 colheres de açúcar refinado, 200 gramas de farinha de trigo, 500 ml de leite, 1 lata de leite condensado, 2 colheres de sopa de amido de milho, 3 maçãs]");
        Log.v("receita","começa :"+torta.getIngredientes().toString()+": termina");
        receitas.add(torta);
        criarReceitaServer(torta);
        receitas.add(new Receita("Joelho de Porco", "Joelho de porco com a casca tostada e crocante.", R.drawable.joelho_de_porco));
        receitas.add(new Receita("Hambúrguer Vegano", "Hambúrguer sem carne para quem quer uma refeição saudável.", R.drawable.hamburguer_vegano));
        receitas.add(new Receita("Bolinho De Carne Moída", "", R.drawable.bolinho_de_carne_moida));
        receitas.add(new Receita("Filé À Parmegiana", "", R.drawable.file_parmegiana));
        receitas.add(new Receita("Costela Na Pressão Com Linguíça", "", R.drawable.costela_na_pressao));
        receitas.add(new Receita("Camarão com creme de leite", "", R.drawable.camarao_com_creme_de_leite));
        receitas.add(new Receita("Sopa de abóbora", "", R.drawable.sopa_de_abobora));

    }

    private void initializeAdapter() {
        adapter = new RVAdapter(receitas);
        rv.setAdapter(adapter);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            nome = acct.getDisplayName();
            email = acct.getEmail();
            ((TextView) findViewById(R.id.txtBarEmail)).setText(nome);
            ((TextView) findViewById(R.id.txtBarNome)).setText(email);
            createServerUser();

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            try {
                imageLoader.get(acct.getPhotoUrl().toString(),
                        ImageLoader.getImageListener(imgvPerfil,
                                R.mipmap.ic_launcher,
                                R.mipmap.ic_launcher));

                //Loading image
                //imgvPerfil.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);
            } catch (NullPointerException e) {
                //imgvPerfil.setImageResource(R.drawable.profile_icon);

            }

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
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
        //rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Intent intentMain = new Intent(ActivityMain.this,
                        ActivityReceita.class);
                intentMain.putExtra("receita", receitas.get(position));
                intentMain.putExtra("nome", nome);
                intentMain.putExtra("email", email);
                ActivityMain.this.startActivity(intentMain);
                Log.i("Content ", " Main layout ");
            }
        });
    }

    private void doPermissions(){
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();
        }
    }

    private void doGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
        }
    }

    private void getReceitasFromServer(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_receitas,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("volley","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            try {
                                addReceitas_JSON(response);
                                adapter.notifyDataSetChanged();
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
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

    private void addReceitas_JSON(String response) throws JSONException{
        JSONObject f= new JSONObject(response);
        //Log.v("json",f.toString());
        JSONArray a= f.getJSONArray("Receitas");
        //Log.v("json",a.toString());
        int i=0;
        while(i<a.length()){
            //Log.v("json",a.get(i).toString());
            f=new JSONObject(a.get(i).toString());
            /*Log.v("json",f.get("Id").toString());
            Log.v("json",f.get("Idcategoria").toString());
            Log.v("json",f.get("Nome").toString());
            Log.v("json",f.get("Tempopreparo").toString());
            Log.v("json",f.get("Descricao").toString());
            Log.v("json",f.get("Ingredientes").toString());
            Log.v("json",f.get("Passos").toString());*/
            receitas.add(new Receita(f));
            Log.v("json",""+(receitas==null));

            i++;
        }




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

    private void criarReceitaServer(final Receita receita){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_criar_receita+
                    "&nome="+receita.getNome()+"123"+
                    "&tempopreparo="+receita.getTempopreparo()+
                    "&descricao="+receita.getDescricao()+
                    "&ingredientes="+receita.getIngredientes().toString()+
                    "&passos="+receita.getPassos().toString()+
                    "&categoria="+1+
                    "&idUser="+13,
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
        }
        serverLogin();
    }
}