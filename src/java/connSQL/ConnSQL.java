/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connSQL;

/**
 *
 * @author personal
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ConnSQL {

    /**
     * @param args the command line arguments
     */
    protected Connection conn = null;
    protected Statement stmt = null;

    /**
     *
     * Conecta con la base de datos 'prueba' del servidor MySQL local.
     *
     *
     */
    public void conectar() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        this.conn = DriverManager.getConnection("jdbc:mysql://localhost/mcr3", "root", "");
        this.stmt = conn.createStatement();
    }

    /**
     *
     * @throws Exception DOCUMENT ME!
     *
     */
    public void desconectar() throws Exception {
        if (this.stmt != null) {
            try {
                this.stmt.close();
            } catch (SQLException SQLE) {
                SQLE.printStackTrace();
            }
        }
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException SQLE) {
                SQLE.printStackTrace();
            }
        }
    }

    public void consultarRegistro(String SQLquery) throws SQLException {

        ResultSet rs = stmt.executeQuery(SQLquery);
        int columns = rs.getMetaData().getColumnCount();
        System.out.println("------------------------");
        System.out.println();
        while (rs.next()) {
            for (int i = 1; i <= columns; i++) {
                System.out.println(rs.getString(i));
            }
            System.out.println();
        }
        rs.close();
        stmt.close();
        System.out.println("------------------------");
        System.out.flush();
    }

    public ArrayList<String> senseDefinicion(String lemma) throws SQLException {
        ArrayList<String> defs = new ArrayList<String>();
        try {
            this.stmt = conn.createStatement();
            System.out.println("select offset from `wei_spa-30_variant` where word='" + lemma + "'");

            ResultSet rs = stmt.executeQuery("select offset from `wei_spa-30_variant` where word='" + lemma + "'");

            System.out.println("------------------------");
            System.out.println();

            while (rs.next()) {
                defs.add(rs.getString(1));
            }
            rs.close();

            for (int i = 0; i < defs.size(); i++) {
                ResultSet rs2 = stmt.executeQuery("select gloss from `wei_spa-30_synset` where offset='" + defs.get(i) + "'");
                while (rs2.next()) {
                    defs.set(i, defs.get(i) + " " + rs2.getString(1));
                    System.out.println(defs.get(i));
                }
                rs2.close();
            }

            stmt.close();
            System.out.println("------------------------");
            System.out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defs;
    }

    public String englishSense(String senseSpa) throws SQLException {
        this.stmt = conn.createStatement();
        System.out.println("select word,sense from `wei_eng-30_variant` where offset='eng-" + senseSpa.substring(4) + "' limit 1");
        ResultSet rs = stmt.executeQuery("select word,sense from `wei_eng-30_variant` where offset='eng-" + senseSpa.substring(4) + "' limit 1");
        System.out.println("------------------------");
        String ans = "";
        if (rs.next()) {
            ans = rs.getString(1) + "." + rs.getString(2);
        }
        System.out.println("ans = " + ans);
        rs.close();
        stmt.close();
        System.out.println("------------------------");
        System.out.flush();
        return ans;
    }
    
    public String clasificar_agente(String spa) throws SQLException{
        if(spa.length()<8)
            return null;
        String sens=spa.substring(7);
        System.out.println("ññ "+sens);
        this.stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select semf from `wei_ili_record` where iliOffset='ili-30-" + spa.substring(7) + "'");
        System.out.println("------------------------");
        int ans = 0;
        if (rs.next()) {
            ans = rs.getInt(1);
        }
        System.out.println("aans"+ans);
        String clasif="";
        if(ans==6){
            clasif="Agente de software";
        }else if(ans==18){
            clasif="Agente de ambiente";
        }else{
            clasif=null;
        }
        System.out.println("ans = " + ans+" clasif "+clasif);
        rs.close();
        stmt.close();
        System.out.println("------------------------");
        System.out.flush();
        return clasif;
    }

    public static void main(String[] args) throws Exception {
        ConnSQL mn = new ConnSQL();

        try {
            mn.conectar();
            System.out.println("Conexion Satisfactoria!!!");
            //mn.consultarRegistro("SELECT * FROM persona");
            mn.englishSense("spa-30-08420278-n");
            System.out.println("La Consulta se realizo exitosamente!!!");
            mn.desconectar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//select offset from `wei_ili_record` where word='" + lemma + "'