/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc;

import be.Cliente;
import be.Cliente_;
import be.DetalleVentaProducto;
import be.DetalleVentaProducto_;
import be.Producto;
import be.Producto_;
import be.TipoProducto;
import be.TipoProducto_;
import be.Venta;
import be.Venta_;
import be.ZonaCiudad;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;

/**
 *
 * @author root
 */
@Stateless
public class ProductoFacade extends AbstractFacade<Producto> implements ProductoFacadeLocal {
    @PersistenceContext(unitName = "sistema-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProductoFacade() {
        super(Producto.class);
    }
    @Override
    public List<Producto> traerProducto_consolidado(Date fecha_) {

        // agregar aqui
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Producto> cq = getEntityManager().getCriteriaBuilder().createQuery(Producto.class);
        Root<Producto> registro = cq.from(Producto.class);

      
            ListJoin<Producto, DetalleVentaProducto> join01 = registro.join(Producto_.detalleVentaProductoList);
            Join<DetalleVentaProducto, Venta> join02 = join01.join(DetalleVentaProducto_.venta);
            Join<Producto, TipoProducto> join03 = registro.join(Producto_.tipoProducto);
            cq.orderBy(cb.asc(join03.get(TipoProducto_.descripcion))).distinct(true);
          

            cq.where(
                    getEntityManager().getCriteriaBuilder().and(
                            getEntityManager().getCriteriaBuilder().equal(join02.get(Venta_.estadoExistencia), 1),
                            getEntityManager().getCriteriaBuilder().equal(join02.get(Venta_.fechaVenta),fecha_)
                    ));
        
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }
    @Override
    public Integer contar_consolidado(Producto pro, ZonaCiudad zona_,Date fecha_) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<Producto> registro = cq.from(Producto.class);
       
        

        ListJoin<Producto, DetalleVentaProducto> join01 = registro.join(Producto_.detalleVentaProductoList);
            Join<DetalleVentaProducto, Venta> join02 = join01.join(DetalleVentaProducto_.venta);
            Join<Venta, Cliente> join03 = join02.join(Venta_.cliente);
           Expression<Integer> suma = cb.sum(join01.get(DetalleVentaProducto_.cantidad));
cq.select(suma);
        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        cb.equal(join02.get(Venta_.estadoExistencia), 1),
                        cb.equal(join02.get(Venta_.fechaVenta), fecha_),
                        cb.equal(join01.get(DetalleVentaProducto_.producto), pro),
                        cb.equal(join03.get(Cliente_.zonaCiudad), zona_)
                ));
        Integer l = getEntityManager().createQuery(cq).getSingleResult();
      
        if(l!=null){
            return l;
        }else{
            return 0;
        }
        
    }
}
