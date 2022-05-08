/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dao.ClienteContext;
import dao.enums.StatusEnum;
import entidades.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Samu-Pc
 */
@WebServlet("/cliente")
public class ClienteServlet extends HttpServlet {

    final Gson gson = new Gson();
    private final ClienteContext clienteContext = new ClienteContext();

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String numDoc = request.getParameter("documento");
        String jsonString = "";

        if (numDoc == null) {
            ArrayList<Cliente> clientes = clienteContext.getTodosClientes();
            if (clientes.size() < 1) {
                response.setStatus(404);
                return;
            }

            jsonString = gson.toJson(clientes);
        } else {
            int documento = 0;

            try {
                documento = Integer.parseInt(numDoc);
            } catch (NumberFormatException nfe) {
                System.out.println(String.format("El documento %s no es numero.", numDoc));
                System.out.println(nfe.toString());
                response.setStatus(404);
                return;
            }

            Cliente cliente = clienteContext.getCliente(documento);

            if (cliente == null) {
                response.setStatus(404);
                return;
            }

            jsonString = gson.toJson(cliente);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonString);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cliente cliente = this.mapJsonBodyToCliente(request);

        if (cliente == null) {
            response.setStatus(400);
            return;
        }

        StatusEnum status = clienteContext.crearOActualizar(cliente, false);

        if (status == StatusEnum.DUPLICATED_CLIENT || status == StatusEnum.ERROR) {
            response.setStatus(400);
            return;
        }

        String jsonString = gson.toJson(cliente);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonString);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cliente cliente = this.mapJsonBodyToCliente(request);

        if (cliente == null) {
            response.setStatus(400);
            return;
        }

        StatusEnum status = clienteContext.crearOActualizar(cliente, true);

        if (status == StatusEnum.ERROR) {
            response.setStatus(400);
            return;
        }

        String jsonString = gson.toJson(cliente);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(jsonString);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String numDoc = request.getParameter("documento");
        
        if (numDoc == null) {
            response.setStatus(400);
            return;
        }

        int documento = 0;

        try {
            documento = Integer.parseInt(numDoc);
        } catch (NumberFormatException nfe) {
            System.out.println(String.format("El documento %s no es numero.", numDoc));
            System.out.println(nfe.toString());
            response.setStatus(404);
            return;
        }

        if (documento < 1) {
            response.setStatus(404);
            return;
        }

        StatusEnum status = clienteContext.borrar(documento);

        if (status == StatusEnum.SUCCESS) {
            response.setStatus(400);
            return;
        }

        response.setStatus(201);
        return;

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private Cliente mapJsonBodyToCliente(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        String line = null;

        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            Cliente cliente = gson.fromJson(sb.toString(), Cliente.class);
            return cliente;
        } catch (IOException | JsonParseException ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}
