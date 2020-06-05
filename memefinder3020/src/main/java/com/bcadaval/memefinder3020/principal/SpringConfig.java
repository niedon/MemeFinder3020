package com.bcadaval.memefinder3020.principal;

import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bcadaval.memefinder3020.modelo.beans.xml.Ajustes;
import com.bcadaval.memefinder3020.utils.AjustesUtils;
import com.bcadaval.memefinder3020.utils.RutasUtils;

@Configuration
@EnableTransactionManagement
@PropertySources(value = { @PropertySource("application.properties"),@PropertySource("hibernate.properties")  })
public class SpringConfig {
	
	@Autowired private Environment env;
	@Autowired AjustesUtils ajustesUtils;
	
	@Bean
	public RutasUtils rutasUtils() {
		return new RutasUtils(env.getProperty("custom.arranquetest"));
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
		
		String url = env.getProperty("spring.datasource.url") + rutasUtils().RUTA_BD_AA;
		dataSource.setUrl(url);
		
		dataSource.setUsername(env.getProperty("spring.datasource.username"));
		dataSource.setPassword(env.getProperty("spring.datasource.password"));
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
	
	@Bean
	public JAXBContext contextoXml() throws JAXBException {
		return JAXBContext.newInstance(Ajustes.class);
	}
	
	@Bean
	public AjustesUtils ajustesUtils() throws JAXBException {
		return new AjustesUtils(rutasUtils(), contextoXml());
	}
	
	@Bean
	public ResourceBundle resourceBundle() throws JAXBException {
		return ResourceBundle.getBundle("txt/textos", ajustesUtils().getLocale());
	}
	
}
