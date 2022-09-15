/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import bc.DetalleAlmacenProductosFacadeLocal;
import bc.ProductoFacadeLocal;
import bc.StockProductoTiendaOrigenFacadeLocal;
import bc.VentaFacadeLocal;
import bc.ZonaCiudadFacadeLocal;
import be.DetalleAlmacenProductos;
import be.DetalleVentaProducto;
import be.Producto;
import be.StockProductoTiendaOrigen;
import be.Tienda;
import be.Venta;
import be.ZonaCiudad;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.BarcodeQRCode;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import javax.faces.context.ExternalContext;
import utils.Utils;

/**
 *
 * @author argos
 */
@ManagedBean
@SessionScoped
public class ManagedBeanReportes implements Serializable {

    @EJB
    private VentaFacadeLocal ventaFacade;

    @EJB
    private ZonaCiudadFacadeLocal zonaCiudadFacade;
    @EJB
    private ProductoFacadeLocal productoFacade;

    @EJB
    private DetalleAlmacenProductosFacadeLocal detalleAlmacenProductosFacade;
    @EJB
    private StockProductoTiendaOrigenFacadeLocal stockProductoTiendaOrigenFacade;

    private Date fecha_inicio = new Date();
    Font bigFont = FontFactory.getFont("Helvetica", "Windows-1254", 14.0F, 1, BaseColor.BLACK);
    Font bigFont12 = FontFactory.getFont("Helvetica", "Windows-1254", 12.0F, 1, BaseColor.BLACK);
    Font pequeFont = FontFactory.getFont("Helvetica", "Windows-1254", 11.0F, 1, BaseColor.BLACK);
    Font titulopequeFont = FontFactory.getFont("Helvetica", "Windows-1254", 8.0F, 1, BaseColor.BLACK);
    Font peque = FontFactory.getFont("Arial", 14, BaseColor.BLACK);
    Font fontmed08 = FontFactory.getFont("Arial", 8, BaseColor.BLACK);
    Font fontmed11 = FontFactory.getFont("Arial", 11, BaseColor.BLACK);
    Font fontmed12_bold = FontFactory.getFont("Arial", 11, 1, BaseColor.BLACK);
    Font fondo_titulo = FontFactory.getFont("Arial", 12, BaseColor.BLACK);
    Font fondo_detalle_productos = FontFactory.getFont("Arial", 8, BaseColor.BLACK);
    Font fondo_denominacion = FontFactory.getFont("Arial", 7, BaseColor.BLACK);
    Font fondo_consolidado_titulos_mercados = FontFactory.getFont("Arial", 7.5f, BaseColor.BLACK);
    Font fondo_consolidado = FontFactory.getFont("Arial", 8.5f, BaseColor.BLACK);
    Font fondo_consolidado_bold = FontFactory.getFont("Arial", 8.5f, Font.BOLD);

    private final float[] anchocol03 = {160, 90, 70, 70};
    private final float[] anchocol_02 = {16, 434};
    private final float[] anchocol_tabla03 = {120, 220, 50};
    private final float[] ancho_detalle = {15, 75, 20, 20};
    private int cant_v = 0;
    private int cant_f = 0;
    private int cantidad_detalle = 15;
    float[] headerWidths = {250, 10, 250};

    public ManagedBeanReportes() {
    }

    public void reporte_consolidado_mercado(Date fecha_venta) throws DocumentException, IOException {
        List<Venta> lista_ventas = ventaFacade.filtroFechas("fechaVenta", fecha_venta, fecha_venta, "idVenta", 0);
        SimpleDateFormat formato_fechas = new SimpleDateFormat("EEEE dd/MM/YYYY");
        SimpleDateFormat formato_fechas02 = new SimpleDateFormat("dd-MM-YYYY");
        StringBuilder cadena_inicio = new StringBuilder(formato_fechas.format(fecha_venta));
        StringBuilder cadena_reporte = new StringBuilder(formato_fechas02.format(fecha_venta));
        List<ZonaCiudad> lista_zonas = zonaCiudadFacade.traerZonas_consolidado(fecha_venta);
        int numero_mercados = lista_zonas.size();

        // dejamos espacio para la primera y ultima filla
        float[] cabeceras_ = new float[numero_mercados + 2];
        Integer[] suma_mercados = new Integer[numero_mercados + 2];
        for (int i = 0; i < suma_mercados.length - 1; i++) {
            suma_mercados[i] = 0;
        }
        // posicion 0 es fija
        cabeceras_[0] = 170;
        float espacio_cabezera = 560 / (numero_mercados + 1);
        for (int i = 1; i < (numero_mercados + 1); i++) {
            cabeceras_[i] = espacio_cabezera;
        }
        // este es el ultimo espacio para cabezera
        cabeceras_[numero_mercados + 1] = 70;
        FacesContext facexcontext = FacesContext.getCurrentInstance();
        ValueExpression vex = facexcontext.getApplication().getExpressionFactory().createValueExpression(facexcontext.getELContext(), "#{managedBeanLogin}", ManagedBeanLogin.class);
        ManagedBeanLogin beanLogin = (ManagedBeanLogin) vex.getValue(facexcontext.getELContext());

        FacesContext context = FacesContext.getCurrentInstance();

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);//int marginLeft,   int marginRight,   int marginTop,   int marginBottom

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        //writer.setPageEvent(new ManagedBeanReportes.Watermark(""));
        if (!document.isOpen()) {
            document.open();
        }

