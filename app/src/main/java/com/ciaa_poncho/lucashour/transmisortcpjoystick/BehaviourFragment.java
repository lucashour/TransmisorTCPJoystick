package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BehaviourFragment extends Fragment implements View.OnTouchListener {

    private TextView x_axis;
    private TextView y_axis;
    private ImageView joystick;
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
            joystick = (ImageView) view.findViewById(R.id.joystick);
            joystick.setOnTouchListener(this);
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

    private boolean existsIpAddress(){
        if (TcpSocketData.getInstance().getIpAddress() == null) {
            showToastMessage("Configuración de dirección IP destino requerida.");
            return false;
        }
        return true;
    }

    private static String formatMessageToSend(int value, int motorId){
        String string = String.valueOf(value);
        return ("%" + String.valueOf(string.length() + 1) + String.valueOf(motorId) + string);
    }

    private void displayInformationMessage(String message){
        if (!message.equals(""))
            showToastMessage(message);
    }

    private void showToastMessage(String message){
        showToast(message, Toast.LENGTH_SHORT);
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

    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getActionMasked();
        int value_y;
        int value_x;


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
        x_axis.setText(String.valueOf(pwm_motor1));
        y_axis.setText(String.valueOf(pwm_motor2));

        return true;
    }

    public int formatNumber(float value){
        return Math.round((value / this.joystick.getHeight()) * 200);
    }
}