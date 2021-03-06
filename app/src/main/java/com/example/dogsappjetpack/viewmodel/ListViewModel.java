package com.example.dogsappjetpack.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.dogsappjetpack.model.DogBreed;
import com.example.dogsappjetpack.model.DogDao;
import com.example.dogsappjetpack.model.DogDatabase;
import com.example.dogsappjetpack.model.DogsApiService;
import com.example.dogsappjetpack.util.NotificationsHelper;
import com.example.dogsappjetpack.util.SharedpreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

// If we did not need to use Context than use only "ViewModel" otherwise use "AndroidViewModel"
// One ViewModel can display only one view, example "ListFragment", If we want to display another view than
// replace it with existing view.
public class ListViewModel extends AndroidViewModel {

    // Create some variable of MutableLiveData with data type, example "<List<DogBreed>>"
    // MutableLiveData means it is changeable of it's value.
    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<List<DogBreed>>();
    public MutableLiveData<Boolean> dogLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    // Create a variable of DogsApiService
    private DogsApiService dogsService = new DogsApiService();
    // collect disposable observer
    private CompositeDisposable disposable = new CompositeDisposable();

    // Create a instance of AsyncTask to use inserting data in any time
    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;
    // Create a instance of AsyncTask to use retrieving data in any time
    private AsyncTask<Void, Void, List<DogBreed>> retrieveTask;

    // Create an instance of SharedPreferenceHelper class
    private SharedpreferencesHelper prefHelper = SharedpreferencesHelper.getInstance(getApplication());
    // minute * second * milli second * micro second * nano second
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L;  // 5 minutes


    // Default constructor of the AndroidViewModel
    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * This method refresh/Update the data we found from API or database but currently we use fake data
     * Its good practice that at first check data by "fetchFromRemote() method than
     * check data by fetchFromDatabase() method
     * First time fetch data from remote and than fetch from database
     */
    public void refresh() {
        // call the checkCacheDuration() method
        checkCacheDuration();

        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();

        if (updateTime != 0 && currentTime - updateTime < refreshTime){
            fetchFromDatabase();
        }else {
            fetchFromRemote();
        }
    }

    /**
     * Create a method to refresh our swipe refresh layout
     */
    public void refreshBypassCache(){
        fetchFromRemote();
    }

    /**
     * this method will update the 'refreshTime' variable
     * retrieve the cache from SharedPreferences
     */
    private void checkCacheDuration(){
        String cachePreference = prefHelper.getCacheDuration();
        if (!cachePreference.equals("")){
            try {
                int cachePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L;// milliSecond * microSecond * nanoSecond(for Long type)
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    // Create a method to fetch data from database
    private void fetchFromDatabase(){
        loading.setValue(true);
        retrieveTask = new RetrieveDogsTask();
        retrieveTask.execute();
    }



    // create method to fetch data from API
    private void fetchFromRemote() {
        loading.setValue(true);
        disposable.add(
                dogsService.getDogs()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull List<DogBreed> dogBreeds) {
                                insertTask = new insertDogsTask();
                                insertTask.execute(dogBreeds);
                                Toast.makeText(getApplication(), "Dogs retrieved from endpoint (api)", Toast.LENGTH_SHORT).show();
                                // Show the notification
                                NotificationsHelper.getInstance(getApplication()).createNotification();
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                dogLoadError.setValue(true);
                                loading.setValue(false);
                                e.printStackTrace();
                            }
                        })
        );

    }
    // This method is called when data stored in local database
    // This method will call on main thread
    private void dogsRetrived(List<DogBreed> dogList){
        dogs.setValue(dogList);
        dogLoadError.setValue(false);
        loading.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();

        // if any task is running than cancel it and set value to null.
        if (insertTask != null){
            insertTask.cancel(true);
            insertTask = null;
        }
        // if any task is running than cancel it and set value to null.
        if (retrieveTask != null){
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    /**
     * Create class for AsyncTask (Background thread) to insert data into database
     */
    private class insertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> list = lists[0];
            DogDao dao = DogDatabase.getInstance(getApplication()).dogDao();
            dao.deleteAllDogs();

            ArrayList<DogBreed> newList = new ArrayList<>(list);
            List<Long> result = dao.insertAll(newList.toArray(new DogBreed[0]));
            // create a while loop to get all dogs from the list
            int i = 0;
            while (i < list.size()){
                list.get(i).uuid = result.get(i).intValue();
                i++;
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrived(dogBreeds);
            // save insert time in sharedPreferences
            prefHelper.saveUpdateTime(System.nanoTime());
        }
    }

    /**
     * Create a AsyncTask(Background thread) to retrieve data from database
     */
    private class RetrieveDogsTask extends AsyncTask<Void, Void, List<DogBreed>>{

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase.getInstance(getApplication()).dogDao().getAllDogs();
        }
        // Override onPostExecute method to perform main thread
        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrived(dogBreeds);
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }
}
