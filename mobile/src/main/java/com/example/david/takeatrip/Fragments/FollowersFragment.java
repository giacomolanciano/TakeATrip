package com.example.david.takeatrip.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.david.takeatrip.Classes.Profilo;
import com.example.david.takeatrip.R;
import com.example.david.takeatrip.Utilities.DataObject;

import java.util.ArrayList;

/**
 * Created by lucagiacomelli on 21/02/16.
 */
public class FollowersFragment extends Fragment {

    TextView nome;
    TextView cognome;

    String nomeUtente;
    String cognomeUtente;

    public FollowersFragment(String nomeUtente, String cognomeUtente) {
        this.nomeUtente = nomeUtente;
        this.cognomeUtente = cognomeUtente;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_followers, container, false);
        nome= (TextView)v.findViewById(R.id.Nome);
        nome.setText(nomeUtente);
        cognome= (TextView)v.findViewById(R.id.Cognome);
        cognome.setText(cognomeUtente);
        return v;
    }
}

