/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Asus
 */
public final class LocalSearch {

    LinkedList<LinkedList> recorridos;
    LinkedList<order> nodosIniciales;
    LinkedList<order> nodosFinales;
    LinkedList<Integer> HIR;
    LinkedList<LinkedList> nodosxruta;
    int mtxDistancia[][];
    int CRMAX;
    LinkedList<Integer> ci;
    LinkedList<Integer> CRi;
    int dispersion;

    public LocalSearch(LinkedList<order> nodosIniciales, LinkedList<order> nodosFinales, LinkedList<Integer> HIR, LinkedList<LinkedList> nodosxruta, int[][] mtxDistancia, LinkedList<Integer> ci, int dispersion) {
        this.nodosIniciales = nodosIniciales;
        CRi = new LinkedList<>();
        this.HIR = HIR;
        recorridos = new LinkedList<>();
        this.nodosFinales = new LinkedList<>();
        for (int i = 0; i < HIR.size(); i++) {
            this.nodosFinales.add(nodosFinales.get(i).clonar());
        }
        this.nodosIniciales = nodosIniciales;
        this.nodosxruta = nodosxruta;
        this.mtxDistancia = mtxDistancia;
        this.ci = ci;
        this.dispersion = dispersion;
    }

    LinkedList bestInsertion(int ruta, int nmaxNodos) {
        LinkedList<order> recorrido = new LinkedList<>();
        recorrido.add(nodosIniciales.get(ruta));
        //System.out.println(recorrido.getLast());
        int pos = 1;
        int ultimaDistanciaVuelta=0;
        while (recorrido.size() < nodosxruta.get(ruta).size() + 1) {
            order nodoActual = recorrido.getLast();
            int posActual = nodoActual.getPosMtx();
            order mejorNodo = null;
            int menorDistanciaIda = 10000000;
            int menordistanciaDevuelta = 10000000;
            order nodoanterior = null;
            int contadormm = 0; // Cuenta los posibles nodos
            int menorD = 1000000000;
            int distancia=1000000000;
            for (int j = 0; j < nodosxruta.get(ruta).size(); j++) {
                order nodoProbar = (order) nodosxruta.get(ruta).get(j);
                int posProbar = nodoProbar.getPosMtx();
                int distanciaIda = mtxDistancia[posActual][posProbar];
                int distanciaDevuelta = mtxDistancia[posProbar][nodosIniciales.get(ruta).getPosMtx()];
                if (nodoProbar.estaRuta == false && nodoanterior != nodoProbar) {
                    distancia=distanciaIda+distanciaDevuelta-ultimaDistanciaVuelta;
                    if (distancia < menorD) {
                        menorD = distancia;
                        menorDistanciaIda= distanciaIda;
                        menordistanciaDevuelta= distanciaDevuelta;
                        mejorNodo= nodoProbar;
                    }

                }
            }
            mejorNodo.setPosRuta(nodoActual.getPosRuta() + 1);
            mejorNodo.setEstaRuta(true);
            mejorNodo.setPosRuta(pos);
            recorrido.add(mejorNodo);
            int dis = mtxDistancia[nodoActual.getPosMtx()][mejorNodo.getPosMtx()];
            mejorNodo.setTiempovisita(nodoActual.getTiempovisita() + menorDistanciaIda);
            ultimaDistanciaVuelta=menordistanciaDevuelta;
        }
        int dist2 = mtxDistancia[recorrido.getLast().getPosMtx()][0];
        int dist = ultimaDistanciaVuelta;
        nodosFinales.get(ruta).setTiempovisita(recorrido.getLast().getTiempovisita() + dist);
        CRi.add(nodosFinales.get(ruta).getTiempovisita());
        recorrido.add(nodosFinales.get(ruta));

        //System.out.println(nodosFinales.get(ruta));
        // System.out.println("Initial solution:" + CRMAX);
        return recorrido;

    }

