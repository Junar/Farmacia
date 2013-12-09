package cl.gob.datos.farmacias.fragment;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.controller.AppController;

import com.junar.searchpharma.Pharmacy;

public class ComplaintPharmaActivity extends FragmentActivity {

    private Pharmacy pharma;
    private static final int TAKE_PHOTO = 1999;
    private static final int SEND_MAIL = 2000;
    private static final String TAG = null;
    private EditText name;
    private EditText text;
    private static File mediaFile;
    private TextView pharmacyName;

    // TODO: Add Mail when the report work with a webService.
    // private EditText mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_complaint);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);

        long pharId = getIntent().getExtras().getLong("id");

        pharma = AppController.getInstace().getPharmaById(pharId);

        name = (EditText) findViewById(R.id.pharma_complaint_name);
        // TODO: Add Mail when the report work with a webService.
        // mail = (EditText) findViewById(R.id.pharma_complaint_email);
        text = (EditText) findViewById(R.id.pharma_complaint_text);

        TextView address = (TextView) findViewById(R.id.pharma_address);
        address.setText(pharma.getAddress());

        pharmacyName = (TextView) findViewById(R.id.pharma_name);
        pharmacyName.setText(pharma.getName());

        Button btnPhoto = (Button) findViewById(R.id.take_photo);
        btnPhoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                mediaFile = getOutputMediaFile();

                if (mediaFile != null) {
                    Intent camaraIntent = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(mediaFile));
                    startActivityForResult(camaraIntent, TAKE_PHOTO);
                } else {
                    // TODO: TIRAR MENSAJE
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.menu_send_mail:
            validateFields();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pharmacy_complain, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PHOTO && resultCode != RESULT_OK
                && mediaFile != null && mediaFile.exists()) {
            mediaFile.delete();
            mediaFile = null;
        } else if (requestCode == SEND_MAIL) {
            finish();
        }
    }

    private void validateFields() {
        StringBuilder texto = new StringBuilder();
        texto.append(text.getText().toString());
        if (TextUtils.isEmpty(name.getText().toString())) {
            name.setError(getString(R.string.pharma_complaint_name_empty)
                    .toString());
            name.requestFocus();
        } else if (TextUtils.isEmpty(texto)) {
            name.setError(null);
            text.setError(getString(R.string.pharma_complaint_msg_empty)
                    .toString());
            text.requestFocus();
        } else {
            text.setError(null);
            name.setError(null);
            texto.insert(0, "Nombre Completo: " + name.getText().toString()
                    + "\nObservaciones: ");
            sendMail(texto);
        }
    }

    private void sendMail(StringBuilder texto) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent
                .putExtra(
                        android.content.Intent.EXTRA_EMAIL,
                        new String[] { getString(R.string.pharma_complaint_email_address) });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                getString(R.string.pharma_complaint_email_subject) + " "
                        + pharmacyName.getText());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                texto.toString());
        if (mediaFile != null && mediaFile.exists()) {
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM,
                    Uri.fromFile(mediaFile));
        }

        startActivityForResult(Intent.createChooser(emailIntent,
                getString(R.string.pharma_complaint_email_intent)), SEND_MAIL);
    }

    private File getOutputMediaFile() {
        File mediaFileTmp = null;
        try {
            mediaFileTmp = File.createTempFile(
                    "Report_" + System.currentTimeMillis(), ".jpg");
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return mediaFileTmp;
    }
}
