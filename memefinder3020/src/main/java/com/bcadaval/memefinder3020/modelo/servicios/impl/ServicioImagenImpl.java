package com.bcadaval.memefinder3020.modelo.servicios.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import com.bcadaval.memefinder3020.excepciones.ConstraintViolationException;
import com.bcadaval.memefinder3020.excepciones.NotFoundException;
import com.bcadaval.memefinder3020.modelo.beans.Categoria;
import com.bcadaval.memefinder3020.modelo.beans.Etiqueta;
import com.bcadaval.memefinder3020.modelo.beans.Imagen;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusqueda;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenBusquedaExportar;
import com.bcadaval.memefinder3020.modelo.beans.temp.ImagenTemp;
import com.bcadaval.memefinder3020.modelo.servicios.Servicio;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioCategoria;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioEtiqueta;
import com.bcadaval.memefinder3020.modelo.servicios.ServicioImagen;
import com.bcadaval.memefinder3020.utils.Constantes;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@Service
public class ServicioImagenImpl extends Servicio implements ServicioImagen {

	private static final Logger log = LogManager.getLogger(ServicioImagenImpl.class);
	
	@Autowired private RutasUtils rutasUtils;
	
	@Autowired private ServicioEtiqueta servicioEtiqueta;
	@Autowired private ServicioCategoria servicioCategoria;
	
	@Override
	public List<Imagen> getAll() {
		
		log.debug(".getAll() - Iniciando recuperación de todas las imágenes");
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
        cq.select(root);
        
        List<Imagen> resultados = em.createQuery(cq).getResultList();
        log.debug(".getAll() - Finalizada recuperación de todas las imágenes: " + resultados.size());
        return resultados;
	}
	
