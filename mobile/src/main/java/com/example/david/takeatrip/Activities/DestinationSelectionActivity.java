package com.example.david.takeatrip.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.example.david.takeatrip.R;

public class DestinationSelectionActivity extends AppCompatActivity {


    private final int TEMPERATURA_MASSIMA_PICKER = 40;
    private final int TEMPERATURA_MINIMA_PICKER = 20;
    private final int PRESSIONE_MASSIMA_PICKER = 1500;
    private final int PRESSIONE_MINIMA_PICKER = 800;
    private final int PRESSIONE_DEFAULT_PICKER = 1000;



    private NumberPicker pickerPressure, pickerTemperature;
    private Spinner HumiditySpinner, WindSpinner, TemperatureSpinner;
    private Button SearchButton;

    private ArrayAdapter<CharSequence> adapter,adapter2,adapter3;
    private int positionOnSpinner = 1, positionOnSpinner2 = 1, positionOnSpinner3 = 3;



    private String temperature, pressure, humidity, speedWind;
    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_selection);


        if(getIntent() != null){
            Intent intent = getIntent();
            email = intent.getStringExtra("email");

        }

        /*
        pickerTemperature = (NumberPicker)findViewById(R.id.TemperaturePicker);
        pickerTemperature.setMaxValue(TEMPERATURA_MASSIMA_PICKER);
        pickerTemperature.setMinValue(TEMPERATURA_MINIMA_PICKER);
        pickerTemperature.setWrapSelectorWheel(false);
        */

        pickerPressure = (NumberPicker)findViewById(R.id.PressurePicker);
        pickerPressure.setMaxValue(PRESSIONE_MASSIMA_PICKER);
        pickerPressure.setMinValue(PRESSIONE_MINIMA_PICKER);
        pickerPressure.setWrapSelectorWheel(false);
        pickerPressure.setValue(PRESSIONE_DEFAULT_PICKER);


        HumiditySpinner = (Spinner)findViewById(R.id.spinnerHumidity);
        WindSpinner = (Spinner)findViewById(R.id.spinnerWind);
        TemperatureSpinner = (Spinner)findViewById(R.id.spinnerTemperature);


        adapter = ArrayAdapter.createFromResource(this, R.array.livelliUmidita, android.R.layout.simple_spinner_item);
        adapter2 = ArrayAdapter.createFromResource(this, R.array.livelloVento, android.R.layout.simple_spinner_item);
        adapter3 = ArrayAdapter.createFromResource(this, R.array.livelloTemperatura, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        HumiditySpinner.setAdapter(adapter);
        HumiditySpinner.setSelection(positionOnSpinner);
        WindSpinner.setAdapter(adapter2);
        WindSpinner.setSelection(positionOnSpinner2);
        TemperatureSpinner.setAdapter(adapter3);
        TemperatureSpinner.setSelection(positionOnSpinner3);


        HumiditySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                positionOnSpinner = position;
                humidity = adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        WindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionOnSpinner2 = position;
                speedWind = adapter2.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TemperatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                positionOnSpinner3 = position;

                String stringTemperature = adapter3.getItem(position).toString();



                if(stringTemperature.toLowerCase().equals("very very cold")){
                    double temperature2 = 273.15 - 15;
                    temperature = String.valueOf(temperature2);
                }
                else if(stringTemperature.toLowerCase().equals("very cold")){
                    double temperature2 = 273.15 - 5;
                    temperature = String.valueOf(temperature2);
                }
                else if(stringTemperature.toLowerCase().equals("cold")){
                    double temperature2 = 273.15 + 5;
                    temperature = String.valueOf(temperature2);
                }
                else if(stringTemperature.toLowerCase().equals("moderate")){
                    double temperature2 = 273.15 + 15;
                    temperature = String.valueOf(temperature2);
                }
                else if(stringTemperature.toLowerCase().equals("hot")){
                    double temperature2 = 273.15 + 25;
                    temperature = String.valueOf(temperature2);
                }
                else if(stringTemperature.toLowerCase().equals("very hot")){
                    double temperature2 = 273.15 + 35;
                    temperature = String.valueOf(temperature2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        SearchButton = (Button)findViewById(R.id.AvviaRicercaDestinazione);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // definisco l'intenzione


                pressure = String.valueOf(pickerPressure.getValue());


                Intent intent = new Intent(DestinationSelectionActivity.this, PossibleDestinationActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("temperatura", temperature);
                intent.putExtra("pressione", pressure);
                intent.putExtra("umidita", humidity);
                intent.putExtra("velocitaVento", speedWind);

                // passo all'attivazione dell'activity
                startActivity(intent);
            }
        });





    }

}
