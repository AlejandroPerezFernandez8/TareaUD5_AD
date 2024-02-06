package controlador.factory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javax.swing.JOptionPane;
import modelo.dao.Actividad_DAO;
import modelo.dao.Socio_DAO;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Conexion {

    private static MongoClient Cliente;
    private static MongoDatabase BD;
    private static CodecRegistry pojoCodecRegistry;
    private static final String nameBD = "Tarea";//*******Indica la BD **************
    private static final String IP = "192.168.56.117";//*******Indica la IP ***************
    private static final int PORT = 27017;

    public static void Cerrar() {

        if (BD != null) {
            Cliente.close();
        }
    }

    public static MongoDatabase getBD() {
        return BD;
    }
    
    public static MongoCollection getColeccion(String nombre) {
        return BD.getCollection(nombre);
    }
    
    public static MongoDatabase ConectarconMapeo() {
        try {
            //Cuando usamos codec para mapear el modelo. El modelo lo tenemos que poner sin contructor.

            pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            Cliente = new MongoClient(IP,
                    MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
            BD = Cliente.getDatabase(nameBD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en la conexión a la base de datos ");
        }
        return null;
    }


    public static CodecRegistry getRegistry() {
        return pojoCodecRegistry;
    }
   //Define los  métodos para los objetos dao
   public static Actividad_DAO getActividad_DAO(){
       return new Actividad_DAO();
   }

   public static Socio_DAO getsoSocio_DAO(){
       return new Socio_DAO();
   }

}
