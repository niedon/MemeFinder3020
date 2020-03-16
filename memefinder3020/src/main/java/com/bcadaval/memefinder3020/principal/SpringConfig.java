package com.bcadaval.memefinder3020.principal;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bcadaval.memefinder3020.utils.Constantes;

@Configuration
@EnableTransactionManagement
public class SpringConfig {

	@Bean
	public GestorDeVentanas gestorDeVentanas() {
		return new GestorDeVentanas();
	}
	
	@Bean
	public SpringFxmlLoader springFxmlLoader() {
		return new SpringFxmlLoader();
	}
	
	//---------------------------------------------------
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		
		String url = "jdbc:hsqldb:file:" + Constantes.RUTA_BD;
		dataSource.setUrl(url);
		
		dataSource.setUsername("SA");
		dataSource.setPassword("");
		return dataSource;
		
	}
	

	@Bean
	public EntityManager entityManager() {
		return entityManagerFactory().createEntityManager();
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory() {
		
	      LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
	      em.setDataSource(dataSource());
	      em.setPackagesToScan("com.bcadaval.memefinder3020.modelo.beans");     
	      JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
	      em.setJpaVendorAdapter(vendorAdapter);
	      em.setPersistenceUnitName("default");
	      em.afterPropertiesSet();
	      return em.getObject();
	   }
	
}
