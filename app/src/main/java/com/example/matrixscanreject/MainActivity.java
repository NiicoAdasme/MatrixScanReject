package com.example.matrixscanreject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MainActivity extends Activity {

    private Spinner spinnerBodega;
    private Spinner spinnerUbi;
    private Button btnEscanear;

    private RequestQueue queue;

    List<String> listBodega = new ArrayList<>();
    JSONArray bodegas = new JSONArray();
    String bodegaID = "Seleccione Bodega...";
    String posicionID = "Seleccione Posicion...";

    List<String> listPosicion = new ArrayList<>();
    JSONArray posiciones = new JSONArray();

    ArrayList<String> listLotes = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerBodega = (Spinner) findViewById(R.id.spinnerBodega);
        spinnerUbi = (Spinner) findViewById(R.id.spinnerUbi);
        btnEscanear = (Button) findViewById(R.id.btnSiguiente);

        // Before starting Scan, we need the codes available
        queue = Volley.newRequestQueue(this);

        listBodega.add("Seleccione Bodega...");
        listPosicion.add("Seleccione Posicion...");

        // Se reciben los datos de las bodegas.
        getBodega("http://35.211.170.102/getBodegas.php");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listBodega);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBodega.setAdapter(adapter);
        spinnerBodega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //((TextView) view).setTextColor(Color.RED);
                String text = parent.getItemAtPosition(position).toString();
                Log.v("Bodega Selected", text);
                // do a request to fetch the position from store
                // code...
                // find the store id for the store selected
                for (int i=0; i < bodegas.length(); i++) {
                    try {
                        JSONObject object = new JSONObject(bodegas.get(i).toString());
                        //Log.v("TAG_obj", object.toString());
                        //Log.v("TAG_spinner_value", spinnerBodega.getSelectedItem().toString());
                        //Log.v("on item selected", text);
                        if (!(object.getString("bodega_nombre").equals("Seleccione Bodega...")) && object.getString("bodega_nombre").equals(spinnerBodega.getSelectedItem().toString()) || object.getString("bodega_nombre").equals(text)) {
                            bodegaID = object.getString("bodega_id");
                            Log.v("bodega_id", bodegaID);
                            // fetch to get positions
                            getPosicion("http://35.211.170.102/getPosicion.php?bodega_id="+bodegaID);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // getPosicion
        // if pos >= 1  then
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listPosicion);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUbi.setAdapter(adapter1);

        spinnerUbi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Log.v("Position Selected", text);
                for (int i=0; i < posiciones.length(); i++) {
                    try {
                        JSONObject object = new JSONObject(posiciones.get(i).toString());
                        if (!(object.getString("posicion_nombre").equals("Seleccione Posicion...")) && object.getString("posicion_nombre").equals(spinnerUbi.getSelectedItem().toString()) || object.getString("posicion_nombre").equals(text)) {
                            posicionID = object.getString("posicion_id");
                            Log.v("PoscionID", posicionID);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // get Lotes and check if exists
                getLotes("http://35.211.170.102/getLote.php?posicion_id="+posicionID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Click escanear
        btnEscanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bodegaID == "Seleccione Bodega..."){
                    Toast.makeText(MainActivity.this, "Por favor seleccione una bodega", Toast.LENGTH_LONG).show();
                }else{
                    if (posicionID == "Seleccione Posicion..."){
                        Toast.makeText(MainActivity.this, "Por favor seleccione una posición", Toast.LENGTH_LONG).show();
                    }else{
                        if (listLotes.size() > 0) {
                            Intent intent = new Intent(MainActivity.this, MatrixScanActivity.class);
                            intent.putExtra("bodega", bodegaID);
                            intent.putExtra("posicion", posicionID);
                            startActivity(intent, savedInstanceState);
                        }else{
                            Toast.makeText(MainActivity.this, "No hay lotes asociados a esta posición", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }

    public void getBodega(String URL) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++) {
                    try {
                        bodegas.put(response.get(i));
                        JSONObject object = new JSONObject(response.get(i).toString());;
                        listBodega.add(object.getString("bodega_nombre"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.v("Object Bodegas", bodegas.toString());
                Log.v("listBodega", listBodega.toString() );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("VolleyError", error.toString());
            }
        })
        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                Log.d("VolleyError", "volleyError" + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","mmunoz","a8dab92ff8dfa9f45aa89f551c56188d93683c4aa3db6b84aba748872221b934");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization ", auth);
                return params;
            }
        };
        queue.add(request);
    }

    public void getPosicion(String URL) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                // make sure to clear the previous data before fetch
                listPosicion.clear();
                listPosicion.add("Seleccione Posicion...");
                for (int i=0; i < response.length(); i++) {
                    try {
                        posiciones.put(response.get(i));
                        JSONObject object = new JSONObject(response.get(i).toString());;
                        listPosicion.add(object.getString("posicion_nombre"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.v("Object Posiciones", posiciones.toString());
                Log.v("listPosicion", listPosicion.toString() );
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR_TAG", error.toString());
            }
        })
        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                Log.d("VolleyError", "volleyError" + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("bodega_id", bodegaID);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","mmunoz","a8dab92ff8dfa9f45aa89f551c56188d93683c4aa3db6b84aba748872221b934");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization ", auth);
                return params;
            }
        };
        queue.add(request);
    }

    public void getLotes(String URL) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i < response.length(); i++) {
                    try {
                        JSONObject object = new JSONObject(response.get(i).toString());
                        listLotes.add(object.getString("lote"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.v("listLotes", listLotes.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyError", error.toString());
            }
        })
        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                Log.e("VolleyError", volleyError.toString());
                return super.parseNetworkError(volleyError);
            }


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s","mmunoz","a8dab92ff8dfa9f45aa89f551c56188d93683c4aa3db6b84aba748872221b934");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization ", auth);
                return params;
            }
        };
        queue.add(request);
    }

}


