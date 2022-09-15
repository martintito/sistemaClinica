/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author root
 */
@Entity
@Table(name = "empleado_urls_sistema")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EmpleadoUrlsSistema.findAll", query = "SELECT e FROM EmpleadoUrlsSistema e")
    , @NamedQuery(name = "EmpleadoUrlsSistema.findByPkId", query = "SELECT e FROM EmpleadoUrlsSistema e WHERE e.pkId = :pkId")
    , @NamedQuery(name = "EmpleadoUrlsSistema.findByFecRegistro", query = "SELECT e FROM EmpleadoUrlsSistema e WHERE e.fecRegistro = :fecRegistro")
    , @NamedQuery(name = "EmpleadoUrlsSistema.findByUsuRegistro", query = "SELECT e FROM EmpleadoUrlsSistema e WHERE e.usuRegistro = :usuRegistro")
    , @NamedQuery(name = "EmpleadoUrlsSistema.findByEstadoExistencia", query = "SELECT e FROM EmpleadoUrlsSistema e WHERE e.estadoExistencia = :estadoExistencia")})
public class EmpleadoUrlsSistema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pk_id")
    private Integer pkId;
    @Column(name = "fec_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecRegistro;
    @Column(name = "usu_registro")
    private Integer usuRegistro;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado_existencia")
    private int estadoExistencia;
    @JoinColumn(name = "fk_urls_sistema", referencedColumnName = "pk_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private UrlsSistema urlsSistema;
    @JoinColumn(name = "fk_empleado", referencedColumnName = "id_empleado")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Empleado empleado;

    public EmpleadoUrlsSistema() {
    }

    public EmpleadoUrlsSistema(Integer pkId) {
        this.pkId = pkId;
    }

    public EmpleadoUrlsSistema(Integer pkId, int estadoExistencia) {
        this.pkId = pkId;
        this.estadoExistencia = estadoExistencia;
    }

    public Integer getPkId() {
        return pkId;
    }

    public void setPkId(Integer pkId) {
        this.pkId = pkId;
    }

    public Date getFecRegistro() {
        return fecRegistro;
    }

    public void setFecRegistro(Date fecRegistro) {
        this.fecRegistro = fecRegistro;
    }

    public Integer getUsuRegistro() {
        return usuRegistro;
    }

    public void setUsuRegistro(Integer usuRegistro) {
        this.usuRegistro = usuRegistro;
    }

    public int getEstadoExistencia() {
        return estadoExistencia;
    }

    public void setEstadoExistencia(int estadoExistencia) {
        this.estadoExistencia = estadoExistencia;
    }

    public UrlsSistema getUrlsSistema() {
        return urlsSistema;
    }

    public void setUrlsSistema(UrlsSistema urlsSistema) {
        this.urlsSistema = urlsSistema;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pkId != null ? pkId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EmpleadoUrlsSistema)) {
            return false;
        }
        EmpleadoUrlsSistema other = (EmpleadoUrlsSistema) object;
        if ((this.pkId == null && other.pkId != null) || (this.pkId != null && !this.pkId.equals(other.pkId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.EmpleadoUrlsSistema[ pkId=" + pkId + " ]";
    }
    
}
