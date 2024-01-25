package com.example.ringmap.ui.places;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ringmap.Mapa;
import com.example.ringmap.R;
import com.example.ringmap.databinding.FragmentPlacesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlacesFragment extends Fragment implements FavoriteLocationsAdapter.OnItemClickListener{

    private FragmentPlacesBinding binding;

    private FavoriteLocationsAdapter adapter;

    FloatingActionButton mAddFab;

    // These are taken to make visible and invisible along with FABs
    TextView deleteText, addPlaceText, mTextPlaces;

    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPlacesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        InicializaComponents();

        mTextPlaces.setText("você ainda não tem nenhum lugar favorito.");

        isAllFabsVisible = false;

        mAddFab.setOnClickListener(view -> {

            AppCompatActivity activity = (AppCompatActivity) requireActivity();

            Intent intent = new Intent(activity, Mapa.class);
            startActivity(intent);
        });


        // Crie uma instância do seu adaptador
        adapter = new FavoriteLocationsAdapter();
        adapter.setOnItemClickListener(this);


        RecyclerView recyclerView = root.findViewById(R.id.recyclerViewLocations);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .document(userId)
                .collection("FavoriteLocations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<FavoriteLocation> favoriteLocations = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            FavoriteLocation location = document.toObject(FavoriteLocation.class);
                            location.setId(document.getId());
                            favoriteLocations.add(location);
                        }
                        updateUI(favoriteLocations);
                    } else {
                        // Tratar falha ao obter localizações favoritas do Firebase
                        Log.e("Firestore", "Error getting documents.", task.getException());
                    }
                });
        return root;
    }

    private void updateUI(List<FavoriteLocation> favoriteLocations) {
        if (favoriteLocations.isEmpty()) {
            mTextPlaces.setText("Você ainda não tem nenhum lugar favorito.");
        } else {
            mTextPlaces.setText(""); // Limpa a mensagem anterior

            // Atualiza o RecyclerView com os locais favoritos
            adapter.setFavoriteLocations(favoriteLocations);
        }
    }

    private void InicializaComponents() {
        mTextPlaces = binding.textPlaces;
        mAddFab = binding.addFab;
        deleteText = binding.deleteTextFab;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(FavoriteLocation favoriteLocation, String action) {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();

        switch (action){
            case "open":
                Intent intent = new Intent(activity, Mapa.class);
                intent.putExtra("Lat",favoriteLocation.getLocationPoint().getLatitude());
                intent.putExtra("Lng",favoriteLocation.getLocationPoint().getLongitude());
                intent.putExtra("Radius",favoriteLocation.getRadius());
                intent.putExtra("Id",favoriteLocation.getId());
                startActivity(intent);
                break;
            case "delete":
                CollectionReference colRef = FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .collection("FavoriteLocations");

                        colRef.document(favoriteLocation.getId())
                                .delete().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Favorito Removido com sucesso", Toast.LENGTH_SHORT).show();
                                        colRef.get().addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                List<FavoriteLocation> favoriteLocations = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task2.getResult()) {
                                                    FavoriteLocation location = document.toObject(FavoriteLocation.class);
                                                    location.setId(document.getId());
                                                    favoriteLocations.add(location);
                                                }
                                                updateUI(favoriteLocations);
                                            } else {
                                                // Tratar falha ao obter localizações favoritas do Firebase
                                                Toast.makeText(activity, "problema de conexão", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        // Tratar falha ao obter localizações favoritas do Firebase
                                        Toast.makeText(activity, "erro ao remover usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });



            break;

            default:

                break;

        }



    }
}