        try {

            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

            Chunk underline = new Chunk("REPORTE CONSOLIDADO DE:" + cadena_inicio.toString().toUpperCase() + "\n", fondo_denominacion);
            document.add(new Paragraph(underline));
            document.add(new Paragraph("\n"));

            PdfPTable tabla_princial = new PdfPTable(numero_mercados + 2);
            tabla_princial.setWidthPercentage(100);
            tabla_princial.setTotalWidth(800f);
            // table.setTotalWidth(540f);
            tabla_princial.setLockedWidth(true);

            tabla_princial.setWidths(cabeceras_);
            tabla_princial.getDefaultCell();

            tabla_princial.addCell(construir_celda("/", fondo_consolidado, Paragraph.ALIGN_LEFT, 1, 1));

            for (int i = 1; i < (numero_mercados + 1); i++) {
                tabla_princial.addCell(construir_celda(lista_zonas.get(i - 1).getDescripcionZonaCiudad().toUpperCase(), fondo_consolidado_titulos_mercados, Paragraph.ALIGN_CENTER, 1, 1));

            }
            tabla_princial.addCell(construir_celda("TOTAL", fondo_consolidado_bold, Paragraph.ALIGN_RIGHT, 1, 1));

            for (Producto pro : productoFacade.traerProducto_consolidado(fecha_venta)) {
                tabla_princial.addCell(construir_celda(pro.getNombreProducto().toUpperCase() + " (" + pro.getTipoProducto().getNombreTipoProducto() + ")", fondo_consolidado, Paragraph.ALIGN_LEFT, 1, 1));
                Integer suma_ = 0;
                for (int i = 1; i < (numero_mercados + 1); i++) {
                    // traer el count aqui
                    Integer data_ = productoFacade.contar_consolidado(pro, lista_zonas.get(i - 1), fecha_venta);

                    suma_ = suma_ + data_;
                    tabla_princial.addCell(construir_celda(String.valueOf(data_), fondo_consolidado, Paragraph.ALIGN_CENTER, 1, 1));
                    suma_mercados[i] = suma_mercados[i] + data_;
                }
                tabla_princial.addCell(construir_celda(String.valueOf(suma_), fondo_consolidado_bold, Paragraph.ALIGN_RIGHT, 1, 1));

            }
            // ultima linea
            tabla_princial.addCell(construir_celda("TOTAL", fondo_consolidado_bold, Paragraph.ALIGN_LEFT, 1, 1));

            Integer suma_final = 0;
            for (int i = 1; i < (numero_mercados + 1); i++) {
                suma_final = suma_final + suma_mercados[i];
                tabla_princial.addCell(construir_celda(String.valueOf(Math.toIntExact(suma_mercados[i])), fondo_consolidado_bold, Paragraph.ALIGN_CENTER, 1, 1));

            }
            tabla_princial.addCell(construir_celda(String.valueOf(Math.toIntExact(suma_final)), fondo_consolidado_bold, Paragraph.ALIGN_RIGHT, 1, 1));

            document.add(tabla_princial);

            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            document.close();

            String fileName = "reporte_Consolidado-" + cadena_reporte.toString();

            writePDFToResponse(context.getExternalContext(), baos, fileName);
            context.responseComplete();

        } catch (Exception de) {
            de.printStackTrace();
        }
    }

    public void reporte_consolidado(Date inicio_, Date fin_) throws DocumentException, IOException {
        List<Venta> lista_ventas = ventaFacade.filtroFechas("fechaVenta", inicio_, fin_, "idVenta", 0);
        SimpleDateFormat formato_fechas = new SimpleDateFormat("dd-MM-YYYY");
        StringBuilder cadena_inicio = new StringBuilder(formato_fechas.format(inicio_));
        StringBuilder cadena_fin = new StringBuilder(formato_fechas.format(fin_));

        FacesContext facexcontext = FacesContext.getCurrentInstance();
        ValueExpression vex = facexcontext.getApplication().getExpressionFactory().createValueExpression(facexcontext.getELContext(), "#{managedBeanLogin}", ManagedBeanLogin.class);
        ManagedBeanLogin beanLogin = (ManagedBeanLogin) vex.getValue(facexcontext.getELContext());

        FacesContext context = FacesContext.getCurrentInstance();

        Document document = new Document(PageSize.A5.rotate(), 20, 20, 20, 20);//int marginLeft,   int marginRight,   int marginTop,   int marginBottom

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        //writer.setPageEvent(new ManagedBeanReportes.Watermark(""));
        if (!document.isOpen()) {
            document.open();
        }

        try {

            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

            for (Venta venta_ : lista_ventas) {

                PdfPTable tabla_princial = new PdfPTable(3);
                tabla_princial.setWidthPercentage(100);
                tabla_princial.setTotalWidth(520f);
                // table.setTotalWidth(540f);
                tabla_princial.setLockedWidth(true);

                tabla_princial.setWidths(headerWidths);
                tabla_princial.getDefaultCell();

                PdfPTable tabla_izquierda = new PdfPTable(1);
                //float[] headerWidths_iz = {225};
                tabla_izquierda.setWidthPercentage(100);
                tabla_izquierda.setTotalWidth(225);
                tabla_izquierda.setLockedWidth(true);
                //tabla_izquierda.setWidths(headerWidths_iz);
                tabla_izquierda.getDefaultCell();

                BarcodeQRCode qrcode = new BarcodeQRCode(venta_.getIdVenta().toString(), 80, 80, null);
                Image qrcodeImage = qrcode.getImage();
                qrcodeImage.setAbsolutePosition(190f, 320f);
                document.add(qrcodeImage);

                BarcodeQRCode qrcode2 = new BarcodeQRCode(venta_.getIdVenta().toString(), 80, 80, null);
                Image qrcodeImage2 = qrcode2.getImage();
                qrcodeImage2.setAbsolutePosition(480f, 320f);
                document.add(qrcodeImage2);

                String logo_ = extContext.getRealPath("//resources//images//logo_mol.png");

                Image img_logo = Image.getInstance(logo_);
                img_logo.setAlignment(Image.ALIGN_LEFT);
                img_logo.scalePercent(25);
//img_logo.setBorder(Image.BOX); 
//img_logo.setBorderWidth(0); 
//img_logo.setBorderColor(BaseColor.BLACK);
                PdfPCell cell_logo = new PdfPCell(img_logo, false);
                cell_logo.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
                cell_logo.setColspan(1);
                cell_logo.setBorder(Rectangle.NO_BORDER);
                tabla_izquierda.addCell(cell_logo);

                PdfPCell cell_01 = new PdfPCell();
                cell_01 = new PdfPCell(new Paragraph("MOLINDUSTRIA \n DERIVADOS DE MAIZ Y OTROS", fondo_titulo));
                cell_01.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
                cell_01.setColspan(1);
                cell_01.setBorder(Rectangle.NO_BORDER);
                tabla_izquierda.addCell(cell_01);

                tabla_izquierda.addCell(construir_celda("CEL. 921841063 / 926325912 / 939713121 / 980147211 / 959117453 ", fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));

                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/YYYY");
                StringBuilder cadena = new StringBuilder(formato.format(venta_.getFechaVenta()));

                Chunk underline = new Chunk("FECHA DE VENTA: " + cadena.toString().toUpperCase() + "   ESTADO:" + venta_.getEstadoVenta().getNombreTestadoVenta().toUpperCase() + "      COD: " + venta_.getIdVenta(), fondo_denominacion);
                underline.setUnderline(0.2f, -2f); //0.1 thick, -2 y-location
                PdfPCell fecha = new PdfPCell(new Paragraph(underline));
                fecha.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
                fecha.setColspan(1);
                fecha.setBorder(Rectangle.NO_BORDER);
                tabla_izquierda.addCell(fecha);

                tabla_izquierda.addCell(construir_celda("SEÑOR(ES): " + venta_.getCliente().getNombreCliente(), fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));
                tabla_izquierda.addCell(construir_celda("DIRECCIÓN: " + venta_.getCliente().getZonaCiudad().getDescripcionZonaCiudad(), fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));

                PdfPTable tabla_detalle_venta = new PdfPTable(4);
                //float[] headerWidths_detalle = {15,75,20,20};
                tabla_detalle_venta.setWidthPercentage(100);
                //tabla_detalle_venta.setLockedWidth(true);
                tabla_detalle_venta.setWidths(ancho_detalle);
                //tabla_detalle_venta.getDefaultCell();
                tabla_detalle_venta.addCell(construir_celda("CAN", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                tabla_detalle_venta.addCell(construir_celda("DESCRIPCION", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                tabla_detalle_venta.addCell(construir_celda("P.U", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                tabla_detalle_venta.addCell(construir_celda("IMP.", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                int cant_ = 0;
                for (DetalleVentaProducto det : venta_.getDetalleVentaProductoList()) {

                    tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getCantidad()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                    cant_ = cant_ + det.getCantidad();
                    String nombre_ = "";
                    if (det.getProducto().getNombreProducto().length() > 25) {
                        nombre_ = det.getProducto().getNombreProducto().toLowerCase().substring(0, 25);
                    } else {
                        nombre_ = det.getProducto().getNombreProducto().toLowerCase();
                    }
                    tabla_detalle_venta.addCell(construir_celda(nombre_, fondo_detalle_productos, Paragraph.ALIGN_LEFT, 1, 1));
                    tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getPrecioVenta().doubleValue()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                    tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getSubTotal().doubleValue()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                }

                for (PdfPCell cell_ : celdas_vacias(cantidad_detalle - venta_.getDetalleVentaProductoList().size())) {
                    tabla_detalle_venta.addCell(cell_);
                }

                tabla_izquierda.addCell(tabla_detalle_venta);
                PdfPCell cell_denominacion = new PdfPCell();
                cell_denominacion = new PdfPCell(new Paragraph("SON : " + Utils.cantidadConLetra(venta_.getTotalVenta()) + " Soles.", fondo_denominacion));
                cell_denominacion.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
                cell_denominacion.setColspan(1);
                cell_denominacion.setBorder(Rectangle.NO_BORDER);
                tabla_izquierda.addCell(cell_denominacion);

                tabla_izquierda.addCell(construir_celda("CANT. SACOS :  " + cant_ + "        MONTO TOTAL: S./ " + String.valueOf(venta_.getTotalVenta()), fondo_detalle_productos, Paragraph.ALIGN_RIGHT, 1, 1));

                PdfPCell tab_iz = new PdfPCell();
                tab_iz = new PdfPCell(tabla_izquierda);
                tab_iz.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                tab_iz.setColspan(1);
                tab_iz.setBorder(Rectangle.NO_BORDER);
                tab_iz.setPaddingLeft(-60);
                tabla_princial.addCell(tab_iz);

                PdfPTable central = new PdfPTable(1);
                //float[] headerWidths_iz = {225};
                central.setWidthPercentage(100);
                central.setTotalWidth(10);
                central.setLockedWidth(true);
                //tabla_izquierda.setWidths(headerWidths_iz);
                central.getDefaultCell();
                tabla_princial.addCell(central);

                PdfPCell tab_der = new PdfPCell();
                tab_der = new PdfPCell(tabla_izquierda);
                tab_der.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                tab_der.setColspan(1);
                tab_der.setBorder(Rectangle.NO_BORDER);
                tabla_princial.addCell(tab_der);

                tabla_princial.addCell(tabla_izquierda);
                document.add(tabla_princial);
                document.newPage();
            }

            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            document.close();

            String fileName = "reporte_ventas-" + cadena_inicio.toString() + "_al_" + cadena_fin;

            writePDFToResponse(context.getExternalContext(), baos, fileName);
            context.responseComplete();

        } catch (Exception de) {
            de.printStackTrace();
        }
    }

    public void reporte_venta(Venta venta_) throws DocumentException, IOException {
        venta_ = ventaFacade.find(venta_.getIdVenta());

        FacesContext facexcontext = FacesContext.getCurrentInstance();
        ValueExpression vex = facexcontext.getApplication().getExpressionFactory().createValueExpression(facexcontext.getELContext(), "#{managedBeanLogin}", ManagedBeanLogin.class);
        ManagedBeanLogin beanLogin = (ManagedBeanLogin) vex.getValue(facexcontext.getELContext());

        FacesContext context = FacesContext.getCurrentInstance();

        Document document = new Document(PageSize.A5.rotate(), 20, 20, 20, 20);//int marginLeft,   int marginRight,   int marginTop,   int marginBottom

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        //writer.setPageEvent(new ManagedBeanReportes.Watermark(""));
        if (!document.isOpen()) {
            document.open();
        }

        try {

            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();

            PdfPTable tabla_princial = new PdfPTable(3);
            tabla_princial.setWidthPercentage(100);
            tabla_princial.setTotalWidth(520f);
            // table.setTotalWidth(540f);
            tabla_princial.setLockedWidth(true);
            // float[] headerWidths = {255,10,255};
            tabla_princial.setWidths(headerWidths);
            tabla_princial.getDefaultCell();

            PdfPTable tabla_izquierda = new PdfPTable(1);
            //float[] headerWidths_iz = {225};
            tabla_izquierda.setWidthPercentage(100);
            tabla_izquierda.setTotalWidth(225);
            tabla_izquierda.setLockedWidth(true);
            //tabla_izquierda.setWidths(headerWidths_iz);
            tabla_izquierda.getDefaultCell();

            BarcodeQRCode qrcode = new BarcodeQRCode(venta_.getIdVenta().toString(), 80, 80, null);
            Image qrcodeImage = qrcode.getImage();
            qrcodeImage.setAbsolutePosition(190f, 320f);
            document.add(qrcodeImage);

            BarcodeQRCode qrcode2 = new BarcodeQRCode(venta_.getIdVenta().toString(), 80, 80, null);
            Image qrcodeImage2 = qrcode2.getImage();
            qrcodeImage2.setAbsolutePosition(480f, 320f);
            document.add(qrcodeImage2);

            String logo_ = extContext.getRealPath("//resources//images//logo_mol.png");

            Image img_logo = Image.getInstance(logo_);
            img_logo.setAlignment(Image.ALIGN_LEFT);
            img_logo.scalePercent(25);
//img_logo.setBorder(Image.BOX); 
//img_logo.setBorderWidth(0); 
//img_logo.setBorderColor(BaseColor.BLACK);
            PdfPCell cell_logo = new PdfPCell(img_logo, false);
            cell_logo.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
            cell_logo.setColspan(1);
            cell_logo.setBorder(Rectangle.NO_BORDER);
            tabla_izquierda.addCell(cell_logo);

            PdfPCell cell_01 = new PdfPCell();
            cell_01 = new PdfPCell(new Paragraph("MOLINDUSTRIA \n DERIVADOS DE MAIZ Y OTROS", fondo_titulo));
            cell_01.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
            cell_01.setColspan(1);
            cell_01.setBorder(Rectangle.NO_BORDER);
            tabla_izquierda.addCell(cell_01);

            tabla_izquierda.addCell(construir_celda("CEL. 921841063/926325912/939713121/980147211/959117453 ", fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/YYYY");
            StringBuilder cadena = new StringBuilder(formato.format(venta_.getFechaVenta()));

            Chunk underline = new Chunk("FECHA DE VENTA: " + cadena.toString().toUpperCase() + "   ESTADO:" + venta_.getEstadoVenta().getNombreTestadoVenta().toUpperCase() + "      COD: " + venta_.getIdVenta(), fondo_denominacion);
            underline.setUnderline(0.2f, -2f); //0.1 thick, -2 y-location
            PdfPCell fecha = new PdfPCell(new Paragraph(underline));
            fecha.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
            fecha.setColspan(1);
            fecha.setBorder(Rectangle.NO_BORDER);
            tabla_izquierda.addCell(fecha);

            tabla_izquierda.addCell(construir_celda("SEÑOR(ES): " + venta_.getCliente().getNombreCliente(), fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));
            tabla_izquierda.addCell(construir_celda("DIRECCIÓN: " + venta_.getCliente().getZonaCiudad().getDescripcionZonaCiudad(), fondo_denominacion, Paragraph.ALIGN_LEFT, 1, 1));

            PdfPTable tabla_detalle_venta = new PdfPTable(4);
            // float[] headerWidths_detalle = {15,75,20,20};
            tabla_detalle_venta.setWidthPercentage(100);
            //tabla_detalle_venta.setLockedWidth(true);
            tabla_detalle_venta.setWidths(ancho_detalle);
            //tabla_detalle_venta.getDefaultCell();
            tabla_detalle_venta.addCell(construir_celda("CAN", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
            tabla_detalle_venta.addCell(construir_celda("DESCRIPCION", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
            tabla_detalle_venta.addCell(construir_celda("P.U", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
            tabla_detalle_venta.addCell(construir_celda("IMP.", fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
            int cant_ = 0;
            for (DetalleVentaProducto det : venta_.getDetalleVentaProductoList()) {
                cant_ = cant_ + det.getCantidad();
                tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getCantidad()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));

                String nombre_ = "";
                if (det.getProducto().getNombreProducto().length() > 25) {
                    nombre_ = det.getProducto().getNombreProducto().toLowerCase().substring(0, 25);
                } else {
                    nombre_ = det.getProducto().getNombreProducto().toLowerCase();
                }
                tabla_detalle_venta.addCell(construir_celda(nombre_, fondo_detalle_productos, Paragraph.ALIGN_LEFT, 1, 1));
                tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getPrecioVenta().doubleValue()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
                tabla_detalle_venta.addCell(construir_celda(String.valueOf(det.getSubTotal().doubleValue()), fondo_detalle_productos, Paragraph.ALIGN_CENTER, 1, 1));
            }

            for (PdfPCell cell_ : celdas_vacias(cantidad_detalle - venta_.getDetalleVentaProductoList().size())) {
                tabla_detalle_venta.addCell(cell_);
            }
            tabla_izquierda.addCell(tabla_detalle_venta);
            PdfPCell cell_denominacion = new PdfPCell();
            cell_denominacion = new PdfPCell(new Paragraph("SON : " + Utils.cantidadConLetra(venta_.getTotalVenta()) + " Soles.", fondo_denominacion));
            cell_denominacion.setHorizontalAlignment(Paragraph.ALIGN_LEFT);
            cell_denominacion.setColspan(1);
            cell_denominacion.setBorder(Rectangle.NO_BORDER);
            tabla_izquierda.addCell(cell_denominacion);

            tabla_izquierda.addCell(construir_celda("CANT. SACOS :  " + cant_ + "        MONTO TOTAL: S./ " + String.valueOf(venta_.getTotalVenta()), fondo_detalle_productos, Paragraph.ALIGN_RIGHT, 1, 1));

            PdfPCell tab_iz = new PdfPCell();
            tab_iz = new PdfPCell(tabla_izquierda);
            tab_iz.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            tab_iz.setColspan(1);
            tab_iz.setBorder(Rectangle.NO_BORDER);
            tab_iz.setPaddingLeft(-60);
            tabla_princial.addCell(tab_iz);

            PdfPTable central = new PdfPTable(1);
            //float[] headerWidths_iz = {225};
            central.setWidthPercentage(100);
            central.setTotalWidth(10);
            central.setLockedWidth(true);
            //tabla_izquierda.setWidths(headerWidths_iz);
            central.getDefaultCell();
            tabla_princial.addCell(central);

            PdfPCell tab_der = new PdfPCell();
            tab_der = new PdfPCell(tabla_izquierda);
            tab_der.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            tab_der.setColspan(1);
            tab_der.setBorder(Rectangle.NO_BORDER);
            tabla_princial.addCell(tab_der);

            tabla_princial.addCell(tabla_izquierda);
            document.add(tabla_princial);

            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            document.close();

            String fileName = "venta-" + venta_.getIdVenta();

            writePDFToResponse(context.getExternalContext(), baos, fileName);
            context.responseComplete();

        } catch (Exception de) {
            de.printStackTrace();
        }
    }

    public PdfPCell construir_celda(String datos, Font fuente_, int centrado, int border, int coldpan) {
        PdfPCell celda_ = new PdfPCell();
        celda_ = new PdfPCell(new Paragraph(datos, fuente_));
        celda_.setHorizontalAlignment(centrado);
        celda_.setColspan(coldpan);
        celda_.setBorder(border);
        return celda_;
    }

    public List<PdfPCell> celdas_vacias(int cantidad) {
        List<PdfPCell> resp = new LinkedList<PdfPCell>();
        PdfPCell celda_ = new PdfPCell();
        celda_ = new PdfPCell(new Paragraph("\n", fondo_detalle_productos));
        celda_.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        celda_.setColspan(1);
        celda_.setBorder(Rectangle.NO_BORDER);
        for (int i = 0; i < cantidad; i++) {
            resp.add(celda_);
            resp.add(celda_);
            resp.add(celda_);
            resp.add(celda_);
        }
        return resp;
    }

    public void inventario() throws DocumentException, IOException {
        FacesContext facexcontext = FacesContext.getCurrentInstance();
        ValueExpression vex = facexcontext.getApplication().getExpressionFactory().createValueExpression(facexcontext.getELContext(), "#{managedBeanLogin}", ManagedBeanLogin.class);
        ManagedBeanLogin beanLogin = (ManagedBeanLogin) vex.getValue(facexcontext.getELContext());

        FacesContext context = FacesContext.getCurrentInstance();

        Document document = new Document(PageSize.A4, 25, 25, 75, 25);//int marginLeft,   int marginRight,   int marginTop,   int marginBottom

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        writer.setPageEvent(new ManagedBeanReportes.Watermark(""));
        if (!document.isOpen()) {
            document.open();
        }

        try {

            ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
            //String imageUrl1 = extContext.getRealPath("//resources//images/logo0002.png");
            //Image welladigital = Image.getInstance(imageUrl1);
            //welladigital.setAbsolutePosition(377f, 760f);
            //welladigital.scalePercent(40);
            //document.add(welladigital);

            //crear tabla PARA NOMBRE DEL AÑO
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setTotalWidth(450f);
            // table.setTotalWidth(540f);
            table.setLockedWidth(true);
            float[] headerWidths = {120, 20, 310};
            table.setWidths(headerWidths);
            table.getDefaultCell();

            SimpleDateFormat formato = new SimpleDateFormat("EEEE dd MMMM YYYY");
            StringBuilder cadena = new StringBuilder(formato.format(fecha_inicio));

            Chunk underline = new Chunk("FECHA DE INVENTARIO:" + cadena.toString().toUpperCase(), bigFont12);
            underline.setUnderline(0.2f, -2f); //0.1 thick, -2 y-location
            PdfPCell table5 = new PdfPCell(new Paragraph(underline));
            table5.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
            table5.setColspan(3);
            table5.setBorder(Rectangle.NO_BORDER);
            table.addCell(table5);

            document.add(table);

            document.add(new Paragraph("\n", pequeFont));

            PdfPCell table2 = new PdfPCell();

            table2 = new PdfPCell(new Paragraph(beanLogin.getObjetoEmpleado().getTienda().getNombreTienda(), pequeFont));
            table2.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
            table2.setColspan(3);
            table2.setBorder(Rectangle.NO_BORDER);
            table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setTotalWidth(450f);
            table.setLockedWidth(true);
            table.setWidths(headerWidths);
            table.getDefaultCell();
            table.addCell(table2);
            document.add(table);

            document.add(new Paragraph("\n", pequeFont));

            document.add(traerSubtabla(beanLogin.getObjetoEmpleado().getTienda()));
            formato = new SimpleDateFormat("yyyy-MM-dd");
            cadena = new StringBuilder(formato.format(fecha_inicio));

            //document.add(traerSubtabla02(cadena.toString()));
            document.add(new Paragraph("\n", pequeFont));
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String ctxPath = ((ServletContext) ctx.getContext()).getContextPath();
            document.close();
            formato = new SimpleDateFormat("dd_MM_yyyy");
            cadena = new StringBuilder(formato.format(fecha_inicio));
            String fileName = cadena.toString();

            writePDFToResponse(context.getExternalContext(), baos, fileName);
            context.responseComplete();

        } catch (Exception de) {
            de.printStackTrace();
        }
    }

    public class Watermark extends PdfPageEventHelper {

        PdfTemplate total;
        private String encabezado;
        protected Phrase watermark = new Phrase(" ",
                new Font(Font.FontFamily.HELVETICA, 8.0F, 0, new BaseColor(200, 200, 200)));

        public Watermark(String codHecho) {

            encabezado = codHecho;
        }

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 16);
        }

        @Override
        public void onStartPage(PdfWriter writer, Document document) {
            //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Inicio de Pagina - El lado oscuro de Java"), 200, 830, 0);

            try {
                /* Image qrcodeImage = codigoQr.getImage();
                 qrcodeImage.setAbsolutePosition(445f, 745f);
                 document.add(qrcodeImage);
                 ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
                 String imageUrl1 = extContext.getRealPath("//resources//images//DEFAULT//membretepnp.jpg");
                 Image welladigital = Image.getInstance(imageUrl1);
                 welladigital.setAbsolutePosition(25f, 745f);
                 welladigital.scalePercent(40);
                 document.add(welladigital);
                 Paragraph parrafo = new Paragraph("CARP. POL. N°: " + encabezado, titulopequeFont);
                 agregaLineaText("CARP. POL. N°: " + encabezado, 412f, 747f, writer, 8);*/

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onCloseDocument(PdfWriter writer, Document document) {
            // ColumnText.showTextAligned(total, Element.ALIGN_LEFT, new Phrase(String.valueOf(writer.getPageNumber() - 1)), 2, 2, 0);
        }

        // Getter and Setters
        public String getEncabezado() {
            return encabezado;
        }

        public void setEncabezado(String encabezado) {
            this.encabezado = encabezado;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            //ColumnText.showTextAligned(canvas, 1, this.watermark, 298.0F, 421.0F, 45.0F);
            for (int fila = 0; fila < 50; fila++) {
                float dataFila = (float) fila * 50;

                for (int i = 0; i < 17; i++) {
                    float data = (float) i * 40;
                    //ColumnText.showTextAligned(canvas, 1, this.watermark, data, 400.0F, 40.0F);
                    //ColumnText.showTextAligned(canvas, 1, this.watermark, data, dataFila, 40.0F);
                }

            }

        }
    }

    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            externalContext.setResponseHeader("Pragma", "public");
            externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            externalContext.setResponseContentLength(baos.size());
            OutputStream out = externalContext.getResponseOutputStream();
            baos.writeTo(out);
            externalContext.responseFlushBuffer();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public PdfPTable traerSubtabla(Tienda t) throws DocumentException {
        PdfPTable tabla_PesajeDetalle = new PdfPTable(4);
        tabla_PesajeDetalle.setWidthPercentage(100);
        tabla_PesajeDetalle.setTotalWidth(450f);
        tabla_PesajeDetalle.setLockedWidth(true);

        tabla_PesajeDetalle.setWidths(anchocol03);
        tabla_PesajeDetalle.getDefaultCell();

        PdfPCell Cell_Headers = new PdfPCell(new Paragraph("PRODUCTO", titulopequeFont));
        Cell_Headers.setBackgroundColor(BaseColor.LIGHT_GRAY);
        Cell_Headers.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        Cell_Headers.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);

        tabla_PesajeDetalle.addCell(Cell_Headers);

        Cell_Headers = new PdfPCell(new Paragraph("UBICACIONES", titulopequeFont));
        Cell_Headers.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        Cell_Headers.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        Cell_Headers.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabla_PesajeDetalle.addCell(Cell_Headers);

        Cell_Headers = new PdfPCell(new Paragraph("MINIMO", titulopequeFont));
        Cell_Headers.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        Cell_Headers.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        Cell_Headers.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabla_PesajeDetalle.addCell(Cell_Headers);

        Cell_Headers = new PdfPCell(new Paragraph("STOCK TOTAL", titulopequeFont));
        Cell_Headers.setBackgroundColor(BaseColor.LIGHT_GRAY);
        Cell_Headers.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        Cell_Headers.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        tabla_PesajeDetalle.addCell(Cell_Headers);

        for (StockProductoTiendaOrigen det : stockProductoTiendaOrigenFacade.lista_stock_tienda(t)) {
            tabla_PesajeDetalle.addCell(traerCelda(det.getProducto().getNombreProducto()));
            String ubicaciones_ = "";
            for (DetalleAlmacenProductos p : detalleAlmacenProductosFacade.lista_para_stock_tienda(t, det.getProducto())) {
                if (p.getUbicacionFisica().getIdUbicacionFisica() > 1) {
                    ubicaciones_ = p.getUbicacionFisica().getNombreUbicacionFisica() + ";" + ubicaciones_;
                }
            }
            tabla_PesajeDetalle.addCell(traerCelda(ubicaciones_));
            tabla_PesajeDetalle.addCell(traerCelda(String.valueOf(det.getCantidadMinimaStock())));
            tabla_PesajeDetalle.addCell(traerCelda(String.valueOf(det.getCantidad())));
        }
        return tabla_PesajeDetalle;
    }

    public PdfPCell traerCelda(String data) {
        PdfPCell resultado = new PdfPCell(new Paragraph(data, titulopequeFont));
        resultado.setHorizontalAlignment(Paragraph.ALIGN_CENTER);
        resultado.setVerticalAlignment(Paragraph.ALIGN_MIDDLE);
        return resultado;
    }
}
