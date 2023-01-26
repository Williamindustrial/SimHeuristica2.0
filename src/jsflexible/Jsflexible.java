package jsflexible;

import java.io.IOException;
import vrp.ventana;

/**
 *
 * @author Asus
 */
public class Jsflexible {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        //reader r = new reader("C:\\Users\\Asus\\Desktop\\Inicial\\guia.txt", "C:\\Users\\Asus\\Desktop\\Inicial\\", 0);

        /*entrada v = new entrada();
        v.setVisible(true);
        v.setLocationRelativeTo(null);  */
                
         double LSI= 10; double LII = 
          0.01; double VII= 0.1; double fee= 0.03; int perturbaciones=4; reader
          r = new
          reader("C:\\Users\\lizbe\\Downloads\\ela38.fjs",
                  "C:\\Users\\lizbe\\OneDrive\\Desktop\\Nueva carpeta",
                  "C:\\Users\\lizbe\\Downloads\\c101.txt",
                  0, perturbaciones); 
                  double alfa= 0.8; 
                  double beta= 0.3; 
                  int nHormigas= 390; 
                  int w=3000;
                  r.slocal(w,nHormigas,beta,alfa,w,"");
         
         
          
          
          
          
          
          
          
          
          
         /**
         * reader r1 = new
         * reader("C:\\Users\\Asus\\Desktop\\TRM\\ela31.fjs","C:\\Users\\Asus\\Desktop\\Sol\\31",
         * 0, LSI, LII, VII,fee); r1.propuestoAlgo(10,nHormigas,beta,alfa);
         *
         * reader r2 = new
         * reader("C:\\Users\\Asus\\Desktop\\TRM\\ela27.fjs","C:\\Users\\Asus\\Desktop\\Sol\\27",
         * 0, LSI, LII, VII,fee); r2.propuestoAlgo(10,nHormigas,beta,alfa); try
         * { BufferedReader bf = new BufferedReader(new
         * FileReader("C:\\Users\\Asus\\Desktop\\EXP\\expp.csv")); String
         * bfRead; int contador = 0; while ((bfRead = bf.readLine()) != null) {
         * StringTokenizer lineasConEspacios = new StringTokenizer(bfRead);
         * String cadenaLetras[] = bfRead.split(";"); String NExperimento =
         * cadenaLetras[0]; int nHormigas = Integer.parseInt(cadenaLetras[1]);
         * double fee = Double.parseDouble(cadenaLetras[2]); double VII =
         * Double.parseDouble(cadenaLetras[3]); double LII =
         * Double.parseDouble(cadenaLetras[4]); double LSI =
         * Double.parseDouble(cadenaLetras[5]); double beta =
         * Double.parseDouble(cadenaLetras[6]); double alfa =
         * Double.parseDouble(cadenaLetras[7]); int w =
         * Integer.parseInt(cadenaLetras[8]);; int perturbaciones =
         * Integer.parseInt(cadenaLetras[9]); String dirrecionGuardarR =
         * "C:\\Users\\Asus\\Desktop\\EXP"; String direccion =
         * "C:\\Users\\Asus\\Desktop\\TRM\\ela25.fjs"; String rs =
         * dirrecionGuardarR + File.separator + NExperimento; File rn = new
         * File(rs); rn.mkdir(); String rutaSolucion = dirrecionGuardarR +
         * File.separator + NExperimento; File rutaNueva = new
         * File(rutaSolucion); rutaNueva.mkdir(); try { // reader r = new
         * reader(direccion, rutaSolucion, 0, LSI, LII, VII, fee,
         * perturbaciones); // // r.propuestoAlgo(5, nHormigas, beta, alfa, w);
         * //r.algoritmo(Integer.parseInt(tiempo.getText())); //r.colonia(); }
         * catch (Exception ex) {
         * Logger.getLogger(entrada.class.getName()).log(Level.SEVERE, null,
         * ex); }
         *
         * System.out.println("");
         *
         * }
         *
         * } catch (Exception ex) {
         * Logger.getLogger(Jsflexible.class.getName()).log(Level.SEVERE, null,
         * ex); }
         *
                 */
                /**
                 * ventana v = new ventana(); v.setVisible(true);
        v.setLocationRelativeTo(null);
                 */
          
    }

}
