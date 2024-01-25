package com.example.ringmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 3030;
    private TextView text_tela_cadastro;
    private TextView text_esqueceu_senha;
    private EditText edit_email, edit_senha;
    private Button btn_entrar;
    private SignInButton btn_google;
    private ProgressBar progressBar;
    String[] mensagens = {"Preecha todos os campos!!!", "Login efetuado com sucesso!!!", "As senhas devem ser iguais!!!"};
    FirebaseAuth auth;
    FirebaseFirestore database;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 20;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        IniciarComponents();

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        text_tela_cadastro.setOnTouchListener((view, motionEvent) -> {
            SublinhaTextView(R.id.text_tela_cadastro);
            return false;
        });
        text_tela_cadastro.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, Tela_Cadastro.class);
            startActivity(intent);
        });

//        ações do botão de esqueceu a senha

        text_esqueceu_senha.setOnTouchListener((view, motionEvent) -> {
            SublinhaTextView(R.id.text_esqueceu_senha);
            return false;
        });
        text_esqueceu_senha.setOnClickListener(view -> {

            Intent intent = new Intent(MainActivity.this, senha_esquecida.class);
            startActivity(intent);
        });

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    AutenticarUsuario(v);
                }
            }
        });
    }

    private void googleSignIn() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            } catch (Exception e) {
                Toast.makeText(this, "erro ao acessar sua conta google",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("nome",user.getDisplayName());

                    DocumentReference documentReference = database.collection("usuarios").document(user.getUid());
                    documentReference.set(map);
                    TelaPrincipal();

                }
                else{
                    Toast.makeText(MainActivity.this, "algo deu errado.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void AutenticarUsuario(View view) {
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressBar.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TelaPrincipal();
                        }
                    }, 1000);
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "Email inválido!!!";
                    } catch (Exception e) {
                        erro = "Erro ao logar usuário!!!";
                    }

                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = auth.getCurrentUser();

        if(usuarioAtual != null) {
            TelaPrincipal();
        }
    }

    private void TelaPrincipal () {
        Intent intent = new Intent(MainActivity.this, tela_inicial.class);
        startActivity(intent);
        finish();
    }

    private void IniciarComponents() {
        text_tela_cadastro = findViewById(R.id.text_tela_cadastro);
        text_esqueceu_senha = findViewById(R.id.text_esqueceu_senha);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_password);
        btn_entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.PB_entrar);
        btn_google = findViewById(R.id.bt_login_google);
    }

    private void SublinhaTextView(int ViewID) {
        TextView textview = findViewById(ViewID);
        textview.setPaintFlags(textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

    }

}