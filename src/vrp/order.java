/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrp;

/**
 *
 * @author Asus
 */
public class order {
    int posMtx;
    int x;
    int y;
    int pedido;
    int j;
    int tiempovisita;
    boolean estaRuta;
    int posRuta;
    int ruta;
    int readyTime;
    int dueDate;

    public order(int posMtx, int x, int y, int pedido, int j ) {
        this.posMtx = posMtx;
        this.x = x;
        this.y = y;
        this.pedido = pedido;
        this.estaRuta= false;
        this.j= j;
    }
    
    public void setDueDate(int duedate){
        this.dueDate=duedate;
    }
    public void setReadyTime(int readyTime){
        this.readyTime=readyTime;
    }

    public void setTiempovisita(int tiempovisita) {
        this.tiempovisita = tiempovisita;
    }

    public int getPosMtx() {
        return posMtx;
    }

    public int getTiempovisita() {
        return tiempovisita;
    }

    public boolean isEstaRuta() {
        return estaRuta;
    }

    public void setEstaRuta(boolean estaRuta) {
        this.estaRuta = estaRuta;
    }

    
    public String toString1() {
        return "order{" + "posRuta=" + posRuta + ", x=" + x + ", y=" + y + ", pedido=" + pedido + ", tiempovisita=" + tiempovisita + ", estaRuta=" + estaRuta + '}';
    }
    
    @Override
    public String toString() {
        return "[" + (pedido+1) +","+ (j+1)+ ","+ posMtx+"], [x=" + x + ", y=" + y  +",Time "+tiempovisita+ "]"  ;
    }

    public int getPedido() {
        return pedido;
    }

    public int getPosRuta() {
        return posRuta;
    }

    public void setPosRuta(int posRuta) {
        this.posRuta = posRuta;
    }

    public int getRuta() {
        return ruta;
    }

    public void setRuta(int ruta) {
        this.ruta = ruta;
    }

    public order clonar(){
        order nueva = new order(posMtx, x, y, pedido,j);
        if(pedido==-1){
            nueva.setTiempovisita(tiempovisita);
        }
        return nueva;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getJ() {
        return j;
    }

    public int getReadyTime() {
        return readyTime;
    }

    public int getDueDate() {
        return dueDate;
    }
    
}
