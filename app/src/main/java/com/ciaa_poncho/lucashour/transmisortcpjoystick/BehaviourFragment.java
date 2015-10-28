package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class BehaviourFragment extends Fragment implements View.OnTouchListener {

    private TextView x_axis;
    private TextView y_axis;
    private ImageView joystick;
    private Toast toast;
    private int scale;

    public BehaviourFragment(){}

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        scale = joystick.getHeight();
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

    private static String formatMessageToSend(int value){
        String string = String.valueOf(value);
        return (String.valueOf(string.length() + 1) + "%" + string);
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
        int value;
        switch (action){
            case MotionEvent.ACTION_DOWN:
                value = formatNumber(event.getX());
                if (value <= 200 && value >= 0)
                    x_axis.setText(String.valueOf(value));
                value = formatNumber(event.getY());
                if (value <= 200 && value >= 0)
                    y_axis.setText(String.valueOf(value));
                break;
            case MotionEvent.ACTION_MOVE:
                value = formatNumber(event.getX());
                if (value <= 200 && value >= 0)
                    x_axis.setText(String.valueOf(value));
                value = formatNumber(event.getY());
                if (value <= 200 && value >= 0)
                    y_axis.setText(String.valueOf(value));
                break;
        }

        return true;
    }

    public int formatNumber(float value){
        return Math.round((value / this.joystick.getHeight()) * 200);
    }
}