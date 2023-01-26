/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsflexible;

/**
 *
 * @author William
 */
public class rutaCritica {
   operation x;
   operation y;

    public rutaCritica(operation x, operation y) {
        this.x = x;
        this.y = y;
    }

    

    public operation getX() {
        return x;
    }

    public operation getY() {
        return y;
    }

    @Override
    public String toString() {
        return "rutaCritica{" + "x=" + x + ", y=" + y + '}';
    }
  
  
    
}
