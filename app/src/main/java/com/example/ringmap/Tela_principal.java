package com.example.ringmap;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class Tela_principal extends AppCompatActivity {
    private Button btn_text, btn_mapa, btn_inicial;
    private TextView text_email, text_user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        IniciarComponents();
        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Tela_principal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_mapa.setOnClickListener(view -> {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

            // Verificar se o URI é nulo (pode acontecer se o dispositivo não tiver um som de alarme padrão)
            if (alarmSound == null) {
                // Se não houver som de alarme padrão, exiba uma mensagem de aviso
                return;
            }

            // Criar um objeto Ringtone com o URI do som do alarme padrão
            Ringtone ringtone = RingtoneManager.getRingtone(this, alarmSound);

            // Tocar o som do alarme
            if (ringtone != null) {
                Intent intent = new Intent(Tela_principal.this, Mapa.class);
                startActivity(intent);
            }

            });

        btn_inicial.setOnClickListener(view -> {
            Intent intent = new Intent(Tela_principal.this, tela_inicial.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null) {
                    text_user.setText(documentSnapshot.getString("nome"));
                    text_email.setText(email);
                }
            }
        });
    }



    private void IniciarComponents() {
        btn_mapa = findViewById(R.id.bt_mapa);
        btn_text = findViewById(R.id.bt_deslogar);
        btn_inicial = findViewById(R.id.bt_inicial);
        text_email = findViewById(R.id.text_email);
        text_user = findViewById(R.id.text_user);
    }
}
