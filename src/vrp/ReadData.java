/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
public class ReadData {

    LinkedList<Integer> I;
    LinkedList<Integer> J;
    LinkedList<Integer> x;
    LinkedList<Integer> y;
    LinkedList<Integer> ReadyTime;
    LinkedList<Integer> DueDate;
    LinkedList<Integer> customer;
    LinkedList<Integer> Demand;
    LinkedList<Integer> job;
    int mtxDistancia[][];
    String file;
    int numeroPedidos;
    LinkedList<Integer> ci;
    int minci;
    int makespan;
    int CRMAX;
    int bestSolution;
    int minutos;
    int diversificacion;
    int h;
    int numerodePedidos;
    String direcionArchivos;
    boolean isgrasp;

    /**
     *
     * @param file Dirección del archivo
     * @param np Numero de pedidos
     * @param minutos Minutos de partidad de los vehículos
     * @param time tiempo de corrida o numero de corridas
     * @param bograsp yes if the algorithm gras is run, otherwise execute VND
     * algorithm
     */
    public ReadData(String file, int np, int minutos, int time, boolean bograsp) {
        this.file = file;
        customer = new LinkedList<>();
        x = new LinkedList<>();
        y = new LinkedList<>();
        ReadyTime = new LinkedList<>();
        DueDate = new LinkedList<>();
        numeroPedidos = np;
        CRMAX = 10000000;
        h = minutos;
        this.minutos = time;
        this.isgrasp = bograsp;
        job = new LinkedList<>();
        this.Demand = new LinkedList<>();
    }

    public void setCi(LinkedList<Integer> ci) {
        this.ci = ci;
    }

    public void readTSP(LinkedList<Integer> x, LinkedList<Integer> y,
            LinkedList<Integer> DueDate, LinkedList<Integer> customer,
            LinkedList<Integer> Demand, int neib, boolean initial) throws FileNotFoundException, IOException {
        this.x = x;
        this.y = y;
        this.DueDate = DueDate;
        this.customer = customer;
        this.Demand = Demand;
        mtxDistance();
        minci();
        rutas();
        asignarPedidos();
        heuristicaDividirRutas();
        // write("C:\\Users\\Asus\\Desktop\\TRMINSTANCE2", ins);

//--------------------De aqui para abajo es el algoritmo
        //vecinoCercano();
        //System.out.println("----------------vn");
        //localsearch();
        //inicialGRASP();
        /**
         * if (isgrasp) { Grasp(); } else { VND(neib); }
         */
        if (initial == false) {
            VND(neib);
        }
    }

    int nLotes = 0;

    public void mtxDistance() {
        mtxDistancia = new int[x.size()][y.size()];
        for (int i = 0; i < x.size(); i++) {
            for (int j = i; j < mtxDistancia[i].length; j++) {
                int x1 = x.get(i);
                int x2 = x.get(j);
                int y1 = y.get(i);
                int y2 = y.get(j);
                int distancia = calcularDist(x1, x2, y1, y2);
                mtxDistancia[i][j] = distancia;
                mtxDistancia[j][i] = distancia;
            }
        }
    }

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

    HashMap<Double, Double> valoresTime = new HashMap<>();

    void guardarSoluciones(double time, double solution) throws IOException {
        valoresTime.put(time, solution);
        //guardarSol(direcionArchivos, "Time");
    }

    public int calcularDist(int x1, int x2, int y1, int y2) {
        int x = (x1 - x2);
        int y = (y1 - y2);
        double dii = Math.pow(x, 2) + Math.pow(y, 2);
        double b = Math.sqrt(dii);
        double c = (int) Math.round(b) * 60 / 30;
        return (int) c;
    }


    LinkedList<order> ordenes = new LinkedList<>();
    LinkedList<LinkedList> ordxPed = new LinkedList<>();
    LinkedList<order> nodosIniciales = new LinkedList<>();
    LinkedList<order> nodosFinales = new LinkedList<>();

    public void asignarPedidos() {
        for (int i = 0; i < HIR.size(); i++) {
            order nodoInicial;
            order nodoFinal;
            nodoInicial = new order(0, x.get(0), y.get(0), -1, -1);
            nodoFinal = new order(0, x.get(0), y.get(0), -2, -2);
            nodoInicial.setTiempovisita(HIR.get(i));
            nodosIniciales.add(nodoInicial);
            nodosFinales.add(nodoFinal);
        }
        int contador = 1;
        for (int i = 0; i < nLotes; i++) {
            LinkedList<order> ord = new LinkedList<>();
            for (int j = 0; j < numeroPedidos; j++) {
                order orden = new order(contador, x.get(contador), y.get(contador), i, j);
                orden.setReadyTime(ci.get(i));
                //int nvs = DueDate.get(contador) - ReadyTime.get(contador);
                ordenes.add(orden);
                ord.add(orden);
                contador++;
            }
            ordxPed.add(ord);
        }

    }

