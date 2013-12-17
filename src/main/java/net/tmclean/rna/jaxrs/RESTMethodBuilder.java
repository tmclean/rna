package net.tmclean.rna.jaxrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import net.tmclean.rna.RNA;
import net.tmclean.rna.core.MethodBuilder;

public abstract class RESTMethodBuilder extends MethodBuilder
{
	private Class<?> annotation = null;
	private String path = null;
	private String[] consumes = null;
	private String[] produces = null;

	private List<String> params = new ArrayList<String>();
	
	private Map<String, Class<?>> paramTypes = new HashMap<String, Class<?>>();
	
	private Map<String, String> queryParams = new HashMap<String, String>();
	private Map<String, String> pathParams = new HashMap<String, String>();
	
	public RESTMethodBuilder( RESTBuilder builder, Class<?> annotation, String path )
	{
		super( builder );
		
		logger.debug( "Creating new RESTMethodBuilder for class with HTTP method annotation {} at path {}", annotation.getName(), path );
		
		this.annotation = annotation;
		this.path = path;
	}
	
	public RESTMethodBuilder consumes( String ... types )
	{
		if( logger.isDebugEnabled() )
			logger.debug( "Setting method to consume media types {}", new Object[]{ types } );
		
		this.consumes = types;
		
		return this;
	}
	
	public RESTMethodBuilder produces( String modelName, String ... types ) throws NotFoundException
	{
		if( logger.isDebugEnabled() )
			logger.debug( "Setting method to produce model {} consume media types {}", modelName, new Object[]{ types } );
		
		returns( RNA.getModel( modelName ) );
		
		this.produces = types;
		
		return this;
	}
	
	public RESTMethodBuilder takesPathParam( String name, Class<?> paramType )
	{
		logger.debug( "Setting method to take path paramter {} with type {}", name, paramType.getName() );
		
		String paramName = randParamName();
		
		logger.debug( "Parameter {} is named {}", name, paramName );
		
		pathParams.put( paramName, name );
		paramTypes.put( paramName, paramType );
		
		params.add( paramName );
		
		return this;
	}
	
	public RESTMethodBuilder takesQueryParam( String name, Class<?> paramType ) throws NotFoundException
	{
		logger.debug( "Setting method to take query parameter {} with type {}", name, paramType.getName() );
		
		String paramName = randParamName();
		
		queryParams.put( paramName, name );
		paramTypes.put( paramName, paramType );

		params.add( paramName );
		
		return this;
	}
	
	@Override
	public RESTMethodBuilder returns(Class<?> returnType) throws NotFoundException 
	{
		super.returns(returnType);
		
		return this;
	}
	
	@Override
	public RESTMethodBuilder withSource( String src ) 
	{
		super.withSource( src );
		
		return this;
	}
	
	@Override
	public RESTBuilder apply() throws CannotCompileException, NotFoundException 
	{
		logger.debug( "Applying resource method to resource" );
		
		setPublic();
		
		String methodName = randMethodName();
		logger.debug( "Method is named {}", methodName );
		named( methodName );

		logger.debug( "Setting method parameters" );
		
		for( String paramName : params )
		{
			Class<?> paramType = paramTypes.get( paramName );
			logger.debug( "Adding parameter {} with type {}", paramName, paramType.getName() );
			this.takes( paramName, paramType );
		}
		
		this.build();
		
		logger.debug( "Setting HTTP method annotation {}", annotation.getName() );
		
		this.annotatedBy( annotation ).apply();
		
		logger.debug( "Setting path to {}", path );
		
		this.annotatedBy( Path.class ).withStringParam( "value", path ).apply();
		
		if( produces != null && produces.length > 0 )
		{
			logger.debug( "Setting produces annotation" );
			this.annotatedBy( Produces.class ).withStringsParam( "value", produces ).apply();
		}
		
		if( consumes != null && consumes.length > 0 )
		{
			logger.debug( "Setting consumes annotation" );
			this.annotatedBy( Consumes.class ).withStringsParam( "value", produces ).apply();
		}
		
		logger.debug( "Setting parameter annotations" );
		for( int i=0; i<getNumParams(); i++ )
		{
			String param = getParamAtOrdinal( i );
			
			if( queryParams.containsKey( param ) )
			{
				String queryParamName = queryParams.get( param );
				logger.debug( "Setting parameter {} to a query parameter named", queryParamName );
				paramAnnotatedBy( param, QueryParam.class ).withStringParam( "value", queryParams.get( param ) ).apply();
			}

			if( pathParams.containsKey( param ) )
			{
				String pathParamName = pathParams.get( param );
				logger.debug( "Setting parameter {} to a query parameter named", pathParamName );
				paramAnnotatedBy( param, PathParam.class ).withStringParam( "value", pathParamName ).apply();
			}
		}
		
		logger.debug( "Adding REST method to class" );
		
		return (RESTBuilder)super.apply();
	}
	
	private String randParamName()
	{
		return "param_" + UUID.randomUUID().toString().replace( "-", "" );
	}
	
	private String randMethodName()
	{
		return "method_" + UUID.randomUUID().toString().replace( "-", "" );
	}
}
