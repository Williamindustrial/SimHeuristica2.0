/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import vrp.LecturaDatos;
import vrp.LocalSearch;
import vrp.ReadData;

/**
 *
 * @author willi
 */
public class reader {

    /**
     * Factor de flexibilidad
     */
    double factorFlexibilidad;
    /**
     * Dirección archivo de origen
     */
    String file;
    /**
     * Dirección a guardar archivos resultantes
     */
    String RutaSolucion;

    /**
     * Criterio a optimizar
     */
    int optimizador;
    /**
     * Retorna el número total de operaciones
     */
    int numeroOp = 0;
    /**
     * Vector que contiene el número de operaciones de cada pedido
     */
    int[] numeroOperaciones;
    /**
     * Contiene el orden de las maquinas
     */
    LinkedList<LinkedList> listaMaquinas;
    /**
     * Operación final e inicial
     */
    operation opInicial, opFinal;
    /**
     * Matriz de las feromonas
     */
    feromonas mtx;
    /**
     * Hora de inicio del algoritmo
     */
    long inicio;
    /**
     * Límite superior de feromonas
     */
    double li;
    /**
     * Límite superior de feromonas
     */
    double ls;
    /**
     * Valor inicial de las feromonas;
     */
    double vi;
    /**
     * Factor de evaporación
     */
    double initial;

    /**
     * Constructor del lector
     *
     * @param file Dirección del archivo a leer
     * @param direccionGuardarSolucion Dirección del archivo a guardar
     * @param directionvrpfile Dirección donde leer el archivo vrp
     * @param perturbaciones Número de perturbaciones
     */
    public reader(String file, String direccionGuardarSolucion, String directionvrpfile, double initial, int perturbaciones) {
        this.file = file;
        this.RutaSolucion = direccionGuardarSolucion;
        factorFlexibilidad = 1.3;
        valorBest = 10000000;
        this.directionvrpfile = directionvrpfile;
        this.perturbacion = perturbaciones;
        this.initial=initial;
    }

    /**
     * Total de pedidos
     */
    int numeroPedidos = 0;
    /**
     * Número de maquinas del schedulling
     */
    int numeroMaquinas = 0;
    /**
     * Maximo número de operaciones por pedidos
     */
    int maximoOPeracionesxPedidos = 0;
    /**
     * Estructura que guarda los pedidos La posición [i] contiene el pedido
     */
    LinkedList<pedido> pedidos = new LinkedList<>();
    LinkedList<pedido> pedidos2 = new LinkedList<>();
    /**
     * El valor best en static
     */
    static double valorBest;
    /**
     * Soluciones grafica
     */

    LinkedList<HashMap> soluciones = new LinkedList<>();
    /**
     * Cantidad de perturbaciones
     */
    int perturbacion;

