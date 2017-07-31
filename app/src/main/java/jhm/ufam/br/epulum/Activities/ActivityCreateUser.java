package jhm.ufam.br.epulum.Activities;

        import android.animation.Animator;
        import android.animation.AnimatorListenerAdapter;
        import android.annotation.TargetApi;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.support.design.widget.TextInputEditText;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.AutoCompleteTextView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import org.json.JSONObject;

        import jhm.ufam.br.epulum.Classes.HttpHandler;
        import jhm.ufam.br.epulum.Classes.Usuario;
        import jhm.ufam.br.epulum.R;

public class ActivityCreateUser extends AppCompatActivity {
    private UserCreateTask mAuthTask = null; // Acompanhe a tarefa de login para garantir que possamos cancelá-la, se solicitado
    // interface de usuário (UI) references.
    private EditText inputEmail;
    private EditText inputNome;
    private EditText inputPassword;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        inputEmail = (EditText) findViewById(R.id.novoEmail);
        inputNome = (EditText) findViewById(R.id.novoNome);
        inputPassword = (EditText) findViewById(R.id.novaSenha);

        Button btn_Criar_Usuario = (Button) findViewById(R.id.btn_cadastrar_usuario);
        btn_Criar_Usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.create_form);
        mProgressView = findViewById(R.id.create_progress);
        showProgress(false);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        // Reset errors.
        inputEmail.setError(null);
        inputNome.setError(null);
        inputPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = inputEmail.getText().toString();
        String nome = inputNome.getText().toString();
        String senha = inputPassword.getText().toString();

        boolean inputValid = true;
        View focusView = null;

        // Check for a valid email address.
        if (!isEmailValid(email)) { // verifica se contem @ no conteudo do email
            Log.d("Debug"," Email::não possui '@' no conteudo do email ");
            inputEmail.setError(getString(R.string.error_invalid_email));
            focusView = inputEmail;
            inputValid = false;
        }
        if(inputNome.getText().toString().isEmpty()){
            inputNome.setError(getString(R.string.error_field_required));
            focusView = inputNome;
            inputValid = false;
        }
        // Check for a valid password, if the user entered one.
        if(!isPasswordValid(senha)){
            inputPassword.setError(getString(R.string.error_invalid_password));
            focusView = inputPassword;
            inputValid = false;
        }

        if (inputValid) {
            Log.d("Debug"," inputValid::true");
            showProgress(true);
            mAuthTask = new UserCreateTask(email,nome,senha);
            mAuthTask.execute((Void) null);

        } else {
            Log.d("Debug"," inputValid::false");
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
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
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

    private class UserCreateTask extends AsyncTask<Void, Void, Boolean> {
        Usuario user = new Usuario();
        final String mEmail;
        final String mnome;
        final String mPassword;

        UserCreateTask(String email,String nome, String password) {
            this.mEmail = email;
            this.mnome = nome;
            this.mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //String email = inputEmail.getText().toString();
            //String nome = inputEmail.getText().toString();
            //String senha = inputEmail.getText().toString();

            HttpHandler sh = new HttpHandler();
            String url = "https://epulum.000webhostapp.com/epulumDev/getController.php?acao=createUsuario&email="+mEmail+"&nome="+mnome+"&senha="+mPassword;
            String jsonResponse = sh.makeServiceCall(url);
            Log.d("debug "," go: " + jsonResponse);
            if(objectJson(jsonResponse)){
                Log.d("debug", "Deveria abrir o actiity login");
                final Intent it = new Intent(ActivityCreateUser.this,ActivityLogin.class);
                startActivity(it);
            }else{
                final Intent it = new Intent(ActivityCreateUser.this,ActivityCreateUser.class);
                startActivity(it);
            }
            // TODO: register the new account here.
            return true;
        }

        boolean objectJson(String jsonResponse){
            boolean sucesso;

            try {
                JSONObject response = new JSONObject(jsonResponse);
                // Getting JSON Array node

                sucesso = response.getString("Sucess").equals("1");

                final String MENSAGEM  = response.getString("Mensagem");
                Log.d("Debug","sucesso::" + sucesso);
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
                Log.d("Json parsing error:", e.getMessage());
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