    LinkedList GRASP(int ruta, int nmaxNodos) {
        LinkedList<order> recorrido = new LinkedList<>();
        recorrido.add(nodosIniciales.get(ruta));
        //System.out.println(recorrido.getLast());
        int pos = 1;
        while (recorrido.size() < nodosxruta.get(ruta).size() + 1) {
            order nodoActual = recorrido.getLast();
            int posActual = nodoActual.getPosMtx();
            order[] mejoresNodos = new order[nmaxNodos];
            int[] menorDistancia = new int[nmaxNodos];
            order nodoanterior = null;
            int contadormm = 0; // Cuenta los posibles nodos
            for (int i = 0; i < nmaxNodos; i++) {
                int menorD = 1000000000;
                for (int j = 0; j < nodosxruta.get(ruta).size(); j++) {
                    order nodoProbar = (order) nodosxruta.get(ruta).get(j);
                    int posProbar = nodoProbar.getPosMtx();
                    int distancia = mtxDistancia[posActual][posProbar];
                    boolean estaEnLista = false;
                    for (int k = 0; k < mejoresNodos.length && estaEnLista == false; k++) {
                        order nodoOrder = mejoresNodos[k];
                        if (nodoOrder == nodoProbar) {
                            estaEnLista = true;
                        }
                    }
                    if (nodoProbar != nodoActual && nodoProbar.estaRuta == false && nodoanterior != nodoProbar && estaEnLista == false) {
                        if (nodoProbar != nodoActual && nodoProbar.estaRuta == false) {
                            if (distancia < menorD) {
                                menorD = distancia;
                                menorDistancia[contadormm] = distancia;
                                mejoresNodos[contadormm] = nodoProbar;
                            }
                        }
                    }
                }
                nodoanterior = mejoresNodos[i];
                if (nodoanterior != null) {
                    contadormm++;
                }
            }
            Random r = new Random();
            int valorDado = r.nextInt(contadormm);
            order mejornodo = mejoresNodos[valorDado];
            int menordistancia = menorDistancia[valorDado];
            mejornodo.setPosRuta(nodoActual.getPosRuta() + 1);
            mejornodo.setEstaRuta(true);
            mejornodo.setPosRuta(pos);
            pos++;
            recorrido.add(mejornodo);
            int dis = mtxDistancia[nodoActual.getPosMtx()][mejornodo.getPosMtx()];
            mejornodo.setTiempovisita(nodoActual.getTiempovisita() + menordistancia);
            //System.out.println(mejornodo);
        }
        int dist = mtxDistancia[recorrido.getLast().getPosMtx()][0];
        nodosFinales.get(ruta).setTiempovisita(recorrido.getLast().getTiempovisita() + dist);
        CRi.add(nodosFinales.get(ruta).getTiempovisita());
        recorrido.add(nodosFinales.get(ruta));

        //System.out.println(nodosFinales.get(ruta));
        // System.out.println("Initial solution:" + CRMAX);
        return recorrido;

    }

    HashMap relocate() {
        int best = CRMAX;
        HashMap mejorMovimiento = new HashMap();
        for (int i = 0; i < recorridos.size(); i++) {
            for (int j = 1; j < recorridos.get(i).size() - 1; j++) {
                order nodox = (order) recorridos.get(i).get(j);
                order nodoi = (order) recorridos.get(i).get(j - 1);
                order nodoj = (order) recorridos.get(i).get(j + 1);
                int pedidox = nodox.getPedido();
                for (int k = 0; k < recorridos.size(); k++) {
                    for (int l = 1; l < recorridos.get(k).size() - 1; l++) {
                        order nodok = (order) recorridos.get(k).get(l - 1);
                        order nodol = (order) recorridos.get(k).get(l);
                        if (HIR.get(k) > ci.get(pedidox)) {
                            //&& i!=j
                            if (nodok != nodox && nodol != nodox) {
                                HashMap<String, Integer> CR = calcularRelocate(nodox, nodoi, nodoj, nodok, nodol, i, k);
                                int CRI = CR.get("CR");
                                if (best > CRI) {
                                    best = CRI;
                                    mejorMovimiento.put('x', nodox);
                                    mejorMovimiento.put('i', nodoi);
                                    mejorMovimiento.put('j', nodoj);
                                    mejorMovimiento.put('k', nodok);
                                    mejorMovimiento.put('l', nodol);
                                    mejorMovimiento.put("RA", i);
                                    mejorMovimiento.put("RN", k);
                                    mejorMovimiento.put("PosAx", j);
                                    mejorMovimiento.put("PosNx", l);
                                    mejorMovimiento.putAll(CR);
                                }
                            }

                            }
                        }
                    }
                }
            }
        //System.out.println("best:" + best);
        return mejorMovimiento;
    }

