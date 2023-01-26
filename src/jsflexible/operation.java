/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author William
 */
public class operation {

    // Elementos del constructor
    int pedido, operation;
    HashMap<Integer, Double> SetMachineInicial = new HashMap<>();
    HashMap<Integer, Double> SetMachine;

    // Tiempo de procesamiento
    double startTime = 0, finalTime = 0, processingTime = 0;
    int maquina;
    // Anteriores y suguientes en ruta
    operation anteriorRuta;
    operation siguieteRuta;
    // Anteriores y siguientes en máquina
    operation anteriorM, siguientM;
    int arcos = -1;
    // Esta en ruta
    boolean realizado = false;
    static int totalPedidos = 0;
    //
    int posicionMatrizF;
    // colas
    double[] colas = new double[totalPedidos];
    // ruta critica
    boolean estaRuta = false, halloReasignacion;
    // inicial 
    boolean inicial = false, fin = false;
    /**
     * La colonia de hormigas realizo esta operacion
     */
    boolean acoH = false;
    /**
     * Colas de la heuristica inicial
     */
    int qw = 0;

    /**
     * Contructor: inicializa las variables
     *
     * @param pedido numero del pedido
     * @param operation número de la operación en el pedido
     * @param SetMachine máquinas con tiempos opcionales
     *
     */
    public operation(int pedido, int operation, HashMap SetMachine) {
        this.pedido = pedido;
        this.operation = operation;
        this.SetMachine = SetMachine;
    }

    /**
     * Agrega las operaciones siguientes del pedido por máquina
     *
     * @param operacionSiguiete operación siguiente por máquina
     */
    public void addOperaciónSiguiente(operation operacionSiguiete) {
        siguientM = operacionSiguiete;
    }

    /**
     * Agrega las operaciones anteriores del pedido por máquina
     *
     * @param operacionAnterior operacion anterior por máquina
     */
    public void addOperaciónAnterior(operation operacionAnterior) {
        anteriorM = operacionAnterior;
    }

    /**
     * Agrega el anterior en ruta
     *
     * @param anteriorRuta
     */
    public void setAnteriorRuta(operation anteriorRuta) {
        this.anteriorRuta = anteriorRuta;
    }

    /**
     * Afrega el siguiente en ruta
     *
     * @param siguieteRuta
     */
    public void setSiguieteRuta(operation siguieteRuta) {
        this.siguieteRuta = siguieteRuta;
    }

    /**
     * Halla la hora de inicio, número de arcos, tiempo de alistamiento de la
     * operación y hora final de la operación
     *
     */
    public void operar() {
        setStartTime();
        setProcessingTime(maquina);
        finalTime = startTime + processingTime;
        arcos = Math.max(anteriorM.getArcos(), anteriorRuta.getArcos()) + 1;
    }

    /**
     * Devuelve la hora de inicio de la operación
     *
     * @return Hora de inicio de la operación
     */
    public double getStartTime() {
        return startTime;
    }

    /**
     * Devuelve la hora de terminación de la operación
     *
     * @return tiempo de terminación de la operación
     */
    public double getFinalTime() {
        return finalTime;
    }

    /**
     * Devuelve el tiempo de procesamiento de la operación
     *
     * @return Tiempo de procesamiento de la operación
     */
    public double getProcessingTime() {
        return processingTime;
    }

    /**
     * Genera la hora final de procesamiento
     *
     * @param machine Máquina selecionada
     */
    public void setProcessingTime(int machine) {
        maquina = machine;
        processingTime = SetMachine.get(machine);
    }

    /**
     * Devuelve el pedido al que pertenece la operación
     *
     * @return Pedido al que pertenece la operación
     */
    public int getPedido() {
        return pedido;
    }

    /**
     * Devuelve el número de arcos de la operación
     *
     * @return Número de arcos de la operación
     */
    public int getArcos() {
        return arcos;
    }

    /**
     * Devuelve el estado de la operación
     *
     * @return boolean esta realizado
     */
    public boolean isRealizado() {
        return realizado;
    }

    /**
     * Camia el estado de la operación
     *
     * @param realizado true si ya esta hecha, false si aún falta
     */
    public void setRealizado(boolean realizado) {
        this.realizado = realizado;
    }

    /**
     * Retorna la máquina selecionadas para la operación
     *
     * @return Maquinas selecionadas
     */
    public int getMaquinaSelecionada() {
        return maquina;
    }

    /**
     * Retorna el número de la operación del pedido
     *
     * @return número de operación del pedido
     */
    public int getOperation() {
        return operation;
    }

    /**
     * Cambia el tiempo de inicio de la operacion
     */
    public void setStartTime() {
        this.startTime = Math.max(anteriorM.getFinalTime(), anteriorRuta.getFinalTime());
    }

    /**
     * Cambia el tiempo de inicio de la operacion final e inicial
     *
     * @param inicio Hora de inicio de la operación final e inicial
     */
    public void setStartTime1(double inicio) {
        this.startTime = inicio;
    }

    /**
     * Cambia el numero de arcos
     *
     * @param arcos
     */
    public void setArcos(int arcos) {
        this.arcos = arcos;
    }

    /**
     * Retorna las colas
     *
     * @return colas double[]
     */
    public double[] getColas() {
        return colas;
    }

    /**
     * Edita las colas de la operación
     *
     * @param colas colas double []
     */
    public void setColas(double[] colas) {
        this.colas = colas;
    }

    /**
     * Halla las colas y agrega elementos a la ruta critica
     *
     * @param rutaC LinkedLis<> ruta c
     */
    double q;

