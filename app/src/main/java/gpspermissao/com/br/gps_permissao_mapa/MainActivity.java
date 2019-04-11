package gpspermissao.com.br.gps_permissao_mapa;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int RESQUEST_PERMISSION_GPS = 1001;

    private TextView locationTextView;

    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        locationTextView = findViewById(R.id.locationTextView);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri gmmIntentUri = Uri.parse(String.format("geo:%f,%f?q=%s",
                        lat,
                        lon,
                        getString(R.string.busca)));

                // Diz que a intenção é abrir uma action View
                Intent intent = new Intent (Intent.ACTION_VIEW,gmmIntentUri);

                //especifica que será o Google Maps
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                 lat = location.getAltitude();
                 lon = location.getLongitude();
                double alt = location.getAltitude();

                String t = String.format("Lat: %f, Long: %f, Alt: %f", lat, lon, alt);
                locationTextView.setText(t);


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

    }



    @Override
    protected void onStart() {
        super.onStart();

        //verifica se a aplicação tem acesso ao gps
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            //Liga Hardware de GPS
            locationManager.requestLocationUpdates(
                    //constante que representa o hardware de GPS
                    LocationManager.GPS_PROVIDER,
                    //segundos
                    2000,
                    // Metros
                    0,

                    //Registra o IObjeto Observador
                    locationListener

            );

        }else{
            //PEDE PERMISSÃO
            String []permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, RESQUEST_PERMISSION_GPS);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    //Não é de ciclo de vida
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if(requestCode == RESQUEST_PERMISSION_GPS){

            if(grantResults.length >=1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Método pedido pelo Android Studio
                if(ActivityCompat.checkSelfPermission
                        (this,Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){}

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        200,
                        0,
                        locationListener
                );

            }
        }else{
            Toast.makeText(this, getString(R.string.sem_gps_nao_rola), Toast.LENGTH_SHORT).show();
        }

    }
}
