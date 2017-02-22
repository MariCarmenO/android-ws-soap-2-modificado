package com.example.eneko.serviciowebsoap;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private EditText txtNombre;
    private EditText txtTelefono;
    private EditText txtIdCliente;
    private TextView txtResultado;
    private Button btnEnviar;
    private Button btnEnviar2;
    private Button btnConsultar;
    private Button btnModificar;
    private Button btnBuscar;
    private ListView lstClientes;
    private String textoNombre;
    private String textoTelefono;
    private String textoIdCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre = (EditText)findViewById(R.id.txtNombre);
        textoNombre = txtNombre.getText().toString();
        txtTelefono = (EditText)findViewById(R.id.txtTelefono);
        textoTelefono = txtTelefono.getText().toString();
        txtIdCliente = (EditText)findViewById(R.id.txtIdCliente);
        textoIdCliente = txtIdCliente.getText().toString();
        txtResultado = (TextView)findViewById(R.id.txtResultado);
        btnEnviar = (Button)findViewById(R.id.btnEnviar);
        btnEnviar2 = (Button)findViewById(R.id.btnEnviar2);
        btnConsultar = (Button)findViewById(R.id.btnConsultar);
        btnModificar = (Button)findViewById(R.id.btnModificar);
        btnBuscar = (Button)findViewById(R.id.btnBuscar);
        lstClientes = (ListView)findViewById(R.id.lstClientes);


        btnEnviar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textoTelefono = txtTelefono.getText().toString();
                textoNombre = txtNombre.getText().toString();
                TareaWSInsercion1 tarea = new TareaWSInsercion1(textoNombre,textoTelefono);
                tarea.execute();
            }
        });

        btnEnviar2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textoTelefono = txtTelefono.getText().toString();
                textoNombre = txtNombre.getText().toString();
                TareaWSInsercion2 tarea = new TareaWSInsercion2(textoNombre,textoTelefono);
                tarea.execute();
            }
        });

        btnConsultar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textoTelefono = txtTelefono.getText().toString();
                textoNombre = txtNombre.getText().toString();
                TareaWSConsulta tarea = new TareaWSConsulta(textoNombre,textoTelefono);
                tarea.execute();
            }
        });

        btnModificar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textoIdCliente = txtIdCliente.getText().toString();
                textoNombre = txtNombre.getText().toString();
                textoTelefono = txtTelefono.getText().toString();
                TareaWSModificacion tarea = new TareaWSModificacion(textoIdCliente,textoNombre,textoTelefono);
                tarea.execute();
            }
        });

        btnBuscar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                textoIdCliente = txtIdCliente.getText().toString();
                textoNombre = txtNombre.getText().toString();
                textoTelefono = txtTelefono.getText().toString();
                TareaWSClienteIndividual tarea = new TareaWSClienteIndividual(textoIdCliente,textoNombre,textoTelefono);
                tarea.execute();
            }
        });
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSConsulta extends AsyncTask<String,Integer,Boolean> {

        private String nombre;
        private String telefono;
        private Cliente[] listaClientes;

        public TareaWSConsulta(String textoNombre, String textoTelefono) {
            this.nombre = textoNombre;
            this.telefono = textoTelefono;
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://tempuri.org/";
            final String URL="http://10.107.57.21:8282/ServicioWebSoap2/ServicioClientes.asmx";
            final String METHOD_NAME = "ListadoClientes";
            final String SOAP_ACTION = "http://tempuri.org/ListadoClientes";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapObject resSoap =(SoapObject)envelope.getResponse();

                listaClientes = new Cliente[resSoap.getPropertyCount()];

                for (int i = 0; i < listaClientes.length; i++)
                {
                    SoapObject ic = (SoapObject)resSoap.getProperty(i);

                    Cliente cli = new Cliente();
                    cli.id = Integer.parseInt(ic.getProperty(0).toString());
                    cli.nombre = ic.getProperty(1).toString();
                    cli.telefono = Integer.parseInt(ic.getProperty(2).toString());

                    listaClientes[i] = cli;
                }
            }
            catch (Exception e)
            {
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                //Rellenamos la lista con los nombres de los clientes
                final String[] datos = new String[listaClientes.length];

                for(int i=0; i<listaClientes.length; i++)
                    datos[i] = listaClientes[i].nombre;

                ArrayAdapter<String> adaptador =
                        new ArrayAdapter<String>(MainActivity.this,
                                android.R.layout.simple_list_item_1, datos);

                lstClientes.setAdapter(adaptador);
            }
            else
            {
                txtResultado.setText("Error!");
            }
        }
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSInsercion1 extends AsyncTask<String,Integer,Boolean> {

        private Cliente[] listaClientes;
        private String nombre;
        private String telefono;

        public TareaWSInsercion1(String textoNombre, String textoTelefono) {
            this.nombre = textoNombre;
            this.telefono = textoTelefono;
        }

        protected Boolean doInBackground(String... params) {


            boolean resul = true;

            final String NAMESPACE = "http://tempuri.org/";
            final String URL="http://10.107.57.21:8282/ServicioWebSoap2/ServicioClientes.asmx";
            final String METHOD_NAME = "NuevoClienteSimple";
            final String SOAP_ACTION = "http://tempuri.org/NuevoClienteSimple";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("nombre", nombre);
            request.addProperty("telefono", telefono);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                String res = resultado_xml.toString();

                if(!res.equals("1"))
                    resul = false;
            }
            catch (Exception e)
            {
                Log.e("error1",e.getMessage());
                Log.e("error","exception");
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
                txtResultado.setText("Insertado OK");
            else
                txtResultado.setText("Error!");
        }
    }

    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSInsercion2 extends AsyncTask<String,Integer,Boolean> {

        private Cliente[] listaClientes;
        private String nombre;
        private String telefono;

        public TareaWSInsercion2(String textoNombre, String textoTelefono) {
            this.nombre = textoNombre;
            this.telefono = textoTelefono;
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://tempuri.org/";
            final String URL="http://10.107.57.21:8282/ServicioWebSoap2/ServicioClientes.asmx";
            final String METHOD_NAME = "NuevoClienteObjeto";
            final String SOAP_ACTION = "http://tempuri.org/NuevoClienteObjeto";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            Cliente cli = new Cliente();
            cli.nombre = nombre;
            cli.telefono = Integer.parseInt(telefono);

            PropertyInfo pi = new PropertyInfo();
            pi.setName("cliente");
            pi.setValue(cli);
            pi.setType(cli.getClass());

            request.addProperty(pi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            envelope.addMapping(NAMESPACE, "Cliente", cli.getClass());

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                String res = resultado_xml.toString();

                if(!res.equals("1"))
                    resul = false;
            }
            catch (Exception e)
            {
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
                txtResultado.setText("Insertado OK");
            else
                txtResultado.setText("Error!");
        }
    }


    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSModificacion extends AsyncTask<String,Integer,Boolean> {

        private String idCliente;
        private String nombre;
        private String telefono;
        private Cliente[] listaClientes;

        public TareaWSModificacion(String textoIdCliente, String textoNombre, String textoTelefono) {
            this.idCliente = textoIdCliente;
            this.nombre = textoNombre;
            this.telefono = textoTelefono;
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;

            final String NAMESPACE = "http://tempuri.org/";
            final String URL="http://10.107.57.21:8282/ServicioWebSoap2/ServicioClientes.asmx";
            final String METHOD_NAME = "ModificarTelefono";
            final String SOAP_ACTION = "http://tempuri.org/ModificarTelefono";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("idCliente", idCliente);
            request.addProperty("telefono", telefono);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);

            try
            {
                transporte.call(SOAP_ACTION, envelope);

                SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
                Log.e("errorR",resultado_xml.toString());
                String res = resultado_xml.toString();

                if(!res.equals("1"))
                    resul = false;
            }
            catch (Exception e)
            {
                resul = false;
            }

            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
                txtResultado.setText("Modificado OK");
            else
                txtResultado.setText("Error!");
        }
    }
    //Tarea Asíncrona para llamar al WS de consulta en segundo plano
    private class TareaWSClienteIndividual extends AsyncTask<String,Integer,Boolean> {

        private String idCliente;
        private String nombre;
        private String telefono;
        private Cliente cliente;

        public TareaWSClienteIndividual(String textoIdCliente, String textoNombre, String textoTelefono) {
            this.idCliente = textoIdCliente;
            this.nombre = textoNombre;
            this.telefono = textoTelefono;
        }

        protected Boolean doInBackground(String... params) {

            boolean resul = true;
            final String NAMESPACE = "http://tempuri.org/";
            final String URL="http://10.107.57.21:8282/ServicioWebSoap2/ServicioClientes.asmx";
            final String METHOD_NAME = "ClienteIndividual";
            final String SOAP_ACTION = "http://tempuri.org/ClienteIndividual";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            request.addProperty("textoIdCliente", idCliente);

            SoapSerializationEnvelope envelope =
                    new SoapSerializationEnvelope(SoapEnvelope.VER11);

            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);


            try {
                transporte.call(SOAP_ACTION, envelope);
            } catch (IOException e) {
                Log.e("error",e.toString());
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("error",e.toString());
                e.printStackTrace();
            }

            try {
                SoapObject resSoap =(SoapObject)envelope.getResponse();

                cliente = new Cliente();
                //SoapObject ic = (SoapObject)resSoap.getProperty(0);
                cliente.id = Integer.parseInt(resSoap.getProperty(0).toString());
                cliente.nombre = resSoap.getProperty(1).toString();
                cliente.telefono = Integer.parseInt(resSoap.getProperty(2).toString());

            } catch (SoapFault soapFault) {
                soapFault.printStackTrace();
                resul = false;
            }



            return resul;
        }

        protected void onPostExecute(Boolean result) {

            if (result)
            {
                //Rellenamos la lista con los nombres de los clientes

                txtIdCliente.setText(Integer.toString(cliente.id));
                txtNombre.setText(cliente.nombre);
                txtTelefono.setText(Integer.toString(cliente.telefono));
            }
            else
            {
                txtResultado.setText("Error!");
            }
        }
    }
}
