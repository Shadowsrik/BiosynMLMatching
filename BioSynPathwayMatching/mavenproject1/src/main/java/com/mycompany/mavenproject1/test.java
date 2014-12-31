/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mycompany.mavenproject1;

import ThriftService.ThriftServer;

/**
 *
 * @author srdu001
 */
public class test {
    public static void main(String[] args) throws Exception {
        ThriftServer server = new ThriftServer();
       try {
           server.init("8282");
       } catch (Exception ex) {
           
       } 
    }
}
