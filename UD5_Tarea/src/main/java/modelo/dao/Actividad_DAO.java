/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import controlador.factory.Conexion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JTextArea;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author Alejandro.perezferna
 */
public class Actividad_DAO {

    public void insertarActividad(Document socio, Document actividad) {
         Conexion.getBD().getCollection("Gimnasio").updateOne(
                new Document("_id",socio.get("_id")),
                Updates.push("actividades",actividad)
                );
    }

    public Document getActividad(String nombre, int id_socio) {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Bson filtro_nombre = Filters.eq("actividades.nombre",nombre);
        Bson filtro_id = Filters.eq("_id",id_socio);
        Document actividad = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.match(Filters.and(filtro_id,filtro_nombre))
        )).first();
        
        return actividad;
    }

    public void eliminarActividad(Document actividad) {
        Document actividad_separada = (Document) actividad.get("actividades");
        
        Conexion.getBD().getCollection("Gimnasio").updateMany(actividad, 
                new Document("$pull",new Document("actividades",actividad_separada)));
    }

    
    
    
    public void getActividades(JTextArea txtAreaActividadesEnGimnasio) {
        txtAreaActividadesEnGimnasio.setText("");
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        
        List<Document> actividades = (List<Document>) coleccion.aggregate(Arrays.asList( 
                Aggregates.unwind("$actividades"),
                Aggregates.group("actividades.nombre",
                        Accumulators.addToSet("Actividades","$actividades.nombre")
                ),
                Aggregates.project(Document.parse("{Actividades:1}"))
        )).into(new ArrayList<>());
        
        for (Document actividade : actividades) {
            txtAreaActividadesEnGimnasio.append("Actividades que se realizan en el gimnasio \n"+actividade.get("Actividades"));
        }
        
    }

    public void getExitoActividades(JTextArea txtArea) {
        String NombreActividadMasExitosa;
        String NombreActividadMenosExitosa;
        txtArea.setText("");
        
        
        //CONSULTA DE LA ACTIVIDAD MAS EXITOSA
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        
        Document actividadMasExitosa = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.group("$actividades.nombre",
                        Accumulators.sum("total", 1)
                        ),
                Aggregates.sort(Sorts.descending("total")),
                Aggregates.limit(1),
                Aggregates.project(Document.parse("{_id:1}"))
        )).first();
        
        NombreActividadMasExitosa = actividadMasExitosa.getString("_id");
        
        
        //CONSULTA DE LA ACTIVIDAD MENOS EXITOSA
        Document actividadMenosExitosa = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.group("$actividades.nombre", 
                        Accumulators.sum("total", 1)
                ),
                Aggregates.sort(Sorts.ascending("total")),
                Aggregates.limit(1),
                Aggregates.project(Document.parse("{_id: 1}"))
        )).first();

        NombreActividadMenosExitosa = actividadMenosExitosa.getString("_id");

        txtArea.setText("Actividad con mas exito del gimnasio:  " + NombreActividadMasExitosa+"\n");
        txtArea.append("Actividad con menos exitos del gimasion  " + NombreActividadMenosExitosa);
        
        
        
    }

    public void getActividadMonitor(String nombreMonitor, JTextArea txtAreaConsultaMonitor) {
        txtAreaConsultaMonitor.setText("");
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Bson filtro =Filters.eq("actividades.monitor",nombreMonitor);
        
        List<Document> actividadesMonitor =  (List<Document>) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.match(filtro),
                Aggregates.group("actividades.nombre",
                        Accumulators.addToSet("actividadesImpartidas", "$actividades.nombre")
                        ),
                Aggregates.project(Document.parse("{actividadesImpartidas:1}")) 
        )).into(new ArrayList<>());
        
        if (actividadesMonitor.isEmpty()){
            txtAreaConsultaMonitor.setText("El monitor no existe o no imparte clases");
            return;
        }
        
        txtAreaConsultaMonitor.append("Actividades que imparte " + nombreMonitor + ":\n");
        for (Document document : actividadesMonitor) {
            txtAreaConsultaMonitor.append(document.get("actividadesImpartidas").toString()+"\n");
        }
        
    }
}
