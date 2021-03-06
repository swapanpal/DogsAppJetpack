package com.example.dogsappjetpack.view;

import android.content.ClipData;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dogsappjetpack.R;
import com.example.dogsappjetpack.databinding.ItemDogBinding;
import com.example.dogsappjetpack.model.DogBreed;
import com.example.dogsappjetpack.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DogsListAdapter extends RecyclerView.Adapter<DogsListAdapter.DogViewHolder>
        implements DogClickListener {
    // create a list of dogs
    private ArrayList<DogBreed> dogsList;
    // public constructor for dogs list
    public DogsListAdapter(ArrayList<DogBreed> dogsList) {
        this.dogsList = dogsList;
    }

    // method to update dogs list
    public void updateDogsList(List<DogBreed> newDogsList){
        dogsList.clear();
        dogsList.addAll(newDogsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDogBinding view = DataBindingUtil.inflate(inflater, R.layout.item_dog, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        holder.itemView.setDog(dogsList.get(position));
        holder.itemView.setListener(this);

//        ImageView image = holder.itemView.findViewById(R.id.imageView);
//        TextView name = holder.itemView.findViewById(R.id.name);
//        TextView lifespan = holder.itemView.findViewById(R.id.lifespan);
//        LinearLayout layout = holder.itemView.findViewById(R.id.dogLayout);
//
//        name.setText(dogsList.get(position).dogBreed);
//        lifespan.setText(dogsList.get(position).lifeSpan);
//        // Code the below line after creating Util class for Glide
//        // This line of code load image from API by using Glide library
//        Util.loadImage(image, dogsList.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
//       // set a clickListener to this view (new View.OnClickListener) to go detail
//        layout.setOnClickListener(view -> {
//            ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
//            action.setDogUuid(dogsList.get(position).uuid);
//            Navigation.findNavController(layout).navigate(action);
//        });
    }
    @Override
    public void onClicked(View v) {
        String uuidString = ((TextView)v.findViewById(R.id.dogId)).getText().toString();
        int uuid = Integer.valueOf(uuidString);

        ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
          action.setDogUuid(uuid);
           Navigation.findNavController(v).navigate(action);

    }

    @Override
    public int getItemCount() {
        return dogsList.size();
    }

    public class DogViewHolder extends RecyclerView.ViewHolder {
        public ItemDogBinding itemView;
        public DogViewHolder(@NonNull ItemDogBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }
}
