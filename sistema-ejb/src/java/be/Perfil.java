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
@Table(name = "perfil")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Perfil.findAll", query = "SELECT p FROM Perfil p")
    , @NamedQuery(name = "Perfil.findByPkId", query = "SELECT p FROM Perfil p WHERE p.pkId = :pkId")
    , @NamedQuery(name = "Perfil.findByNombre", query = "SELECT p FROM Perfil p WHERE p.nombre = :nombre")
    , @NamedQuery(name = "Perfil.findByDescripcion", query = "SELECT p FROM Perfil p WHERE p.descripcion = :descripcion")
    , @NamedQuery(name = "Perfil.findByFecRegistro", query = "SELECT p FROM Perfil p WHERE p.fecRegistro = :fecRegistro")
    , @NamedQuery(name = "Perfil.findByUsuRegistro", query = "SELECT p FROM Perfil p WHERE p.usuRegistro = :usuRegistro")
    , @NamedQuery(name = "Perfil.findByEstadoExistencia", query = "SELECT p FROM Perfil p WHERE p.estadoExistencia = :estadoExistencia")})
public class Perfil implements Serializable {

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
    @OneToMany(mappedBy = "perfil", fetch = FetchType.LAZY)
    private List<UrlsSistema> urlsSistemaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "perfil", fetch = FetchType.LAZY)
    private List<EmpleadoPerfil> empleadoPerfilList;

    public Perfil() {
    }

    public Perfil(Integer pkId) {
        this.pkId = pkId;
    }

    public Perfil(Integer pkId, int estadoExistencia) {
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
    public List<UrlsSistema> getUrlsSistemaList() {
        return urlsSistemaList;
    }

    public void setUrlsSistemaList(List<UrlsSistema> urlsSistemaList) {
        this.urlsSistemaList = urlsSistemaList;
    }

    @XmlTransient
    public List<EmpleadoPerfil> getEmpleadoPerfilList() {
        return empleadoPerfilList;
    }

    public void setEmpleadoPerfilList(List<EmpleadoPerfil> empleadoPerfilList) {
        this.empleadoPerfilList = empleadoPerfilList;
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
        if (!(object instanceof Perfil)) {
            return false;
        }
        Perfil other = (Perfil) object;
        if ((this.pkId == null && other.pkId != null) || (this.pkId != null && !this.pkId.equals(other.pkId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.Perfil[ pkId=" + pkId + " ]";
    }
    
}
