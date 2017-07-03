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
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVAdapter;
import jhm.ufam.br.epulum.Classes.Receita;
import jhm.ufam.br.epulum.Classes.SpeechHandler;

public class ActivityReceitasSalvas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Receita> receitas;
    private RecyclerView rv;
    private SpeechHandler sh;
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

        rv=(RecyclerView)findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);


        sh=new SpeechHandler(getApplicationContext());
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ItemClickSupport.addTo(rv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                sh.Speak(receitas.get(position).getName());
                Intent intentMain = new Intent(ActivityReceitasSalvas.this ,
                        ActivityReceita.class);
                intentMain.putExtra("receita",receitas.get(position));
                intentMain.putExtra("nome",nome);
                intentMain.putExtra("email",email);
                ActivityReceitasSalvas.this.startActivity(intentMain);
                Log.i("Content "," Main layout ");
            }
        });
        initializeData();
        initializeAdapter();
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
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this ,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            sh.Speak("Criar receita");
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this ,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            sh.Speak("Receitas salvas");
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this ,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);
        }  else if (id == R.id.nav_perfil) {
            sh.Speak("Perfil");
            Intent intentNewActivity = new Intent(ActivityReceitasSalvas.this ,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityReceitasSalvas.this.startActivity(intentNewActivity);

        }else if(id == R.id.nav_site){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeData(){
        receitas = new ArrayList<>();
        Receita torta=new Receita("Torta de Maçã", "Uma Torta de Maçã muito gostosa e simples.", R.drawable.torta_de_maca);
        torta.addIngrediente(new Ingrediente(100, "gramas","de manteiga"));
        torta.addIngrediente(new Ingrediente(2, "gemas",""));
        torta.addIngrediente(new Ingrediente(4, "colheres","de açúcar refinado"));
        torta.addIngrediente(new Ingrediente(200, "gramas","de farinha de trigo"));
        torta.addIngrediente(new Ingrediente(500, "m l","de leite"));
        torta.addIngrediente(new Ingrediente(1, "lata","de leite condensado"));
        torta.addIngrediente(new Ingrediente(2, "colheres","de sopa de amido de milho"));
        torta.addIngrediente(new Ingrediente(3, "maçãs",""));
        torta.addPasso("misture a manteiga, as gemas e o açúcar");
        torta.addPasso("Junte a farinha aos poucos, até formar uma massa que não grude nas mãos.");
        torta.addPasso("Forre com a massa uma forma de torta redonda untada levemente com manteiga e fure toda a superfície com um garfo e leve ao forno pré-aquecido em temperatura média ou baixa para a massa dourar, aproximadamente 15 minutos");
        torta.addPasso("numa panela, coloque a água e o açúcar e leve ao fogo");
        torta.addPasso("Ao ferver, junte as fatias de maçãs para cozinhar levemente sem deixar desmanchar, apenas uns 2 minutos");
        torta.addPasso("Retire as maçãs com uma escumadeira e acrescente a gelatina à água que sobrou na panela, mexendo bem");
        torta.addPasso("Deixe esfriar e leve a geladeira por 10 minutos");

        LeitorReceita lr= new LeitorReceita(torta,sh);
        lr.LerReceita();
        receitas.add(torta);
        receitas.add(new Receita("Joelho de Porco", "Joelho de porco com a casca tostada e crocante.", R.drawable.joelho_de_porco));
        receitas.add(new Receita("Hambúrguer Vegano", "Hambúrguer sem carne para quem quer uma refeição saudável.", R.drawable.hamburguer_vegano));
        receitas.add(new Receita("Bolinho De Carne Moída", "", R.drawable.bolinho_de_carne_moida));
        receitas.add(new Receita("Filé À Parmegiana", "", R.drawable.file_parmegiana));
        receitas.add(new Receita("Costela Na Pressão Com Linguíça", "", R.drawable.costela_na_pressao));
        receitas.add(new Receita("Camarão com creme de leite", "", R.drawable.camarao_com_creme_de_leite));
        receitas.add(new Receita("Sopa de abóbora", "", R.drawable.sopa_de_abobora));

    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(receitas);
        rv.setAdapter(adapter);
    }
}
