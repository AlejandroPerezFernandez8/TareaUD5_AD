/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;


import controlador.factory.Conexion;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
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

    public static void modificarSocio() {
           int id = Integer.parseInt(ventana.getTxt_id_socio().getText().trim());
           String nombre = ventana.getTxt_nombre_socio().getText();
           String dni = ventana.getTxt_dni_socio().getText();
           String fecha_alta = ventana.getTxt_fechaAlta_socio().getText();
           Double cuota = Double.valueOf(ventana.getTxt_cuota_socio().getText().trim());
           
           Document socio_modificado = new Document();
           Document socio = socio_dao.getSocio(id);
           
           if(socio == null){
               JOptionPane.showMessageDialog(ventana,"No existe el socio");
               return;
           }
           
           socio_modificado.append("_id", id)
                   .append("socio", new Document()
                           .append("nombre", nombre)
                           .append("dni", dni)
                           .append("fecha_alta",fecha_alta)
                           .append("cuota", cuota)
                   );
            
           socio_dao.modificarSocio(socio_modificado,socio);
    }

    public static void insertarActividad() {
        try {
            String nombre  = ventana.getTxtNombre_actividad().getText();
            String tipo    = ventana.getTxtTipo_actividad().getText();
            String fecha   = ventana.getTxtFecha_actividad().getText();
            int duracion   = Integer.parseInt(ventana.getTxtDuracion_actividad().getText());
            String monitor = ventana.getTxtMonitor_actividad().getText();
            int id_socio   = Integer.parseInt(ventana.getTxtIdSocio_actividad().getText());
            
            Document socio     = new Document();
            Document actividad = new Document();
            
            //comprobamos que todos los campos esten completos
            if (nombre.isEmpty() || fecha.isEmpty() || monitor.isEmpty()){
                JOptionPane.showMessageDialog(ventana,"Rellena todos los campos");
            }
            
            //comprobamos que el socio existe
            socio = socio_dao.getSocio(id_socio);
            if (socio == null){JOptionPane.showMessageDialog(ventana,"El socio no existe");return;}
            
            //insertamos en el socio la actividad
            actividad.append("nombre", nombre)
                    .append("tipo",tipo )
                    .append("fecha", fecha)
                    .append("duracion", duracion)
                    .append("monitor", monitor);
                    
            actividad_dao.insertarActividad(socio,actividad);
            JOptionPane.showMessageDialog(ventana,"Activiad Insertada");
        }catch (NumberFormatException nfe){
            JOptionPane.showMessageDialog(ventana,"Error de formato numerico");
        } catch (Exception e) {
        }
    }

    public static void eliminarActividad() {
        try {
            //BUSCAMOS LA ACTIVIDAD POR NOMBRE Y BORRAMOS LA PRIMERA
            String nombre  = ventana.getTxtNombre_actividad().getText();
            int id_socio   = Integer.parseInt(ventana.getTxtIdSocio_actividad().getText());
            Document actividad = actividad_dao.getActividad(nombre,id_socio);
            if (actividad == null){
                JOptionPane.showMessageDialog(ventana,"No existe una actividad con ese nombre");
                return;
            }
            actividad_dao.eliminarActividad(actividad);
            JOptionPane.showMessageDialog(ventana,"Actividad borrada");
        }catch (NumberFormatException nfe){
            JOptionPane.showMessageDialog(ventana,"Error de formato numerico");
        } catch (Exception e) {
        }
    }
    public static void consulta4() {
        try {
            ventana.getTxt_id_socio().setText("");
            //- Debes obtener un informe de lo que cobra el gimnasio en un mes en concreto en total,
            // desglosado en lo que paga cada socio
            //2€ cada actividad de tipo2
            //4# cada actividad de tipo3

            //Primero obtenemos la suma de todas las cuotas y de cada tipo de actividad
            double totalCuotas = socio_dao.getTotalCuotas();
            int totalActividadesTipo2 = socio_dao.getTotalActividadesT2();
            int totalActividadesTipo3 = socio_dao.getTotalActividadesT3();
            
            Double totalingresos = totalCuotas + (totalActividadesTipo2 *2) + (totalActividadesTipo3*4);
            ventana.getTxtArea_consulta4().setText("Total de ingresos en el gimnasio: \n" + totalingresos+"\n");
            
            //AHORA POR EMPLEADO
            ArrayList<Document> cuotasPorSocio= socio_dao.getCuotasporSocio();
            ArrayList<Document> actividadesPorSocio= socio_dao.getActividadesporSocio();
            
            for (int i = 0; i < cuotasPorSocio.size(); i++) {
                Document cuotaYsocio = cuotasPorSocio.get(i);
                Document actividadPorSocio = actividadesPorSocio.get(i);
                ventana.getTxtArea_consulta4().append("------------------------------------\n");
                ventana.getTxtArea_consulta4().append("ID de socio: "+cuotaYsocio.get("_id").toString() + "\n");
                ventana.getTxtArea_consulta4().append("Cuota Fija: "+cuotaYsocio.get("cuota").toString() + "€\n");
                ventana.getTxtArea_consulta4().append("Gastos en actividades tipo 2: "+ (actividadPorSocio.getInteger("tipo2")*2 )+ "€\n");
                ventana.getTxtArea_consulta4().append("Gastos en actividades tipo 3: "+ (actividadPorSocio.getInteger("tipo3")*4 )+ "€\n");
            }
            
        } catch (Exception e) {
        }
    }
    public static void BuscarActividadesEnGimnasio() {
        try {
            actividad_dao.getActividades(ventana.getTxtAreaActividadesEnGimnasio());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,"Excepcion inesperada");
        }
    }
    public static void buscarExitoActividades() {
         try {
            actividad_dao.getExitoActividades(ventana.getTxtAreaExitoActividades());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,"Excepcion inesperada");
        }
    }
    public static void buscarActividadesPorMonitor() {
        try {
            String nombreMonitor = ventana.getTxtNombreMonitorConsulta().getText();
            if (nombreMonitor.isEmpty()){
                JOptionPane.showMessageDialog(ventana,"Cubre el nombre del monitor");
                return;
            }
            actividad_dao.getActividadMonitor(nombreMonitor,ventana.getTxtAreaConsultaMonitor());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana,"Error inesperado");
        } 
    }
    
}
