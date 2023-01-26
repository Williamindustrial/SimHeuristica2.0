/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import vrp.LecturaDatos;
import vrp.LocalSearch;
import vrp.ReadData;

/**
 *
 * @author Asus
 */
public class hormiga {

    LinkedList<pedido> pedidos;
    LinkedList<operation> listaTotalOperaciones;
    int numeroPedidos, maxNumeroOperaciones, numeroMaquinas, numeroTotalOperaciones, optimizador;
    double[] mantenimiento;
    LinkedList<LinkedList> ordenMaquinas;
    LinkedList<operation> ordenSolutionF;
    operation opInicial, opFInal;
    feromonas mtx;

    /**
     * Constructor clase
     *
     * @param pedidos Arreglo de pedidos
     * @param listaTotalOperaciones lista total de operaciones
     * @param numeroPedidos total de pedidos del problema
     * @param maxNumeroOperaciones maximo número de operaciones
     * @param numeroMaquinas total de maquinas del problema
     * @param numeroTotalOperaciones Total de operaciones
     * @param opInicial Operacion inicial
     * @param opfinal OPeracion final
     * @param optimizador optimizador, seleciona la estadistica a trabajar
     * @param mtx matriz de feromonas
     * @param directionvrpfile
     */
    public hormiga(LinkedList<pedido> pedidos, LinkedList<operation> listaTotalOperaciones, int numeroPedidos, int maxNumeroOperaciones, int numeroMaquinas, int numeroTotalOperaciones, operation opInicial, operation opfinal, int optimizador, feromonas mtx, String directionvrpfile) throws IOException {
        this.pedidos = pedidos;
        this.listaTotalOperaciones = listaTotalOperaciones;
        this.numeroPedidos = numeroPedidos;
        this.maxNumeroOperaciones = maxNumeroOperaciones;
        this.numeroMaquinas = numeroMaquinas;
        this.numeroTotalOperaciones = numeroTotalOperaciones;
        this.opInicial = opInicial;
        this.opFInal = opfinal;
        this.optimizador = optimizador;
        this.mtx = mtx;
        ordenSolutionF = new LinkedList<>();
        algoritmo(directionvrpfile);
    }

    /**
     * Inicializa la lista que contiene el total de operaciones
     */
    private void crearLista(LinkedList<pedido> pedidos) {
        for (int i = 0; i < pedidos.size(); i++) {
            for (int j = 0; j < pedidos.get(i).operaciones.size(); j++) {
                listaTotalOperaciones.add(pedidos.get(i).operaciones.get(j));
            }
        }
    }

    /**
     * Lista de máquinas
     *
     * @param pedidos pedidos
     * @return Lista de maquinas rellena con operaciones
     */
    private LinkedList[] listaOPeracionesXMaquinas(LinkedList<pedido> pedidos) {
        LinkedList[] listaMaquinas = new LinkedList[numeroMaquinas];
        for (int i = 0; i < numeroMaquinas; i++) {
            LinkedList<operation> lisM = new LinkedList<>();
            for (int j = 0; j < pedidos.size(); j++) {
                for (int k = 0; k < pedidos.get(j).getOperaciones().size(); k++) {
                    operation op = pedidos.get(j).getOperaciones().get(k);
                    HashMap<Integer, Double> SetMachine = op.getSetMachine();
                    if (SetMachine.containsKey(i)) {
                        lisM.add(op);
                    }
                }
            }
            listaMaquinas[i] = lisM;
        }
        return listaMaquinas;
    }
    /**
     * Mtx: Matrix de arcos usados
     */
    LinkedList<LinkedList> arcosUsados = new LinkedList<>();

