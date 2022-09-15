/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc;

import be.Cliente;
import be.Cliente_;
import be.Venta;
import be.Venta_;
import be.ZonaCiudad;
import be.ZonaCiudad_;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;

/**
 *
 * @author root
 */
@Stateless
public class ZonaCiudadFacade extends AbstractFacade<ZonaCiudad> implements ZonaCiudadFacadeLocal {
    @PersistenceContext(unitName = "sistema-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ZonaCiudadFacade() {
        super(ZonaCiudad.class);
    }
    @Override
    public List<ZonaCiudad> traerZonas_consolidado(Date fecha_) {

        // agregar aqui
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ZonaCiudad> cq = getEntityManager().getCriteriaBuilder().createQuery(ZonaCiudad.class);
        Root<ZonaCiudad> registro = cq.from(ZonaCiudad.class);

      
            ListJoin<ZonaCiudad, Cliente> join01 = registro.join(ZonaCiudad_.clienteList);
            ListJoin<Cliente, Venta> join02 = join01.join(Cliente_.ventaList);
            cq.orderBy(cb.asc(registro.get(ZonaCiudad_.descripcionZonaCiudad))).distinct(true);
          
            cq.where(
                    getEntityManager().getCriteriaBuilder().and(
                            getEntityManager().getCriteriaBuilder().equal(join02.get(Venta_.estadoExistencia), 1),
                            getEntityManager().getCriteriaBuilder().equal(join02.get(Venta_.fechaVenta),fecha_)
                    ));
        
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

}
