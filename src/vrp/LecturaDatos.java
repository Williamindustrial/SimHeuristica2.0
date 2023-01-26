/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import jsflexible.solution;

/**
 *
 * @author Asus
 */
public class LecturaDatos {
    LinkedList<Integer> x= new LinkedList<>();
    LinkedList<Integer> y= new LinkedList<>();
    LinkedList<Integer> DueDate= new LinkedList<>();
    LinkedList<Integer> customer= new LinkedList<>();
    LinkedList<Integer> Demand= new LinkedList<>();
    int neib;
    int numeroIteraciones;
    public void readTSP(int ins, int neib, String file) throws FileNotFoundException, IOException {
        this.neib=neib;
        BufferedReader bf = new BufferedReader(new FileReader(file));
        String bfRead;
        int contador = 0;
        while ((bfRead = bf.readLine()) != null) {
            StringTokenizer lineasConEspacios = new StringTokenizer(bfRead);
            String cadenaLetras[] = bfRead.split(" ");
            if(contador==4){
                //System.out.println(cadenaLetras);
            }
            if (contador >= 9) {
                //System.out.println(cadenaLetras);
                int contador2 = 0;
                for (int i = 0; i < cadenaLetras.length; i++) {
                    String cadenaLetra = cadenaLetras[i];
                    if (!"".equals(cadenaLetra)) {
                        switch (contador2) {
                            case 0:
                                customer.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 1:
                                x.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 2:
                                y.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 3:
                                Demand.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 4:
                               // ReadyTime.add(Integer.parseInt(cadenaLetra));
                                break;
                            case 5:
                                DueDate.add(Integer.parseInt(cadenaLetra));
                                break;
                            default:
                                break;
                        }
                        contador2++;
                    }
                }
            }
            contador++;
        }
    }

    public void setNumeroIteraciones(int numeroIteraciones) {
        this.numeroIteraciones = numeroIteraciones;
    }
    
    ReadData r = null;
    public void nueva(String localizacionVRP, solution SOL) throws IOException{
        r = new ReadData(localizacionVRP,
                1, 120, 2, false);
        r.setDiversificacion(2);
        r.setCi(SOL.getCIVec());
        r.readTSP(x, y, DueDate, customer, Demand, neib, false);
    }
    
    public void initialSol(String localizacionVRP,LinkedList<Integer> CI) throws IOException{
        r = new ReadData(localizacionVRP,
                1, 120, 2, false);
        r.setDiversificacion(2);
        r.setCi(CI);
        r.readTSP(x, y, DueDate, customer, Demand, neib, true);
    }

    public ReadData getR() {
        return r;
    }
    
    
    
}
