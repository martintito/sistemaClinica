/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author root
 */
@Entity
@Table(name = "urls_sistema")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UrlsSistema.findAll", query = "SELECT u FROM UrlsSistema u")
    , @NamedQuery(name = "UrlsSistema.findByPkId", query = "SELECT u FROM UrlsSistema u WHERE u.pkId = :pkId")
    , @NamedQuery(name = "UrlsSistema.findByNombre", query = "SELECT u FROM UrlsSistema u WHERE u.nombre = :nombre")
    , @NamedQuery(name = "UrlsSistema.findByDescripcion", query = "SELECT u FROM UrlsSistema u WHERE u.descripcion = :descripcion")
    , @NamedQuery(name = "UrlsSistema.findByFecRegistro", query = "SELECT u FROM UrlsSistema u WHERE u.fecRegistro = :fecRegistro")
    , @NamedQuery(name = "UrlsSistema.findByUsuRegistro", query = "SELECT u FROM UrlsSistema u WHERE u.usuRegistro = :usuRegistro")
    , @NamedQuery(name = "UrlsSistema.findByEstadoExistencia", query = "SELECT u FROM UrlsSistema u WHERE u.estadoExistencia = :estadoExistencia")})
public class UrlsSistema implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "pk_id")
    private Integer pkId;
    @Size(max = 100)
    @Column(name = "nombre")
    private String nombre;
    @Size(max = 100)
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "fec_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecRegistro;
    @Column(name = "usu_registro")
    private Integer usuRegistro;
    @Basic(optional = false)
    @NotNull
    @Column(name = "estado_existencia")
    private int estadoExistencia;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "urlsSistema", fetch = FetchType.LAZY)
    private List<EmpleadoUrlsSistema> empleadoUrlsSistemaList;
    @JoinColumn(name = "fk_perfil", referencedColumnName = "pk_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Perfil perfil;

    public UrlsSistema() {
    }

    public UrlsSistema(Integer pkId) {
        this.pkId = pkId;
    }

    public UrlsSistema(Integer pkId, int estadoExistencia) {
        this.pkId = pkId;
        this.estadoExistencia = estadoExistencia;
    }

    public Integer getPkId() {
        return pkId;
    }

    public void setPkId(Integer pkId) {
        this.pkId = pkId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    @XmlTransient
    public List<EmpleadoUrlsSistema> getEmpleadoUrlsSistemaList() {
        return empleadoUrlsSistemaList;
    }

    public void setEmpleadoUrlsSistemaList(List<EmpleadoUrlsSistema> empleadoUrlsSistemaList) {
        this.empleadoUrlsSistemaList = empleadoUrlsSistemaList;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
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
        if (!(object instanceof UrlsSistema)) {
            return false;
        }
        UrlsSistema other = (UrlsSistema) object;
        if ((this.pkId == null && other.pkId != null) || (this.pkId != null && !this.pkId.equals(other.pkId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.UrlsSistema[ pkId=" + pkId + " ]";
    }
    
}
