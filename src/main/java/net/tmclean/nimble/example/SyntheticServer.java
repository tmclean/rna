package net.tmclean.nimble.example;

import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.ws.rs.core.MediaType;

import net.tmclean.nimble.Nimble;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class SyntheticServer 
{	
	private String deployURL = null;
	private Configuration freemarker = null;
	
	public SyntheticServer( String deployURL )
	{
		this.deployURL = deployURL;
		
		this.freemarker = new Configuration();
		freemarker.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);	
	}
	
	public void start() throws CannotCompileException, NotFoundException, InstantiationException, IllegalAccessException
	{
		JacksonJsonProvider json = new JacksonJsonProvider();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion( Inclusion.ALWAYS );
		json.setMapper( mapper );
		
		for( int i=0; i<10; i++ )
			buildTestPOJO( int.class, String.class, i );
		
		List<Class<?>> serviceClasses = buildServiceClasses();
		serviceClasses.add( RegistryResource.class );
		
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		sf.setResourceClasses( serviceClasses );
		
		for( Class<?> serviceClass : serviceClasses )
		{
			if( serviceClass.getName().equals( RegistryResource.class.getName() ) )
			{
				continue;
			}
			
			sf.setResourceProvider( serviceClass, new SingletonResourceProvider( serviceClass.newInstance() ) );
		}
		
		sf.setResourceProvider( RegistryResource.class, new SingletonResourceProvider( new RegistryResource( freemarker ) ) );
		
		sf.setAddress( deployURL );
		sf.setProvider( new JacksonJsonProvider() );
		
		BindingFactoryManager manager = sf.getBus().getExtension( BindingFactoryManager.class );
		
		JAXRSBindingFactory factory = new JAXRSBindingFactory();
		factory.setBus( sf.getBus() );
		
		manager.registerBindingFactory( JAXRSBindingFactory.JAXRS_BINDING_ID, factory );
		
		Server server = sf.create();
		server.start();
	}
	
	private List<Class<?>> buildServiceClasses() throws CannotCompileException, NotFoundException
	{	
		Class<?> clazz1 = Nimble.newEndpoint().at( "/restTest" )
				   .withGet( "/test/{path}" )
				      .produces( "testPOJO0", MediaType.APPLICATION_JSON )
				      .takesQueryParam( "test", int.class )
				      .takesPathParam( "path", String.class )
				      .withSource( "{return new " + Nimble.getModelClassName( "testPOJO0" ) + "($1, $2);}" )
				      .apply()
					.apply();
		
		Class<?> clazz2 = Nimble.newEndpoint().at( "/restTest2" )
				   .withGet( "/test2/{path}" )
				      .produces( "testPOJO1", MediaType.APPLICATION_JSON )
				      .takesQueryParam( "test", int.class )
				      .takesPathParam( "path", String.class )
				      .withSource( "{return new " + Nimble.getModelClassName( "testPOJO1" ) + "($1, $2);}" )
				      .apply()
					.apply();
		
		List<Class<?>> classes = new ArrayList<>();
		classes.add( clazz1 );
		classes.add( clazz2 );
		
		return classes;
	}
	
	private void buildTestPOJO( Class<?> nested1, Class<?> nested2, int i ) throws CannotCompileException, NotFoundException
	{
		Nimble.newModel()
					.named( "testPOJO"+i )
					.setPublic()
					.withBeanField( "value", nested1, null )
					.withBeanField( "value2", nested2, null )
					.withConstructor( "{this.value=$1;this.value2=$2;}", nested1, nested2 )
					.apply();
	}
}
