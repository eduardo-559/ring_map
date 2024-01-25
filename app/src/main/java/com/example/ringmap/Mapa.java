package com.example.ringmap;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Mapa extends FragmentActivity implements LocationListener, PersistentNotificationService.OnServiceInteractionListener, OnMapReadyCallback, AlarmFragment.OnFragmentInteractionListener, ContentBottomFragment.OnFragmentInteractionListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private boolean FLAG_ALARM = false;
    private Marker marker;
    private Circle circle;
    private String locationId = null;
    private String locationName = null;
    private LocationManager locationManager;
    private PersistentNotificationService service = new PersistentNotificationService();
    private static final long MIN_TIME = 500;  // atualização de localização a cada 5 segundos
    private static final float MIN_DISTANCE = 0;  // atualização de localização a cada 10 metros

    // Obtenha o FragmentManager
    FragmentManager fragmentManager = getSupportFragmentManager();

    // Crie uma transação para adicionar o fragmento ao FrameLayout
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


    // Infle o layout do conteúdo no bottom_fragment

    ContentBottomFragment contentBottomFragment = new ContentBottomFragment(); // Substitua pelo nome do seu Fragment


    AlarmFragment alarmFragment = new AlarmFragment();

    private static final String TAG = "info";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyC2gLStife8TZazXCPKyEs_v3MejlmdaCQ");
        }
        PlacesClient PlacesClient = Places.createClient(this);


        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(-4.970833, -39.015),
                new LatLng(-4.970833, -39.015)
        ));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        autocompleteFragment.setCountries("BR");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                jump(place.getLatLng());
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Toast.makeText(Mapa.this, "Erro: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        fragmentTransaction.replace(R.id.bottom_fragment, contentBottomFragment);
        fragmentTransaction.detach(contentBottomFragment);
        fragmentTransaction.commit();


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Recebe atualizações de localização aqui
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,14));
        //verifica se chegou no destino
        if (FLAG_ALARM) {
            boolean retorno = verify_destiny(userLatLng);
            if (retorno == true) {
                PersistentNotificationService.setCallback(this);
                Intent serviceIntent = new Intent(Mapa.this, PersistentNotificationService.class);
                startService(serviceIntent);
            }
        }
    }

    private boolean verify_destiny(LatLng userLatLng) {
// Obtém as coordenadas do centro do círculo
        if (circle == null || userLatLng == null) {
            return false;
        }
        double circleLatitude = circle.getCenter().latitude;
        double circleLongitude = circle.getCenter().longitude;

        // Calcula a distância entre o usuário e o centro do círculo
        float[] distanceResult = new float[1];
        Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, circleLatitude, circleLongitude, distanceResult);

        // Verifica se a distância é menor ou igual ao raio do círculo
        return distanceResult[0] <= circle.getRadius();
    }

    @Override
    public void onFragmentInteraction(String message) {
        String[] args = message.split(" ");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LatLng position = marker.getPosition();
        GeoPoint geoPoint = new GeoPoint(position.latitude, position.longitude);
        Map<String, Object> dadosDocumento = new HashMap<>();
        dadosDocumento.put("locationName", args[1]);
        dadosDocumento.put("locationPoint", geoPoint);
        dadosDocumento.put("radius", circle.getRadius());
        switch (args[0]) {
            case "editTextRadius":
                circle.setRadius(Integer.parseInt(args[1]));
                break;
            case "CancelAlarm":
                FLAG_ALARM = false;

                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.bottom_fragment, contentBottomFragment);
                fragmentTransaction.detach(alarmFragment);
                fragmentTransaction.attach(contentBottomFragment);
                fragmentTransaction.commit();
                Toast.makeText(Mapa.this, "Alarme Cancelado", Toast.LENGTH_SHORT).show();

                break;
            case "favoritar":
                if (locationId == null) {
                    FirebaseFirestore.getInstance().
                            collection("usuarios")
                            .document(userId)
                            .collection("FavoriteLocations").add(dadosDocumento);
                } else {
                    FirebaseFirestore.getInstance().
                            collection("usuarios")
                            .document(userId)
                            .collection("FavoriteLocations").document(locationId).set(dadosDocumento);
                }
                if (args[2].equals("alarm")) {
                    StartAlarm();
                } else {
                    Toast.makeText(this, "Favorito salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Mapa.this, tela_inicial.class);
                    startActivity(intent);
                }
            default:

                break;
        }

    }


    private void StartAlarm() {
        FLAG_ALARM = true;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_fragment, alarmFragment);
        fragmentTransaction.commit();

    }

    public void jump(LatLng latLng, int radius) {
        if (FLAG_ALARM == false) {
            if (marker != null)
                marker.remove();
            marker = mMap.addMarker(new MarkerOptions().position(latLng));

            // Adicione ou atualize o círculo
            if (circle == null) {
                circle = mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(radius)
                        .strokeColor(Color.argb(255, 23, 61, 255))
                        .fillColor(Color.argb(64, 0, 0, 255)));
            } else {
                circle.setCenter(latLng);
                circle.setRadius(radius);
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.attach(contentBottomFragment);
            fragmentTransaction.commit();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }


    }

    public void jump(LatLng latLng) {
        // Chama a versão principal com o valor padrão de 500 para optionalValue
        jump(latLng, 500);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            LatLng latLng = new LatLng(extras.getDouble("Lat"), extras.getDouble("Lng"));
            jump(latLng, extras.getInt("Radius"));
            locationId = extras.getString("Id");

        } else {
            Toast.makeText(Mapa.this, "Alarme Cancelado", Toast.LENGTH_SHORT).show();
        }
        // Habilitar a camada de tráfego para uma melhor visualização (opcional)
        mMap.setTrafficEnabled(true);

        // Habilitar a camada de localização do usuário
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS},
                    3030);
            return;

        }
        mMap.setMyLocationEnabled(true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, (LocationListener) this);

        mMap.setOnMapClickListener(this::onMapClick);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quixada, 14));

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (FLAG_ALARM == false)
            mudar_marcador(latLng);
    }





    public void mudar_marcador(LatLng latLng) {
        fragmentTransaction = fragmentManager.beginTransaction();
        // Adiciona ou remove o marcador
        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions().position(latLng));

            // Adiciona ou atualiza o círculo
            if (circle == null) {
                circle = mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(500)
                        .strokeColor(Color.argb(255, 23, 61, 255))
                        .fillColor(Color.argb(64, 0, 0, 255)));
            } else {
                circle.setCenter(latLng);
            }
            fragmentTransaction.attach(contentBottomFragment);

        } else {
            marker.remove();
            marker = null;

            // Remove o círculo quando o marcador é removido
            if (circle != null) {
                circle.remove();
                circle = null;
            }
            fragmentTransaction.detach(contentBottomFragment);

        }
        fragmentTransaction.commit();
    }

    @Override
    public void onServiceDataReceived(String data) {
        if(data.equals("close")){
            FLAG_ALARM = false;
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.bottom_fragment, contentBottomFragment);
            fragmentTransaction.detach(alarmFragment);
            fragmentTransaction.attach(contentBottomFragment);
            fragmentTransaction.commit();
        }
    }
}