    private void algoritmo(String directionvrpfile) throws IOException {
        LecturaDatos l = new LecturaDatos();
        l.readTSP(0, 1, directionvrpfile);
        ReadData bestR = null;
        int[] CI = new int[numeroPedidos];
        for (int i = 0; i < numeroPedidos; i++) {
            pedidos.get(i).Qw();
            CI[i] = (int) pedidos.get(i).gettotalTiempoPromedio();
        }
        //LinkedList<feromona> feromonas = new LinkedList<>();
        int numeroOperacionesHechas = 0;
        pedidos.get(0).getOperaciones().getFirst().getAnteriorRuta().setAcoH(true);
        pedidos.get(0).getOperaciones().getFirst().getAnteriorRuta().setRealizado(true);
        LinkedList[] listaMaquinas = listaOPeracionesXMaquinas(pedidos);
        LinkedList<LinkedList> listaMaquinasNueva = new LinkedList();
        for (int i = 0; i < listaMaquinas.length; i++) {
            listaMaquinasNueva.add(new LinkedList());
        }
        double[] tiempoAcumulado = new double[numeroMaquinas];
        while (numeroOperacionesHechas != numeroTotalOperaciones) {
            for (int i = 0; i < numeroMaquinas; i++) {
                LinkedList<operation> operacionesPosibles = new LinkedList<>();
                int menorCalculo = 100000000;
                int posicionEliminar = 0;
                for (int j = 0; j < listaMaquinas[i].size(); j++) {
                    operation op = (operation) listaMaquinas[i].get(j);
                    if (op.getAnteriorRuta().isAcoH() & op.isAcoH() == false) {
                        operacionesPosibles.add(op);
                    }
                }
                LinkedList<Double> tiXn = new LinkedList<>();
                double sumriXN = 0;
                //calculo pxt
                if (!operacionesPosibles.isEmpty()) {
                    int bestH = 10000000;
                    int[] newCI = new int[numeroPedidos];
                    operation selecionada = operacionesPosibles.getFirst();
                    operation x = opInicial;
                    if (!listaMaquinasNueva.get(i).isEmpty()) {
                        x = (operation) listaMaquinasNueva.get(i).getLast();
                    }
                    for (int j = 0; j < operacionesPosibles.size(); j++) {
                        operation y = operacionesPosibles.get(j);
                        int tiempoFinal = (int) (calculo(y, x,i) + y.getQw());
                        LinkedList<Integer> ci = new LinkedList<>();
                        int[] AUXCI = new int[numeroPedidos];
                        for (int k = 0; k < numeroPedidos; k++) {
                            if (y.getPedido() == k) {
                                ci.add(tiempoFinal);
                                AUXCI[k] = tiempoFinal;
                                if (tiempoFinal == 0) {
                                    System.out.println("-");
                                }
                            } else {
                                ci.add(CI[k]);
                                AUXCI[k] = CI[k];
                            }
                        }
                        l.initialSol(null, ci);
                        ReadData r = l.getR();
                        r.VND(2);
                        LocalSearch bl = r.getBestSol();
                        if (bl.getCRMAX() < bestH) {
                            bestH = bl.getCRMAX();
                            selecionada = y;
                            newCI = AUXCI;
                        }
                    }
                    System.out.println("aaaaaaaaaaaaa");
                    for (int j = 0; j < newCI.length; j++) {
                        int k = newCI[j];
                        System.out.println(k);
                    }
                    CI = newCI;

                    System.out.println(selecionada.toString());
                    selecionada.addOperaciónAnterior(x);
                    x.addOperaciónSiguiente(selecionada);
                    selecionada.selecionarMaquina(i);
                    selecionada.operar();
                    selecionada.setAcoH(true);
                    listaMaquinas[i].remove(selecionada);
                    listaMaquinasNueva.get(i).add(selecionada);
                    ordenSolutionF.add(selecionada);
                    numeroOperacionesHechas++;
                    operation opu = (operation) listaMaquinasNueva.get(i).getLast();
                    opu.addOperaciónSiguiente(opFInal);

                }

            }
        }
        ordenMaquinas = listaMaquinasNueva;
        calcularEstadistica(pedidos, optimizador);
        //System.out.println(valorLocal());
    }

    /**
     * Actualiza la feromonas
     *
     * @param fe factor de evaoporación
     */
    void evaporar(double fEvaporacion) {
        mtx.evaporar(fEvaporacion);
    }

    double makespan = 0;
    double tm = 0;
    double Tci = 0;
    double Tti = 0;
    double valorLocal = 0;
    double Pci = 0;
    double Pti = 0;
    LinkedList<Integer> ci = new LinkedList<>();

    /**
     * Calcula las estadisticas del problema
     *
     * @param pedidos estructura de pedidos
     * @param estadistica estadistica
     */
    public void calcularEstadistica(LinkedList<pedido> pedidos, int estadistica) {
        for (int i = 0; i < pedidos.size(); i++) {
            ci.add((int) (pedidos.get(i).getCi()));
            switch (estadistica) {
                case 0:
                    makespan = Math.max(makespan, pedidos.get(i).getCi());
                    valorLocal = makespan;
                    break;
                case 1:
                    tm = Math.max(tm, pedidos.get(i).getTi());
                    valorLocal = tm;
                    break;
                case 2:
                    Tci += pedidos.get(i).getCi();
                    valorLocal = Tci;
                    break;
                case 3:
                    Tti += pedidos.get(i).getTi();
                    valorLocal = Tti;
                    break;
                case 4:
                    Pci += pedidos.get(i).Pci();
                    valorLocal = Pci;
                    break;
                default:
                    Pti += pedidos.get(i).Pti();
                    valorLocal = Pti;
            }
        }
//        System.out.println(valorLocal);
    }

