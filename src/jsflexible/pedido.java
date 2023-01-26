/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.awt.Font;
import java.util.LinkedList;

/**
 *
 * @author William
 */
public class pedido {

    /**
     * Numero de operaciones
     */
    int numerooperaciones;
    /**
     * Arreglo dinamico de operaciones
     */
    LinkedList<operation> operaciones;
    /**
     * Estadisticas
     */
    double wi, dueDate;
    double ci = 0;
    double ti = 0;
    double Pti;
    double Pci;
    int u;
    double tcm;

    /**
     * Constructor
     *
     * @param numerooperaciones Número de operaciones del pedido
     * @param operaciones Conjunto de operaciones del perdido
     */
    public pedido(int numerooperaciones, LinkedList<operation> operaciones) {
        this.numerooperaciones = numerooperaciones;
        this.operaciones = operaciones;
    }

    /**
     * Retorna el peso ponderado del pedido
     *
     * @return wi
     */
    public double getWi() {
        return wi;
    }

    /**
     * retorna el número de operaciones del pedido
     *
     * @return número de operaciones
     */
    public int getNumerooperaciones() {
        return numerooperaciones;
    }

    /**
     * Retorna las operaciones del pedido
     *
     * @return operaciones
     */
    public LinkedList<operation> getOperaciones() {
        return operaciones;
    }

    /**
     * Retorna el hora de finalización del pedido
     *
     * @return Ci
     */
    public double getCi() {
        ci = operaciones.getLast().getFinalTime();
        return ci;
    }

    /**
     * Retorna la tardanza del pedido
     *
     * @return Ti
     */
    public double getTi() {
        ti = Math.max(operaciones.getLast().getFinalTime() - dueDate, 0);
        return ti;
    }

    /**
     * Retorna la tardanza ponderada del pedido
     *
     * @return wi*ti
     */
    public double Pti() {
        Pti = getTi() * getWi();
        return Pti;
    }

    /**
     * Retorna el flujo ponderado del pedido
     *
     * @return wi*ci
     */
    public double Pci() {
        Pci = operaciones.getLast().getFinalTime() * wi;
        return Pci;
    }

    /**
     * Retorna el número de pedidos tardios
     *
     * @return U
     */
    public int getU() {
        double tardanza = Math.max(operaciones.getLast().getFinalTime() - dueDate, 0);
        if (tardanza > 0) {
            u = 1;
        }
        return u;
    }

    /**
     * Clona el pedido
     *
     * @return Pedido clonado
     */
    public pedido clonar()  {
        LinkedList<operation> operacionesClonadas = new LinkedList<>();
        for (int i = 0; i < operaciones.size(); i++) {
            operation op = operaciones.get(i).clonar();
            op.setProcessingTime(operaciones.get(i).getMaquinaSelecionada());
            if (i == 0) {
                op.setAnteriorRuta(operaciones.get(i).getAnteriorRuta());
            } else if (i==operaciones.size()-1) {
                op.setSiguieteRuta(operaciones.get(i).getSiguieteRuta());
                operacionesClonadas.get(i-1).setSiguieteRuta(op);
                op.setAnteriorRuta(operacionesClonadas.get(i-1));
            } else {
                operacionesClonadas.get(i-1).setSiguieteRuta(op);
                op.setAnteriorRuta(operacionesClonadas.get(i-1));
            }
            operacionesClonadas.add(op);
        }
        pedido pedidoClonado = new pedido(numerooperaciones, operacionesClonadas);
        pedidoClonado.setdueDate(dueDate);
        pedidoClonado.setWi(wi);
        
        return pedidoClonado;
    }
    public pedido clonar1()  {
        LinkedList<operation> operacionesClonadas = new LinkedList<>();
        for (int i = 0; i < operaciones.size(); i++) {
            operation op = operaciones.get(i).clonar();
            if (i == 0) {
                op.setAnteriorRuta(operaciones.get(i).getAnteriorRuta());
            } else if (i==operaciones.size()-1) {
                op.setSiguieteRuta(operaciones.get(i).getSiguieteRuta());
                operacionesClonadas.get(i-1).setSiguieteRuta(op);
                op.setAnteriorRuta(operacionesClonadas.get(i-1));
            } else {
                operacionesClonadas.get(i-1).setSiguieteRuta(op);
                op.setAnteriorRuta(operacionesClonadas.get(i-1));
            }
            operacionesClonadas.add(op);
        }
        pedido pedidoClonado = new pedido(numerooperaciones, operacionesClonadas);
        pedidoClonado.setdueDate(dueDate);
        pedidoClonado.setWi(wi);
        
        return pedidoClonado;
    }

    /**
     * Retorna el tiempo de produción del pedido
     *
     * @return total time
     */
    public double gettotalTiempoPromedio() {
        double total = 0;
        for (int i = 0; i < operaciones.size(); i++) {
            total += operaciones.get(i).promedioTiempo();
        }
        return total;
    }

    /**
     * Agrega el peso ponderado al pedido
     *
     * @param wi Peso ponderado
     */
    public void setWi(double wi) {
        this.wi = wi;
    }

    

    /**
     * Devuelve la fecha de vencimiento del pedido
     *
     * @return Fecha de vencimiento del pedido
     */
    public double getDueDate() {
        return dueDate;
    }

    /**
     * Halla la fecha de vencimiento del pedido, para ello se utiliza la
     * sumatoria del menor tiempo en cada máquina multiplicado por un factor de
     * flexibilidad
     *
     * @param dueDate
     */
    public void setDueDate(double dueDate) {
        this.dueDate = dueDate;
    }
    /**
     * Metodo para clonar el due date
     * @param dueDate due date
     */
    
    public void setdueDate(Double dueDate){
        this.dueDate= dueDate;
    }
    
    public void Qw(){
        for (int i = numerooperaciones-1; i >=0; i--) {
            operation x = operaciones.get(i);
            x.setQw();
        }
    }

}