	@Override
	public List<Imagen> getAllPorId(Collection<Integer> listaId) throws ConstraintViolationException{
		
		log.debug(".getAllPorId() - Iniciando recuperación de imágenes por ID");
		
		if(listaId==null) {
			log.error(".getAllPorId() - La lista es nula");
			throw new ConstraintViolationException("Lista nula");
		}else if(listaId.isEmpty()) {
			log.warn(".getAllPorId() - La lista está vacía");
			return Collections.emptyList();
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
        Root<Imagen> root = cq.from(Imagen.class);
        
        In<Integer> pIn = cb.in(root.get("id"));
        for(Integer id : listaId) {
        	pIn.value(id);
        }
        
        cq.select(root).where(pIn);
        
        List<Imagen> resultados = em.createQuery(cq).getResultList();
        log.debug(".getAllPorId() - Finalizada recuperación de imágenes por ID: " + resultados.size());
        return resultados;
		
	}
	
	@Override
	public List<Integer> getAllIds() {
		
		log.debug(".getAllIds() - Iniciando recuperación de todas las ID");
		
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
		Root<Imagen> root = cq.from(Imagen.class);
		
		cq.select(root.get("id"));
		
		List<Integer> resultados = em.createQuery(cq).getResultList();
		log.debug(".getAllIds() - Finalizada recuperación de ID de imágenes: " + resultados.size());
		return resultados;
		
	}
	
	@Override
	public Page<Imagen> getBusqueda(ImagenBusqueda busqueda, Pageable pageable) throws ConstraintViolationException {
		
		log.debug(".getBusqueda() - Iniciando recuperación de imágenes por búsqueda");
		
		if(busqueda==null) {
			log.error(".getBusqueda() - ImagenBusqueda null");
			throw new ConstraintViolationException("Objeto de búsqueda nulo");
		}
		if(pageable==null) {
			log.error(".getBusqueda() - Pageable null");
			throw new ConstraintViolationException("Pageable nulo");
		}
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		log.debug(".getBusqueda() - ================ CONDICIONES ================");
		
		//Con nombre %X%
		if(busqueda.getNombre() != null && !busqueda.getNombre().trim().isEmpty()) {
			predicados.add(cb.like(cb.upper(root.get("nombre")), '%'+busqueda.getNombre().trim().toUpperCase()+'%'));
			log.debug(".getBusqueda() - NOMBRE: " + busqueda.getNombre().trim().toUpperCase());
		}
		
		//Sin categoría
		if(busqueda.isBuscarSinCategoria()) {
			predicados.add(cb.isNull(root.get("categoria")));
			log.debug(".getBusqueda() - CATEGORÍA: [imágenes sin categoría]");
		//Con categoría X
		}else if(busqueda.getCategoria()!=null) {
			predicados.add(cb.equal(root.get("categoria"), busqueda.getCategoria().getId()));
			log.debug(".getBusqueda() - CATEGORÍA: " + busqueda.getCategoria().getNombre());
		}
		
		//Después de X
		if(busqueda.getFechaDespues()!=null) {
			predicados.add(cb.greaterThan(root.get("fecha"), busqueda.getFechaDespues()));
			log.debug(".getBusqueda() - DESPUÉS DE: " + busqueda.getFechaDespues().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		}
		
		//Antes de X
		if(busqueda.getFechaAntes()!=null) {
			predicados.add(cb.lessThan(root.get("fecha"), busqueda.getFechaAntes()));
			log.debug(".getBusqueda() - ANTES DE: " + busqueda.getFechaAntes().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		}
		
		//Sin etiquetas
		if(busqueda.isBuscarSinEtiquetas()) {
			predicados.add(cb.isEmpty(root.get("etiquetas")));
			log.debug(".getBusqueda() - ETIQUETAS: [imágenes sin etiqueta]");
		}else {
			
			//Con al menos tales etiquetas
			if(busqueda.getEtiquetas()!=null) {
				ArrayList<String> listaEtiquetasLog = new ArrayList<String>(busqueda.getEtiquetas().size());
				for(Etiqueta et : busqueda.getEtiquetas()) {
					listaEtiquetasLog.add(et.getNombre());
					predicados.add(cb.isMember(et, root.get("etiquetas")));
				}
				log.debug(".getBusqueda() - ETIQUETAS: " + listaEtiquetasLog);
			}
		}
		
		log.debug(".getBusqueda() - =============================================");
		
		Predicate[] predicadosArray = predicados.toArray(new Predicate[predicados.size()]);
		
		cq.select(root).where(predicadosArray);
		List<Imagen> resultado =  em.createQuery(cq).setMaxResults(pageable.getPageSize()).setFirstResult((int)pageable.getOffset()).getResultList();
		
		CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
		Root<Imagen> rootCount = countQuery.from(Imagen.class);
		countQuery.select(cb.count(rootCount)).where(cb.and(predicadosArray));
		
		Long count = em.createQuery(countQuery).getSingleResult();
		
		Page<Imagen> resultados = new PageImpl<>(resultado, pageable, count);
		log.debug(".getBusqueda() - Finalizada recuperación de imágenes por búsqueda: " + count);
		return resultados;
		
	}
	
	@Override
	public List<Imagen> getBusquedaExportar(ImagenBusquedaExportar busqueda) throws ConstraintViolationException {
		
		log.debug(".getBusquedaExportar() - Iniciando recuperación de imágenes para exportar");
		
		if(busqueda==null) {
			log.error(".getBusquedaExportar() - ImagenBusqueda null");
			throw new ConstraintViolationException("Objeto de búsqueda nulo");
		}
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		List<Predicate> predicados = new ArrayList<Predicate>();
		
		log.debug(".getBusquedaExportar() - ================ CONDICIONES ================");
		if(busqueda.isTodasMenos()) {
			log.debug(".getBusquedaExportar() - TODOS LOS RESULTADOS EXCEPTO:");
		}else {
			log.debug(".getBusquedaExportar() - SOLO RESULTADOS QUE COINCIDAN CON:");
		}
		
		//Etiquetas
		if(busqueda.getEtiquetas()!=null && ! busqueda.getEtiquetas().isEmpty()) {
			
			ArrayList<String> etiquetasLog = new ArrayList<String>(busqueda.getEtiquetas().size());
			
			for(String s : busqueda.getEtiquetas()) {
				
				try {
					Etiqueta et = servicioEtiqueta.getPorNombre(s);
					
					if(busqueda.isTodasMenos()) {
						predicados.add(cb.isNotMember(et, root.get("etiquetas")));
					}else {
						predicados.add(cb.isMember(et,  root.get("etiquetas")));
					}
					
					etiquetasLog.add(et.getNombre());
				} catch (ConstraintViolationException | NotFoundException e) {
					log.error(".getBusquedaExportar() - Etiqueta no encontrada: " + s, e.getMessage());
					continue;
				}
			}
			
			log.debug(".getBusquedaExportar() - ETIQUETAS: " + etiquetasLog);
			
		}
		
		//Categoría
		if(busqueda.getCategoria()!=null) {
			
			if(busqueda.isTodasMenos()) {
				predicados.add(cb.notEqual(root.get("categoria"), busqueda.getCategoria().getId()));
			}else {
				predicados.add(cb.equal(root.get("categoria"), busqueda.getCategoria().getId()));
			}
			
			log.debug(".getBusquedaExportar() - CATEGORÍA: " + busqueda.getCategoria().getNombre());
		}
		
		//Después de X
		if(busqueda.getDespuesDe() != null) {
			
			if(busqueda.isTodasMenos()) {
				predicados.add(cb.not(cb.greaterThan(root.get("fecha"), busqueda.getDespuesDe())));
			}else {
				predicados.add(cb.greaterThan(root.get("fecha"), busqueda.getDespuesDe()));
			}
			
			log.debug(".getBusqueda() - DESPUÉS DE: " + busqueda.getDespuesDe().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		}
		
		//Antes de X
		if(busqueda.getAntesDe() != null) {
			
			if(busqueda.isTodasMenos()) {
				predicados.add(cb.not(cb.lessThan(root.get("fecha"), busqueda.getAntesDe())));
			}else {
				predicados.add(cb.lessThan(root.get("fecha"), busqueda.getAntesDe()));
			}
			log.debug(".getBusqueda() - ANTES DE: " + busqueda.getAntesDe().format(DateTimeFormatter.ofPattern(Constantes.FORMAT_DDMMYYHHMM)));
		}
		
		Predicate[] predicadosArray = predicados.toArray(new Predicate[predicados.size()]);
		
		cq.select(root).where(predicadosArray);
		List<Imagen> resultado =  em.createQuery(cq).getResultList();
		
		log.debug(".getBusqueda() - Finalizada recuperación de imágenes por búsqueda: " + resultado.size());
		return resultado;
		
	}
	
	@Override
	public Imagen getPorId(Integer id) throws ConstraintViolationException, NotFoundException {
		
		log.debug(".getPorId() - Iniciando recuperación de imagen por ID: " + id);
		
		if(id==null) {
			log.error(".getPorId() - ID nula");
			throw new ConstraintViolationException("ID nula");
		}
		
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		final Root<Imagen> root = cq.from(Imagen.class);
		
		Predicate p = cb.equal(root.get("id"), id);
		
		cq.select(root).where(p);
		
		try {
			log.debug(".getPorId() - Finalizada recuperación de imagen por ID");
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			log.debug(".getPorId() - Finalizada recuperación de imagen por ID (no encontrada)");
			throw new NotFoundException();
		}
		
	}
	
	@Override
	public List<Imagen> getUltimas(int num) throws ConstraintViolationException {
		
		log.debug(".getUltimas() - Iniciando recuperación de últimas imágenes");
		
		if(num<1) {
			log.error(".getUltimas() - Se ha introducido un valor menor que 1: " + num);
			throw new ConstraintViolationException("No se permiten valores menores que 1");
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		Root<Imagen> root = cq.from(Imagen.class);
		cq.orderBy(cb.desc(root.get("fecha")));
		
		List<Imagen> resultados = em.createQuery(cq).setMaxResults(num).getResultList();
		log.debug(".getUltimas() - Finalizada recuperación de las últimas " + num + " imágenes: " + resultados.size());
		return resultados;
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Imagen anadir(ImagenTemp iTemp) throws ConstraintViolationException {
		
		log.debug(".anadir() - Iniciando adición de imagen");
		
		if(iTemp==null) {
			log.error(".anadir() - ImagenTemp nula");
			throw new ConstraintViolationException("Se ha recibido una imagen vacía");
		}
		
		File archivoImagen = iTemp.getImagen();
		validarArchivoImagen(archivoImagen);
		
		Imagen img = new Imagen();
		String nombre = iTemp.getNombre();
		if(nombre==null || nombre.trim().isEmpty()) {
			//Siempre tendrá . porque se comprueba al principio de este método
			nombre = iTemp.getImagen().getName().substring(0,iTemp.getImagen().getName().indexOf('.'));
		}else {
			nombre = nombre.trim();
		}
		img.setNombre(nombre.length()>64 ? nombre.substring(0,64) : nombre);
		img.setExtension(archivoImagen.getName().substring(archivoImagen.getName().lastIndexOf('.')+1));
		if(iTemp.getCategoria()!=null &&  ! iTemp.getCategoria().isEmpty()) {
			img.setCategoria(servicioCategoria.getOCrear(iTemp.getCategoria()));
		}
		img.setFecha(LocalDateTime.now());
		
		iTemp.getEtiquetasString().forEach(el -> {
			try {
				img.getEtiquetas().add(servicioEtiqueta.getOCrear(el));
			} catch (ConstraintViolationException e) {
				log.error(".anadir() - Etiqueta inválida: " + el);
			}
		});
		
		em.persist(img);
		
		File copiada = rutasUtils.getFileDeImagen(img);
		try {
			copiada.getParentFile().mkdirs();
			FileSystemUtils.copyRecursively(archivoImagen, copiada);
		} catch (IOException e1) {
			log.error(".anadir() - Error copiando File");
			throw new ConstraintViolationException("Error copiando el archivo de imagen");
		}
		
		em.flush();
		
		Imagen anadida = em.merge(img);
		log.debug(".anadir() - Finalizada adición de imagen");
		return anadida;
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public Imagen editar(Imagen imagen) {
		
		log.debug(".editar() - Iniciando edición de imagen");
		Imagen im = em.merge(imagen);
		log.debug(".editar() - Finalizada edición de imagen");
		return im;
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Imagen imagen) {
		
		log.debug(".eliminar() - Iniciando eliminación de imagen");
		
		File f = rutasUtils.getFileDeImagen(imagen);
		
		em.remove(em.contains(imagen) ? imagen : em.merge(imagen));
		
		if( ! FileSystemUtils.deleteRecursively(f)) {
			log.error(".eliminar() - No se ha podido eliminar el arhcivo de imagen: " + f.getAbsolutePath());
		}
		
		log.debug(".eliminar() - Finalizada eliminación de imagen");
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void eliminar(Integer id) throws ConstraintViolationException {
		
		log.debug(".eliminar() - Iniciando eliminación de imagen por ID");
		
		if(id==null) {
			log.error(".eliminar() - ID nula");
			throw new ConstraintViolationException("ID null");
		}
		
		try {
			
			log.debug(".eliminar() - ID: " + id);
			
			eliminar(getPorId(id));
			
		} catch (NotFoundException e) {
			log.error(".eliminar() - La imagen no existe");
			throw new ConstraintViolationException("La imagen no existe");
		}
		
		
	}
	
	@Override
	public void sustituirImagen(File fNueva, Imagen imgOriginal) throws ConstraintViolationException {
		
		log.debug(".sustituirImagen() - Iniciando sustitución de imagen");
		
		validarArchivoImagen(fNueva);
		if(imgOriginal==null) {
			log.error(".sustituirImagen() - Imagen nula");
		}
		
		try {
			
			Path rutaArchivo = FileSystems.getDefault().getPath(
					rutasUtils.RUTA_IMAGENES_AC,
					String.format("%d.%s", imgOriginal.getId(),imgOriginal.getExtension()));
			
			Files.copy(fNueva.toPath(), rutaArchivo,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			log.error(".sustituirImagen() - Excepción I/O: " + e.getMessage());
			throw new ConstraintViolationException("Error copiando la imagen");
		}catch(InvalidPathException e) {
			log.error(".sustituirImagen() - Ruta de imagen inválida: " + e.getMessage());
			throw new ConstraintViolationException("Ruta de mensaje inválida");
		}
		
		log.debug(".sustituirImagen() - Finalizada sustitución de imagen");
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void borrarPorId(Collection<Integer> ids) throws ConstraintViolationException {
		
		log.debug(".borrarPorId() - Iniciando eliminación de imágenes");
		
		if(ids==null || ids.isEmpty()) {
			log.error(".borrarPorId() - Lista null");
			throw new ConstraintViolationException("La lista de imágenes no puede estar vacía");
		}
		
		log.debug(".borrarPorId() - ID de imágenes a borrar: " + ids);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaDelete<Imagen> cd = cb.createCriteriaDelete(Imagen.class);
		Root<Imagen> root = cd.from(Imagen.class);
		
		cd.where(root.get("id").in(ids));
		
		int borradas = em.createQuery(cd).executeUpdate();
		
		log.debug(".borrarPorId() - Finalizada eliminación de imágenes: " + borradas);
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void fusionarCategorias(List<Categoria> categorias, String nuevoNombre) throws ConstraintViolationException {
		
		log.debug(".fusionarCategorias() - Iniciando fusión de categorías");
		
		if(categorias==null || categorias.size() < 2) {
			log.error(".fusionarCategorias() - No hay categorías que fusionar");
			throw new ConstraintViolationException("La lista de categorías no puede estar vacía");
		}
		
		Categoria categoriaMasAntigua = categorias.get(0);
		for(Categoria c : categorias) {
			if(c.getId() < categoriaMasAntigua.getId()) {
				categoriaMasAntigua = c;
			}
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaUpdate<Imagen> cu = cb.createCriteriaUpdate(Imagen.class);
		Root<Imagen> root = cu.from(Imagen.class);
		
		List<Predicate> listaOr = new ArrayList<Predicate>();
		for(Categoria c : categorias) {
			if(c != categoriaMasAntigua) {
				listaOr.add(cb.equal(root.get("categoria"), c));
			}
		}
		
		Predicate predicateOr;
		
		if(listaOr.size()==1) {
			predicateOr = listaOr.get(0);
		}else {
			predicateOr = cb.or(listaOr.toArray(new Predicate[listaOr.size()]));
		}
		
		cu.set(root.get("categoria"), categoriaMasAntigua);
		cu.where(predicateOr);
		
		int alteradas = em.createQuery(cu).executeUpdate();
		
		log.debug(".fusionarCategorias() - Imágenes alteradas: " + alteradas);
		
		List<Categoria> nuevasCat = new ArrayList<Categoria>(categorias.size());
		categorias.forEach(el -> nuevasCat.add(em.find(Categoria.class, el.getId())));
		
		for(Categoria c : nuevasCat) {
			if(c.getId() != categoriaMasAntigua.getId()) {
				servicioCategoria.eliminar(c);
			}
		}
		
		boolean llamarAlServicio = true;
		try {
			nuevoNombre = validarNombre(nuevoNombre);
		}catch (ConstraintViolationException e) {
			log.error(".fusionarCategorias() - Error validando nombre de categoría");
			llamarAlServicio = false;
		}
		if(llamarAlServicio) {
			servicioCategoria.editar(categoriaMasAntigua, nuevoNombre);
		}
		
		log.debug(".fusionarCategorias() - Finalizada fusión de categorías");
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void borrarPorCategoria(Categoria cat) throws ConstraintViolationException {
		
		log.debug(".borrarPorCategoria() - Iniciando borrado por categoría");
		
		if(cat==null) {
			log.error(".borrarPorCategoria() - Categoría nula");
			throw new ConstraintViolationException("Categoría nula");
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Imagen> querySelect = cb.createQuery(Imagen.class);
		Root<Imagen> rootSelect = querySelect.from(Imagen.class);
		Predicate pSelect = cb.equal(rootSelect.get("categoria"), cat);
		querySelect.where(pSelect);
		List<Imagen> listaSelect = em.createQuery(querySelect).getResultList();

		if(listaSelect.isEmpty()) {
			log.debug(".borrarPorCategoria() - Finalizado borrado por categoría (no hay imágenes en estacategoría)");
			return;
		}
		
		CriteriaDelete<Imagen> cd = cb.createCriteriaDelete(Imagen.class);
		Root<Imagen> root = cd.from(Imagen.class);
		
		Predicate p = cb.equal(root.get("categoria"), cat);
		
		cd.where(p);
		
		int borradas = em.createQuery(cd).executeUpdate();
		log.debug(".borrarPorCategoria() - Imágenes borradas: " + borradas);
		
		listaSelect.forEach(el -> {
			File f = rutasUtils.getFileDeImagen(el);
			if( ! FileSystemUtils.deleteRecursively(f)) {
				log.error(".borrarPorCategoria() - No se ha podido borrar archivo: " + f.getAbsolutePath());
			}
		});
		
		log.debug(".borrarPorCategoria() - Finalizado borrado por categoría");
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void fusionarEtiquetas(List<Etiqueta> etiquetasNoMerge, String nuevoNombre) throws ConstraintViolationException{
		
		log.debug(".fusionarEtiquetas() - Iniciando fusión de etiquetas");
		
		if(etiquetasNoMerge==null || etiquetasNoMerge.size() < 2) {
			log.error(".fusionarEtiquetas() - No hay etiquetas que fusionar: " + etiquetasNoMerge.size());
			throw new ConstraintViolationException("La lista de etiquetas no puede estar vacía");
		}
		
		//Se hace merge a las etiquetas para evitar errores de persistencia
		List<Etiqueta> etiquetas = new ArrayList<Etiqueta>(etiquetasNoMerge.size());
		etiquetasNoMerge.forEach(et -> etiquetas.add(em.contains(et) ? et : em.merge(et)));
		
		//Se aísla la más antigua para fusionarlas todas en esta
		Etiqueta etiquetaMasAntigua = etiquetas.get(0);
		for(Etiqueta et : etiquetas) {
			if(et.getId() < etiquetaMasAntigua.getId()) {
				etiquetaMasAntigua = et;
			}
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Imagen> cq = cb.createQuery(Imagen.class);
		Root<Imagen> root = cq.from(Imagen.class);
		
		ArrayList<Etiqueta> etiquetasSinLaAntigua = new ArrayList<Etiqueta>(etiquetas);
		etiquetasSinLaAntigua.remove(etiquetaMasAntigua);
		
		ArrayList<Predicate> predicadosOr = new ArrayList<Predicate>(etiquetas.size()-1);
		
		etiquetasSinLaAntigua.forEach(et -> predicadosOr.add(cb.isMember(et, root.get("etiquetas"))));
		
		Predicate predicateOr;
		
		if(predicadosOr.size()==1) {
			predicateOr = predicadosOr.get(0);
		}else {
			predicateOr = cb.or(predicadosOr.toArray(new Predicate[predicadosOr.size()]));
		}
		
		cq.where(predicateOr);
		
		//  Todas las imágenes que tienen alguna de las etiquetas menos la antigua
		//  (se exceptúan las que tienen solo la antigua, ya que esta será
		//  editada mediante servicioEtiqueta)
		List<Imagen> resultados = em.createQuery(cq).getResultList();
		
		for(Imagen img : resultados) {
			img.getEtiquetas().removeAll(etiquetasSinLaAntigua);
			img.getEtiquetas().add(etiquetaMasAntigua);
		}
		
		
		boolean llamarAlServicio = true;
		try {
			nuevoNombre = validarNombre(nuevoNombre);
		} catch (ConstraintViolationException e) {
			log.error(".fusionarEtiquetas() - Error validando nombre de etiqueta");
			llamarAlServicio = false;
		}
		
		if(llamarAlServicio) {
			servicioEtiqueta.editar(etiquetaMasAntigua, nuevoNombre);
		}else {
			//Puedes encontrarte con alguien haciendo caca
		}
		
		log.debug(".fusionarEtiquetas() - Finalizada fusión de etiquetas");
		
	}
	
	@Override
	@Transactional(rollbackOn = Exception.class)
	public void borrarPorEtiqueta(Etiqueta et) throws ConstraintViolationException{
		
		log.debug(".borrarPorEtiqueta() - Iniciando borrado por etiqueta");
		
		if(et == null) {
			log.error(".borrarPorEtiqueta() - Etiqueta nula");
			throw new ConstraintViolationException("Etiqueta nula");
		}
		
		et = em.contains(et) ? et : em.merge(et);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		
		CriteriaQuery<Imagen> querySelect = cb.createQuery(Imagen.class);
		Root<Imagen> rootSelect = querySelect.from(Imagen.class);
		Predicate pSelect = cb.isMember(et, rootSelect.get("etiquetas"));
		querySelect.where(pSelect);
		
		List<Imagen> listaSelect = em.createQuery(querySelect).getResultList();
		if(listaSelect.isEmpty()) {
			log.debug(".borrarPorEtiqueta() - Finalizado borrado por etiqueta (no hay imágenes con esta etiqueta)");
			return;
		}
		
		Set<Etiqueta> setEtiquetasTotales = new HashSet<Etiqueta>(); 
		
		CriteriaDelete<Imagen> cd = cb.createCriteriaDelete(Imagen.class);
		Root<Imagen> root = cd.from(Imagen.class);
		In<Integer> clausulaIn = cb.in(root.get("id"));
		for(Imagen img : listaSelect) {
			clausulaIn = clausulaIn.value(img.getId());
			setEtiquetasTotales.addAll(img.getEtiquetas());
		}
		
		cd.where(clausulaIn);
		
		int borradas = em.createQuery(cd).executeUpdate();
		log.debug(".borrarPorEtiqueta() - Imágenes borradas: " + borradas);
		
		setEtiquetasTotales.forEach(el -> servicioEtiqueta.check(el));
		
		listaSelect.forEach(el -> {
			File f = rutasUtils.getFileDeImagen(el);
			if( ! FileSystemUtils.deleteRecursively(f)) {
				log.error(".borrarPorEtiqueta() - No se ha podido borrar archivo: " + f.getAbsolutePath());
			}
		});
		
		log.debug(".borrarPorEtiqueta() - Finalizado borrado por etiqueta");
		
	}
	
}