    public void hallarColas(LinkedList<rutaCritica> rutaC) {
//        System.out.println(toString());
        operation opSiguiente = siguientM;
        
        double horaInicioSiguienteCalculada = finalTime;
        if (horaInicioSiguienteCalculada == opSiguiente.getStartTime() && opSiguiente.estaRuta) {
            estaRuta = true;
            rutaCritica r = new rutaCritica(this, opSiguiente);
            rutaC.add(r);
        }
        if (siguieteRuta.getStartTime() == finalTime && siguieteRuta.estaRuta) {
            rutaCritica r = new rutaCritica(this, siguieteRuta);
            rutaC.add(r);
            estaRuta = true;
        }
    }

    /**
     * Encuentra la hora final
     */
    public void setFinalTime() {
        this.finalTime = startTime + processingTime;
    }

    /**
     * Retorna si la poeración pertenece a la ruta critica
     *
     * @return boolean esta en ruta
     */
    public boolean isEstaRuta() {
        return estaRuta;
    }

    /**
     * retorna si la operación ya fue reasignada
     *
     * @return booelan
     */
    public boolean isHalloReasignacion() {
        return halloReasignacion;
    }

    /**
     * Retorna si la operación ya fue reasignada
     *
     * @param halloReasignacion boolean
     */
    public void setHalloReasignacion(boolean halloReasignacion) {
        this.halloReasignacion = halloReasignacion;
    }

    /**
     * Retorna la operación anterior de la máquina
     *
     * @return operación anterior
     */
    public operation getAnteriorM() {
        return anteriorM;
    }

    /**
     * Retorna la operación siguiente de la máquina
     *
     * @return operación siguiente
     */
    public operation getSiguientM() {
        return siguientM;
    }

    /**
     * Retorna la operacion anterior en ruta
     *
     * @return operacion anterior en máquina
     */
    public operation getAnteriorRuta() {
        return anteriorRuta;
    }

    /**
     * Retorna la operacion siguiente en ruta
     *
     * @return operación siguiente en ruta
     */
    public operation getSiguieteRuta() {
        return siguieteRuta;
    }

    /**
     * Clona la operación
     *
     * @return operación clonada
     */
    public operation clonar() {
        operation op = new operation(pedido, operation, SetMachine);
        op.setPosMatriz(posicionMatrizF);
        return op;
    }

    @Override
    public String toString() {
        LinkedList<Double> cola = new LinkedList<>();
        for (int i = 0; i < colas.length; i++) {
            cola.add(colas[i]);
        }
        return "{" + pedido + "-" + operation +"}";
        //return "{" + pedido + "-" + operation + "A(" + arcos + ")" + "-P(" + processingTime + ")" + "-M" + "-(" + maquina + ")-" + "(" + startTime + "-" + finalTime + ") Q" + cola + "}";
    }

    /**
     * Es la operación, una operación inicial
     *
     * @param inicial
     */
    public void setInicial(boolean inicial) {
        this.inicial = inicial;
    }

    /**
     * Es la operación una operación final
     *
     * @param fin
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }

    /**
     * Retorna si la operación es la operación inicial
     *
     * @return inical
     */
    public boolean isInicial() {
        return inicial;
    }

    /**
     * Retorna si la operación es la operación final
     *
     * @return final
     */
    public boolean isFin() {
        return fin;
    }

    /**
     * Si la operación anterior en máquina esta realizada ya esta realizada
     *
     * @return esta realizado
     */
    public boolean verificar() {
        return anteriorRuta.isRealizado() & anteriorM.isRealizado();
    }

    /**
     * Asigna la máquina selecionada para la operación
     *
     * @param machine máquina selecionada desde la operación
     */
    public void selecionarMaquina(int machine) {
        maquina = machine;
    }

    /**
     * Devuelve el conjunto de las maquinas posibles para la operación
     *
     * @return Conjunto de máquinas
     */
    public HashMap<Integer, Double> getSetMachine() {
        return SetMachine;
    }

    /**
     * Asigna la opsición en la matrz
     *
     * @param pos
     */
    public void setPosMatriz(int pos) {
        posicionMatrizF = pos;
    }

    /**
     * Retorna la posición en la matriz
     *
     * @return
     */
    public int getPosicionMatrizF() {
        return posicionMatrizF;
    }

    /**
     *
     * @return
     */

    public boolean isAcoH() {
        return acoH;
    }

    /**
     * Edita el estado de la operción en la aco
     *
     * @param acoH true esta hecha, false el contrario
     */
    public void setAcoH(boolean acoH) {
        this.acoH = acoH;
    }

    /**
     *
     * @param estaRuta
     */

    public void setEstaRuta(boolean estaRuta) {
        this.estaRuta = estaRuta;
    }
    HashMap<Integer, Double> fermoMaquina;

    /**
     *
     */
    public void fermonaMaquina() {
        fermoMaquina = new HashMap<>();
    }

    /**
     *
     * @return
     */
    public double getQ() {
        return q;
    }

    public int getQw() {
        return qw;
    }

    public void setQw() {
        if(siguieteRuta.getSetMachine()!=null){
            qw = (int) siguieteRuta.getQw()+siguieteRuta.promedioTiempo();
        }
        
    }

    public int promedioTiempo() {
        int promedio = 0;
        int suma = 0;
        int contador = 0;
        for (Map.Entry<Integer, Double> set : SetMachine.entrySet()) {
            if(set.getValue()>suma){
                suma = (int) (set.getValue()+0);
            }
            
            contador++;
        }
        promedio = suma / contador;
        return suma;
    }
}


