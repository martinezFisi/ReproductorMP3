package com.martinez.pruebareproductor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener
{
    //Estado de la SD CARD
    String estado;
    //TextView de aviso
    //TextView aviso;
    //File que contiene la ruta raiz de la SD CARD
    File sd;

    //Identificador de permiso de escritura en la SD
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    //Titulo de la cancion
    private TextView tituloTextView;
    //Tiempo de la cancion
    private TextView tiempoTextView;

    //String de canciones
    private String canciones[];
    //ListView
    private ListView cancionesListView;
    //ArrayAdapter para el ListView
    private ArrayAdapter<String> adaptador;

    //Posicion de la cancion actual
    private int posicionActual;
    //Posicion de la cancion anterior
    private int posicionAnterior = 0;

    //Reproductor
    private MediaPlayer mp;

    //Botones del reproducto
    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton nextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //aviso = (TextView)findViewById( R.id.aviso );

        //Inicializamos los widgets a usar
        tituloTextView = (TextView)findViewById( R.id.tituloCancion );
        tiempoTextView = (TextView)findViewById( R.id.tiempo );
        cancionesListView = (ListView)findViewById( R.id.listaCanciones );
        playButton = (ImageButton)findViewById( R.id.playButton );
        previousButton = (ImageButton)findViewById( R.id.previousButton );
        nextButton = (ImageButton)findViewById( R.id.nextButton );

        //Obtenemos permisos
        obtenerPermisosEscritura();
        obtenerPermisosLectura();

        //Añadimos los eventos
        cancionesListView.setOnItemClickListener( this );
        playButton.setOnClickListener( this );
        previousButton.setOnClickListener( this );
        nextButton.setOnClickListener( this );

        //crearArchivo();
        obtenerCanciones();
        //ejecutar();






    }


    //Metodo obtenerCanciones()
    private void obtenerCanciones()
    {
        //*****Obtenemos los archivos de /Music que terminen con .mp3****
        //Avanzamos hasta la carpeta /Music/
        File filesMP3 = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/" );
        //Obtenemos los .mp3
        canciones = filesMP3.list(

                new FilenameFilter()
                {
                    @Override
                    public boolean accept(File dir, String filename)
                    {
                        String lowercaseName = filename.toLowerCase();

                        if( lowercaseName.endsWith(".mp3") )
                            return true;
                        else
                            return false;

                    }
                }

        );


        //Inicializamos el adaptador
        adaptador = new ArrayAdapter<String>( getApplicationContext(), android.R.layout.simple_list_item_1, canciones );
        //Añadimos el adaptador al listview
        cancionesListView.setAdapter( adaptador );


    }






    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText( getApplicationContext(), "Permisos de Escritura concedidos", Toast.LENGTH_SHORT).show();

                }
                else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText( getApplicationContext(), "Permisos de Lectura concedidos", Toast.LENGTH_SHORT).show();

                }
                else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                break;

        }//Fin switch()

    }//Fin void()



    //Metodo obtenerPermisosEscritura()
    public void obtenerPermisosEscritura()
    {
        //Estado de la SD CARD del telefono
        estado = Environment.getExternalStorageState();

        //Confirmamos que la SD CARD esta disponible y podemos leer y escribir
        if( estado.equals( Environment.MEDIA_MOUNTED)  )
        {

            if( ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
            {

                if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) )
                {
                    //Explicación
                }
                else
                {
                    ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE );
                }

            }

        }
    }

    //Metodo obtenerPermisosLectura()
    public void obtenerPermisosLectura()
    {
        //Estado de la SD CARD del telefono
        estado = Environment.getExternalStorageState();

        //Confirmamos que la SD CARD esta disponible y podemos leer y escribir
        if( estado.equals( Environment.MEDIA_MOUNTED)  )
        {

            if( ContextCompat.checkSelfPermission( this, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED )
            {

                if( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.READ_EXTERNAL_STORAGE ) )
                {
                    //Explicación
                }
                else
                {
                    ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE );
                }

            }

        }
    }


    /*
    //Metodo crearArchivo()
    private void crearArchivo()
    {
        //Obtenemos la ruta raiz
        sd = Environment.getExternalStorageDirectory();
        //Creamos un archivo para escribirlo en la ruta del SD CARD
        File miFile = new File( sd.getAbsolutePath(), "miFile4.txt" );


        try
        {
            //Registramos el archivo en la SD CARD
            OutputStreamWriter output = new OutputStreamWriter( new FileOutputStream(miFile) );
            //Escribimos algo en el archivo miFile.text
            output.write( "Prueba de escritura en la sd card" );
            //Cerramos el archivo
            output.close();

            aviso.setText( "Registro exitoso" );

        }
        catch (Exception e)
        {
            aviso.setText("Error de tipo "+e.toString());
        }
    }*/



    //Metodo reproducir()
    public void reproducir( String nombreCancion )
    {
        Uri datos = Uri.parse( Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/"+nombreCancion ) ;
        mp = MediaPlayer.create( getApplicationContext(), datos);
        mp.start();
    }






    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        try
        {
            posicionActual = position;

            Log.i("Mi Tag", "\nPosicion Anterior: "+posicionAnterior);
            Log.i("Mi Tag", "Posicion Actual: "+posicionActual);

            String nombreCancion = canciones[ posicionActual ];

            //Cambiamos el titulo a la de la cancion actual
            tituloTextView.setText( nombreCancion );
            //Reproducimos la cancion actual
            reproducir( nombreCancion );

            /*
            //Cambiamos los colores del item seleccionado y del anterior
            parent.getChildAt( posicionAnterior ).setBackgroundColor( Color.rgb( 28, 28, 25 ) );
            parent.getChildAt( posicionActual ).setBackgroundColor( Color.rgb( 233, 202, 0 ) );
            */

            posicionAnterior = posicionActual;
        }
        catch( Exception e )
        {
            Toast.makeText( getApplicationContext(), "Error de tipo "+e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View v)
    {

        switch( v.getId() )
        {
            case(R.id.playButton):

                break;

            case(R.id.previousButton):

                break;

            case(R.id.nextButton):

                break;

        }

    }



















}
