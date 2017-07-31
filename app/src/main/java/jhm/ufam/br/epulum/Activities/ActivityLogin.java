package jhm.ufam.br.epulum.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import jhm.ufam.br.epulum.Classes.HttpHandler;
import jhm.ufam.br.epulum.Classes.Usuario;
import jhm.ufam.br.epulum.R;


public class ActivityLogin extends AppCompatActivity {

    private UserLoginTask mAuthTask = null; // Acompanhe a tarefa de login para garantir que possamos cancelá-la, se solicitado

    // interface de usuário (UI) references.
    private AutoCompleteTextView inputEmail; // Email de entrada
    private EditText inputPassword;          // Senha de entrada

    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView idTextView;

    private View mProgressView;
    private View mLoginFormView;

    String id;
    String email;
    String nome;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Set up the login form.
        inputEmail = (AutoCompleteTextView) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);

        Button btn_login = (Button) findViewById(R.id.bnt_entrar);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button btn_Tela_Cadastro = (Button) findViewById(R.id.bnt_cadastrar);
        btn_Tela_Cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("debug", "Deverá abrir a tela de criar usuário");
                final Intent it = new Intent(ActivityLogin.this,ActivityCreateUser.class);
                startActivity(it);
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputEmail.setError(null);
        inputPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        boolean inputValid = true;
        View focusView = null;

        // Check for a valid email address.
        if (!isEmailValid(email)) { // verifica se contem @ no conteudo do email
            Log.d("Debug"," Email::não possui '@' no conteudo do email ");
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            inputValid = false;
        }
        // Check for a valid password, if the user entered one.
        if(!isPasswordValid(password)){
            inputPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputPassword;
            inputValid = false;
        }

        if (inputValid) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d("Debug"," cancel::true");
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        } else {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            Log.d("Debug"," cancel::false");
            focusView.requestFocus();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        Usuario user = new Usuario();
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            /*
            Can't create handler inside thread that has not called Looper.prepare()
            TODO: attempt authentication against a network service.
            */
            HttpHandler sh = new HttpHandler();
            String url = "https://epulum.000webhostapp.com/epulumDev/getController.php?acao=login&email="+mEmail+"&senha="+mPassword;
            String jsonResponse = sh.makeServiceCall(url);
            Log.d("debug", jsonResponse);
            if(objectJson(jsonResponse)){
                Log.d("debug", "Deveria abrir o actiity fake");
                final Intent it = new Intent(ActivityLogin.this,ActivityMain.class);
                it.putExtra("Usuario", user);
                startActivity(it);
            }else{
                Intent novaView = new Intent(ActivityLogin.this,ActivityLogin.class);
                startActivity(novaView);
            }

            // TODO: register the new account here.
            return true;
        }

        boolean objectJson(String jsonResponse){
            boolean sucesso;
            try {
                JSONObject response = new JSONObject(jsonResponse);
                // Getting JSON Array node
                sucesso = Boolean.valueOf(response.getString("Sucess"));
                final String MENSAGEM  = response.getString("Mensagem");
                if(sucesso){
                    Log.d("Debug:: ", "deveria ter entrado no if de sucesso");
                    JSONObject usuario  = response.getJSONObject("Usuario");
                    String id = usuario.getString("Id");
                    String email = usuario.getString("Email");
                    String nome = usuario.getString("Nome");
                    String senha = usuario.getString("Senha");
                    String foto = usuario.getString("Foto");
                    user = new Usuario(id,email,nome,senha,foto);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), MENSAGEM, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    Log.d("Debug", "retornei true");
                    return true;
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), MENSAGEM, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                    Log.d("Debug", "retornei false 1");
                    return false;
                }
            } catch (final Exception e) {
                Log.d("Json parsing error: ", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Json parsing error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
                Log.d("Debug", "retornei false 2");
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                inputPassword.setError(getString(R.string.error_incorrect_password));
                inputPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}

