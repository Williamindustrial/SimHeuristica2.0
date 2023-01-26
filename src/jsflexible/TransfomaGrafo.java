/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

import java.util.LinkedList;

/**
 *
 * @author William
 */
public class TransfomaGrafo {

    /**
     * Estructura donde se guardan los ordenes de las máquinas
     */
    LinkedList<LinkedList> ordenMaquinas;
    /**
     * Estructura donde se guardan los ordenes de las máquinas
     */
    LinkedList<LinkedList> ordenMaquinasxArcos;
    /**
     * Estructura de pedidos
     */
    LinkedList<pedido> pedidos;
    /**
     * Estructura de pedidos clonados
     */
    LinkedList<pedido> pedidosClonados;
    /**
     * Máquinas en común
     */
    /**
     * Operaciones que intervienen en la inversión
     */
    operation x, y;

    /**
     * Valor booleano que determinia el tipo de movimiento True si es inversión
     * False si es reasignación
     */
    boolean inv;
    /**
     * Valor booleano que determina que si se realiza el movimiento swap
     */
    boolean swap;

    /**
     * Operaciones final e inicial
     */
    operation opInicial, opFinal;
    /**
     * orden final de las máqinas
     */
    LinkedList<LinkedList> ordenFinalMaquinas;
    /**
     * Orden por arcos
     */
    LinkedList<LinkedList> ordenXarcos;

    /**
     * Orden del recorrido
     */
    LinkedList<operation> orden = new LinkedList<>();
    /**
     * Valor calculado de la estimación
     */
    double valorCalculado;

    /**
     * Contructor para la inversión
     *
     * @param ordenMaquinas Estructura de datos que contiene la ruta de las
     * maquinas
     * @param pedidos Estructura de datos de los pedidos
     * @param x operación x
     * @param y operación y
     * @param ordenXarcos ordenArcos
     * @param inv Valor booleano que tenermina el moviento: True si es
     * inversión, false si es reasignación
     * @param valorCalculado es el valor de la estimación
     */
    public TransfomaGrafo(LinkedList<LinkedList> ordenMaquinas, LinkedList<pedido> pedidos, operation x, operation y, LinkedList<LinkedList> ordenXarcos, double valorCalculado, boolean inv, boolean swap) {
        this.ordenMaquinas = ordenMaquinas;
        this.pedidos = pedidos;
        this.x = x;
        this.y = y;
        this.ordenXarcos = ordenXarcos;
        this.inv = inv;
        this.valorCalculado = valorCalculado;
        this.swap=swap;
    }

