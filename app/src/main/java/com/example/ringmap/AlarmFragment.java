package com.example.ringmap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.ringmap.databinding.FragmentAlarmBinding;

public class AlarmFragment extends Fragment {
    private AlarmFragment.OnFragmentInteractionListener mListener;
    FragmentAlarmBinding binding;
    AppCompatButton btCancel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    binding = FragmentAlarmBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        InicializarComponentes();
        btCancel.setOnClickListener(view -> {
            if (mListener != null){
                mListener.onFragmentInteraction("CancelAlarm ");
            }

        });
        return root;
    }

    private void InicializarComponentes() {
        btCancel = binding.btCancel;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String mensagem);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AlarmFragment.OnFragmentInteractionListener) {
            mListener = (AlarmFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " deve implementar OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

