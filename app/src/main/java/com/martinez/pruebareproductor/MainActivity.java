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
import android.widget.ProgressBar;
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
    //Progressbar
    private ProgressBar progressBar;

    //String de canciones
    private String canciones[];
    //ListView
    private ListView cancionesListView;
    //ArrayAdapter para el ListView
    private ArrayAdapter<String> adaptador;

    //Posicion de la cancion actual
    private int posicionActual = 0;
    //Posicion de la cancion anterior
    private int posicionAnterior = 0;

    //Reproductor
    private MediaPlayer mp;
    //Posición del tiempo actual de la cancion
    private int posicion;
    //Tiempo total de la cancion
    private int tiempoTotal;

    //Estado de la reproduccion
    private boolean reproduciendo = false;

    //Botones del reproducto
    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton nextButton;

    //Hilo secundario
    private Hilo hiloSecundario;

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
        progressBar = (ProgressBar)findViewById( R.id.progressBar );

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

        //Hilo
        hiloSecundario = new Hilo();
        hiloSecundario.setActividadPrincipal( this );
        hiloSecundario.execute();




    }

    public void setEstadoReproduccion( boolean reproduciendo )
    {
        this.reproduciendo = reproduciendo;
    }

    public TextView getTiempoTextView()
    {
        return tiempoTextView;
    }

    public ProgressBar getProgressBar()
    {
        return progressBar;
    }

    public boolean getEstadoReproduccion()
    {
        return reproduciendo;
    }

    public int getTiempoTotalCancion()
    {
        return tiempoTotal;
    }


    //Metodo obtenerCanciones()
    private void obtenerCanciones()
    {
        try
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
        catch( Exception e )
        {
            Toast.makeText( getApplicationContext(), "Error de tipo "+e.toString(), Toast.LENGTH_LONG ).show();
        }


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



    //Metodo destruir()
    private void destruir()
    {
        if( mp != null )
            mp.release();
    }


    //Metodo reproducir()
    private void reproducir()
    {
        destruir();
        Uri ruta = Uri.parse( Environment.getExternalStorageDirectory().getAbsolutePath()+"/Music/"+canciones[posicionActual] ) ;
        mp = MediaPlayer.create( getApplicationContext(), ruta);
        tiempoTotal = mp.getDuration()/1000;
        progressBar.setMax( tiempoTotal );
        mp.start();

        playButton.setBackgroundResource(0);
        playButton.setImageResource( android.R.drawable.ic_media_pause );

        hiloSecundario.setSegundos(0);
        hiloSecundario.setMinutos(0);
        hiloSecundario.setContador(0);

    }

    //Metodo pausar()
    private void pausar()
    {
        //Verificamos que el objeto mp esté creado y en ejecución
        if( mp != null && mp.isPlaying() )
        {
            //Obtenemos la posicion actual de la cancion
            posicion = mp.getCurrentPosition();
            //Paramos la reproduccion
            mp.pause();
            playButton.setBackgroundResource(0);
            playButton.setImageResource( android.R.drawable.ic_media_play );

        }
    }

    //Metodo continuar()
    private void continuar()
    {
        //Verificamos que el objeto mp esté creado y que esté pausado
        if( mp!=null && mp.isPlaying()==false )
        {
            //Llevamos la cancion a la posicion en la que estaba
            mp.seekTo( posicion );
            //Reproducimos
            mp.start();
            playButton.setBackgroundResource(0);
            playButton.setImageResource( android.R.drawable.ic_media_pause );
        }
    }






    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        try
        {
            posicionActual = position;

            //Cambiamos el titulo a la de la cancion actual
            tituloTextView.setText( canciones[posicionActual] );


            if( mp == null )
            {
                reproducir();
                reproduciendo = !reproduciendo;
            }
            else
            {
                if( posicionActual == posicionAnterior )
                {
                    if( reproduciendo )
                        pausar();
                    else
                        continuar();
                    reproduciendo = !reproduciendo;
                }
                else
                {
                    reproducir();
                    reproduciendo = true;
                }
            }


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
        Toast.makeText( getApplicationContext(), "Hiciste click", Toast.LENGTH_SHORT).show();
        switch( v.getId() )
        {
            case(R.id.playButton):

                if( mp == null )
                {
                    reproducir();
                    reproduciendo = !reproduciendo;
                }
                else
                {
                    if( reproduciendo )
                    {
                        pausar();
                    }
                    else
                    {
                        continuar();
                    }
                    reproduciendo = ! reproduciendo;
                }

                break;

            case(R.id.previousButton):

                Log.i("Mi tag", "Se presionó el boton previous");

                posicionAnterior = posicionActual;
                posicionActual--;

                if( posicionActual >= 0 )
                    reproducir();
                else
                {
                    posicionActual = canciones.length - 1;
                    posicionAnterior = 0;
                    reproducir();
                }

                reproduciendo = true;

                break;

            case(R.id.nextButton):

                posicionAnterior = posicionActual;
                posicionActual++;

                if( posicionActual < canciones.length )
                    reproducir();
                else
                {
                    posicionActual = 0;
                    posicionAnterior = canciones.length - 1;
                    reproducir();
                }

                reproduciendo = true;

                break;

        }

        //Cambiamos el titulo a la de la cancion actual
        tituloTextView.setText( canciones[posicionActual] );

    }



















}
