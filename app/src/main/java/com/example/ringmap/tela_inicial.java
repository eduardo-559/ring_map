package com.example.ringmap;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ringmap.databinding.ActivityTelaInicialBinding;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;

public class tela_inicial extends AppCompatActivity {


    private ActivityTelaInicialBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTelaInicialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_tela_inicial);
        // Aplique o estilo personalizado à BottomNavigationView
        binding.navView.setItemIconTintList(null); // Isso é usado para evitar que as cores dos ícones sejam alteradas automaticamente

        // Aplique o estilo personalizado
        binding.navView.setItemBackgroundResource(R.color.azul_escuro); // Substitua "background_menu" pelo seu drawable de background
        binding.navView.setItemTextColor(ContextCompat.getColorStateList(this, R.color.white)); // Substitua "sua_cor_texto" pela cor desejada
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

}