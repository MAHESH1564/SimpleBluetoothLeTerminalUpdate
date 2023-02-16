package de.kai_morich.simple_bluetooth_le_terminal;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.Manifest;

import android.content.pm.PackageManager;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private View mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.fragment);
        requestBluetoothPermissions();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, new DevicesFragment(), "devices").commit();
        else
            onBackStackChanged();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, "Bluetooth permission granted",
                                Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(mLayout, "Bluetooth permission denied",
                                Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void requestBluetoothPermissions () {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.BLUETOOTH_CONNECT)) {
            Snackbar.make(mLayout, "Need Bluetooth for scanning",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", view -> {
                        // Request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                4);
                    }).show();

        } else {
            Snackbar.make(mLayout, "Bluetooth unavailable", Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 4);
            }
        }
    }
    @Override
    public void onBackStackChanged() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount()>0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
