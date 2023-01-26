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
import java.util.Map;

/**
 *
 * @author Asus
 */
public class solution {

    /**
     * Arbol de pedidos
     */
    LinkedList<pedido> pedidos;
    /**
     * Vector número de operaciones
     */
    int[] numeroOperaciones;
    /**
     * Matrices de setup por máquina
     */
    LinkedList<double[][]> CambiosTiemposXMaquina;
    /**
     * Dirección para guardar los archivos
     */

    String direcciónGuardarArchivos;
    /**
     * Secuencia de solución
     */
    LinkedList<operation> listaOp;
    /**
     * Nodos inicial y final
     */
    operation opInicial, opFinal;
    /**
     * Lista de máquinas
     */
    LinkedList<LinkedList> listaMaquinas;
    /**
     * Estadisticas
     */
    double makespan = 0;
    double tarMax = 0, valorLocal;
    double sumCi = 0, sumTi = 0, Pci = 0, Pti = 0, NPT = 0;
    int numerMaximoArcos = 0;
    int criterio;
    /**
     * Ruta critica
     */
    LinkedList<rutaCritica> rutaC = new LinkedList<>();
    /**
     * Lista de máquinas ordenadas por arcos
     */
    LinkedList<LinkedList> listaMaquinasXArcos = new LinkedList<>();
    /**
     *
     */
    LinkedList<LinkedList> listaOperacionesPorArcos = new LinkedList<>();
    /**
     * Lista del vecindario
     */
    LinkedList<TransfomaGrafo> vecindario = new LinkedList<>();
    /**
     * Lista del vecindario completo
     */
    LinkedList<TransfomaGrafo> vecindarioCompleto = new LinkedList<>();
    /**
     * Best local
     */
    Double valorBest = 10000000000000.0;
    /**
     * Numero maximo de candidatos
     */
    int nmax;

    /**
     * Crea el constructor de la solución
     *
     * @param pedidos Arbol de pedidos
     * @param listaMaquinas Lista de máquinas inicial
     * @param numeroOperaciones vector numero de operaciones de cada trabajo del
     * pedido
     * @param listaOp orden de la solución inicial
     * @param opInicial nodo inicial null
     * @param opFinal nodo final null
     * @param criterio Criterio
     */
    public solution(LinkedList<pedido> pedidos, LinkedList<LinkedList> listaMaquinas, int[] numeroOperaciones, LinkedList<operation> listaOp, operation opInicial, operation opFinal, int criterio) {
        this.pedidos = pedidos;
        this.listaMaquinas = listaMaquinas;
        this.numeroOperaciones = numeroOperaciones;
        this.listaOp = listaOp;
        this.opInicial = opInicial;
        this.opFinal = opFinal;
        this.criterio = criterio;
    }

    /**
     * Soluciona el problema
     */
    LinkedList<LinkedList> ordenPorArcos = new LinkedList<>();

    public void solucionar() {
        LinkedList<operation> listaOperaciones = (LinkedList<operation>) listaOp.clone();
        int contador = 0;
        while (!listaOperaciones.isEmpty()) {
            for (int i = contador; i < listaOperaciones.size(); i++) {
                operation op = listaOperaciones.get(i);
                if (listaOperaciones.get(i).verificar()) {
                    listaOperaciones.get(i).operar();
                    listaOperaciones.get(i).setRealizado(true);
                    numerMaximoArcos = Math.max(listaOperaciones.get(i).getArcos(), numerMaximoArcos);
                    listaOperaciones.remove(i);
                    break;
                }
            }
        }
        

    }

    /**
     * Hallas las estadisticas
     */
    private void estadisticas() {
        for (int i = 0; i < pedidos.size(); i++) {
            makespan = Math.max(makespan, pedidos.get(i).getCi());
        }
    }

    /**
     * Halla las colas y la ruta critica del grafo
     *
     * @param estadistica para hallar la ruta critica
     * @param n numero de arcos que se alejaran
     * @param ni
     * @param pedido
     * @param isIndividial
     */
    public void colas(int estadistica, int n, int ni, int pedido, boolean isIndividial) {
        
        for (int i = 0; i < listaMaquinas.size(); i++) {
            LinkedList<LinkedList> guardarArcos = new LinkedList<>();
            for (int j = 0; j <= numerMaximoArcos; j++) {
                LinkedList<operation> arco = new LinkedList<>();
                for (int k = 0; k < listaMaquinas.get(i).size(); k++) {
                    operation op = (operation) listaMaquinas.get(i).get(k);
                    if (op.getArcos() == j) {
                        arco.add(op);
                    }
                }
                guardarArcos.add(arco);
            }
            listaMaquinasXArcos.add(guardarArcos);
        }
        estadisticas();
        selecionarEstadistica(estadistica);
        
        opFinal.setStartTime1(makespan);
        opFinal.setFinalTime();
        opFinal.setArcos(numerMaximoArcos);
        LinkedList<operation> listaOperaciones = (LinkedList<operation>) listaOp.clone();
        for (int i = 0; i <= numerMaximoArcos; i++) {
            LinkedList<operation> a = new LinkedList<>();
            for (int j = 0; j < listaMaquinasXArcos.size(); j++) {
                LinkedList<operation> b = (LinkedList<operation>) listaMaquinasXArcos.get(j).get(i);
                if (!b.isEmpty()) {
                    LinkedList<operation> arco = (LinkedList<operation>) listaMaquinasXArcos.get(j).get(i);
                    for (int k = 0; k < arco.size(); k++) {
                        operation c = arco.get(k);
                        a.add(pedidos.get(c.getPedido()).getOperaciones().get(c.getOperation()));
                        operation bi=pedidos.get(c.getPedido()).getOperaciones().get(c.getOperation());
                        //bi.hallarColas(rutaC);
                    }
                }
            }
            //listaOperacionesPorArcos.add(a);
            ordenPorArcos.add(a);
        }
        for (int i = numerMaximoArcos; i >= 0; i--) {
            for (int j = 0; j < ordenPorArcos.get(i).size(); j++) {
                operation op = (operation) ordenPorArcos.get(i).get(j);
                op.hallarColas(rutaC);
            }
            listaOperacionesPorArcos.addFirst(ordenPorArcos.get(i));
        }
        algoritmo(n, ni, pedido, isIndividial);
    }

