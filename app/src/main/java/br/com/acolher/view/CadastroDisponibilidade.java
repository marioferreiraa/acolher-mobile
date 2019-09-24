package br.com.acolher.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import br.com.acolher.R;
import br.com.acolher.apiconfig.RetrofitInit;
import br.com.acolher.controller.DisponibilidadeController;
import br.com.acolher.controller.UsuarioController;
import br.com.acolher.model.Consulta;
import br.com.acolher.model.Endereco;
import br.com.acolher.model.Status;
import br.com.acolher.model.Usuario;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroDisponibilidade extends AppCompatActivity {

    private RetrofitInit retrofitInit = new RetrofitInit();
    private static final String TAG = "API";
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TextInputLayout inputNome, inputData, inputHora, inputCPR_CRM;
    private ImageButton btnCalendar;
    private Button concluirCadastro, buttonCancelar;
    private int currentHour;
    private int currentMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_cadastro_disponibilidade);

        pegaIdCampos();

        inputCPR_CRM.getEditText().setText("8454654");
        inputCPR_CRM.setEnabled(false);
        inputNome.getEditText().setText("Medico");
        inputNome.setEnabled(false);
        inputData.getEditText().setText("23/10/2019");

        concluirCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( validateForm() ){
                    Consulta novaConsulta = new Consulta();
                    Endereco endereco = new Endereco(4,"52000000","R rua","Recife","PE", "Bairro", "150","-34.91507716476917","-8.149791409918464");
                    Usuario profissional = new Usuario();
                    profissional.setCodigo(1);
                    String hora = inputHora.getEditText().getText().toString();
                    String sData = inputData.getEditText().getText().toString();


                    novaConsulta.setData(sData);
                    novaConsulta.setHora(hora);
                    novaConsulta.setStatusConsulta(Status.DISPONIVEL);
                    novaConsulta.setEndereco(endereco);
                    novaConsulta.setProfissional(profissional);

                    cadastroConsulta(novaConsulta);

                }

            }
        });

        buttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(CadastroDisponibilidade.this, MapsActivity.class);
                startActivity(home);
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTime();
                openCalendar();
            }
        });

        inputData.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputData.getEditText().clearFocus();
                    openCalendar();
                }
            }
        });
        inputHora.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    inputHora.getEditText().clearFocus();
                    openTime();
                }
            }
        });

    }

    private void pegaIdCampos() {
        inputCPR_CRM = findViewById(R.id.input_CRP_CRM);
        inputNome = findViewById(R.id.inputNomeCompleto);
        inputData = findViewById(R.id.inputDataNasc);
        btnCalendar = findViewById(R.id.btnCalendar);
        inputHora = findViewById(R.id.inputHora);
        buttonCancelar = findViewById(R.id.buttonCancelar);
        concluirCadastro = findViewById(R.id.buttonConcluirCadastro);
    }

    public void openCalendar(){

        inputData.setErrorEnabled(false);

        calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(CadastroDisponibilidade.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                inputData.getEditText().setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void openTime(){
        calendar = Calendar.getInstance();
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        currentMinute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(CadastroDisponibilidade.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                inputHora.getEditText().setText(String.format("%02d:%02d", hourOfDay, minutes));
            }
        }, currentHour, currentMinute, true);

        timePickerDialog.show();
    }

    public boolean validateForm(){

        String nome = inputNome.getEditText().getText().toString();
        String data = inputData.getEditText().getText().toString();
        String hora = inputHora.getEditText().getText().toString();

        if(!DisponibilidadeController.empty(nome)){
            inputNome.setError("Nome não dede ficar em branco");
            return false;
        }

        if(!DisponibilidadeController.empty(data)){
            inputData.setError("Campo Obrigatorio");
            return false;
        }

        if(!DisponibilidadeController.empty(hora)){
            inputHora.setError("Campo Obrigatorio");
            return false;
        }


        return true;
    }


    private void cadastroConsulta(Consulta consulta){

        Call<Consulta> cadastro = retrofitInit.getService().cadastroConsulta(consulta);

        cadastro.enqueue(new Callback<Consulta>() {
            @Override
            public void onResponse(Call<Consulta> call, Response<Consulta> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, String.valueOf(response.code()));
                    Intent home = new Intent(CadastroDisponibilidade.this, MapsActivity.class);
                    startActivity(home);
                } else {
                    Log.d(TAG, "erro");
                    Log.d(TAG, String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Consulta> call, Throwable t) {

            }
        });
    }
}