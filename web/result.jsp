<%-- 
    Document   : newjsp
    Created on : 27-ene-2014, 23:26:16
    Author     : personal
--%>

<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<!--
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>        
        <p>Ingrese la especificación</p>
        <input textarea rows="4" cols="50">At w3schools.com you will learn how to make a website. We offer free tutorials in all web development technologies. 
        </textarea>
        <br>
        <input type="submit" value="Submit">
    </body>
</html>
-->

<jsp:useBean id="user" class="nl2kaos.NL2KAOSf" scope="session"/> 
<jsp:setProperty name="user" property="*"/>
<%@ page import="java.util.ArrayList" %>
<HTML>
    <HEAD>
        <title>Paso 1 - Resultados - NL2KAOS</TITLE>
        <STYLE type="text/css">
            p,form,h1{ margin-left: 100px;}
        </STYLE>
        <script type="text/javascript">
            function deleteRow(tableID) {
                var table = document.getElementById(tableID).tBodies[0];
                var rowCount = table.rows.length;

                for (var i = 0; i < rowCount; i++) {
                    var row = table.rows[i];
                    var chkbox = row.cells[0].getElementsByTagName('input')[0];
                    if (null != chkbox && true == chkbox.checked) {
                        table.deleteRow(i);
                        rowCount--;
                        i--;

                    }
                }
            }
        </script>
    </HEAD>
    <BODY>
        <h1>Paso 1 - Depurar elementos identificados</h1>
        <p>Oberve los elementos identificados.</p>
        <p>En caso de querer agregar alguno, o cambiar la redacción, favor devuélvase y </p>
        <p>redacte nuevamente la especificación, teniendo en cuenta las restricciones establecidas al lenguaje. <a href="#">(?)</a></p>
        <div class="formulario">

            <FORM METHOD=POST ACTION="#">
                <table  class="imagetable"  id="datatable">
                    <%
                        user.setTexto(request.getParameter("texto"));
                        user.procesar();
                        ArrayList<String> rObjs = new ArrayList<String>();
                        rObjs = user.getStringGoals();
                        out.println("<tr><td><h3>Objetivos:</H3></tr></td>");
                        for (int i = 0; i < rObjs.size(); i++) {
                    %>
                    <tr><td><%=rObjs.get(i)%></td></tr>
                            <%
                                }
                                ArrayList<String> rAgs = new ArrayList<String>();
                                out.println("<tr><td><h3>Agentes:</H3></td></tr>");
                                rAgs = user.getStringAgents();
                                for (int i = 0; i < rAgs.size(); i++) {
                            %>
                    <tr><td><%=rAgs.get(i)%></td></tr>
                            <%
                                }
                                out.println("<tr><td><h3>Entidades:</H3></td></tr>");
                                ArrayList<String> rEnts = new ArrayList<String>();
                                rEnts = user.getStringEntities();
                                for (int i = 0; i < rEnts.size(); i++) {
                            %>
                    <tr><td><%=rEnts.get(i)%></td></tr>
                            <%
                                }
                                out.println("<tr><td><h3>Operaciones:</H3></td></tr>");
                                ArrayList<String> rOps = new ArrayList<String>();
                                rOps = user.getStringOperations();
                                for (int i = 0; i < rOps.size(); i++) {
                            %>
                    <tr><td><%=rOps.get(i)%></td></tr>
                            <%
                                }
                            %>
                </table>
                <%
                    String connectionURL = "jdbc:mysql://localhost:3306/kaos";
                    Connection connection = null;
                    PreparedStatement pstatement = null;
                    Class.forName("com.mysql.jdbc.Driver").newInstance();
                    int updateQuery = 0;
                    connection = DriverManager.getConnection(connectionURL, "root", "");

                    try {
                        String queryString = "delete from objetivos";
                        pstatement = connection.prepareStatement(queryString);
                        updateQuery = pstatement.executeUpdate();
                        queryString = "delete from agentes";
                        pstatement = connection.prepareStatement(queryString);
                        updateQuery = pstatement.executeUpdate();
                        queryString = "delete from atributos";
                        pstatement = connection.prepareStatement(queryString);
                        updateQuery = pstatement.executeUpdate();
                        queryString = "delete from entidades";
                        pstatement = connection.prepareStatement(queryString);
                        updateQuery = pstatement.executeUpdate();
                        queryString = "delete from operaciones";
                        pstatement = connection.prepareStatement(queryString);
                        updateQuery = pstatement.executeUpdate();

                    } catch (Exception ex) {
                        out.println("Unable to connect to batabase.");
                    }

                    for (int i = 0; i < rObjs.size(); i++) {
                        try {
                            String queryString = "INSERT INTO objetivos(id,text) VALUES (?, ?)";
                            pstatement = connection.prepareStatement(queryString);
                            pstatement.setString(1, null);
                            pstatement.setString(2, rObjs.get(i));
                            updateQuery = pstatement.executeUpdate();
                        } catch (Exception ex) {
                            out.println("Unable to connect to batabase.obs");
                        }
                    }

                    for (int i = 0; i < rAgs.size(); i++) {
                        try {
                            String queryString = "INSERT INTO agentes(id,text) VALUES (?, ?)";
                            pstatement = connection.prepareStatement(queryString);
                            pstatement.setString(1, null);
                            pstatement.setString(2, rAgs.get(i));
                            updateQuery = pstatement.executeUpdate();
                        } catch (Exception ex) {
                            out.println("Unable to connect to batabase.ags");
                        }
                    }

                    for (int i = 0; i < rEnts.size(); i++) {
                        try {
                            if (rEnts.get(i).contains("-")) {

                                pstatement = connection.prepareStatement("SELECT MAX(id) FROM entidades");
                                ResultSet rs = pstatement.executeQuery();
                                String id_entidad="";
                                if (rs.next()) {
                                    id_entidad = rs.getString(1);
                                }
                                

                                String queryString = "INSERT INTO atributos(id,text,entidad) VALUES (?, ?, ?)";
                                pstatement = connection.prepareStatement(queryString);
                                pstatement.setString(1, null);
                                pstatement.setString(2, rEnts.get(i));
                                pstatement.setString(3, id_entidad);
                                updateQuery = pstatement.executeUpdate();

                            } else {
                                String queryString = "INSERT INTO entidades(id,text) VALUES (?, ?)";
                                pstatement = connection.prepareStatement(queryString);
                                pstatement.setString(1, null);
                                pstatement.setString(2, rEnts.get(i));
                                updateQuery = pstatement.executeUpdate();
                            }

                        } catch (Exception ex) {
                            out.println("Unable to connect to batabase.ents");
                        }
                    }

                    for (int i = 0; i < rOps.size(); i++) {
                        try {
                            String queryString = "INSERT INTO operaciones(id,text) VALUES (?, ?)";
                            pstatement = connection.prepareStatement(queryString);
                            pstatement.setString(1, null);
                            pstatement.setString(2, rOps.get(i));
                            updateQuery = pstatement.executeUpdate();
                        } catch (Exception ex) {
                            out.println("Unable to connect to batabase.ops");
                        }
                    }
                    pstatement.close();
                    connection.close();
                %>
                <!--<input type="button" value="Borrar elemento seleccionado" onclick="deleteRow('datatable');" />-->
                <br>
                <a href="http://localhost/kaosgateway"><input type="button" value="Siguiente" /></a>
                
            </FORM>
        </DIV>
    </BODY>
</HTML>