    /**
     * Retorna el valor local
     *
     * @return
     */
    public double valorLocal() {
        return valorLocal;
    }

    public void writeMono(String ruta, String name) throws IOException {
        DecimalFormat df = new DecimalFormat("#.00");
        String rutaP = ruta;
        ruta = ruta + File.separator + name + ".txt";
        FileWriter escribir = new FileWriter(ruta);
        BufferedWriter escritor = new BufferedWriter(escribir);
        LinkedList<String> tiemposFpedido = new LinkedList<>();
        LinkedList<String> dueDate = new LinkedList<>();
        LinkedList<String> W = new LinkedList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            tiemposFpedido.add(df.format(pedidos.get(i).getCi()));
            dueDate.add(df.format(pedidos.get(i).getDueDate()));
            W.add(df.format(pedidos.get(i).getWi()));
        }
        switch (optimizador) {
            case 0:
                escritor.write("Makespan: " + df.format(makespan));
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                break;
            case 1:
                escritor.write("Maximum tardiness: " + df.format(tm));
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                escritor.write("Due date:        " + dueDate);
                escritor.newLine();
                escritor.newLine();
                escritor.newLine();
                break;
            case 2:
                escritor.write("Tc " + df.format(Tci));
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                escritor.newLine();
                escritor.newLine();
                break;
            case 3:
                escritor.write("Tt " + df.format(Tti));
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                escritor.write("Due date:        " + dueDate);
                escritor.newLine();
                escritor.newLine();
                escritor.newLine();
                break;
            case 4:
                escritor.write("TWi*Ci: " + df.format(Pci));
                escritor.newLine();
                escritor.write("FINAL TIME: " + tiemposFpedido);
                escritor.newLine();
                escritor.write("WEIGHTED WEIGHTS: " + W);
                escritor.newLine();

                break;
            case 5:
                escritor.write("TWi*Ti: " + df.format(Pti));
                escritor.newLine();
                escritor.write("FINAL TIME: " + tiemposFpedido);
                escritor.newLine();
                escritor.write("DUE DATE:   " + dueDate);
                escritor.newLine();
                escritor.write("WEIGHTED WEIGHTS: " + W);
                escritor.newLine();
                break;
            default:
                break;
        }
        escritor.write("OPERATION BY MACHINE: ");
        escritor.newLine();
        escritor.newLine();

        for (int i = 0; i < ordenMaquinas.size(); i++) {
            String linea = "Machine: " + (i + 1) + ">>";
            for (int j = 0; j < ordenMaquinas.get(i).size(); j++) {
                operation x = (operation) ordenMaquinas.get(i).get(j);
                linea = linea + "-(" + (x.getPedido() + 1) + "-" + (x.getOperation() + 1) + ")(" + df.format(x.getStartTime()) + "-" + df.format(x.getFinalTime()) + ")";
            }
            escritor.write(linea);
            escritor.newLine();
        }
        escritor.close();
        escribir.close();

    }

    public LinkedList<operation> getOrdenSolutionF() {
        return ordenSolutionF;
    }

    public double calculo(operation y, operation pmx, int maquina) {
        y.setProcessingTime(maquina);
        double finPmx = 0;
        double calculo;
        if (pmx == null) {
            finPmx = 0;
        } else {
            finPmx = pmx.getFinalTime();
        }
        calculo = Math.max(y.getAnteriorRuta().getFinalTime(), finPmx) + y.getProcessingTime();
        return calculo;
    }

    public double getValorLocal() {
        return valorLocal;
    }

    public LinkedList<LinkedList> getOrdenMaquinas() {
        return ordenMaquinas;
    }

    public LinkedList<pedido> getPedidos() {
        return pedidos;
    }

    public LinkedList<Integer> getCi() {
        return ci;
    }
    LecturaDatos lecturer = new LecturaDatos();

    LocalSearch calcularCMRAX(String localizacionVRP, LinkedList Ci) throws IOException {
        lecturer.initialSol(localizacionVRP, Ci);
        ReadData r = lecturer.getR();
        return r.getBestSol();
    }

}
