package cl.gob.datos.farmacias.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.controller.AppController;
import cl.gob.datos.farmacias.helpers.Utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junar.searchpharma.Pharmacy;

public class PharmaDetailActivity extends FragmentActivity {

    private Pharmacy pharma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pharmacy_detail);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);

        long pharId = getIntent().getExtras().getLong("id");

        pharma = AppController.getInstace().getPharmaById(pharId);

        TextView pharmacyHorario = (TextView) findViewById(R.id.pharmacy_horario);
        pharmacyHorario.setText(getText(R.string.current_day) + " "
                + Utils.getDatePhone(false) + " - " + pharma.getSchedule());

        TextView address = (TextView) findViewById(R.id.parking_direccion);
        address.setText(pharma.getAddress());

        TextView pharmacyName = (TextView) findViewById(R.id.pharma_name);
        pharmacyName.setText(pharma.getName());

        Button btnRoute = (Button) findViewById(R.id.btn_pharmacy_ruta);
        btnRoute.setEnabled(pharma.getLatitude() != 0
                && pharma.getLongitude() != 0);
        btnRoute.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = "http://maps.google.com/maps?daddr="
                        + pharma.getLatitude() + "," + pharma.getLongitude();
                PharmaDetailActivity.this.startActivity(new Intent(
                        "android.intent.action.VIEW", Uri.parse(str)));
            }
        });

        Button btnCall = (Button) findViewById(R.id.btn_pharmacy_telefono);
        btnCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent localIntent = new Intent("android.intent.action.DIAL");
                localIntent.setData(Uri.parse("tel:" + pharma.getPhone()));
                startActivity(localIntent);
            }
        });

        Button btnReport = (Button) findViewById(R.id.btn_pharmacy_report);
        btnReport.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent pharmaComplain = new Intent(getApplicationContext(),
                        ComplaintPharmaActivity.class);
                pharmaComplain.putExtra("id", pharma.getId());
                startActivity(pharmaComplain);
            }
        });
        ImageView dividerPhone = (ImageView) findViewById(R.id.divider_phone);
        if (pharma.getPhone().length() > 0) {
            dividerPhone.setVisibility(View.VISIBLE);
            btnCall.setVisibility(View.VISIBLE);
        } else {
            dividerPhone.setVisibility(View.GONE);
            btnCall.setVisibility(View.GONE);
        }

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm
                .findFragmentById(R.id.map);
        GoogleMap mapa = fragment.getMap();

        MarkerOptions markerOpt = new MarkerOptions().position(new LatLng(
                pharma.getLatitude(), pharma.getLongitude()));
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        Marker marker = mapa.addMarker(markerOpt);
        marker.showInfoWindow();
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),
                16));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pharmacy_detail, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) shareItem
                .getActionProvider();
        mShareActionProvider.setShareIntent(Utils.createShareIntent(
                getApplicationContext(), pharma));

        return super.onCreateOptionsMenu(menu);
    }
}
