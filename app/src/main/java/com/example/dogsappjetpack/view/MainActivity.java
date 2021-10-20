package com.example.dogsappjetpack.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.dogsappjetpack.R;

public class MainActivity extends AppCompatActivity {

    private static final int  PERMISSION_SEND_SMS = 432;

    // Create a instance of Nav Controller for up button
    private NavController navController;

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);

        // for up button
        navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }

    // Check the permission to send sms has or not
    public void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(this)
                        .setTitle("Send SMS Permission")
                        .setMessage("This app requires access to send sms")
                           //  new DialogInterface.OnClickListener()
                        .setPositiveButton("Ask Me", (dialogInterface, which) -> {
                            requestSmsPermission();
                        })
                        // new DialogInterface.OnClickListener()
                        .setNegativeButton("No", (dialogInterface, which) -> {
                            notifyDetailFragment(false);
                        })
                        .show();
            }else {
                requestSmsPermission();
                // To request multiple permission
                String[] permissions = {Manifest.permission.SEND_SMS};
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_SEND_SMS);
            }

        } else {
            notifyDetailFragment(true);
        }
    }
    // Requesting for permission
    private void requestSmsPermission(){
        String[] permissions = {Manifest.permission.SEND_SMS};
        ActivityCompat.requestPermissions(this, permissions,PERMISSION_SEND_SMS);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_SEND_SMS:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    notifyDetailFragment(true);
                }else {
                    notifyDetailFragment(false);
                }
                break;
            }
        }
    }

    /**
     * Notify the status to Detail Fragment
     * @param permissionGranted
     */
    private void notifyDetailFragment(boolean permissionGranted){
    Fragment activeFragment = fragment.getChildFragmentManager().getPrimaryNavigationFragment();
    if (activeFragment instanceof DetailFragment){
        ((DetailFragment) activeFragment).onPermissionResult(permissionGranted);
    }
    }
}