        HashMap<String, Integer> calcularRelocate(order nodox, order nodoi, order nodoj, order nodok, order nodol, int rutaActual, int rutaNueva) {
        order nodoFinal1 = (order) recorridos.get(rutaActual).getLast();
        order nodoFinal2 = (order) recorridos.get(rutaNueva).getLast();
        int CR1 = CRi.get(rutaActual);
        int CR2 = CRi.get(rutaNueva);
        int Tix = mtxDistancia[nodoi.getPosMtx()][nodox.getPosMtx()];
        int Txj = mtxDistancia[nodox.getPosMtx()][nodoj.getPosMtx()];
        int Tij = mtxDistancia[nodoi.getPosMtx()][nodoj.getPosMtx()];
        int Tkl = mtxDistancia[nodok.getPosMtx()][nodol.getPosMtx()];
        int Tkx = mtxDistancia[nodok.getPosMtx()][nodox.getPosMtx()];
        int Txl = mtxDistancia[nodox.getPosMtx()][nodol.getPosMtx()];
        int CRC1 = 0;
        int CRC2 = 0;
        int CR = 0;
        if (rutaActual != rutaNueva) {
            CRC1 = CR1 - (Tix + Txj) + Tij;
            CRC2 = CR2 - Tkl + Tkx + Txl;
        } else {
            CRC1 = CR1 - (Tkl + Tix + Txj) + Tkx + Txl + Tij;
            CRC2 = CR1 - (Tkl + Tix + Txj) + Tkx + Txl + Tij;
        }
        for (int i = 0; i < HIR.size(); i++) {
            order o = (order) recorridos.get(i).getLast();
            if (i != rutaNueva && i != rutaActual) {
                if (CR < CRi.get(i)) {
                    CR = CRi.get(i);
                }
            }
        }

        HashMap<String, Integer> respuesta = new HashMap<>();
        CR = Math.max(CR, CRC1);
        CR = Math.max(CR, CRC2);
        respuesta.put("CRA", CRC1);
        respuesta.put("CRN", CRC2);
        respuesta.put("CR", CR);

        return respuesta;
    }

    void editarRutasRelocate(HashMap mejorMovimiento) {
        order nodox = (order) mejorMovimiento.get('x');
        int RA = (int) mejorMovimiento.get("RA");
        int RN = (int) mejorMovimiento.get("RN");
        int posAx = (int) mejorMovimiento.get("PosAx");
        int posNx = (int) mejorMovimiento.get("PosNx");
        int delta1 = (int) mejorMovimiento.get("CRA");
        int delta2 = (int) mejorMovimiento.get("CRN");
        /*
        System.out.println("x: " + nodox);
        System.out.println("i: " + mejorMovimiento.get('i'));
        System.out.println("j: " + mejorMovimiento.get('j'));
        System.out.println("k: " + mejorMovimiento.get('k'));
        System.out.println("l: " + mejorMovimiento.get('l'));
        System.out.println("CRA: " + recorridos.get(RA));
        System.out.println("CRN: " + recorridos.get(RN));*/
        if (RA == RN) {
            if (posAx < posNx) {
                recorridos.get(RN).add(posNx, nodox);
                recorridos.get(RA).remove(posAx);
            } else {
                recorridos.get(RA).remove(posAx);
                recorridos.get(RN).add(posNx, nodox);
            }
        } else {
            recorridos.get(RN).add(posNx, nodox);
            recorridos.get(RA).remove(posAx);
        }
        CRi.set(RA, delta1);
        CRi.set(RN, delta2);
        /* System.out.println("RA1: " + recorridos.get(RA));
        System.out.println("RN1: " + recorridos.get(RN));*/
        updateCRMAX();

        //System.out.println(CRMAX);
    }

    boolean applyrelocate() {
        HashMap mejorMovimiento = relocate();
        //System.out.println("relocate");
        boolean a = false;
        while (!mejorMovimiento.isEmpty()) {
            editarRutasRelocate(mejorMovimiento);
            mejorMovimiento = relocate();
            a = true;
        }
        return a;
    }

    HashMap calcularSwap(int ruta) {
        HashMap bestmovement = new HashMap();
        int bestD = 1000000;
        int delta = 0;
        int best = 0;
        for (int i = 1; i < recorridos.get(ruta).size() - 2; i++) {
            order nodox = (order) recorridos.get(ruta).get(i);
            order nodoPRX = (order) recorridos.get(ruta).get(i - 1);
            order nodoFRX = (order) recorridos.get(ruta).get(i + 1);
            for (int j = i + 2; j < recorridos.get(ruta).size() - 1; j++) {
                order nodoy = (order) recorridos.get(ruta).get(j);
                order nodoPRY = (order) recorridos.get(ruta).get(j - 1);
                order nodoFRY = (order) recorridos.get(ruta).get(j + 1);
                delta = mtxDistancia[nodoPRY.getPosMtx()][nodox.getPosMtx()]
                        + mtxDistancia[nodox.getPosMtx()][nodoFRY.getPosMtx()]
                        + mtxDistancia[nodoPRX.getPosMtx()][nodoy.getPosMtx()]
                        + mtxDistancia[nodoy.getPosMtx()][nodoFRX.getPosMtx()]
                        - mtxDistancia[nodoPRX.getPosMtx()][nodox.getPosMtx()]
                        - mtxDistancia[nodox.getPosMtx()][nodoFRX.getPosMtx()]
                        - mtxDistancia[nodoPRY.getPosMtx()][nodoy.getPosMtx()]
                        - mtxDistancia[nodoy.getPosMtx()][nodoFRY.getPosMtx()];
                if (delta < 0) {
                    if (delta < best) {
                        best = delta;
                        bestmovement.clear();
                        bestmovement.put("x", nodox);
                        bestmovement.put("px", nodoPRX);
                        bestmovement.put("fx", nodoFRX);
                        bestmovement.put("y", nodoy);
                        bestmovement.put("py", nodoPRY);
                        bestmovement.put("fy", nodoFRY);
                        bestmovement.put("opt", delta);
                        bestmovement.put("posX", i);
                        bestmovement.put("posY", j);
                    }
                }
            }
        }
        //System.out.println(best);
        return bestmovement;
    }