    /**
     * Lee el archivo
     *
     * @throws FileNotFoundException: No se encontro el archivo
     * @throws IOException: El archivo no es correcto
     * @throws Exception: El archivo no es correcto
     */
    public void flexible() throws FileNotFoundException, IOException, Exception {
        LinkedList<Integer> di = new LinkedList<>();
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String bfRead;
        int numeroMatriz = 0;
        double[][] matric = new double[numeroPedidos + 1][numeroPedidos + 1];
        int numeroVecesMatriz = 0;
        int pos = 1;
        ArrayList<Integer> numeradores = new ArrayList();
        int numeroGuia = 0;
        try {
            bf = new BufferedReader(new FileReader(file));
            int numeroLineas = 0;
            int matrix = 0;
            while ((bfRead = bf.readLine()) != null) {
                StringTokenizer lineasConEspacios = new StringTokenizer(bfRead);
                String cadenaLetras[] = bfRead.split(" ");
                int cadenaNumeros[] = new int[cadenaLetras.length];
                if (numeroLineas == 0) {
                    numeroPedidos = Integer.parseInt(cadenaLetras[0]);
                    operation.totalPedidos = numeroPedidos;
                    numeroMaquinas = Integer.parseInt(cadenaLetras[1]);
                    numeroLineas++;
                } else {
                    for (int w = 0; w < numeroPedidos; w++) {
                        cadenaLetras = bfRead.split(" ");
                        cadenaNumeros = new int[cadenaLetras.length];
                        int numeroCaracteres = 0;
                        int numeroOperaciones = Integer.parseInt(cadenaLetras[0]);
                        numeroOp += numeroOperaciones;
                        if (maximoOPeracionesxPedidos < numeroOperaciones) {
                            maximoOPeracionesxPedidos = numeroOperaciones;
                        }
                        numeroCaracteres++;
                        LinkedList<operation> operaciones = new LinkedList<>();
                        double valorMtotal = 0;
                        for (int i = 0; i < numeroOperaciones; i++) {
                            double tiempoMaximo = 0;
                            int numeroMaquinasO = Integer.parseInt(cadenaLetras[numeroCaracteres]);
                            numeroCaracteres++;
                            HashMap<Integer, Double> tiempos = new HashMap<>();
                            for (int j = 0; j < numeroMaquinasO; j++) {
                                int clave = Integer.parseInt(cadenaLetras[numeroCaracteres]);
                                numeroCaracteres++;
                                double valor = Double.parseDouble(cadenaLetras[numeroCaracteres]);
                                tiempos.put(clave - 1, valor);
                                numeroCaracteres++;
                                tiempoMaximo = valor + tiempoMaximo;
                            }
                            operation op = new operation(w, i, tiempos);
                            double agregar = tiempoMaximo / tiempos.size();
                            valorMtotal = valorMtotal + agregar;
                            op.setPosMatriz(pos);
                            operaciones.add(op);
                            pos++;
                        }
                        int v = (int) Math.round(factorFlexibilidad * valorMtotal);
                        di.add(v);
                        //pedidos.add(operaciones);
                        pedido p = new pedido(numeroOperaciones, operaciones);
                        bfRead = bf.readLine();
                        pedidos2.add(p);
                        pedidos2.getLast().setdueDate(Double.valueOf(v));
                    }
                    matrix++;
                }

            }
            for (int i = 0; i < pedidos.size(); i++) {
                if (i < pedidos.size() * 0.2) {
                    pedidos.get(i).setWi(4);
                } else if (i >= pedidos.size() * 0.2 & i < pedidos.size() * 0.8) {
                    pedidos.get(i).setWi(2);
                } else {
                    pedidos.get(i).setWi(1);
                }
            }
            numeroLineas++;
            System.out.println("Due date " + di);

        } catch (NumberFormatException e) {
            System.out.println("NO SE ENCONTRO ARCHIVO.........");
            e.printStackTrace();
        }
        opInicial = new operation(-1, -1, null);
        opFinal = new operation(pedidos.size(), maximoOPeracionesxPedidos + 1, null);
        System.out.println("FINAL" + opFinal);
        System.out.println("OPINICIAL" + opInicial);
    }

    /**
     * Seleciona la maquina de cada operación
     */
    public void selecionarMaquina() {
        numeroOperaciones = new int[pedidos.size()];
        for (int i = 0; i < pedidos.size(); i++) {
            pedido p = pedidos.get(i);
            for (int j = 0; j < p.getOperaciones().size(); j++) {
                operation op = p.getOperaciones().get(j);
                double menorTiempo = 100000000;
                for (Map.Entry<Integer, Double> entry : op.getSetMachine().entrySet()) {
                    if (menorTiempo > entry.getValue()) {
                        op.selecionarMaquina(entry.getKey());
                        menorTiempo = entry.getValue();
                    }
                }
                if (op.equals(pedidos.get(i).getOperaciones().getFirst())) {
                    op.setAnteriorRuta(opInicial);
                } else if (op.equals(pedidos.get(i).getOperaciones().getLast())) {
                    operation ant = pedidos.get(i).getOperaciones().get(j - 1);
                    ant.setSiguieteRuta(op);
                    op.setAnteriorRuta(pedidos.get(i).getOperaciones().get(j - 1));
                    op.setSiguieteRuta(opFinal);
                } else {
                    operation ant = pedidos.get(i).getOperaciones().get(j - 1);
                    ant.setSiguieteRuta(op);
                    op.setAnteriorRuta(pedidos.get(i).getOperaciones().get(j - 1));
                }
            }
            numeroOperaciones[i] = pedidos.get(i).getNumerooperaciones();
            //numeroOp+= pedidos.get(i).getNumerooperaciones();
        }
    }

