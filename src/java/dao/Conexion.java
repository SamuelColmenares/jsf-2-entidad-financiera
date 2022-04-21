/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author Samu-Pc CONEXION
 */
public class Conexion {

    protected Connection cnn;

    protected void conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/basededatos_entidadfinanciera";
            cnn = DriverManager.getConnection(url, "root", "");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    protected void desconectar() {
        try {
            if (cnn != null) {
                cnn.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