    void comprobarMovimientmo() {
        int mc = 0;
        for (int i = 0; i < recorridos.size(); i++) {
            int mmc = HIR.get(i);
            for (int j = 1; j < recorridos.get(i).size(); j++) {
                order inicial = (order) recorridos.get(i).get(j - 1);
                order finalO = (order) recorridos.get(i).get(j);
                int distancia = mtxDistancia[inicial.getPosMtx()][finalO.getPosMtx()];
                finalO.setTiempovisita(inicial.getTiempovisita() + distancia);
                mmc = mmc + distancia;
            }
            if (mc < mmc) {
                mc = mmc;
            }
        }
        if (mc != CRMAX) {
            //System.out.println("--------------");
        }
        // System.out.println("Real:" + mc + " Calculado:" + CRMAX);
    }

    void editarRutaSwap(HashMap bestmovement, int ruta) {
        //System.out.println(recorridos.get(ruta));
        order nodox = (order) bestmovement.get("x");
        order nodoPx = (order) bestmovement.get("px");
        order nodoFx = (order) bestmovement.get("fx");
        //System.out.println("x" + nodox);
        order nodoy = (order) bestmovement.get("y");
        order nodoPy = (order) bestmovement.get("py");
        order nodoFy = (order) bestmovement.get("fy");
        int posX = (int) bestmovement.get("posX");
        int posY = (int) bestmovement.get("posY");
        int delta = (int) bestmovement.get("opt");
        //nodo x
        int dist = mtxDistancia[nodoPy.posMtx][nodox.getPosMtx()];
        nodox.setTiempovisita(nodoPy.getTiempovisita() + dist);
        recorridos.get(ruta).set(posY, nodox);
        //nodo y

        //System.out.println("y" + nodoy);
        int disty = mtxDistancia[nodoPx.getPosRuta()][nodoy.getPosMtx()];
        nodoy.setTiempovisita(nodoPx.getTiempovisita() + disty);
        recorridos.get(ruta).set(posX, nodoy);
        //System.out.println(recorridos.get(ruta));
        CRi.set(ruta, CRi.get(ruta) + delta);
        updateCRMAX();

    }

    boolean applySwap() {

        // System.out.println("swap");
        int contador = 0;
        for (int i = 0; i < HIR.size(); i++) {
            HashMap bestMovement = calcularSwap(i);
            while (!bestMovement.isEmpty()) {
                editarRutaSwap(bestMovement, i);
                bestMovement = calcularSwap(i);
                contador++;
            }
        }
        // System.out.println("Finaliza swap");
        if (contador > 0) {
            return true;
        } else {
            return false;
        }
    }

    void VND1() {
        applyrelocate();
        boolean appswap = true;
        while (appswap) {
            applyrelocate();
            appswap = applySwap();
        }
        // System.out.println("permutacion");
    }

    void VND2() {
        applySwap();
        boolean apprelocate = true;
        while (apprelocate) {
            applySwap();
            apprelocate = applyrelocate();
        }
        // System.out.println("permutacion");
    }