    /**
     * Genera el orden inicial de las operaciones
     *
     * @return orden inicial de las operaciones
     */
    public LinkedList generarGrupoDeOperaciones() {
        selecionarMaquina();
        LinkedList<operation> listaOp = new LinkedList<>();
        LinkedList<Double> due = new LinkedList<>();
        LinkedList<Integer> posicionesDuedate = new LinkedList<>();
        listaMaquinas = new LinkedList<>();
        for (int i = 0; i < numeroMaquinas; i++) {
            LinkedList<operation> listaM = new LinkedList<>();
            listaMaquinas.add(listaM);
        }
        for (int i = 0; i < pedidos.size(); i++) {
            due.add(pedidos.get(i).getDueDate());
        }
        for (int i = 0; i < pedidos.size(); i++) {
            double menor = 1000000000;
            int posicion = 0;
            for (int j = 0; j < due.size(); j++) {
                if (menor > due.get(j)) {
                    menor = due.get(j);
                    posicion = j;
                }
            }
            posicionesDuedate.add(posicion);
            due.set(posicion, 10000000.0);
        }

        for (int i = 0; i < maximoOPeracionesxPedidos; i++) {
            for (int j = 0; j < pedidos.size(); j++) {
                if (i < pedidos.get(posicionesDuedate.get(j)).getOperaciones().size()) {
                    operation add = pedidos.get(posicionesDuedate.get(j)).getOperaciones().get(i);
                    int maquina = add.getMaquinaSelecionada();
                    listaOp.add(add);
                    if (listaMaquinas.get(maquina).isEmpty()) {
                        add.addOperaciónAnterior(opInicial);
                    } else if (i == pedidos.get(posicionesDuedate.get(j)).getOperaciones().size() - 1) {
                        add.addOperaciónAnterior((operation) listaMaquinas.get(maquina).getLast());
                        operation ant = (operation) listaMaquinas.get(maquina).getLast();
                        ant.addOperaciónSiguiente(add);
                        add.addOperaciónSiguiente(opFinal);
                    } else {
                        add.addOperaciónAnterior((operation) listaMaquinas.get(maquina).getLast());
                        operation ant = (operation) listaMaquinas.get(maquina).getLast();
                        ant.addOperaciónSiguiente(add);
                    }
                    listaMaquinas.get(maquina).add(add);
                }
            }
        }
        return listaOp;
    }

