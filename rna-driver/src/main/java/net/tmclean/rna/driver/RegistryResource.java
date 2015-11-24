package net.tmclean.rna.driver;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.tmclean.rna.RNA;

@Path( "/registry" )
public class RegistryResource 
{
	@SuppressWarnings("unused")
	private Configuration freemarker = null;
	
	public RegistryResource( Configuration freemarker ) 
	{
		this.freemarker = freemarker;
	}
	@GET
	@Produces( MediaType.TEXT_HTML )
	public String buildRegistry() throws IOException, TemplateException
	{	
		@SuppressWarnings("deprecation")
		Template t = new Template( "registry", new InputStreamReader( this.getClass().getClassLoader().getResourceAsStream( "META-INF/registry.ftl") ) );
	
		Map<String, Object> model = new HashMap<>();
		
		Map<String, Map<String,Object>> models = new HashMap<>();
		for( String modelName : RNA.getModelNames() )
		{
			Map<String, Object> modelProps = new HashMap<>();
			
			modelProps.put( "className", RNA.getModelClassName( modelName ) );
			
			Map<String, Object> modelMethods = new HashMap<>();
			
			for( Method m : RNA.getModel( modelName ).getDeclaredMethods() )
			{
				Map<String, Object> methodProps = new HashMap<>();
				
				List<String> paramClassNames = new ArrayList<>();
				for( Class<?> param : m.getParameterTypes() )
					paramClassNames.add( param.getName() );
				
				methodProps.put( "params", paramClassNames );
				
				methodProps.put( "returns", m.getReturnType().getName() );
				
				modelMethods.put( m.getName(), methodProps );
			}
			
			Map<String, Object> fields = new HashMap<>();
			for( Field field : RNA.getModel( modelName ).getDeclaredFields() )
			{
				fields.put( field.getName(), field.getType().getName() );
			}
			
			List<List<String>> constructors = new ArrayList<>();
			for( Constructor<?> cons : RNA.getModel( modelName ).getConstructors() )
			{
				List<String> consParams = new ArrayList<>();
				for( Class<?> consParam : cons.getParameterTypes() )
					consParams.add( consParam.getName() );
				
				constructors.add( consParams );
			}
			
			Collections.sort( constructors, new Comparator<List<String>>() {
				public int compare(List<String> o1, List<String> o2) 
				{
					return o1.size() - o2.size();
				};
			});
			
			modelProps.put( "constructors", constructors );
			modelProps.put( "fields", fields );
			modelProps.put( "methods", modelMethods );
			
			models.put( modelName, modelProps );
		}

		model.put( "models", models );

		Writer w = new StringWriter();
		t.process( model, w );
		return w.toString();
	}
}