    void apply2opt() {
        for (int i = 0; i < recorridos.size(); i++) {
            LinkedList<order> ruta = recorridos.get(i);
            Random numAleatorio = new Random();
            boolean pasar = false;
            boolean noevaluar = false;
            if (ruta.size() < 5) {
                pasar = true;
                noevaluar = true;
            }
            int delta = 0;
            int n1 = 1;
            int n2 = 2;
            while (pasar == false) {
                n1 = numAleatorio.nextInt(ruta.size() - 1);
                n2 = numAleatorio.nextInt(ruta.size() - 1);
                if (n1 + 1 < n2) {
                    pasar = true;
                    delta = mtxDistancia[ruta.get(n1).getPosMtx()][ruta.get(n2).getPosMtx()]
                            + mtxDistancia[ruta.get(n1 + 1).getPosMtx()][ruta.get(n2 + 1).getPosMtx()]
                            - mtxDistancia[ruta.get(n1).getPosMtx()][ruta.get(n1 + 1).getPosMtx()]
                            - mtxDistancia[ruta.get(n2).getPosMtx()][ruta.get(n2 + 1).getPosMtx()];
                }
            }
            if (!noevaluar) {
                editarrutas2opt(i, n1, n2, delta);
            }
            //System.out.println(delta);
        }

    }

    void editarrutas2opt(int recorrido, int n1, int n2, int delta) {
        int contador = 0;
        LinkedList<order> ruta = recorridos.get(recorrido);
        LinkedList<order> rutanueva = new LinkedList<>();
        for (int i = 0; i <= n1; i++) {
            rutanueva.add(ruta.get(i));
        }
        for (int i = n2; i >= n1 + 1; i--) {
            rutanueva.add(ruta.get(i));

        }
        for (int i = n2 + 1; i < ruta.size(); i++) {
            rutanueva.add(ruta.get(i));
        }
        //System.out.println("A: " + n1);
        //System.out.println("B: "+ n2);
        int calculado = CRi.get(recorrido) + delta;
        CRi.set(recorrido, calculado);
        recorridos.set(recorrido, rutanueva);
        updateCRMAX();
    }

    void APLYGRASP() {
        for (int i = 0; i < HIR.size(); i++) {
            for (int j = 0; j < nodosxruta.get(i).size(); j++) {
                order p = (order) nodosxruta.get(i).get(j);
                p.setEstaRuta(false);
            }
            recorridos.add(GRASP(i, dispersion));
        }

    }

    void inicialGRASP() {
        for (int i = 0; i < HIR.size(); i++) {
            for (int j = 0; j < nodosxruta.get(i).size(); j++) {
                order p = (order) nodosxruta.get(i).get(j);
                p.setEstaRuta(false);
            }
            //System.out.println("---------------------------------------------");
            recorridos.add(GRASP(i, dispersion));
        }
        //System.out.println("Initial Solution:" + CRMAX);
        applyrelocate();
        applySwap();
    }

    void solucionInicial() {
        for (int i = 0; i < HIR.size(); i++) {
            for (int j = 0; j < nodosxruta.get(i).size(); j++) {
                order p = (order) nodosxruta.get(i).get(j);
                p.setEstaRuta(false);
            }
            //System.out.println("---------------------------------------------");
            recorridos.add(GRASP(i, 1));
        }
        updateCRMAX();
        //System.out.println("-->"+CRMAX);
    }

    void ILS() {
        //System.out.println("Initial Solution:" + CRMAX);
        applyrelocate();
        applySwap();
        apply2opt();
    }

    public LinkedList<LinkedList> getRecorridos() {
        return recorridos;
    }

    public int getCRMAX() {
        updateCRMAX();
        return CRMAX;
    }

    void updateCRMAX() {
        CRMAX = 0;
        for (int i = 0; i < CRi.size(); i++) {
            if (CRi.get(i) > CRMAX) {
                CRMAX = CRi.get(i);
            }
        }
        if (CRMAX == 0) {
            System.out.println("");
        }
        //comprobarMovimiento();
        //System.out.println(CRMAX);
    }

    public void write(String ruta, double CpuTime) throws IOException {
        

        DecimalFormat df = new DecimalFormat("#.00");
        comprobarMovimientmo();
        String r = ruta + File.separator + "Routes " + ".txt";
        FileWriter escribir = new FileWriter(r);
        BufferedWriter escritor = new BufferedWriter(escribir);
        escritor.write("CRMAX " + CRMAX);
        escritor.newLine();
        escritor.write("CPU-TIME: " + CpuTime);
        for (int i = 0; i < HIR.size(); i++) {
            int b = i + 1;
            escritor.newLine();
            escritor.write("Route " + (i + 1));
            escritor.newLine();
            order f= (order) recorridos.get(i).getLast();
            escritor.write("CRI: " + df.format(f.getTiempovisita()));
            escritor.write("CRI2: " + df.format(CRi.get(i)));
            escritor.newLine();
            escritor.newLine();
            LinkedList<order> recorrido = recorridos.get(i);
            for (int j = 0; j < recorrido.size(); j++) {
                order ri = recorrido.get(j);
                escritor.newLine();
                escritor.write(ri.toString());
            }

            escritor.newLine();
        }
        escritor.close();
        escribir.close();
    }

}
