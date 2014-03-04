/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

/**
 *
 * @author personal
 */
public class NewClass {
    String us;
    public void otro(){
        us+=" por els const";
    }
    
    public String getUs(){
        otro();
        return us;
    }
    public void setUs(String us){
        this.us=us;
    }
    
}
