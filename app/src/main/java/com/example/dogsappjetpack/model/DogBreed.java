package com.example.dogsappjetpack.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * This model class hold all variables of the API that need in app
 */

@Entity
public class DogBreed {
    // public attribute that will come from API
    // use the same name of the variable that have in API, otherwise use below mention
    // @SerilizedName("") annotation

    @ColumnInfo(name = "breed_id")  // for room database column name
    @SerializedName("id")    // to fetch data from API
    public String breedId;

    @ColumnInfo(name = "dog_name")
    @SerializedName("name")
    public String dogBreed;

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    public String lifeSpan;

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    public String breedGroup;

    @ColumnInfo(name = "fred_for")
    @SerializedName("bred_for")
    public String bredFor;

    public String temperament;  // No need to annotation because we used same name as API

    @ColumnInfo(name = "dog_url")
    @SerializedName("url")
    public String imageUrl;

    @PrimaryKey(autoGenerate = true)
    public int uuid;  // It is not as per API service, we will use it for database(Room Database

    // Constructor for the above mentioned attribute of dog that will come from API
    public DogBreed(String breedId, String dogBreed, String lifeSpan, String breedGroup,
                    String bredFor, String temperament, String imageurl) {
        this.breedId = breedId;
        this.dogBreed = dogBreed;
        this.lifeSpan = lifeSpan;
        this.breedGroup = breedGroup;
        this.bredFor = bredFor;
        this.temperament = temperament;
        this.imageUrl = imageurl;
    }
// Create a empty constructor
    public DogBreed() {
    }
}
