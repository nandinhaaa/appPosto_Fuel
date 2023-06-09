package br.edu.ifsuldeminas.mch.fuel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    //Atributes
    private TextInputEditText textInputEditTextEtanol;
    private TextInputEditText textInputEditTextGas;
    private Button buttonCalcular;
    private ImageView imageViewFuel;
    private ImageView imageViewShare;
    private TextView textViewMessage;
    private static final String PREFS_KEY = "fuel_price";
    private static final String PRECO_ETANOL = "preco_etanol";
    private static final String PRECO_GAS = "preco_gas";
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Carregar o preferences
        preferences = getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);

        // Carregar Componentes

        textInputEditTextEtanol = findViewById(R.id.textInputEditTextEtanol);
        textInputEditTextGas = findViewById(R.id.textInputEditTextGas);
        buttonCalcular = findViewById(R.id.buttonCalcular);
        imageViewFuel = findViewById(R.id.imageViewFuel);
        imageViewShare = findViewById(R.id.imageViewShare);
        textViewMessage = findViewById(R.id.textViewMessage);

        escondeComponentes();

        buttonCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Recuperar Valores
                String stringEtanol = textInputEditTextEtanol.getText().toString();
                String stringGas = textInputEditTextGas.getText().toString();

                //Validar Valores
                if (stringEtanol.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Campo Etanol não pode ser vazio", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Cast dos valores para numero
                Double valorEtanol = Double.parseDouble(stringEtanol);
                Double valorGas = Double.parseDouble(stringGas);

                //Calcula o melhor combustivel
                if (valorEtanol / valorGas <= 0.7) {
                    // Melhor etanol
                    imageViewFuel.setImageResource(R.drawable.ethanol);
                    textViewMessage.setText("Melhor usar Etanol");
                } else {
                    // Melhor gasolina
                    imageViewFuel.setImageResource(R.drawable.gas);
                    textViewMessage.setText("Melhor usar Gasolina");
                }
                textViewMessage.setVisibility(TextView.VISIBLE);
                imageViewFuel.setVisibility(ImageView.VISIBLE);
                imageViewShare.setVisibility(ImageView.VISIBLE);
            }
        });

    }

    private void escondeComponentes() {
        imageViewFuel.setVisibility(ImageView.INVISIBLE);
        imageViewShare.setVisibility(ImageView.INVISIBLE);
        textViewMessage.setVisibility(TextView.INVISIBLE);
    }

    public void shareClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Preços de qual Posto?");

        LayoutInflater inflater = getLayoutInflater();

        View alert = inflater.inflate(R.layout.alert_dialog_gas_station_view, null);

        builder.setView(alert);

        builder.setPositiveButton("Enviar", (dialogInterface, i) -> {
            EditText nomePosto = alert.findViewById(R.id.editTextAlertDialogGasStationId);

            String nomePostoStr = nomePosto.getText().toString();


            // Recuperar valores
            String stringEtanol = textInputEditTextEtanol.getText().toString();
            String stringGas = textInputEditTextGas.getText().toString();
            Double valorEtanol = Double.parseDouble(stringEtanol);
            Double valorGas = Double.parseDouble(stringGas);

            Double proporcao = valorEtanol / valorGas * 100;
            @SuppressLint("DefaultLocale")
            String mensagem = String.format("Preços no posto %s. Etanol R$%.2f " + "Gasolina R$%.2f Proporção R$%.1f. Melhor usar '%s'", nomePostoStr, valorEtanol, valorGas, proporcao, proporcao > 70 ? "Gasolina" : "Etanol");

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra("msg", mensagem);
            intent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(intent, "Compartilhar Preços Combustível");
            startActivity(shareIntent);

        });

        builder.setNegativeButton("cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    //Método onStop criado para quando a variavel n estiver vazia salvar os últimos dados inseridos.
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = preferences.edit();

        String valorEtanol = textInputEditTextEtanol.getText().toString();

        if (!valorEtanol.equals("")) {
            editor.putString(PRECO_ETANOL, valorEtanol);
            editor.apply();
            //ou editor.commit();
        }

        String valorGas = textInputEditTextGas.getText().toString();
        if (!valorGas.equals("")) {
            editor.putString(PRECO_GAS, valorGas);
            editor.apply();
        }
    }

    @Override
    // o Método onResume ele retorna os dados que foram inseridos em primeiro plano
    protected void onResume() {
        super.onResume();

        if (preferences.contains(PRECO_ETANOL)){
            String precoEtanolSalvo = preferences.getString(PRECO_ETANOL, "");
            textInputEditTextEtanol.setText(precoEtanolSalvo);
        }
        if(preferences.contains(PRECO_GAS)){
            String precoGasSalvo = preferences.getString(PRECO_GAS, "");
            textInputEditTextGas.setText(precoGasSalvo);
        }
    }
}