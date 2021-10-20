package com.example.dogsappjetpack.view;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.palette.graphics.Palette;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.dogsappjetpack.R;
import com.example.dogsappjetpack.databinding.FragmentDetailBinding;
import com.example.dogsappjetpack.databinding.SendSmsDialogBinding;
import com.example.dogsappjetpack.model.DogBreed;
import com.example.dogsappjetpack.model.DogPalette;
import com.example.dogsappjetpack.model.SmsInfo;
import com.example.dogsappjetpack.util.Util;
import com.example.dogsappjetpack.viewmodel.DetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailFragment extends Fragment {

    // create some variable
    private int dogUuid;
    private DetailViewModel viewModel;
    private FragmentDetailBinding binding;

    private boolean sendSmsStarted = false;

    private DogBreed currentDog;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment by using DataBinding technology
        FragmentDetailBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail,
                container, false);
        this.binding = binding;
        setHasOptionsMenu(true); // For showing menu in action bar
        return binding.getRoot();
    }
    // override the onViewCreated and add click listener on fab button

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null){
            dogUuid = DetailFragmentArgs.fromBundle(getArguments()).getDogUuid();
        }
        viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.fetch(dogUuid);

        // call the observeViewModel
        observeViewModel();
    }
    private void observeViewModel(){
        viewModel.dogLiveData.observe(getViewLifecycleOwner(), dogBreed -> {
        if (dogBreed != null && dogBreed instanceof DogBreed && getContext() != null){
            currentDog = dogBreed;
            binding.setDog(dogBreed);
            if (dogBreed.imageUrl != null){
                setupBackgroundColor(dogBreed.imageUrl);
            }
        }
        });
    }
    // Create a method to setup background images by using palette library/ Glide
    private void setupBackgroundColor(String url){
        Glide.with(this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                       // Generate the color
                        Palette.from(resource)
                                .generate(new Palette.PaletteAsyncListener() {
                                    @Override
                                    public void onGenerated(@Nullable Palette palette) {
                                        // Retrive color .we can set any type Light/Dark...
                                        int intColor = palette.getLightMutedSwatch().getRgb();

                                        // now create a palette element/Object
                                        DogPalette myPalette = new DogPalette(intColor);
                                        // Set the value to the element
                                        binding.setPalette(myPalette);

                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    // For detail menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send_sms:{
               if (!sendSmsStarted){
                   sendSmsStarted = true;
                   ((MainActivity) getActivity()).checkSmsPermission();

               }
                break;
            }
            case R.id.action_share:{
                Toast.makeText(getContext(), "Action Share", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    public void onPermissionResult(boolean permissionGranted){
      if (isAdded() && sendSmsStarted && permissionGranted){
          SmsInfo smsInfo = new SmsInfo("", currentDog.dogBreed +"bred for" + currentDog.bredFor,
                  currentDog.imageUrl);
          SendSmsDialogBinding dialogBinding = DataBindingUtil.inflate(
                  LayoutInflater.from(getContext()),
                  R.layout.send_sms_dialog,
                  null,
                  false
          );
          new AlertDialog.Builder(getContext())
                  .setView(dialogBinding.getRoot())
                  .setPositiveButton("Send SMS",((dialog, which) -> {
                      if (!dialogBinding.smsDestination.getText().toString().isEmpty()){
                          smsInfo.to = dialogBinding.smsDestination.getText().toString();
                          sendSms(smsInfo);
                      }
                  }))
                  .setNegativeButton("Cancel", ((dialog, which) -> {}))
                  .show();
          sendSmsStarted = false;
          // Binding the data for sms
          dialogBinding.setSmsInfo(smsInfo);
      }
    }
    private void sendSms(SmsInfo smsInfo){

    }
}