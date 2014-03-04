package nl2kaos;

import java.util.ArrayList;

/**
 *
 * @author personal
 */
public class Goal {

    private String verbop;//verboprincipal
    private String def;
    private String category;
    private int posicion;
    private int forma;

    public Goal(String cat) {
        category = cat;
    }
    
     public Goal(String cat,String def) {
        category = cat;
        this.def=def;
    }

    public Goal(String def, String verbo, int posicionVerbo, int forma) {
        this.def = def;
        verbop = verbo;
        posicion=posicionVerbo;
        this.forma = forma;
        category="";
    }

    public Goal(String def, int forma) {
        this.def = def;
        category = "";
        this.forma = forma;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String c) {
        category = c;
    }

    public int getForma() {
        return forma;
    }

    public void setForma(int forma) {
        this.forma = forma;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getVerboP() {
        return verbop;
    }
    
    public int getPosicion(){
        return posicion;
    }
}
