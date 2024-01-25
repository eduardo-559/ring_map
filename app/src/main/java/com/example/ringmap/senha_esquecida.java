package com.example.ringmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class senha_esquecida extends AppCompatActivity {

    private EditText edit_email;
    private Button btn_senha_esquecida;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senha_esquecida);

        iniciarComponentes();

        btn_senha_esquecida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarEmailRecuperacao();
            }
        });
    }

    private void iniciarComponentes() {
        edit_email = findViewById(R.id.edit_email);
        btn_senha_esquecida = findViewById(R.id.bt_entrar);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void enviarEmailRecuperacao() {
        String email = edit_email.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(senha_esquecida.this, "E-mail de recuperação enviado. Verifique seu e-mail.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(senha_esquecida.this, "Falha ao enviar e-mail de recuperação. Verifique o e-mail inserido.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(senha_esquecida.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });
    }
}