    public LinkedList diversificacion2(TransfomaGrafo vecino, int n) {
        LinkedList<TransfomaGrafo> vecinario = new LinkedList<>();
        LinkedList<TransfomaGrafo> vecindarioCompleto = new LinkedList<>();
        vecino.realizar();
        for (int i = 0; i < n; i++) {
            //solution si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, CambiosTiemposXMaquina, direcciónGuardarArchivos, vecino.getOrden(), opInicial, opFinal);
            solution si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, vecino.getOrden(), opInicial, opFinal, optimizador);
            si.solucionar();
            si.colas(optimizador, 1, 1, 0, false);
            vecindarioCompleto = si.getVecindarioCompleto();
            vecinario = si.getVecindario();
            Random r = new Random();
            int a = r.nextInt(((n + i) + 1));
            int posicion = r.nextInt(vecinario.size());
            vecino = (TransfomaGrafo) vecinario.get(posicion);
            vecino.realizar();
        }
        return vecinario;
    }

    
    /**
     * Clonar la estructura de datos que contiene la información
     *
     * @param pedidos
     * @return estructura de datos clonada
     */
    public LinkedList<pedido> clonarLista(LinkedList<pedido> pedidos) {
        LinkedList<pedido> pedidoClonado = new LinkedList<>();
        for (pedido Pedido : pedidos) {
            pedidoClonado.add(Pedido.clonar1());
        }
        return pedidoClonado;
    }

    HashMap<Double, Double> valoresTime = new HashMap<>();

    void guardarSoluciones(double time, double solution) throws IOException {
        valoresTime.put(time, solution);
        guardarSol(RutaSolucion, "Time");
    }

    public void setStocasticos() {
        for (int i = 0; i < pedidos2.size(); i++) {
            pedido p = pedidos2.get(i).clonar1();
            pedidos.add(p);
        }
    }

    String directionvrpfile;

    void slocal(double it, int numeroH, double alfa, double beta, int w, String localizacionVRP) throws Exception {
        flexible();
        setStocasticos();
        //opFinal.setEstaRuta(true);
        mtx = new feromonas(numeroOp, ls, li, vi, numeroMaquinas);
        mtx.inicializar();
        double best = 1000000000.0;
        inicio = System.nanoTime();
        double demor = 0;
        double bestH = 1000000000;
        hormiga b = null;
        hormiga as = null;
        LecturaDatos l = new LecturaDatos();
        l.readTSP(0, 1, directionvrpfile);
        ReadData bestR = null;
        for (int i = 0; i < 1; i++) {
            LinkedList<operation> listaOperation = generarGrupoDeOperaciones();
            hormiga h = new hormiga(clonarLista(pedidos), listaOperation, numeroPedidos, maximoOPeracionesxPedidos, numeroMaquinas, numeroOp, opInicial, opFinal, optimizador, mtx, directionvrpfile);
            l.initialSol(localizacionVRP, h.getCi());
            ReadData r = l.getR();
            r.VND(i);
            LocalSearch bl = r.getBestSol();
            if (bl.getCRMAX() < bestH) {
                System.out.println(bestH);
                bestH = bl.getCRMAX();
                b = h;
                if (bl.getCRMAX() < best) {
                    best = bl.getCRMAX();
                    as = b;
                    bestR = r;
                    bl.write(RutaSolucion, demor);
                    as.writeMono(RutaSolucion, "Initial");
                }
            }
        }
        System.out.println("Local: " + bestH + "Best: " + best);
        long fin = System.nanoTime();
        demor = (fin - inicio) * 1.0e-9;

        opInicial.setInicial(true);

        solution solutionBest = VNS2(b, 3, demor, optimizador, 1, w);
    }

    solution best = null;
    int contadora = 10;

    LecturaDatos lecturer = new LecturaDatos();

    
    solution VNS2(hormiga h, double it, double tActual, int estadistica, int n, int ww) throws IOException {

        lecturer.readTSP(0, 1, directionvrpfile);
        solution inicial = new solution(h.getPedidos(), h.getOrdenMaquinas(), numeroOperaciones, h.getOrdenSolutionF(), opInicial, opFinal, optimizador);
        inicial.solucionar();
        inicial.colas(estadistica, n, 0, 0, false);
        inicial.colas(estadistica, n, 1, 0, false);
        //inicial.colas(estadistica, n, 2, 0, false);
        LinkedList<TransfomaGrafo> vecindario = inicial.getVecindario();
        LinkedList<TransfomaGrafo> vecindario2 = inicial.getVecindario();
        TransfomaGrafo vecino;
        TransfomaGrafo vecinoLocal;
        solution solucionBest = inicial;
        try {
            vecino = inicial.getVecindario().getFirst();
            vecinoLocal = inicial.getVecindario().getFirst();
        } catch (Exception e) {
            vecino = inicial.getVecindarioCompleto().getFirst();
            vecinoLocal = inicial.getVecindarioCompleto().getFirst();
        }

        vecino.realizar();
        TransfomaGrafo vecinoBest = vecino;
        LocalSearch mejorVRP = null;
        double bestLocal = 10000000;
        int x = 0;
        System.out.println(inicial.getValorLocal());
        solution si = inicial;
        solution bi = si;
        System.out.println("inicia");
        double demor = 0;
        int i = 0;
        boolean m = false;
        inicio = System.nanoTime();
        double bestH;
        LocalSearch solLocalr=null; 
        while (i < n) {
            Random numAleatorio = new Random();
            int veci = 0;
            int veciIndez = 0;
            while (demor < (60 * 60)) {
                LinkedList<Integer> LV = new LinkedList<>();
                LV.add(0);
                LV.add(1);
                //LV.add(2);
                int bl = 100000000;
                //int veciIndez = 0;
                //int veci = 0;
                veciIndez = numAleatorio.nextInt(LV.size());
                veci = LV.get(veciIndez);
                while (!vecindario.isEmpty()) {
                    while (!LV.isEmpty()) {
                        boolean mejoro = false;
                        // System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");
                        for (int l = 0; l < vecindario.size(); l++) {
                            vecino = vecindario.get(l);
                            vecino.realizar();
                            si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, vecino.getOrden(), opInicial, opFinal, optimizador);
                            si.solucionar();
                            LocalSearch solcurrently = calcularCMRAX(directionvrpfile, si);
                            double CRMAX = solcurrently.getCRMAX();
                            if (bestLocal > CRMAX) {
                                vecinoLocal = vecino;
                                solLocalr=solcurrently;
                                
                                bestLocal = CRMAX;
                                mejoro = true;
                                m = true;
                                if (valorBest > CRMAX) {
                                    long fin = System.nanoTime();
                                    demor = (fin - inicio) * 1.0e-9;
                                    valorBest = CRMAX;
                                    vecinoBest = vecino;
                                    mejorVRP = solcurrently;
                                    si.writeMono(RutaSolucion, "Best", demor);
                                    mejorVRP.write(RutaSolucion, demor);
                                    best = si;
                                    solucionBest = si;
                                    long fini = System.nanoTime();
                                    tActual = (fini - inicio) * 1.0e-9;
                                    si.writeMono(RutaSolucion, "Best", tActual);
                                    // guardarSoluciones(tActual, valorBest);
                                }
                            }
                        }
                        if (mejoro) {
                            vecino = vecinoLocal;
                            si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, vecino.getOrden(), opInicial, opFinal, optimizador);
                            si.solucionar();
                            //veciIndez = numAleatorio.nextInt(LV.size());
                            //veci = LV.get(veciIndez);
                            LV.clear();
                            LV.add(0);
                            LV.add(1);
                            //LV.add(2);
                            si.colas(estadistica, 0, veci, 0, false);
                            double a = si.getValorLocal();
                            System.out.println("best: " + valorBest + "--local: " + bestLocal + "Vecino: " + veci);
                            vecindario = si.getVecindario();
                        } else {
                            LV.remove(veciIndez);
                            if (LV.size() > 0) {
                                vecino = vecinoLocal;
                                vecino.realizar();
                                si = new solution(vecino.getPedidosClonados(), vecino.getOrdenFinalMaquinas(), numeroOperaciones, vecino.getOrden(), opInicial, opFinal, optimizador);
                                si.solucionar();
                                veciIndez = numAleatorio.nextInt(LV.size());
                                veci = LV.get(veciIndez);
                                System.out.println("Change neibor to " + veci);
                                si.colas(estadistica, 0, veci, 0, false);
                                vecindario = si.getVecindario();
                            }
                        }
                    }
                    if (LV.isEmpty()) {
                        vecindario.clear();
                    }
                }
                
                System.out.println("reinicio");
                Random ri = new Random();
                vecindario = diversificacion2(vecino, perturbacion);
                bestLocal = 1000000000;
                i++;
                long fin = System.nanoTime();
                demor = (fin - inicio) * 1.0e-9;
            }
            // vecindario = tabu(vecinoBest, tiempoTtal, tActual, estadistica, n, ww);
            System.out.println("Sale");
            // Empezo nuevo
            bestLocal = si.getValorLocal();
            //double r2 = (tiempoTtal * 60 - tActual) / (tiempoTtal * 60);
            //double evapoRacionPor = Math.exp(-r2);
            //mtx.evaporar(fe);
            // bi.actualizarFeromona(mtx);
        }
        return solucionBest;
    }
    
    
    LocalSearch calcularCMRAX(String localizacionVRP, solution SOL) throws IOException {
        lecturer.nueva(localizacionVRP, SOL);
        ReadData r = lecturer.getR();
        return r.getBestSol();
    }

   
    LinkedList<TransfomaGrafo> movPenalizados = new LinkedList<>();

    void guardarSol(String ruta, String name) throws IOException {
        String rutaP = ruta;
        ruta = ruta + File.separator + name + ".txt";
        FileWriter escribir = new FileWriter(ruta);
        BufferedWriter escritor = new BufferedWriter(escribir);
        for (Map.Entry<Double, Double> entry : valoresTime.entrySet()) {
            escritor.write(entry.getKey() + " " + entry.getValue());
            escritor.newLine();
        }
        escritor.close();
        escribir.close();
    }

}
