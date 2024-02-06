/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import controlador.factory.Conexion;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import modelo.dao.Actividad_DAO;
import modelo.dao.Socio_DAO;
import org.bson.Document;
import vista.Ventana;

/**
 *
 * @author acceso a datos
 */
public class controlador {
    public static Ventana ventana = new Ventana();
    public static Actividad_DAO actividad_dao;
    public static Socio_DAO socio_dao;
    
    public static void iniciar() {
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
        actividad_dao = Conexion.getActividad_DAO();
        socio_dao     = Conexion.getsoSocio_DAO();
    }

    public static void insertarSocio() {
        try {
           int id = Integer.parseInt(ventana.getTxt_id_socio().getText().trim());
           String nombre = ventana.getTxt_nombre_socio().getText();
           String dni = ventana.getTxt_dni_socio().getText();
           String fecha_alta = ventana.getTxt_fechaAlta_socio().getText();
           Double cuota = Double.valueOf(ventana.getTxt_cuota_socio().getText().trim());
           
           if (socio_dao.getSocio(id) != null){
               JOptionPane.showMessageDialog(ventana, "El socio ya existe");
               return;
           }
           
           socio_dao.insertarSocio(id,nombre,dni,fecha_alta,cuota);
           JOptionPane.showMessageDialog(ventana,"Socio insertado");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(ventana,"Error de formato numerico");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,"Error inesperado");
        }
    }

    public static void borrarSocio() {
         try {
           int id = Integer.parseInt(ventana.getTxt_id_socio().getText().trim());
           String nombre = ventana.getTxt_nombre_socio().getText();
           String dni = ventana.getTxt_dni_socio().getText();
           String fecha_alta = ventana.getTxt_fechaAlta_socio().getText();
           Double cuota = Double.valueOf(ventana.getTxt_cuota_socio().getText().trim());
           String actividades;
           Document socio;
           
           if ((socio = socio_dao.getSocio(id)) == null){
               JOptionPane.showMessageDialog(ventana, "El socio no existe");
               return;
           }
           
           //Antes de borralo encontramos las actividades que realizo
           socio_dao.getActividadesMesActual(id,LocalDate.now().getMonthValue());
           
           
           socio_dao.borrarSocio(socio);
           JOptionPane.showMessageDialog(ventana,"Socio eliminado");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(ventana,"Error de formato numerico");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,"Error inesperado");
        }
    }
    
    
    
    
    

}
