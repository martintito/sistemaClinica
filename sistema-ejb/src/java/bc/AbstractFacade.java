/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;

/**
 *
 * @author root
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public List<T> buscarTerminoExacto(String campoBuscar, String terminoBusqueda, String campoOrdenar, int tipoOrden) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> registro = cq.from(entityClass);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
// TIpo de Orden: descendente=0 , ascendente =1 
// Con campo a ordenar hacemos que : cq.orderBy(cb.asc(registro.get("pkId"))); sea dinamico
        if (tipoOrden == 0) {
            cq.orderBy(cb.desc(registro.get(campoOrdenar)));
        } else {
            cq.orderBy(cb.asc(registro.get(campoOrdenar)));
        }

        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        getEntityManager().getCriteriaBuilder().equal(registro.get("estadoExistencia"), 1),
                        getEntityManager().getCriteriaBuilder().equal(registro.get(campoBuscar), terminoBusqueda)
                )
        );

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

    public List<T> listaActivos(String campoOrdenar, int tipoOrden) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> registro = cq.from(entityClass);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
// TIpo de Orden: descendente=0 , ascendente =1 
// Con campo a ordenar hacemos que : cq.orderBy(cb.asc(registro.get("pkId"))); sea dinamico
        if (tipoOrden == 0) {
            cq.orderBy(cb.desc(registro.get(campoOrdenar)));
        } else {
            cq.orderBy(cb.asc(registro.get(campoOrdenar)));
        }

        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        getEntityManager().getCriteriaBuilder().equal(registro.get("estadoExistencia"), 1)
                )
        );

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

    public List<T> filtroFechas(String campoFiltrar, Date inicio, Date fin, String campoOrdenar, int tipoOrden) {

        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> registro = cq.from(entityClass);

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        if (tipoOrden == 0) {
            cq.orderBy(cb.desc(registro.get(campoOrdenar)));
        } else {
            cq.orderBy(cb.asc(registro.get(campoOrdenar)));
        }
        cq.select(registro);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(fin);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        getEntityManager().getCriteriaBuilder().between(registro.<Date>get(campoFiltrar), inicio, cal.getTime())
                //,getEntityManager().getCriteriaBuilder().equal(registro.get("estadoExistencia"), 1)
                ));
        return getEntityManager().createQuery(cq).getResultList();

    }

    public List<T> buscar_objeto(String campoBuscar, Object terminoBusqueda, String campoOrdenar, int tipoOrden) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> registro = cq.from(entityClass);
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        if (tipoOrden == 0) {
            cq.orderBy(cb.desc(registro.get(campoOrdenar)));
        } else {
            cq.orderBy(cb.asc(registro.get(campoOrdenar)));
        }

        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        getEntityManager().getCriteriaBuilder().equal(registro.get(campoBuscar), terminoBusqueda)
                )
        );

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }
public Object buscar_object_dosParametros(String campo_, Object codigo,String campo02_, Object codigo02) {
        try {
            javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
            javax.persistence.criteria.Root<T> registro = cq.from(entityClass);
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

            cq.where(
                    getEntityManager().getCriteriaBuilder().and(
                            cb.equal(registro.get(campo_), codigo),
                            cb.equal(registro.get(campo02_), codigo02)
                    )
            );
            javax.persistence.Query q = getEntityManager().createQuery(cq);
            return q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
public List<T> filtrado_simple(String campoFiltrar, String dato) {

        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> registro = cq.from(entityClass);
        cq.where(
                getEntityManager().getCriteriaBuilder().and(
                        getEntityManager().getCriteriaBuilder().like(registro.<String>get(campoFiltrar), "%" + dato.toUpperCase() + "%")
                        
                ));

        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }


}
