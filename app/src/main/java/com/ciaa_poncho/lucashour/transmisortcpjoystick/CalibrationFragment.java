package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CalibrationFragment extends Fragment implements View.OnClickListener {

    private EditText calibration_time;
    private EditText calibration_slots;
    private Button calibrate_motor_0_button;
    private Button calibrate_motor_1_button;
    private Button cancel_calibration_button;
    private ProgressBar motor_0_progress;
    private ProgressBar motor_1_progress;
    private Toast toast;

    public CalibrationFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TcpSocketData.getInstance().setUI_context(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_calibration, container, false);

        if(view != null){
            calibration_time = (EditText) view.findViewById(R.id.calibration_time);
            calibration_slots = (EditText) view.findViewById(R.id.calibration_slots);
            calibrate_motor_0_button = (Button) view.findViewById(R.id.calibrate_motor_0_button);
            calibrate_motor_1_button = (Button) view.findViewById(R.id.calibrate_motor_1_button);
            cancel_calibration_button = (Button) view.findViewById(R.id.cancel_calibration_button);
            motor_0_progress = (ProgressBar) view.findViewById(R.id.motor_0_progress);
            motor_1_progress = (ProgressBar) view.findViewById(R.id.motor_1_progress);
            toast = new Toast(getActivity().getApplicationContext());
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calibrate_motor_0_button.setOnClickListener(this);
        calibrate_motor_1_button.setOnClickListener(this);
        cancel_calibration_button.setOnClickListener(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menú de opciones
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calibrate_motor_0_button:
                if (isInformationCompleted())
                    initializeMotorCalibration(0);
                break;
            case R.id.calibrate_motor_1_button:
                if (isInformationCompleted())
                    initializeMotorCalibration(1);
                break;
            case R.id.cancel_calibration_button:
                cancelCalibrationProcess();
                break;
        }
    }

    /* Entradas de tiempo y ranuras de disco */

    private boolean existsTime(){
        int time = getTimeFromInput();
        return (time > 0 && time <= 10000);
    }

    private boolean existsSlots(){
        int slots = getSlotsFromInput();
        return (slots > 0);
    }

    private int getTimeFromInput() {
        return Integer.valueOf(calibration_time.getText().toString());
    }

    private int getSlotsFromInput() {
        return Integer.valueOf(calibration_slots.getText().toString());
    }

    /* Acciones de botones */

    private boolean isInformationCompleted(){
        boolean condition = !calibration_time.getText().toString().equals("") && !calibration_slots.getText().toString().equals("");
        if (!condition)
            showToastMessage("No se han configurado todos los parámetros.");
        return condition;
    }

    private void initializeMotorCalibration(int motor){
        switch (motor){
            case 0:
                motor_0_progress.setProgress(0);
                break;
            case 1:
                motor_1_progress.setProgress(0);
                break;
        }
        int time = getTimeFromInput();
        if (time <= 0 || time > 10000)
            showLongToastMessage("Error de ingreso: El tiempo sólo puede variar entre 1 y 10000 ms");
        else {
            GlobalData.getInstance().setTime(getTimeFromInput());
            GlobalData.getInstance().setSlots(getSlotsFromInput());
            displayInformationMessage(TcpSocketManager.sendDataToSocket("$CARACTERIZAR=" + String.valueOf(motor) + ","+ String.valueOf(getTimeFromInput()) + "$"));
            }
    }

    private void cancelCalibrationProcess(){
        motor_0_progress.setProgress(0);
        motor_1_progress.setProgress(0);
        displayInformationMessage(TcpSocketManager.sendDataToSocket("$CANCELAR_CARACTERIZAR$"));
    }

    /* Toasts */

    private void displayInformationMessage(String message){
        if (!message.equals(""))
            showToastMessage(message);
    }

    private void showToastMessage(String message){
        showToast(message,Toast.LENGTH_SHORT);
    }

    private void showLongToastMessage(String message){
        showToast(message, Toast.LENGTH_LONG);
    }

    private void showToast(String message, int duration){
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(this.getActivity().getApplicationContext(), message, duration);
        toast.show();
    }
}
