package net.tmclean.nimble.jaxrs;

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
import net.tmclean.nimble.Nimble;
import net.tmclean.nimble.core.MethodBuilder;

public abstract class RESTMethodBuilder extends MethodBuilder
{
	private Class<?> annotation = null;
	private String path = null;
	private String[] consumes = null;
	private String[] produces = null;

	private List<String> params = new ArrayList<>();
	
	private Map<String, Class<?>> paramTypes = new HashMap<String, Class<?>>();
	
	private Map<String, String> queryParams = new HashMap<String, String>();
	private Map<String, String> pathParams = new HashMap<String, String>();
	
	public RESTMethodBuilder( RESTBuilder builder, Class<?> annotation, String path )
	{
		super( builder );
		
		this.annotation = annotation;
		this.path = path;
	}
	
	public RESTMethodBuilder consumes( String ... types )
	{
		this.consumes = types;
		
		return this;
	}
	
	public RESTMethodBuilder produces( String modelName, String ... types ) throws NotFoundException
	{
		returns( Nimble.getModel( modelName ) );
		
		this.produces = types;
		
		return this;
	}
	
	public RESTMethodBuilder takesPathParam( String name, Class<?> paramType )
	{
		String paramName = randParamName();
		
		pathParams.put( paramName, name );
		paramTypes.put( paramName, paramType );
		
		params.add( paramName );
		
		return this;
	}
	
	public RESTMethodBuilder takesQueryParam( String name, Class<?> paramType ) throws NotFoundException
	{
		String paramName = randParamName();
		
		queryParams.put( paramName, name );
		paramTypes.put( paramName, paramType );

		params.add( paramName );
		
		return this;
	}
	
	@Override
	public RESTMethodBuilder returns(Class<?> returnType) throws NotFoundException 
	{
		return (RESTMethodBuilder)super.returns(returnType);
	}
	
	@Override
	public RESTBuilder apply() throws CannotCompileException, NotFoundException 
	{
		setPublic();
		named( randMethodName() );

		for( String paramName : params )
			this.takes( paramName, paramTypes.get( paramName ) );
		
		this.build();
		
		this.annotatedBy( annotation ).apply();
		
		this.annotatedBy( Path.class ).withStringParam( "value", path ).apply();
		
		if( produces != null && produces.length > 0 )
			this.annotatedBy( Produces.class ).withStringsParam( "value", produces ).apply();
		
		if( consumes != null && consumes.length > 0 )
			this.annotatedBy( Consumes.class ).withStringsParam( "value", produces ).apply();
		
		for( int i=0; i<getNumParams(); i++ )
		{
			String param = getParamAtOrdinal( i );
			
			if( queryParams.containsKey( param ) )
				paramAnnotatedBy( param, QueryParam.class ).withStringParam( "value", queryParams.get( param ) ).apply();

			if( pathParams.containsKey( param ) )
				paramAnnotatedBy( param, PathParam.class ).withStringParam( "value", pathParams.get( param ) ).apply();
		}
		
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
