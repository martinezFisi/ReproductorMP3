package com.martinez.pruebareproductor;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Hilo extends AsyncTask<Void, String, Void>
{
    //Tiempo de la cancion
    private TextView tiempoTextView;
    //Progressbar de la cancion
    private ProgressBar progressBar;

    //Tiempo
    private int minutos  = 0;
    private int segundos = 0;
    private int contador = 0;

    //Estado de la reproduccion
    private boolean reproduciendo = false;

    //Actividad Principal
    private MainActivity actividadPrincipal;

    public void setActividadPrincipal( MainActivity actividadPrincipal )
    {
        this.actividadPrincipal = actividadPrincipal;
    }

    public void setSegundos( int segundos )
    {
        this.segundos = segundos;
    }

    public void setMinutos( int minutos )
    {
        this.minutos = minutos;
    }

    public void setContador( int contador )
    {
        this.contador = contador;
    }

    //Este metodo ejecuta el 2do hilo paralelo
    @Override
    protected Void doInBackground(Void... params)
    {

        while(true)
        {
            if( actividadPrincipal.getEstadoReproduccion() )
            {
                try
                {
                    contador++;
                    segundos++;
                    if( segundos >= 60 )
                    {
                        segundos = 0;
                        minutos++;
                    }

                    publishProgress();
                    Thread.sleep( 1000 );
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if( contador > actividadPrincipal.getTiempoTotalCancion() )
                {
                    contador = 0;
                    setSegundos(0);
                    setMinutos(0);
                    actividadPrincipal.setEstadoReproduccion( false );
                    publishProgress();
                }

            }

        }


    }

    //Actualiza la interfaz del primer hilo
    @Override
    protected void onProgressUpdate(String... values)
    {
        if( segundos < 10  )
            actividadPrincipal.getTiempoTextView().setText( "0"+minutos+" : 0"+segundos );
        else
            actividadPrincipal.getTiempoTextView().setText( "0"+minutos+" : "+segundos );

        actividadPrincipal.getProgressBar().setProgress( contador );

    }


}
