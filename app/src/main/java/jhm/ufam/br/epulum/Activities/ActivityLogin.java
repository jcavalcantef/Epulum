package jhm.ufam.br.epulum.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import jhm.ufam.br.epulum.R;

/**
 * Created by hendrio on 25/07/17.
 */

public class ActivityLogin extends Activity {
    Button entrar,novo;
    EditText usuario,senha;

    TextView epulum;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entrar = (Button)findViewById(R.id.btn_login);
        usuario = (EditText)findViewById(R.id.edt_usuario);
        senha = (EditText)findViewById(R.id.edt_senha);

        novo = (Button)findViewById(R.id.btn_novo);
        epulum = (TextView)findViewById(R.id.txt_epulum);
        epulum.setVisibility(View.GONE);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuario.getText().toString().equals("admin") &&
                        senha.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong" +
                            " Credentials",Toast.LENGTH_SHORT).show();
                            epulum.setVisibility(View.VISIBLE);
                    epulum.setBackgroundColor(Color.RED);
                    counter--;
                    epulum.setText(Integer.toString(counter));

                    if (counter == 0) {
                        entrar.setEnabled(false);
                    }
                }
            }
        });

        novo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}