    /**
     * Realiza el cambio de la lista de máquinas en el moviemiento inversión
     */
    public void ordenarListaMaquinasInversion() {
        ordenFinalMaquinas = new LinkedList<>();
        pedidosClonados = clonarPedidos(pedidos);
        //System.out.println(x);
        //System.out.println(y);
        //System.out.println(maquinasComun);
        for (int i = 0; i < ordenMaquinas.size(); i++) {
            //System.out.println(i);
            LinkedList<operation> ordenMaquina = ordenMaquinas.get(i);
            //System.out.println(ordenMaquina);
            // orden nuevo de máquinas
            LinkedList<operation> ordenNuevoMaquinas = new LinkedList<>();
            // boolean--> si la maquina esta en comun
            boolean eec = false;

            if (x.getMaquinaSelecionada() == i) {
                for (int j = 0; j < ordenMaquina.size(); j++) {
                    operation op = ordenMaquina.get(j);
                    operation opNueva = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (!op.equals(y)) {
                        if (op.equals(x)) {
                            operation ay = pedidosClonados.get(y.getPedido()).getOperaciones().get(y.getOperation());
                            if (ordenNuevoMaquinas.size() > 0) {
                                ordenNuevoMaquinas.getLast().addOperaciónSiguiente(ay);
                                ay.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                                ay.addOperaciónSiguiente(opNueva);
                                opNueva.addOperaciónAnterior(ay);
                            } else {
                                ay.addOperaciónAnterior(opInicial);
                                ay.addOperaciónSiguiente(opNueva);
                                opNueva.addOperaciónAnterior(ay);
                            }
                            ordenNuevoMaquinas.add(ay);
                            ordenNuevoMaquinas.add(opNueva);
                        } else {
                            if (ordenNuevoMaquinas.size() > 0) {
                                ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opNueva);
                                opNueva.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                            } else {
                                opNueva.addOperaciónAnterior(opInicial);
                            }
                            ordenNuevoMaquinas.add(opNueva);
                        }
                    }
                }
            } else {
                for (int j = 0; j < ordenMaquina.size(); j++) {
                    operation op = ordenMaquina.get(j);
                    operation opNueva = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (ordenNuevoMaquinas.size() > 0) {
                        ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opNueva);
                        opNueva.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                    } else {
                        opNueva.addOperaciónAnterior(opInicial);
                    }
                    ordenNuevoMaquinas.add(opNueva);
                }
            }
            try {
                ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opFinal);

            } catch (Exception e) {

            }
           // System.out.println(ordenNuevoMaquinas);
            ordenFinalMaquinas.add(ordenNuevoMaquinas);
            //System.out.println(ordenNuevoMaquinas);
        }
    }

    public void ordenarListaMaquinasSwap() {
        ordenFinalMaquinas = new LinkedList<>();
        pedidosClonados = clonarPedidos(pedidos);
        //System.out.println(x);
        //System.out.println(y);
        //System.out.println(maquinasComun);
        for (int i = 0; i < ordenMaquinas.size(); i++) {
            //System.out.println(i);
            LinkedList<operation> ordenMaquina = ordenMaquinas.get(i);
            //System.out.println(ordenMaquina);
            // orden nuevo de máquinas
            LinkedList<operation> ordenNuevoMaquinas = new LinkedList<>();
            // boolean--> si la maquina esta en comun
            boolean eec = false;

            if (x.getMaquinaSelecionada() == i) {
                for (int j = 0; j < ordenMaquina.size(); j++) {
                    operation op = ordenMaquina.get(j);
                    operation opNueva = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (op.equals(x)) {
                        operation ay = pedidosClonados.get(y.getPedido()).getOperaciones().get(y.getOperation());
                        if (ordenNuevoMaquinas.size() > 0) {
                            ordenNuevoMaquinas.getLast().addOperaciónSiguiente(ay);
                            ay.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                        } else {
                            ay.addOperaciónAnterior(opInicial);
                        }
                        ordenNuevoMaquinas.add(ay);

                    } else if (op.equals(y)) {
                        operation ay = pedidosClonados.get(x.getPedido()).getOperaciones().get(x.getOperation());
                        if (ordenNuevoMaquinas.size() > 0) {
                            ordenNuevoMaquinas.getLast().addOperaciónSiguiente(ay);
                            ay.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                        } else {
                            ay.addOperaciónAnterior(opInicial);
                        }
                        ordenNuevoMaquinas.add(ay);

                    } else {
                        if (ordenNuevoMaquinas.size() > 0) {
                            ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opNueva);
                            opNueva.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                        } else {
                            opNueva.addOperaciónAnterior(opInicial);
                        }
                        ordenNuevoMaquinas.add(opNueva);
                    }
                }

            } else {
                for (int j = 0; j < ordenMaquina.size(); j++) {
                    operation op = ordenMaquina.get(j);
                    operation opNueva = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (ordenNuevoMaquinas.size() > 0) {
                        ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opNueva);
                        opNueva.addOperaciónAnterior(ordenNuevoMaquinas.getLast());
                    } else {
                        opNueva.addOperaciónAnterior(opInicial);
                    }
                    ordenNuevoMaquinas.add(opNueva);
                }
            }
            try {
                ordenNuevoMaquinas.getLast().addOperaciónSiguiente(opFinal);

            } catch (Exception e) {

            }

            ordenFinalMaquinas.add(ordenNuevoMaquinas);
            //System.out.println(ordenNuevoMaquinas);
        }
    }

    /**
     * Insumos para la inserción
     */
    int maquinaNueva, maquinaAnterior;
    operation K,J, pmx,smx;

    /**
     * Constructor para inserción
     *
     * @param ordenMaquinasxArcos Estructura de datos que contiene la ruta de
     * las máquinas
     * @param ordenMaquinas Estructura de datos que contiene la ruta de las
     * máquinas
     * @param pedidos Estructura de datos que contiene la ruta de los pedidos
     * @param x operación x
     * @param inv Valor booleano que tenermina el moviento: True si es
     * inversión, false si es reasignación
     * @param maquinaNueva La nueva máquina de la operación x
     * @param maquinaAnterior La anterior máquina de la operación x
     * @param J operación j
     * @param K operación k
     * @param pmx
     * @param smx
     * @param ordenXarcos Estructura de datos del orden de las operaciones por
     * arcos
     * @param valorCalculado Valor de la estimación
     */
    public TransfomaGrafo(LinkedList<LinkedList> ordenMaquinasxArcos, LinkedList<LinkedList> ordenMaquinas, LinkedList<pedido> pedidos, operation x, boolean inv, int maquinaNueva, int maquinaAnterior, operation J, operation K, operation pmx, operation smx,LinkedList<LinkedList> ordenXarcos, double valorCalculado) {
        this.ordenMaquinasxArcos = ordenMaquinasxArcos;
        this.pedidos = pedidos;
        this.x = x;
        this.inv = inv;
        this.maquinaNueva = maquinaNueva;
        this.maquinaAnterior = maquinaAnterior;
        this.J = J;
        this.K = K;
        this.pmx= pmx;
        this.smx=smx;
        this.ordenXarcos = ordenXarcos;
        this.ordenMaquinas = ordenMaquinas;
        this.valorCalculado = valorCalculado;
    }

    /**
     * Ordena la lista de máquinas en la operación de la inserción
     */
    public void ordenarListaMaquinasInsercion() {
        ordenFinalMaquinas = new LinkedList<>();
        pedidosClonados = clonarPedidos(pedidos);
        pedidosClonados = clonarPedidos(pedidos);
       // System.out.println("---------------------------------------");
        //System.out.println("x:" + x+ "arcos" + x.getArcos());
        //System.out.println("prx:" + x.getAnteriorRuta()+ "arcos" + x.getAnteriorRuta().getArcos());
        //System.out.println("j:" + J+  "arcos" + J.getArcos());
        //System.out.println("k:" + K+  "arcos" + K.getArcos());
       // System.out.println("n" + maquinaNueva);
        //System.out.println("a" + maquinaAnterior);
        if (maquinaAnterior != maquinaNueva) {
            //  System.out.println("");
        }
        for (int i = 0; i < ordenMaquinas.size(); i++) {
            LinkedList<operation> OrdenMaquinaCambiar = new LinkedList<>();
           // System.out.println(i);
            //System.out.println("I:" + ordenMaquinas.get(i));
            if (i == maquinaNueva) {
                for (int j = 0; j < ordenMaquinas.get(i).size(); j++) {
                    operation op = (operation) ordenMaquinas.get(i).get(j);
                    operation clonada = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (J.isInicial() && j == 0) {
                        operation xClonada = pedidosClonados.get(x.getPedido()).getOperaciones().get(x.getOperation());
                        xClonada.setProcessingTime(maquinaNueva);
//()xClonada.setMaquinasSelecionadas(listaDeMaquinasOperacion());
                        xClonada.addOperaciónAnterior(opInicial);
                        OrdenMaquinaCambiar.add(xClonada);
                    }
                    if (J.equals(op)) {
                        operation xClonada = pedidosClonados.get(x.getPedido()).getOperaciones().get(x.getOperation());
                        xClonada.setProcessingTime(maquinaNueva);
                        //()    xClonada.setMaquinasSelecionadas(listaDeMaquinasOperacion());
                        if (OrdenMaquinaCambiar.size() > 0) {
                            clonada.addOperaciónAnterior(OrdenMaquinaCambiar.getLast());
                            OrdenMaquinaCambiar.getLast().addOperaciónSiguiente(clonada);
                            xClonada.addOperaciónAnterior(clonada);
                            clonada.addOperaciónSiguiente(xClonada);
                            OrdenMaquinaCambiar.add(clonada);
                            OrdenMaquinaCambiar.add(xClonada);
                        } else {
                            clonada.addOperaciónAnterior(opInicial);
                            xClonada.addOperaciónAnterior(clonada);
                            clonada.addOperaciónSiguiente(xClonada);
                            OrdenMaquinaCambiar.add(clonada);
                            OrdenMaquinaCambiar.add(xClonada);
                        }

                    } else if (!op.equals(J) && !op.equals(x)) {
                        if (OrdenMaquinaCambiar.size() > 0) {
                            clonada.addOperaciónAnterior(OrdenMaquinaCambiar.getLast());
                            OrdenMaquinaCambiar.getLast().addOperaciónSiguiente(clonada);
                        } else {
                            clonada.addOperaciónAnterior(opInicial);
                        }
                        OrdenMaquinaCambiar.add(clonada);
                    }
                }

            } else if (i == maquinaAnterior) {
                for (int j = 0; j < ordenMaquinas.get(i).size(); j++) {
                    operation op = (operation) ordenMaquinas.get(i).get(j);
                    operation clonada = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (!x.equals(ordenMaquinas.get(i).get(j))) {
                        if (OrdenMaquinaCambiar.size() > 0) {
                            clonada.addOperaciónAnterior(OrdenMaquinaCambiar.getLast());
                            OrdenMaquinaCambiar.getLast().addOperaciónSiguiente(clonada);
                            OrdenMaquinaCambiar.add(clonada);
                        } else {
                            clonada.addOperaciónAnterior(opInicial);
                            OrdenMaquinaCambiar.add(clonada);
                        }

                    }
                }
            } else {
                for (int j = 0; j < ordenMaquinas.get(i).size(); j++) {
                    operation op = (operation) ordenMaquinas.get(i).get(j);
                    operation clonada = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                    if (OrdenMaquinaCambiar.size() > 0) {
                        clonada.addOperaciónAnterior(OrdenMaquinaCambiar.getLast());
                        OrdenMaquinaCambiar.getLast().addOperaciónSiguiente(clonada);
                        OrdenMaquinaCambiar.add(clonada);
                    } else {
                        clonada.addOperaciónAnterior(opInicial);
                        OrdenMaquinaCambiar.add(clonada);
                    }
                }
            }
            try {
                OrdenMaquinaCambiar.getLast().addOperaciónSiguiente(opFinal);
            } catch (Exception e) {

            }

            ordenFinalMaquinas.add(OrdenMaquinaCambiar);
           // System.out.println("F:" + OrdenMaquinaCambiar);
        }
    }

    public void generarOrden() {
        orden.clear();
        for (int i = 0; i < ordenXarcos.size(); i++) {
            LinkedList<operation> listaDelarco = ordenXarcos.get(i);
            for (int j = 0; j < listaDelarco.size(); j++) {
                operation op = listaDelarco.get(j);
                operation clonada = pedidosClonados.get(op.getPedido()).getOperaciones().get(op.getOperation());
                orden.add(clonada);
            }
        }
        //System.out.println("tamaño:" + orden.size());
        //System.out.println(orden);
    }

    /**
     * Clonar los pedidos
     *
     * @param PEDIDOS pedidos anterior
     * @return pedidos clonados
     */
    private LinkedList clonarPedidos(LinkedList<pedido> PEDIDOS) {
        LinkedList<pedido> Pedidos = new LinkedList<>();
        for (int i = 0; i < PEDIDOS.size(); i++) {
            Pedidos.add(PEDIDOS.get(i).clonar());
        }
        opFinal = Pedidos.getLast().getOperaciones().getLast().getSiguieteRuta();
        opInicial = Pedidos.getFirst().getOperaciones().getFirst().getAnteriorRuta();
        return Pedidos;
    }

    public void realizar() {
        if (inv) {
            ordenarListaMaquinasInversion();
            //System.out.println("valor calulado: "+ valorCalculado);
        } else if (swap) {
            ordenarListaMaquinasSwap();
        } else {
            ordenarListaMaquinasInsercion();
        }
        generarOrden();
    }

    /**
     * public void realizarAlgoritmoPrueba(feromonas f, double valorLocal) { if
     * (inv) { if (x == opInicial) { f.agregarAdcionalFeromona(null, y,
     * valorLocal); } else { f.agregarAdcionalFeromona(y, x, valorLocal);
     * f.penalizarArco(x, y, valorLocal); } } else { // penalizar
     * if(J==opInicial&& K!=opFinal){ f.agregarAdcionalFeromona(null, x,
     * valorLocal); f.agregarAdcionalFeromona(x, K, valorLocal);
     * f.penalizarArco(x, x.getSiguientM(), valorLocal); f.penalizarArco(null,
     * K, valorLocal); }else if(J!=opInicial&& K==opFinal){
     * f.agregarAdcionalFeromona(J, x, valorLocal); f.penalizarArco(x,
     * x.getSiguientM(), valorLocal); }else if(J!=opInicial&& K!=opFinal){
     * f.agregarAdcionalFeromona(J, x, valorLocal); f.agregarAdcionalFeromona(x,
     * K, valorLocal); f.penalizarArco(x, x.getSiguientM(), valorLocal);
     * f.penalizarArco(J, K, valorLocal); } } }
     */
    public LinkedList<pedido> getPedidosClonados() {
        return pedidosClonados;
    }

    public LinkedList<LinkedList> getOrdenFinalMaquinas() {
        return ordenFinalMaquinas;
    }

    public LinkedList<operation> getOrden() {
        return orden;
    }

    @Override
    public String toString() {
        return "TransfomaGrafo{" + "valorCalculado=" + valorCalculado + '}';
    }

    public operation getX() {
        return x;
    }

    public operation getJ() {
        return J;
    }

    public operation getK() {
        return K;
    }

    public boolean isInv() {
        return inv;
    }

    public boolean isSwap() {
        return swap;
    }

    public operation getY() {
        return y;
        
    }

    public operation getPmx() {
        return pmx;
    }

    public operation getSmx() {
        return smx;
    }
    
    String string(){
       // System.out.println(x+"-"+y);
        return x+"-"+y;
    }
    String string2(){
        return x+"-"+J+"-"+"-"+K;
    }

}
