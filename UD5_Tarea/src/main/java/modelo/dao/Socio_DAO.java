/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
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
public class Socio_DAO {

    public Document getSocio (int id) {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Bson filtro = Filters.eq("_id",id);
        
        Document socios = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$socio"),
                Aggregates.match(filtro)
        )).first();
       
        return socios; 
    }
    public void insertarSocio(int id, String nombre, String dni, String fecha_alta, Double cuota) {
        Document nuevo_socio = new Document();
        nuevo_socio.append("_id", id)
                .append("socio", new Document()
                        .append("nombre", nombre)
                        .append("dni",dni)
                        .append("fecha_alta", fecha_alta)
                        .append("cuota", cuota)
                        );
                
        Conexion.getBD().getCollection("Gimnasio").insertOne(nuevo_socio);
    }
    public void borrarSocio  (Document socio) {
        Conexion.getBD().getCollection("Gimnasio").deleteOne(socio);
    }

    public void getActividadesMesActual(int id, int monthValue) {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Bson filtro = Filters.eq("_id",id);
        
        List<Document> socio =  (List<Document>) coleccion.aggregate(Arrays.asList(
                 Aggregates.unwind("$actividades"),
                 Aggregates.match(filtro),
                   Aggregates.project(Document.parse("{actividades:{nombre:1,fecha:1}}"))
        )).into(new ArrayList<>());
        
        
        for (Document document : socio) {
            System.out.println(document.toJson());
        }
    }

    public void modificarSocio(Document socio_modificado, Document socio) {
        Conexion.getBD().getCollection("Gimnasio").updateOne(socio, new Document("$set",socio_modificado));
    }

    public Document getActividades(int id_socio) {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Bson filtro = Filters.eq("_id",id_socio);
        
        Document socio = (Document) coleccion.aggregate(Arrays.asList(
                 Aggregates.match(filtro)
              
        )).first();
        
        return socio;
    }

    public void consulta4(JTextArea txtArea_consulta4) {
        
        
    }

    
    public void getTotalCuotas() {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        Document totalCuotas = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.group( "_id",Accumulators.sum("SumaCuotas", "$socio.cuota")),
                Aggregates.project(Document.parse("{_id:0,SumaCuotas:1}"))
        )).first();
        
        
            System.out.println(totalCuotas.toJson());
    }
    
    
    
    
}
