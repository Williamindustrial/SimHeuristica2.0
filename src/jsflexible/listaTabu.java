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
public class listaTabu {

    TransfomaGrafo arcoPenalizado;
    TransfomaGrafo arcoevaluar;

    public listaTabu(TransfomaGrafo arcoPenalizado, TransfomaGrafo arcoevaluar) {
        this.arcoPenalizado = arcoPenalizado;
        this.arcoevaluar = arcoevaluar;
    }

    public boolean evaluarINV() {
        boolean r = true;
        String a = arcoPenalizado.getX().toString();
        String b = arcoPenalizado.getY().toString();
        String c = arcoevaluar.getX().toString();
        String d = arcoevaluar.getY().toString();
        //System.out.println(arcoPenalizado.getX()+"-"+arcoPenalizado.getY());
        if (a.equals(c) || a.equals(d)) {
            if (b.equals(d) || b.equals(c)) {
                r = false;
            }
        }
        return r;
    }

    public boolean evalaurINS() {
        boolean r = true;
        String a = arcoPenalizado.getX().toString();
        String b = arcoPenalizado.getJ().toString();
        String c = arcoPenalizado.getK().toString();
        String d = arcoevaluar.getX().toString();
        String e = arcoevaluar.getJ().toString();
        String f = arcoevaluar.getK().toString();
        if (a.equals(d)|| a.equals(e)||a.equals(f)) {
            if (b.equals(e)||b.equals(f)||b.equals(d)) {
                if (c.equals(f)) {
                    r = false;
                }
            }
        }
        return r;
    }

    public boolean evaluacion() {
        if (arcoevaluar.isInv() & arcoPenalizado.isInv()) {
            return evaluarINV();
        } else if (arcoevaluar.isSwap() & arcoPenalizado.isSwap()) {
            return evaluarINV();
        } else {
            return evalaurINS();
        }
    }
}
