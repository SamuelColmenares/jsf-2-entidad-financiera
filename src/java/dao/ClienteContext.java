/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.enums.StatusEnum;
import entidades.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Samu-Pc
 */
public class ClienteContext extends Conexion {

    private final String CREAR = "insert into clientes(Num_identificacion,Apellidos,Nombres,Direccion,Telefono) values(?,?,?,?,?)";
    private final String ACTUALIZAR = "update clientes set Num_identificacion=?,Apellidos=?,Nombres=?,Direccion=?,Telefono=? where Num_identificacion=";

    public ArrayList<Cliente> getTodosClientes() {
        System.out.println("###################### INICIA getTodosClientes #######################");
        conectar();
        ArrayList<Cliente> clientes = new ArrayList();
        try {
            Statement stmt = cnn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from clientes");
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setNumIdentificacion(rs.getInt("Num_Identificacion"));
                cliente.setNombres(rs.getString("Nombres"));
                cliente.setApellidos(rs.getString("Apellidos"));
                cliente.setDireccion(rs.getString("Direccion"));
                cliente.setTelefono(rs.getInt("Telefono"));
                clientes.add(cliente);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            desconectar();
        }

        return clientes;
    }

    public Cliente getCliente(int documento) {
        System.out.println("###################### INICIA getCliente #######################");
        conectar();
        Cliente cliente = new Cliente();
        boolean existe = false;
        try {
            Statement stmt = cnn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from clientes where Num_Identificacion=" + documento);

            while (rs.next()) {
                cliente.setNumIdentificacion(rs.getInt("Num_Identificacion"));
                cliente.setNombres(rs.getString("Nombres"));
                cliente.setApellidos(rs.getString("Apellidos"));
                cliente.setDireccion(rs.getString("Direccion"));
                cliente.setTelefono(rs.getInt("Telefono"));
                existe = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            desconectar();
        }

        return existe ? cliente : null;
    }

    public StatusEnum crearOActualizar(Cliente cliente, boolean actualizaSiExiste) {
        System.out.println("###################### INICIA crearOActualizar #######################");

        Cliente existe = this.getCliente(cliente.getNumIdentificacion());
        if (existe != null && !actualizaSiExiste) {
            System.out.println("El cliente " + cliente.getNumIdentificacion() + " Ya existe.");
            return StatusEnum.DUPLICATED_CLIENT;
        }

        String sentencia = existe == null ? CREAR : ACTUALIZAR + existe.getNumIdentificacion();
        StatusEnum status;

        try {
            conectar();
            PreparedStatement stmt = cnn.prepareStatement(sentencia);
            stmt.setInt(1, cliente.getNumIdentificacion());
            stmt.setString(2, cliente.getApellidos());
            stmt.setString(3, cliente.getNombres());
            stmt.setString(4, cliente.getDireccion());
            stmt.setInt(5, cliente.getTelefono());
            status = stmt.executeUpdate() > 0 ? StatusEnum.SUCCESS : StatusEnum.ERROR;
        } catch (Exception e) {
            System.out.println(e);
            status = StatusEnum.ERROR;
        } finally {
            desconectar();
        }

        return status;
    }

    public StatusEnum borrar(int numDoc) {
        StatusEnum status = StatusEnum.ERROR;

        if (numDoc < 1) {
            return status;
        }

        try {
            conectar();
            PreparedStatement stmt = cnn.prepareStatement("delete from clientes where Num_Identificacion = ?");
            stmt.setInt(1, numDoc);
            status = stmt.executeUpdate() > 0 ? StatusEnum.SUCCESS : StatusEnum.ERROR;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            desconectar();
        }

        return status;
    }

}
