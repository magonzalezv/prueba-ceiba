package dominio;

import dominio.repositorio.RepositorioProducto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import dominio.GarantiaExtendida;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) throws GarantiaExtendidaException {
    
    	// Validar si el producto tiene garantia
        if(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) != null) {
        	 throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
        }
        
        	String codigoMinuscula = codigo.toLowerCase();
        	
        	int contadorVocales = 0;

        	// Contar el número de vocales que tiene el codigo
            for (int i = 0; i < codigo.length(); i++){
            	if((codigoMinuscula.charAt(i)=='a') || (codigoMinuscula.charAt(i)=='e') || (codigoMinuscula.charAt(i)=='i') || (codigoMinuscula.charAt(i)=='o' || (codigoMinuscula.charAt(i)=='u'))) {
            		++contadorVocales;
            	}
            }
            
            // Validar si el codigo tiene 3 vocales
            if (contadorVocales  == 3) {
            	throw new GarantiaExtendidaException("Este producto no cuenta con garantía extendida");
            } 
            
            	Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
            	double precioProducto = producto.getPrecio();
                double precioGarantia = 0;
                Date fechaSolicitudGarantia = new Date();
                Date fechaFinGarantia;
                
                if( precioProducto > 500000) {
                	
                	precioGarantia = precioProducto * 0.20;
                	
                	fechaFinGarantia = calcularFechaFinalGarantia(fechaSolicitudGarantia, 200, precioProducto);
                	
                }else {
                	precioGarantia = precioProducto * 0.10;
                	fechaFinGarantia = calcularFechaFinalGarantia(fechaSolicitudGarantia, 200, precioProducto);
                }
                
                GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaSolicitudGarantia, fechaFinGarantia, precioGarantia, nombreCliente);
                
                repositorioGarantia.agregar(garantia);
            
        
        
    }

    public boolean tieneGarantia(String codigo) {
    	boolean tieneGarantia;
    	if (repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) !=null) {
    		tieneGarantia = true;
    	} else {
    		tieneGarantia = false;
    	}
        return tieneGarantia;
    }
    
    
    // Añadir los 200 dias a fechaFinGarantia
    public Date calcularFechaFinalGarantia(Date fechaSolicitudGarantia, int diasHabiles, double costoProducto) {
    
    	LocalDate fechaFinGarantia = fechaSolicitudGarantia.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	if (costoProducto > 500000) {
    		int diasSumados = 1;
            while (diasSumados < diasHabiles) {
            	fechaFinGarantia = fechaFinGarantia.plusDays(1);
                if (!(fechaFinGarantia.getDayOfWeek().equals(DayOfWeek.MONDAY))) {
                    ++diasSumados;
                }
            }
            
            if (fechaFinGarantia.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            	fechaFinGarantia = fechaFinGarantia.plusDays(1);
            }
    	} else {
    		fechaFinGarantia = fechaFinGarantia.plusDays(100);
    	}
       
        
        
        

        return Date.from(fechaFinGarantia.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
