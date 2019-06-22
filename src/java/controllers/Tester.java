/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

/**
 *
 * @author manie
 */
public class Tester {
    public static void main(String[] args)
    {
        System.out.println("Holaaaa");
        String t = Tokken.getToken("123");
        System.out.println(t);
        String l = Tokken.getId(t);
        System.out.println(l);
    }
    
}
