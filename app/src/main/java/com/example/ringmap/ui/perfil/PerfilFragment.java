package com.example.ringmap.ui.perfil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ringmap.MainActivity;
import com.example.ringmap.Tela_principal;
import com.example.ringmap.databinding.FragmentPerfilBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PerfilFragment extends Fragment {

    private Button btn_deslogar;
    private TextView text_email, text_user, text_id;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String usuarioID;

    private FragmentPerfilBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        InicializaComponents();

        btn_deslogar.setOnClickListener(View ->{

                FirebaseAuth.getInstance().signOut();
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            Intent intent = new Intent(activity, MainActivity.class);
                startActivity(intent);
                activity.finish();

        });
        return root;
    }
    @Override
    public void onStart() {
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
                    text_id.setText(usuarioID);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void InicializaComponents() {
        btn_deslogar = binding.btDeslogar;
        text_email = binding.textEmail;
        text_user = binding.textNome;
        text_id = binding.textId;
    }
}