/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc;

import be.Producto;
import be.ZonaCiudad;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author root
 */
@Local
public interface ProductoFacadeLocal {

    void create(Producto producto);

    void edit(Producto producto);

    void remove(Producto producto);

    Producto find(Object id);

    List<Producto> findAll();

    List<Producto> findRange(int[] range);

    int count();
    
    List<Producto> traerProducto_consolidado(Date fecha_);
    Integer contar_consolidado(Producto pro, ZonaCiudad zona_,Date fecha_);
List<Producto> filtrado_simple(String campoFiltrar, String dato);
}
