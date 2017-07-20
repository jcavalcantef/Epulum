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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jhm.ufam.br.epulum.Classes.Categoria;
import jhm.ufam.br.epulum.Classes.CustomVolleyRequest;
import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Database.ReceitaDAO;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, SearchView.OnQueryTextListener {

    private static final int RECORD_REQUEST_CODE = 101;
    private static String TAG = "PermissionDemo";
    private final String server="https://epulum.000webhostapp.com";
    private final String url_base="/epulumDev/mainController.php?acao=";
    private final String url_get_receitas=server+url_base+"readReceitas";
    private final String url_create_user=server+url_base+"createUsuario";
    private final String url_server_login=server+url_base+"login";
    private final String url_criar_receita=server+url_base+"createReceita";
    private final String url_pegar_categorias=server+url_base+"readCategorias";
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
    private List<Categoria> categorias_db;




    ReceitaDAO receitaDAO;

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

        imgvPerfil = (ImageView) findViewById(R.id.imgvPerfilPhoto);
        txtEmailBar = (TextView) findViewById(R.id.txtBarEmail);
        txtNomeBar = (TextView) findViewById(R.id.txtBarNome);

        sh = new SpeechWrapper(getApplicationContext());
        doRecyclerView();
        initializeData();
        initializeAdapter();

        Intent in= getIntent();
        if(!in.hasExtra("email")){
            doPermissions();
            doGoogle();
            createServerUser();
            serverLogin();
            //signIn();
            getReceitasFromServer();
        }

        doButtons();
        sv_procura_receita= (SearchView) findViewById(R.id.sv_procura);
        sv_procura_receita.setOnQueryTextListener(this);



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
        receitas = receitaDAO.getAllReceitas();
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
        int i=0;
        query=query.toLowerCase();
        Log.v("query","text submit :"+query);
        List<Receita> rs= new ArrayList<>();
        while(i<receitas.size()){
            if(receitas.get(i).getNome().toLowerCase().contains(query)){
                rs.add(receitas.get(i));
            }
            else if(receitas.get(i).getDescricao().toLowerCase().contains(query)){
                rs.add(receitas.get(i));
            } else if(categorias_db!=null){
                int j=0;
                while(j<categorias_db.size()){
                    if(categorias_db.get(j).getTipo()==receitas.get(i).get_idcategoria()){
                        if(categorias_db.get(j).getNome().toLowerCase().contains(query)){
                            rs.add(receitas.get(i));
                        }
                    }
                    j++;
                }
            }
            i++;
        }
        if(rs.size()!=0){
            Intent intentNewActivity = new Intent(ActivityMain.this,
                    ActivityListarPesquisaReceitas.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            Bundle args = new Bundle();
            Log.v("query",rs.toString());
            for(int k=0; k<rs.size();k++){
                intentNewActivity.putExtra("rs"+k,rs.get(k));
            }
            intentNewActivity.putExtra("tamanho",rs.size());
            ActivityMain.this.startActivity(intentNewActivity);
        }
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
                receitaDAO.close();
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

    private void doOptions() {

        SearchView search = (SearchView) findViewById(R.id.sv_procura);
        search.setQueryHint("nome da receita");
        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void doButtons(){
        synchronize= (ImageView) findViewById(R.id.img_sync);
        synchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ActivityMain.this,"Sincronizando", Toast.LENGTH_SHORT).show();
                createServerUser();
                serverLogin();
                getReceitasFromServer();
            }
        });
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
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_get_receitas,
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
        Log.v("json",f.toString());
        JSONArray a= f.getJSONArray("Receitas");
        Log.v("json",a.toString());
        int i=0;
        while(i<a.length()){
            Log.v("json",a.get(i).toString());
            f=a.getJSONObject(i);
            /*Log.v("json",f.get("Id").toString());
            Log.v("json",f.get("Idcategoria").toString());
            Log.v("json",f.get("Nome").toString());
            Log.v("json",f.get("Tempopreparo").toString());
            Log.v("json",f.get("Descricao").toString());
            Log.v("json",f.get("Ingredientes").toString());
            Log.v("json",f.get("Passos").toString());*/
            if(receitaDAO.addReceita(new Receita(f))) {
                receitas.add(new Receita(f));
            }
            //Log.v("json",""+(receitas==null));

            i++;
        }

    }

    private void createServerUser(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_create_user+"&email="+em_login+"&nome="+em_nome+"&senha="+em_senha,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.v("volley","Response is: "+ response);
                            // Display the first 500 characters of the response string.
                            //Log.v("server",url_create_user+"&email="+em_login+"$nome="+em_nome+"&senha="+em_senha);

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
    }

    private void serverLogin(){
        RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url_server_login+"&email="+em_login+"&senha="+em_senha,
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url_pegar_categorias,
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
                JSONArray ja=j.getJSONArray("Categorias");
                int i=0;
                while(i<ja.length()){
                    j=ja.getJSONObject(i);
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
}