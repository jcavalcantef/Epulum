package jhm.ufam.br.epulum.Activities;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.solovyev.android.views.llm.LinearLayoutManager;

import java.util.ArrayList;

import jhm.ufam.br.epulum.Classes.ItemClickSupport;
import jhm.ufam.br.epulum.Classes.ListaCompras;
import jhm.ufam.br.epulum.Classes.SpeechWrapper;
import jhm.ufam.br.epulum.Classes.ThreadCriarListaCompras;
import jhm.ufam.br.epulum.Classes.ThreadCriarReceita;
import jhm.ufam.br.epulum.R;
import jhm.ufam.br.epulum.RVAdapter.RVIngredienteAdapter;
import jhm.ufam.br.epulum.RVAdapter.RVPassosAdapter;
import jhm.ufam.br.epulum.Classes.Receita;

/**
 * Created by Mateus on 21/06/2017.
 */

public class ActivityCriarListaCompras extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListaCompras receita;
    private RecyclerView rv_ingredientes;
    private RecyclerView rv_passos;
    private SpeechWrapper sh;
    private TextView txtNomeBar;
    private TextView txtEmailBar;
    private String nome;
    private String email;
    private ThreadCriarListaCompras criarReceita;
    private Thread cr;
    private ActivityCriarListaCompras acr;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private RVIngredienteAdapter RVingradapter;
    private RVPassosAdapter RVPassAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_lista_compras);
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
        Intent in = getIntent();
        if(in.hasExtra("receita")){
            receita = (ListaCompras) in.getSerializableExtra("receita");
        }else receita = new ListaCompras();
        acr=this;


        rv_ingredientes=(RecyclerView)findViewById(R.id.rv_ingrediente);

        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_ingredientes.setLayoutManager(llm);
        rv_ingredientes.setHasFixedSize(true);

        RVingradapter = new RVIngredienteAdapter(receita.getItens());
        rv_ingredientes.setAdapter(RVingradapter);


        txtEmailBar=(TextView)findViewById(R.id.txtBarEmail);
        txtNomeBar=(TextView)findViewById(R.id.txtBarNome);
        nome=in.getStringExtra("nome");
        email=in.getStringExtra("email");
        //txtEmailBar.setText(email);
        //txtNomeBar.setText(nome);
        criarReceita=null;
        cr=null;

        Button comecar = (Button) findViewById(R.id.btn_comecar);
        comecar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cr!=null)
                    cr.interrupt();
                criarReceita = new ThreadCriarListaCompras(receita, getApplicationContext(), sh, acr, RVingradapter);
                cr= new Thread(criarReceita);
                if(!criarReceita.para) {
                    criarReceita.para=false;
                    cr.start();
                }
            }
        });

        Button pararLeitura = (Button) findViewById(R.id.btn_para);
        pararLeitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarReceita.para=true;
                sh.Stop();
                cr.interrupt();

            }
        });

        ItemClickSupport.addTo(rv_ingredientes).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, View v) {
                final Dialog alteraReceitaDialog = new Dialog(ActivityCriarListaCompras.this);
                alteraReceitaDialog.setContentView(R.layout.dialog_alter_text);
                alteraReceitaDialog.setTitle("Item");
                TextView title =(TextView)alteraReceitaDialog.findViewById(R.id.txt_dialog_title);
                title.setText("Ingrediente");
                final EditText ingr = (EditText) alteraReceitaDialog.findViewById(R.id.et_item);
                ingr.setText(receita.getItens().get(position));

                Button altera = (Button)alteraReceitaDialog.findViewById(R.id.btn_alterar);
                altera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.modifyItem(position, ingr.getText().toString());
                        RVingradapter.notifyDataSetChanged();
                        alteraReceitaDialog.dismiss();
                    }
                });

                Button remover = (Button)alteraReceitaDialog.findViewById(R.id.btn_remover);
                remover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        receita.removeItem(position);
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
            Intent intentNewActivity = new Intent(ActivityCriarListaCompras.this ,
                    ActivityMain.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarListaCompras.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_criar_receita) {
            Intent intentNewActivity = new Intent(ActivityCriarListaCompras.this ,
                    ActivityCriarReceita.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarListaCompras.this.startActivity(intentNewActivity);
        } else if (id == R.id.nav_receitas_salvas) {
            Intent intentNewActivity = new Intent(ActivityCriarListaCompras.this ,
                    ActivityReceitasSalvas.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarListaCompras.this.startActivity(intentNewActivity);
        }  else if (id == R.id.nav_perfil) {
            Intent intentNewActivity = new Intent(ActivityCriarListaCompras.this ,
                    ActivityPerfil.class);
            intentNewActivity.putExtra("nome",nome);
            intentNewActivity.putExtra("email",email);
            ActivityCriarListaCompras.this.startActivity(intentNewActivity);

        }else if(id == R.id.nav_site){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epulum.000webhostapp.com"));
            startActivity(browserIntent);
        }else if (id == R.id.nav_lista_compras){
            Intent intentNewActivity = new Intent(ActivityCriarListaCompras.this,
                    ActivityListaCompras.class);
            intentNewActivity.putExtra("nome", nome);
            intentNewActivity.putExtra("email", email);
            ActivityCriarListaCompras.this.startActivity(intentNewActivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void promptSpeechInput(String prompt) {
        criarReceita.newResult=false;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        String languagePref = "pt-BR";//or, whatever iso code...
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        if(prompt!=null) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    prompt);
        }

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
                    criarReceita.setResult(result.get(0));
                    criarReceita.newResult=true;
                    Log.v("result","new result "+result.get(0));
                }
                break;
            }
        }
    }

    public void dataChange(){
        RVingradapter.notifyDataSetChanged();
        RVPassAdapter.notifyDataSetChanged();

    }
}
