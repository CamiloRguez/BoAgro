package com.example.bo_1.boagro;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import at.grabner.circleprogress.CircleProgressView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // URL to get contacts JSON
    private ProgressDialog pDialog;
    private String TAG = WeatherFragment.class.getSimpleName();
    private static String url = "http://192.168.1.148:3001/api/medidas";
    ArrayList<HashMap<String, String>> weatherList;
    private TextView textTemp, textHum;
    HashMap<String, String> medida = new HashMap<>();

    //CircleView
    CircleProgressView mCircleView;

    public WeatherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);



        //CODIGO
        weatherList = new ArrayList<>();

        textTemp = (TextView) view.findViewById(R.id.textTemp);
        textHum = (TextView) view.findViewById(R.id.textHum);
        new GetWeather().execute();
        //***** CODIGO

        //CircleView
        mCircleView = (CircleProgressView) view.findViewById(R.id.tempView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d(TAG, "Progress Changed: " + value);
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //CODIGO
    private class GetWeather extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(WeatherFragment.this.getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = null;
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(jsonStr);
                        jsonObj = jsonArr.getJSONObject(0);
                        Log.d("Objeto: ",jsonObj.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Obtener hora
                    String temp = null, hum = null;
                    if (jsonObj != null) {
                        jsonArr = jsonObj.getJSONArray("datalogers");
                        Log.d("jsonArr: ",jsonArr.toString());
                        JSONObject jsonDataloger = jsonArr.getJSONObject(0);
                        Log.d("jsonData: ",jsonDataloger.toString());
                        JSONObject jsonValores = jsonDataloger.getJSONObject("valores");
                        Log.d("jsonValores: ",jsonValores.toString());
                        temp = jsonValores.getString("temp_inv");
                        Log.d("Temp_inv: ",temp);
                        hum = jsonValores.getString("hum_inv");
                        Log.d("Hum_inv: ",hum);
                    }else{
                        Log.d("NULO","NULO");
                    }

                    medida.put("temp", temp);
                    medida.put("hum",hum);
                    weatherList.add(medida);
                    Log.d("WeatherList: ",weatherList.get(0).toString());


                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            textTemp.setText(medida.get("temp")+"º");
            textHum.setText(medida.get("hum")+"%");
            /*ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, medidaList,
                    R.layout.list_item, new String[]{"id", "hora"},
                    new int[]{R.id.id, R.id.hora});

            lv.setAdapter(adapter);*/
        }

    }
    //***** CODIGO
}
