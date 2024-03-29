/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
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
        //DEBEMOS SACAR CUANTO PAGA POR TIPO2 Y TIPO3
        //LUEGO LO MULTIPLICAMOS Y LO SUMAMOS TODO
         MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");    
                 
         List<Document> pagos = (List<Document>) coleccion.aggregate(Arrays.asList(
               Aggregates.unwind("$actividades"),
               Aggregates.group("actividades.nombre",
            Accumulators.sum("tipo2", Filters.eq("$actividades.tipo", 2)),
            Accumulators.sum("tipo3", Filters.eq("$actividades.tipo", 3))
                         ) 
         )).into(new ArrayList<>());
         
         for (Document pago : pagos) {
             pago.toJson();
        }
    }

    public double getTotalCuotas() {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        
        Document totalCuotas = (Document) coleccion.aggregate(Arrays.asList( 
                Aggregates.group("_id",Accumulators.sum("total", "$socio.cuota"))
        )).first();
        
        return totalCuotas.getDouble("total");
    }

    public int getTotalActividadesT2() {
        MongoCollection<?> coleccion = Conexion.getColeccion("Gimnasio");
        
        Document totalActividadesT2 = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.match(Filters.eq("actividades.tipo", "2")),
                Aggregates.count("Cantidad")  
        )).first();   
        
        if (totalActividadesT2 != null){ 
            return totalActividadesT2.getInteger("Cantidad");
       }else{return 0;}
    }

    public int getTotalActividadesT3() {
        MongoCollection<?> coleccion = Conexion.getColeccion("Gimnasio");
        
        Document totalActividadesT3 = (Document) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.match(Filters.eq("actividades.tipo", "3")),
                Aggregates.count("Cantidad")  
        )).first();   
        
        
       if (totalActividadesT3 != null){ 
            return totalActividadesT3.getInteger("Cantidad");
       }else{return 0;}
    }

    public ArrayList<Document> getCuotasporSocio() {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");
        List<Document> cuotasEmpleados = (List<Document>) coleccion.aggregate(Arrays.asList(
                Aggregates.project(
                        Projections.computed("cuota", "$socio.cuota")
                )
        )).into(new ArrayList<>());
        return (ArrayList<Document>) cuotasEmpleados;
    }

    public ArrayList<Document> getActividadesporSocio() {
        MongoCollection<?> coleccion = Conexion.getBD().getCollection("Gimnasio");

        List<Document> cuotasEmpleados = (List<Document>) coleccion.aggregate(Arrays.asList(
                Aggregates.unwind("$actividades"),
                Aggregates.group(
                        "$_id",
                        Accumulators.sum("tipo2", new Document("$sum", new Document("$cond", Arrays.asList(
                                new Document("$eq", Arrays.asList("$actividades.tipo", "2")), 1, 0
                        )))),
                        Accumulators.sum("tipo3", new Document("$sum", new Document("$cond", Arrays.asList(
                                new Document("$eq", Arrays.asList("$actividades.tipo", "3")), 1, 0
                        ))))
                ),
                Aggregates.project(
                        Projections.fields(
                                Projections.include("tipo2", "tipo3"),
                                Projections.exclude("_id")
                        )
                )
        )).into(new ArrayList<>());
        return (ArrayList<Document>) cuotasEmpleados;
    }


    
    
    
    
}