    /**
     * Seleciona la estadistica y agrega las primeras soluciones de la ruta
     *
     * @param estadistica estadistica a optimizar
     */
    public void selecionarEstadistica(int estadistica) {
        valorLocal=makespan;
        int contador = 0;
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getOperaciones().getLast().getFinalTime() <= makespan) {
                pedidos.get(i).getOperaciones().getLast().estaRuta = true;
                contador++;
            }
        }
    }

    /**
     * Algoritmo
     *
     * @param n numero de veces que se correran los arcos
     * @param ni Vecinandario
     * @param pedido
     * @param isIndividial
     */
    public void algoritmo(int n, int ni, int pedido, boolean isIndividial) {

        for (int i = 0; i < rutaC.size(); i++) {
            operation x = rutaC.get(i).getX();
            operation y = rutaC.get(i).getY();
            

            if (x.getPedido() != y.getPedido()) {
                if (ni == 1) {
                    inversion(x, y);
                }
            }
            if (x.isHalloReasignacion() == false) {
                vn = 0;
                if (ni == 0) {
                    x.setHalloReasignacion(true);
                    insertar(x);
                    vn = 1;
                    insertar(x);
                }
                if (ni == 2) {
                    if (x.getPedido() != y.getPedido()) {
                        inversion(x, y);
                    }
                    //swap(x);
                    insertar(x);
                    //inversion(x, y, L3, isIndividial, pedido);
                }
            }
        }
    }
    LinkedList<Integer> pC;

    /**
     * Inversión de arcos: Se invierten los arcos para las operaciones x y Y
     *
     * @param x operación x
     * @param y operación y
     * @param L3 L3
     */
    public void inversion(operation x, operation y) {
        double examen = 0;
        TransfomaGrafo t = new TransfomaGrafo(listaMaquinas, pedidos, x, y, listaOperacionesPorArcos, examen, true, false);
        vecindario.add(t);
    }

    /**
     * Hace el movimiento de la inserción
     *
     * @param x
     */
    public void insertar(operation x) {
        HashMap<Integer, Double> SetMachine = x.getSetMachine();

        for (Map.Entry<Integer, Double> set : SetMachine.entrySet()) {
            int maquinaNueva = set.getKey();
            evaluarAtras(maquinaNueva, x);
            evaluarAdelante(maquinaNueva, x);
        }
    }

    public void evaluarAdelante(int maquinaNueva, operation x) {
        LinkedList<LinkedList> listaMaquina = listaMaquinasXArcos.get(maquinaNueva);
        for (int i = x.getArcos(); i < listaMaquina.size(); i++) {
            if (listaMaquina.get(i).size() > 0) {
                operation j = (operation) listaMaquina.get(i).getFirst();
                operation k = j.getSiguientM();
                if (j != x & k != x & k != x.getAnteriorRuta() & j != x.getSiguieteRuta()) {
                    boolean HTA = false;
                    if (j.getStartTime() < x.getSiguieteRuta().getStartTime() + x.getSiguieteRuta().getProcessingTime()) {
                        HTA = true;
                    }
                    boolean aaaa = false;
                    if (x.getSiguieteRuta().getArcos() >= j.getArcos()) {
                        HTA = true;
                        aaaa = true;
                    }
                    if (HTA) {
                        insercion(maquinaNueva, x, j, k);
                    } else {
                        break;
                    }
                }
            }
        }
        operation j = (operation) listaMaquinas.get(maquinaNueva).getLast();
        operation k = opFinal;
        if (j != x & k != x & k != x.getAnteriorRuta() & j != x.getSiguieteRuta()) {
            boolean HTA = false;
            if (j.getStartTime() < x.getSiguieteRuta().getStartTime() + x.getSiguieteRuta().getProcessingTime()) {
                HTA = true;
            }
            boolean aaaa = false;
            if (x.getSiguieteRuta().getArcos() >= j.getArcos()) {
                HTA = true;
                aaaa = true;
            }
            if (HTA) {
                insercion(maquinaNueva, x, j, k);
            }

        }

    }
    int vn;

    public void evaluarAtras(int maquinaNueva, operation x) {
        LinkedList<LinkedList> listaMaquina = listaMaquinasXArcos.get(maquinaNueva);
        for (int i = x.getArcos(); i >= 0; i--) {
            if (listaMaquina.get(i).size() > 0) {
                operation j = (operation) listaMaquina.get(i).getFirst();
                operation k = j.getSiguientM();

                if (j != x & k != x & k != x.getAnteriorRuta() & j != x.getSiguieteRuta()) {
                    boolean HTA = false;
                    if (k.getStartTime() + k.getProcessingTime() > x.getAnteriorRuta().getStartTime()) {
                        HTA = true;
                    }
                    if (k.getArcos() >= x.getAnteriorRuta().getArcos()) {
                        HTA = true;
                    }
                    if (HTA) {
                        insercion(maquinaNueva, x, j, k);
                    } else {
                        break;
                    }
                }

            }
        }
        operation j = opInicial;
        operation k = (operation) listaMaquinas.get(maquinaNueva).getFirst();
        if (j != x & k != x & k != x.getAnteriorRuta() & j != x.getSiguieteRuta()) {
            boolean HTA = false;
            if (k.getStartTime() + k.getProcessingTime() > x.getAnteriorRuta().getStartTime()) {
                HTA = true;
            }
            if (k.getArcos() >= x.getAnteriorRuta().getArcos()) {
                HTA = true;
            }
            if (HTA) {
                insercion(maquinaNueva, x, j, k);
            }

        }
    }

    public void insercion(int maquinaNueva, operation x, operation j, operation k) {
        //antecesor en ruta
        operation prx = x.getAnteriorRuta();
        //antecesor en maquina
        operation pmx = x.getAnteriorM();
        //sucesor en ruta
        operation srx = x.getSiguieteRuta();
        //sucesor en maquina
        operation smx = x.getSiguientM();
        // Definir L

        TransfomaGrafo t = new TransfomaGrafo(listaMaquinasXArcos, listaMaquinas, pedidos, x, false, maquinaNueva, x.getMaquinaSelecionada(), j, k, pmx, smx, listaOperacionesPorArcos, 0);
        vecindario.add(t);
    }

    /**
     * Retorna el valor local de la solucón
     *
     * @return valor local
     */
    public double getValorLocal() {
        return valorLocal;
    }

    /**
     * Retorna los mejores hijos del vecindario
     *
     * @return vecindario
     */
    public LinkedList<TransfomaGrafo> getVecindario() {
        return vecindario;
    }

    /**
     * Escribe en consola el valor local
     */
    public void escribirValorLocal() {
        System.out.println(valorLocal);
    }

    /**
     * Retorna todos los hijos del vecindario
     *
     * @return todos los hijos del vecindario
     */
    public LinkedList<TransfomaGrafo> getVecindarioCompleto() {
        return vecindarioCompleto;
    }

    public void writeMono(String ruta, String name, double CpuTime) throws IOException {
        //selecionarEstadistica(0);
        estadisticas();
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
        switch (criterio) {
            case 0:
                escritor.write("Makespan: " + df.format(makespan));
                escritor.newLine();
                escritor.write("CPU Time: " + CpuTime + " Seconds");
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                break;
            case 1:
                escritor.write("Maximum tardiness: " + df.format(tarMax));
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
                escritor.write("Tc " + df.format(sumCi));
                escritor.newLine();
                escritor.newLine();
                escritor.write("Completion Time: " + tiemposFpedido);
                escritor.newLine();
                escritor.newLine();
                escritor.newLine();
                break;
            case 3:
                escritor.write("Tt " + df.format(sumTi));
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

            case 6:
                escritor.write("TJT: " + df.format(NPT));
                escritor.newLine();
                escritor.write("FINAL TIME: " + tiemposFpedido);
                escritor.newLine();
                escritor.write("DUE DATE:   " + dueDate);
                escritor.newLine();
                break;
            default:
                break;
        }
        escritor.write("OPERATION BY MACHINE: ");
        escritor.newLine();
        escritor.newLine();

        for (int i = 0; i < listaMaquinas.size(); i++) {
            String linea = "Machine: " + (i + 1) + ">>";
            for (int j = 0; j < listaMaquinas.get(i).size(); j++) {
                operation x = (operation) listaMaquinas.get(i).get(j);
                linea = linea + "-(" + (x.getPedido() + 1) + "-" + (x.getOperation() + 1) + ")(" + df.format(x.getStartTime()) + "-" + df.format(x.getFinalTime()) + ")";
            }
            escritor.write(linea);
            escritor.newLine();
        }
        escritor.close();
        escribir.close();
    }


    public LinkedList getCIVec() {
        LinkedList<Integer> CI = new LinkedList<>();
        for (int i = 0; i < numeroOperaciones.length; i++) {
            CI.add((int) pedidos.get(i).getCi());
        }
        return CI;
    }
    
    

}
