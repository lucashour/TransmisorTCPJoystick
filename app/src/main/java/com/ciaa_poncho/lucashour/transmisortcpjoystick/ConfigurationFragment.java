package com.ciaa_poncho.lucashour.transmisortcpjoystick;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigurationFragment extends Fragment implements View.OnClickListener {

    private EditText ip_address;
    private EditText port_number;
    private Button button;
    private Button connect_button;
    private Button disconnect_button;
    private Toast toast;

    public ConfigurationFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_config, container, false);

        if (v != null) {

            button = ((Button) v.findViewById(R.id.button));
            connect_button = ((Button) v.findViewById(R.id.connect_button));
            disconnect_button = ((Button) v.findViewById(R.id.disconnect_button));
            ip_address = ((EditText) v.findViewById(R.id.ip_address));
            ip_address.setText(TcpSocketData.getInstance().getIpAddress());
            port_number = ((EditText) v.findViewById(R.id.port_number));
            port_number.setText(String.valueOf(TcpSocketData.getInstance().getPortNumber()));
            toast = new Toast(getActivity().getApplicationContext());
        }

        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(this);
        connect_button.setOnClickListener(this);
        disconnect_button.setOnClickListener(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false); //Indicamos que este Fragment no tiene su propio menu de opciones
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.connect_button:
                displayInformationMessage(TcpSocketManager.connectToSocket());
                break;
            case R.id.disconnect_button:
                displayInformationMessage(TcpSocketManager.disconnectFromSocket());
                break;
            case R.id.button:
                if (ip_address.getText().toString().isEmpty())
                    ip_address.setText("192.168.4.1");
                port_number.setText("8080");
                TcpSocketData.getInstance().setIpAddress(ip_address.getText().toString());
                TcpSocketData.getInstance().setPortNumber(Integer.parseInt(port_number.getText().toString()));
                Toast.makeText(view.getContext(), "Datos configurados correctamente", Toast.LENGTH_SHORT).show();
                break;


        }
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