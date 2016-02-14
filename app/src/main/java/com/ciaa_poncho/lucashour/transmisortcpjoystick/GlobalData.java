package com.ciaa_poncho.lucashour.transmisortcpjoystick;

public class GlobalData {

    private static GlobalData singleton_instance = null;
    private int time;
    private int slots;
    private int [][] rpmCurves;

    static final int CANT_MOTORES = 2;
    static final int NIVELES_PWM = 200;

    protected GlobalData(){
        /* Aplicación de patrón Singleton para mantener una única instancia de la clase
		 * accesible desde cualquier sector de la applicación */
    }

    public static GlobalData getInstance() {
        if(singleton_instance == null) {
            singleton_instance = new GlobalData();
            singleton_instance.setRpmCurves(new int [CANT_MOTORES][NIVELES_PWM]);
        }
        return singleton_instance;
    }

    public int getTime() { return time; }

    public void setTime(int time) { this.time = time; }

    public int getSlots() {return slots; }

    public void setSlots(int slots) { this.slots = slots; }

    public int[][] getRpmCurves() { return rpmCurves; }

    public void setRpmCurves(int[][] rpmCurves) { this.rpmCurves = rpmCurves; }
}
