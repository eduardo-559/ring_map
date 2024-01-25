package com.example.ringmap;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.ringmap.databinding.FragmentBottomMenuMapBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.util.GAuthToken;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class ContentBottomFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FragmentBottomMenuMapBinding binding;

    private EditText editTextNamePlace, editTextRadius;
    private SeekBar seekBar;
    private AppCompatButton button, btn_favoritar;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla o layout do conteúdo
        binding = FragmentBottomMenuMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        InicializarComponentes();
        editTextRadius.setText("500");
        // Configurações adicionais, se necessário



        editTextRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Este método é chamado para notificar que o texto está prestes a ser alterado.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Este método é chamado para notificar que o texto foi alterado.
                String radius = charSequence.toString();
                if (radius.equals(""))
                    radius = "0";
                if (mListener != null){
                    mListener.onFragmentInteraction("editTextRadius "+radius);
                }
            }


            @Override
            public void afterTextChanged(Editable editable) {
                // Este método é chamado para notificar que o texto foi alterado após a alteração.
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Este método é chamado quando o progresso da SeekBar é alterado.
                // O parâmetro 'progress' representa o novo valor da SeekBar.
                // O parâmetro 'fromUser' indica se a alteração foi feita pelo usuário ou programaticamente.

                // Faça algo com o novo progresso, se necessário.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Este método é chamado quando o usuário inicia o toque na SeekBar.
                // Pode ser útil para realizar ações quando o usuário começa a interagir com a SeekBar.
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Este método é chamado quando o usuário para de interagir com a SeekBar.
                // Pode ser útil para realizar ações quando o usuário termina de ajustar o valor.
                String radius = String.valueOf(seekBar.getProgress()*2000/seekBar.getMax());
                editTextRadius.setText(radius);
            }
        });

        button.setOnClickListener(view -> {
            FragmentActivity activity = (FragmentActivity) requireActivity();
            String name = editTextNamePlace.getText().toString();
            if (name.equals("")){
                Toast.makeText(activity, "Nome do local obrigatório", Toast.LENGTH_SHORT).show();
            }else {
                String Message = ("favoritar "+name+" alarm");
                if (mListener != null){
                    mListener.onFragmentInteraction(Message);
                }
            }
        });
        btn_favoritar.setOnClickListener(view -> {
            FragmentActivity activity = (FragmentActivity) requireActivity();
            String name = editTextNamePlace.getText().toString();
            if (name.equals("")){
                Toast.makeText(activity, "Nome do local obrigatório", Toast.LENGTH_SHORT).show();
            }else {
                String Message = ("favoritar "+name+ " fav");
                if (mListener != null){
                    mListener.onFragmentInteraction(Message);
                }
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " deve implementar OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String mensagem);
    }

    private void InicializarComponentes() {
        editTextNamePlace = binding.editNamePlace;
        editTextRadius = binding.editTextRadius;
        seekBar = binding.seekBarRadius;
        button = binding.btSave;
        btn_favoritar = binding.favoritar;
    }

}
