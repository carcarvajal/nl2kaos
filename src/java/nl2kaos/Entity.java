/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl2kaos;

import java.util.ArrayList;

/**
 *
 * @author personal
 */
public class Entity {
    private String name;
    private ArrayList<String> attributes=new ArrayList<String>();
    
    public Entity(String name){
        this.name=name;
    }
    public Entity(String name, String att){
        this.name=name;
        this.attributes.add(att);
    }
     public Entity(String name, ArrayList<String> att){
        this.name=name;
        this.attributes=new ArrayList<String>(att);
    }
    
    public void addAtt(String att){
        attributes.add(att);
    }
    
    public String getName(){
        return name;
    }
    
    public ArrayList<String> getAttributes(){
        return attributes;
    }
}
