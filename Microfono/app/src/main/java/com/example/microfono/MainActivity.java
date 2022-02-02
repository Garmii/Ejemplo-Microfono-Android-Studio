package com.example.microfono;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static int MICROPHONE_PERMISSION_CODE = 200;

    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;

    private Button record;
    private Button stop;
    private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        record = findViewById(R.id.record);
        stop = findViewById(R.id.stop);
        play = findViewById(R.id.play);

        //Si el dispositivo tiene microfono, concede permiso
        if(isMicroPresent()) {
            getMicroPermission();
        }


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Grabamos la entrada de micrófono
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // Elegimos el formato de salida
                    mediaRecorder.setOutputFile(getRecordPath()); // Guardamos el archivo de salida en la ruta especificada
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); // Para grabar audio necesitamos codificarlo
                    mediaRecorder.prepare(); // Preparamos el micrófono
                    mediaRecorder.start(); // El micrófono empieza a escuchar

                    Toast.makeText(MainActivity.this, R.string.ToastGrabar, Toast.LENGTH_LONG).show();

                } catch(IOException | IllegalStateException e){
                   e.printStackTrace();
                }

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mediaRecorder.stop(); // Paramos el microfono
                mediaRecorder.release(); // Liberamos el micrófono
                mediaRecorder = null; // Guardamos a nulo porque al hacer release() el objeto se inutiliza

                Toast.makeText(MainActivity.this, R.string.ToastGrabarDetenido, Toast.LENGTH_LONG).show();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(getRecordPath()); // Cogemos la ruta del audio guardado
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    Toast.makeText(MainActivity.this, R.string.ToastReproducir, Toast.LENGTH_LONG).show();

                } catch(IOException | IllegalStateException e){
                    e.printStackTrace();
                }

            }
        });

        }

        private boolean isMicroPresent() { // Comprueba si el dispositivo tiene microfono

            boolean isMicroPresent=false;

            if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
                isMicroPresent = true;
            }else{
                isMicroPresent = false;
            }

            return isMicroPresent;
        }

        private void getMicroPermission(){ // Comprueba si hay permiso para utilizar el microfono

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);
            }
        }

        private String getRecordPath(){ // Devuelve la ruta del archivo a crear
            ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
            File audioFolder = contextWrapper.getExternalFilesDir(getString(R.string.NombreCarpeta)); // Carpeta con nombre personalizado
            //File audioFolder = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC); // Carpeta con nombre de la clase Enviorenment
            File file = new File(audioFolder,getString(R.string.NombreArchivoPrueba) + getString(R.string.FormatoArchivo));
            return file.getPath();
        }

}