    void cii() {
        ci = new LinkedList<>();
        int contador = 1;
        for (int i = 0; i < nLotes; i++) {
            order o = (order) ordxPed.get(i).getFirst();
            ci.add(o.getReadyTime());
            contador++;
        }
    }

    void minci() {
        minci = 1000000000;
        makespan = 0;
        nLotes = ci.size();
        for (int i = 0; i < ci.size(); i++) {
            if (minci > ci.get(i)) {
                minci = ci.get(i);
            }
            if (makespan < ci.get(i)) {
                makespan = ci.get(i);
            }
        }
    }

    LinkedList<Integer> HIR = new LinkedList<>();

    void rutas() {
        int hi = minci;
        while (hi <= makespan) {
            if (hi + h <= makespan) {
                HIR.add(hi + h);
                hi += h;
            } else {
                HIR.add(hi + h);
                hi += h;
            }
        }
    }

    LinkedList<LinkedList> nodosxruta = new LinkedList<>();

    void heuristicaDividirRutas() {
        for (int i = 0; i < HIR.size(); i++) {
            LinkedList<order> nodos = new LinkedList<>();
            for (int j = 0; j < ordxPed.size(); j++) {
                if (i == 0) {
                    if (ci.get(j) <= HIR.getFirst()) {
                        nodos.addAll(ordxPed.get(j));
                    }
                } else {
                    if (ci.get(j) <= HIR.get(i) && ci.get(j) > HIR.get(i - 1)) {
                        nodos.addAll(ordxPed.get(j));
                    }
                }
            }
            nodosxruta.add(nodos);
        }

    }

    LinkedList<LinkedList> recorridos = new LinkedList<>();

    void vecinoCercano() {
        for (int i = 0; i < HIR.size(); i++) {
            recorridos.add(vecinonear(i));
            //System.out.println();
        }
    }

    LinkedList vecinonear(int ruta) {
        CRMAX=0;
        LinkedList<order> recorrido = new LinkedList<>();
        recorrido.add(nodosIniciales.get(ruta));
        //System.out.println(recorrido.getLast());
        int pos = 1;
        while (recorrido.size() < nodosxruta.get(ruta).size() + 1) {
            order nodoActual = recorrido.getLast();
//            System.out.println(nodoActual);
            int posActual = nodoActual.getPosMtx();
            int menordistancia = 1000000000;
            order mejornodo = null;
            for (int i = 0; i < nodosxruta.get(ruta).size(); i++) {
                order nodoProbar = (order) nodosxruta.get(ruta).get(i);
                int posProbar = nodoProbar.getPosMtx();
                int distancia = mtxDistancia[posActual][posProbar];
                if (nodoProbar != nodoActual && nodoProbar.estaRuta == false) {
                    if (distancia < menordistancia) {
                        menordistancia = distancia;
                        mejornodo = nodoProbar;
                    }
                }
            }
            if (mejornodo == null) {
                //System.out.println("");
            }
            mejornodo.setEstaRuta(true);
            mejornodo.setPosRuta(pos);
            pos++;
            recorrido.add(mejornodo);
            mejornodo.setTiempovisita(nodoActual.getTiempovisita() + menordistancia);
            //System.out.println(mejornodo);
        }
        int dist = mtxDistancia[recorrido.getLast().getPosMtx()][0];
        nodosFinales.get(ruta).setTiempovisita(recorrido.getLast().getTiempovisita() + dist);
        if (CRMAX < nodosFinales.get(ruta).getTiempovisita()) {
            CRMAX = nodosFinales.get(ruta).getTiempovisita();
        }
        BKS=CRMAX;
        CRi.add(nodosFinales.get(ruta).getTiempovisita());
        recorrido.add(nodosFinales.get(ruta));
        //System.out.println(nodosFinales.get(ruta));
        return recorrido;
    }

    LinkedList<Integer> CRi = new LinkedList<>();
    long inicio;

