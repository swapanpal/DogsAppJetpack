package com.example.dogsappjetpack.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dogsappjetpack.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment {
    // bind the floatingActionButton with Butterknife library
    @BindView(R.id.floatingActionButton2)
    FloatingActionButton fab2;

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
    // override the onViewCreated and add click listener on fab button

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab2.setOnClickListener(view1 -> { onGoToList(); });
    }

    // Create helper method to navigate from list fragment to Detail fragment
    void onGoToList(){
        NavDirections action = DetailFragmentDirections.actionList();
        Navigation.findNavController(fab2).navigate(action);


    }
}