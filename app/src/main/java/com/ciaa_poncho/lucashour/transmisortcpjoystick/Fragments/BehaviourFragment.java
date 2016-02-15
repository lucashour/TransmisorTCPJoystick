package com.ciaa_poncho.lucashour.transmisortcpjoystick.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ciaa_poncho.lucashour.transmisortcpjoystick.Calibration.CalibrationData;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.R;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.TCP.TcpSocketData;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.TCP.TcpSocketManager;
import com.ciaa_poncho.lucashour.transmisortcpjoystick.GeneralGUI.ToastManager;

public class BehaviourFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private TextView x_axis;
    private TextView y_axis;
    private TextView direction;
    private ImageView joystick;
    private CheckBox calibration;
    private Toast toast;
    private int pwm_motor1 = 0;
    private int pwm_motor2 = 0;

    public BehaviourFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TcpSocketData.getInstance().setUI_context(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view =  inflater.inflate(R.layout.fragment_behaviour, container, false);
        if(view != null){
            x_axis = (TextView) view.findViewById(R.id.x_axis);
            y_axis = (TextView) view.findViewById(R.id.y_axis);
            direction = (TextView) view.findViewById(R.id.direction);
            joystick = (ImageView) view.findViewById(R.id.joystick);
            joystick.setOnTouchListener(this);
            calibration = (CheckBox) view.findViewById(R.id.calibration_checkbox);
            calibration.setOnClickListener(this);
            toast = new Toast(getActivity().getApplicationContext());
        }

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menú de opciones
    }

    private static String formatMessageToSend(int value, int motorId){
        String string = String.valueOf(value);
        return ("%" + String.valueOf(string.length() + 1) + String.valueOf(motorId) + string);
    }

    /* Listeners (touch y click) */

    public boolean onTouch(View v, MotionEvent event) {
        if (calibration.isChecked())
            performBehaviorWithCalibration(event);
        else
            performBehaviorWithoutCalibration(event);
        return true;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.calibration_checkbox){
            resetMotorsAndJoystick();;
        }
    }

    /* Formateo de strings */

    private int formatNumber(float value){
        return Math.round((value / this.joystick.getHeight()) * 200);
    }

    private String formatNumberToMotorExpression(int value, int motor_id){
        String pwm;
        if (value < 100){
            direction.setText("AVANCE");
            pwm = String.valueOf(100 - value);
        }
        else if (value == 100) {
            direction.setText("FRENADO");
            pwm = "0";
        }
        else{
            direction.setText("RETROCESO");
            pwm = String.valueOf(value - 100);
        }
        return pwm + "%";
    }


    /* Acciones para onTouch */

    private void performBehaviorWithoutCalibration(MotionEvent event){
        int action = event.getActionMasked();
        int value_y, value_x;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                value_y = formatNumber(event.getY());
                value_x = formatNumber(event.getX());

                if (value_x < 200 && value_x > 0 && value_y < 200 && value_y > 0){
                    pwm_motor1 = value_y;
                    pwm_motor2 = value_y;
                    if (value_x < 95){
                        if (value_y < 100)
                            pwm_motor1 = value_y + (int)((100 - value_x)*(100 - value_y)/100.0);
                        else
                            pwm_motor1 = value_y - (int)((100 - value_x)*(value_y - 100)/100.0);
                    }
                    if (value_x > 105){
                        if (value_y < 100)
                            pwm_motor2 = value_y + (int)((value_x - 100)*(100 - value_y)/100.0);
                        else
                            pwm_motor2 = value_y - (int)((value_x - 100)*(value_y - 100)/100.0);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                value_y = formatNumber(event.getY());
                value_x = formatNumber(event.getX());

                if (value_x < 200 && value_x > 0 && value_y < 200 && value_y > 0){
                    pwm_motor1 = value_y;
                    pwm_motor2 = value_y;
                    if (value_x < 95){
                        if (value_y < 100)
                            pwm_motor1 = value_y + (int)((100 - value_x)*(100 - value_y)/100.0);
                        else
                            pwm_motor1 = value_y - (int)((100 - value_x)*(value_y - 100)/100.0);
                    }
                    if (value_x > 105){
                        if (value_y < 100)
                            pwm_motor2 = value_y + (int)((value_x - 100)*(100 - value_y)/100.0);
                        else
                            pwm_motor2 = value_y - (int)((value_x - 100)*(value_y - 100)/100.0);
                    }
                }
                break;
        }
        TcpSocketManager.sendDataToSocket(formatMessageToSend(pwm_motor1, 0));
        TcpSocketManager.sendDataToSocket(formatMessageToSend(pwm_motor2, 1));
        x_axis.setText(formatNumberToMotorExpression(pwm_motor1, 1));
        y_axis.setText(formatNumberToMotorExpression(pwm_motor2, 2));
    }

    private void performBehaviorWithCalibration(MotionEvent event){
        if (CalibrationData.getInstance().getMax_RPM() != 0 ) {
            int action = event.getActionMasked();
            int value_y, value_x;
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    value_y = formatNumber(event.getY());
                    value_x = formatNumber(event.getX());

                    if (value_x < 200 && value_x > 0 && value_y < 200 && value_y > 0) {
                        if (value_y < 100) {
                            pwm_motor1 = 100 - (int) (((100 - value_y) * CalibrationData.getInstance().getMax_pwm_motor0()) / 100.0);
                            pwm_motor2 = 100 - (int) (((100 - value_y) * CalibrationData.getInstance().getMax_pwm_motor1()) / 100.0);
                        } else {
                            pwm_motor1 = (int) (((value_y - 100) * CalibrationData.getInstance().getMax_pwm_motor0()) / 100.0) + 100;
                            pwm_motor2 = (int) (((value_y - 100) * CalibrationData.getInstance().getMax_pwm_motor1()) / 100.0) + 100;
                        }

                        if (value_x < 95) {
                            if (value_y < 100)
                                pwm_motor1 = pwm_motor1 + (int) ((100 - value_x) * (100 - pwm_motor1) / 100.0);
                            else
                                pwm_motor1 = pwm_motor1 - (int) ((100 - value_x) * (pwm_motor1 - 100) / 100.0);
                        }
                        if (value_x > 105) {
                            if (value_y < 100)
                                pwm_motor2 = pwm_motor2 + (int) ((value_x - 100) * (100 - pwm_motor2) / 100.0);
                            else
                                pwm_motor2 = pwm_motor2 - (int) ((value_x - 100) * (pwm_motor2 - 100) / 100.0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    value_y = formatNumber(event.getY());
                    value_x = formatNumber(event.getX());

                    if (value_x < 200 && value_x > 0 && value_y < 200 && value_y > 0) {
                        if (value_y < 100) {
                            pwm_motor1 = 100 - (int) (((100 - value_y) * CalibrationData.getInstance().getMax_pwm_motor0()) / 100.0);
                            pwm_motor2 = 100 - (int) (((100 - value_y) * CalibrationData.getInstance().getMax_pwm_motor1()) / 100.0);
                        } else {
                            pwm_motor1 = (int) (((value_y - 100) * CalibrationData.getInstance().getMax_pwm_motor0()) / 100.0) + 100;
                            pwm_motor2 = (int) (((value_y - 100) * CalibrationData.getInstance().getMax_pwm_motor1()) / 100.0) + 100;
                        }

                        if (value_x < 95) {
                            if (value_y < 100)
                                pwm_motor1 = pwm_motor1 + (int) ((100 - value_x) * (100 - pwm_motor1) / 100.0);
                            else
                                pwm_motor1 = pwm_motor1 - (int) ((100 - value_x) * (pwm_motor1 - 100) / 100.0);
                        }
                        if (value_x > 105) {
                            if (value_y < 100)
                                pwm_motor2 = pwm_motor2 + (int) ((value_x - 100) * (100 - pwm_motor2) / 100.0);
                            else
                                pwm_motor2 = pwm_motor2 - (int) ((value_x - 100) * (pwm_motor2 - 100) / 100.0);
                        }
                    }
                    break;
            }
            TcpSocketManager.sendDataToSocket(formatMessageToSend(pwm_motor1, 0));
            TcpSocketManager.sendDataToSocket(formatMessageToSend(pwm_motor2, 1));
            x_axis.setText(formatNumberToMotorExpression(pwm_motor1, 1));
            y_axis.setText(formatNumberToMotorExpression(pwm_motor2, 2));
        }
        else
            ToastManager.showToastMessage("Es necesario CALIBRAR.", toast, getActivity());
    }

    /* Acciones para onClick */

    private void resetMotorsAndJoystick(){
        TcpSocketManager.sendDataToSocket(formatMessageToSend(100, 0));
        TcpSocketManager.sendDataToSocket(formatMessageToSend(100, 1));
        x_axis.setText(formatNumberToMotorExpression(100, 1));
        y_axis.setText(formatNumberToMotorExpression(100, 2));
    }
}