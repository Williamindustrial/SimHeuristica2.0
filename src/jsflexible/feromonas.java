/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

/**
 *
 * @author Asus
 */
public class feromonas {

    int numeroOperaciones;
    double[][] feromonas;
    double feromonaAgregar;
    /**
     * Limete inferior de las feromonas
     */
    double limiteInferiorFeromonas;
    /**
     * Límite superior de las feromnas
     */
    double limiteSuperiorFeromonas;
    /**
     * Feromona de reinicio
     */
    double valorR;

    /**
     * Contructor de las feromonas
     *
     * @param numeroOperaciones tamaño de la matriz -1
     * @param ls Límiete Superior
     * @param li Límite inferior
     * @param vr Valor inicial y de reinicio de las feromonas
     */
    public feromonas(int numeroOperaciones, double ls, double li, double vr, int numeroMaquinas) {
        this.numeroOperaciones = numeroOperaciones;
        feromonas = new double[numeroOperaciones + 1][numeroOperaciones + 1];
        limiteInferiorFeromonas = li;
        limiteSuperiorFeromonas = ls;
        valorR = vr;
    }

    /**
     * Inicializa la matriz de feromonas
     */
    public void inicializar() {
        for (double[] feromona : feromonas) {
            for (int j = 0; j < feromona.length; j++) {
                feromona[j] = valorR;
            }
        }
    }

    /**
     * Devuelve las feromonas del arco x y
     *
     * @param x posición inicial x
     * @param y posición inicial y
     * @return Retorna las feromonas entre el arco x-y
     */
    public double consultarFeromona(operation x, operation y) {
        if (x == null) {
            return feromonas[0][y.getPosicionMatrizF()];
        } else {
            return feromonas[x.getPosicionMatrizF()][y.getPosicionMatrizF()];
        }
    }

    /**
     * Agrega la cantidad de feromonas que se van a agregar
     *
     * @param valorLocal Valor local de la colonia
     * @param numeroped numero de pedidos
     */
    public void setFeromonaAgregar(double valorLocal, int numeroped) {

        this.feromonaAgregar = 1 / valorLocal;
        //this.feromonaAgregar=0.3;
    }

    /**
     * Evapora las feromonas de todos los arcos
     *
     * @param factorEvaporacion factor de evaporación
     */
    public void evaporar(double factorEvaporacion) {
        for (double[] feromona : feromonas) {
            for (int j = 0; j < feromona.length; j++) {
                feromona[j] = feromona[j] * (1 - factorEvaporacion);
                if (feromona[j]  <= 0.003) {
                    feromona[j]  = 0.5;
                }
            }
        }
    }

    /**
     * Agrega feromona al arco x-y
     *
     * @param x operación inicial
     * @param y operación final
     */
    public void agregarFeromona(operation x, operation y) {
        if (x == null) {
            feromonas[0][y.getPosicionMatrizF()] = feromonaAgregar;
            if (feromonas[0][y.getPosicionMatrizF()] >= 3) {
                feromonas[0][y.getPosicionMatrizF()] = 0.5;
            }
        } else {
            feromonas[x.getPosicionMatrizF()][y.getPosicionMatrizF()] = feromonaAgregar;
            if (feromonas[0][y.getPosicionMatrizF()] >= 3) {
                feromonas[0][y.getPosicionMatrizF()] = 0.5;
            }
        }
    }
}