    void Grasp() throws IOException {
        int BKS = 1000000000;
        LinkedList<LinkedList> mejorRecorrido = new LinkedList<>();
        inicio = System.nanoTime();
        double demor = 0;
        LocalSearch bestSol = null;
        double besttime = 0;
        while (demor < (minutos * 60)) {
            long fin = System.nanoTime();
            demor = (fin - inicio) * 1.0e-9;
            LocalSearch l = new LocalSearch(nodosIniciales, nodosFinales, HIR, nodosxruta, mtxDistancia, ci, diversificacion);
            l.inicialGRASP();
            if (l.getCRMAX() < BKS) {
                BKS = l.getCRMAX();
                mejorRecorrido = l.getRecorridos();
                bestSol = l;
                demor = (fin - inicio) * 1.0e-9;
                besttime = demor;
            }
        }
        bestSol.write(direcionArchivos, besttime);
        // System.out.println("BKS: " + BKS);
        for (int i = 0; i < recorridos.size(); i++) {
            //   System.out.println("->");
            for (int j = 0; j < mejorRecorrido.get(i).size(); j++) {
                //     System.out.println(mejorRecorrido.get(i).get(j));
            }
        }
    }
    int BKS;
    LocalSearch bestSol = null;
    int numeroIteraciones=1;

    public void setNumeroIteraciones(int numeroIteraciones) {
        this.numeroIteraciones = numeroIteraciones;
    }

    public void VND(int neib) throws IOException {
        //System.out.println("A// "+ getCRMAX());
        BKS = 1000000000;
        LinkedList<LinkedList> mejorRecorrido = new LinkedList<>();
        inicio = System.nanoTime();
        double demor = 0;
        LocalSearch l = new LocalSearch(nodosIniciales, nodosFinales, HIR, nodosxruta, mtxDistancia, ci, diversificacion);
        l.solucionInicial();
        bestSol=l;
        double besttime = 0;
        int contador = 0;
        while (contador < 5) {
            contador++;
            long fin = System.nanoTime();
            if (neib == 0) {
                l.VND1();
            } else {
                l.VND2();
            }
            if (l.getCRMAX() < BKS) {
                BKS = l.getCRMAX();
                mejorRecorrido = l.getRecorridos();
                bestSol = l;
                // System.out.println(BKS);
                demor = (fin - inicio) * 1.0e-9;
                besttime = demor;
                l.comprobarMovimientmo();
            }
            l.apply2opt();
        }
        

    }

    public void setDiversificacion(int diversificacion) {
        this.diversificacion = diversificacion;
    }

    public void setMinutos(int minutos) {
        this.minutos = minutos;
    }

    public void setDirecionArchivos(String direcionArchivos) {
        this.direcionArchivos = direcionArchivos;
    }

    public void write(String ruta, int ins) throws IOException {

        DecimalFormat df = new DecimalFormat("#.00");

        String r = ruta + File.separator + "TRM" + ins + ".txt";
        FileWriter escribir = new FileWriter(r);
        BufferedWriter escritor = new BufferedWriter(escribir);
        escritor.write(ci.size() + " " + minci + " " + makespan);
        escritor.newLine();
        escritor.write("Batch " + "  Order " + "    XCOORD " + "    YCOORD " + "   READY TIME" + "    DUE DATE ");
        escritor.newLine();
        int contador = 0;
        order inicial = nodosIniciales.get(1);
        String cadena = (inicial.getPedido() + 1) + "         " + (inicial.getJ() + 1) + "         " + inicial.getX() + "         " + inicial.getY() + "         " + inicial.getReadyTime() + "          " + inicial.getDueDate();
        System.out.println(cadena);
        escritor.write(cadena);
        escritor.newLine();
        for (int i = 0; i < ordxPed.size(); i++) {
            for (int j = 0; j < ordxPed.get(i).size(); j++) {
                order o = (order) ordxPed.get(i).get(j);
                //System.out.println(o);
                cadena = (o.getPedido() + 1) + "         " + (o.getJ() + 1) + "         " + o.getX() + "         " + o.getY() + "         " + o.getReadyTime() + "          " + o.getDueDate();
                System.out.println(cadena);
                escritor.write(cadena);
                escritor.newLine();
            }
        }
        escritor.close();
        escribir.close();
    }

    public int getBKS() {
        return BKS;
    }

    public LocalSearch getBestSol() {
        return bestSol;
    }
    
    public int  getCRMAX(){
        int cm=0;
        for (int i = 0; i < CRi.size(); i++) {
            if(cm<CRi.get(i)){
                cm= CRi.get(i);
            }
            
        }
        
        return cm;
    }

}
