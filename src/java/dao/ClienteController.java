
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dao.enums.EstadoClienteEnum;
import dao.enums.StatusEnum;
import entidades.Cliente;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Samu-Pc
 */
@ManagedBean
@RequestScoped
public class ClienteController extends Conexion {

    private Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    private final ClienteContext clienteContext = new ClienteContext();

    public ArrayList<Cliente> getTodosClientes() {
        ArrayList<Cliente> clientes = new ArrayList();
        clientes = clienteContext.getTodosClientes();
        return clientes;
    }

    public Cliente getCliente(int documento) {
        Cliente cliente = new Cliente();
        cliente = clienteContext.getCliente(documento);
        return cliente;
    }

    public String actualizar(Cliente cliente) {
        StatusEnum res = clienteContext.crearOActualizar(cliente, true);

        if (res == StatusEnum.SUCCESS) {
            if (sessionMap.containsKey("errorMessageEdit")) {
                sessionMap.remove("errorMessageEdit");
            }

            return "/index.xhtml?faces-redirect=true";
        } else {
            sessionMap.put("errorMessageEdit", "Se presentó un inconveniente en el proceso. Intenta más tarde.");
            return "/cliente/editar.xhtml?faces-redirect=true";
        }
    }

    public String crear(Cliente cliente) {
        StatusEnum res = clienteContext.crearOActualizar(cliente, false);

        if (res == StatusEnum.SUCCESS) {
            if (sessionMap.containsKey("errorMessage")) {
                sessionMap.remove("errorMessage");
            }

            return "/index.xhtml?faces-redirect=true";
        } else {
            String mensaje = res == StatusEnum.DUPLICATED_CLIENT
                    ? "El cliente que intentas crear ya existe, por favor valida la información."
                    : "Se presentó un inconveniente en el proceso. Intenta más tarde.";
            sessionMap.put("errorMessage", mensaje);
            return "/cliente/create.xhtml?faces-redirect=true";
        }
    }

    public StatusEnum borrar(int numDoc) {
        StatusEnum status = StatusEnum.ERROR;

        if (numDoc < 1) {
            return status;
        }

        status = clienteContext.borrar(numDoc);
        return status;
    }

    public String editar(int numDoc) {
        Cliente cliente = this.getCliente(numDoc);
        sessionMap.put("editUser", cliente);
        return "/cliente/editar.xhtml?faces-redirect=true";
    }

    public void aprobarTarjeta(int numDoc) {

        if (sessionMap.containsKey("tcStatus")) {
            sessionMap.remove("tcStatus");
        }

        if (sessionMap.containsKey("tcMessage")) {
            sessionMap.remove("tcMessage");
        }

        EstadoClienteEnum listaNegra = this.validarListaNegra(numDoc);
        EstadoClienteEnum datacredito = this.validarDatacredito(numDoc);

        if (listaNegra == EstadoClienteEnum.ERROR || datacredito == EstadoClienteEnum.ERROR) {
            sessionMap.put("tcStatus", "error");
            sessionMap.put("tcMessage", "Hubo un error en el proceso de validación. Inténtalo más tarde.");
            return;
        }

        if (listaNegra == EstadoClienteEnum.LISTA_NEGRA
                || datacredito == EstadoClienteEnum.REPORTADO_DATACREDITO
                || datacredito == EstadoClienteEnum.NO_ENCONTRADO) {
            sessionMap.put("tcStatus", "error");
            sessionMap.put("tcMessage", "Lo sentimos, tu tarjeta no fue aprobada.");

            return;
        }

        sessionMap.put("tcStatus", "ok");
        sessionMap.put("tcMessage", "Tu tarjeta fué aprobada satisfactoriamente.");
        return;
    }

    private EstadoClienteEnum validarListaNegra(int numDoc) {
        EstadoClienteEnum estado = EstadoClienteEnum.ERROR;
        try {
            conectar();
            Statement stmt = cnn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * from listanegra where Num_Identificacion=%s LIMIT 1;", numDoc));
            int pagosVencidos = 0,
                    diasMora = 0;
            boolean existe = false;
            while (rs.next()) {
                pagosVencidos = rs.getInt("PagosVencidos");
                diasMora = rs.getInt("DiasMora");
                existe = true;
            }

            if (existe) {
                estado = pagosVencidos > 2 || diasMora > 60 ? EstadoClienteEnum.LISTA_NEGRA : EstadoClienteEnum.AL_DIA;
            } else {
                estado = EstadoClienteEnum.NO_ENCONTRADO;
            }
        } catch (Exception e) {
            System.out.println(e);
            estado = EstadoClienteEnum.ERROR;
        } finally {
            desconectar();
        }

        return estado;
    }

    private EstadoClienteEnum validarDatacredito(int numDoc) {
        EstadoClienteEnum estado = EstadoClienteEnum.ERROR;
        try {
            conectar();
            Statement stmt = cnn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * from datacredito where Documento=%s LIMIT 1;", numDoc));
            int tiempoMora = 0;
            String calificacion = "F";
            boolean existe = false;
            while (rs.next()) {
                calificacion = rs.getString("Calificacion");
                tiempoMora = rs.getInt("TiempoMora");
                existe = true;
            }

            if (existe) {
                estado = calificacion.equals("A") || calificacion.equals("B") || calificacion.equals("C") || tiempoMora < 45
                        ? EstadoClienteEnum.AL_DIA : EstadoClienteEnum.REPORTADO_DATACREDITO;
            } else {
                estado = EstadoClienteEnum.NO_ENCONTRADO;
            }
        } catch (Exception e) {
            System.out.println(e);
            estado = EstadoClienteEnum.ERROR;
        } finally {
            desconectar();
        }

        return estado;
    }

}
