package net.tmclean.nimble.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class MethodBuilder extends Builder<MethodBuilder, ClassBuilder>
{
	private CtClass cClass           = null;
	private String  name             = null;
	private CtClass returnType       = CtClass.voidType;
	private List<CtClass> parameters = new ArrayList<CtClass>( 0 );
	private String src               = null;
	private Map<String, Integer> paramOrderMap = new HashMap<String, Integer>();
	private Map<Integer, String> paramInvOrderMap = new HashMap<Integer, String>();
	
	private ClassBuilder classBuilder = null;
	private CtMethod method = null;
	
	public MethodBuilder( ClassBuilder classBuilder )
	{
		super( classBuilder.getPool() );

		logger.debug( "Creating new MethodBuilder for class" );
		
		this.classBuilder = classBuilder;
		this.cClass = this.classBuilder.getCTClass();
	}
	
	public MethodBuilder named( String name )
	{
		logger.debug( "Setting method name to {}", name );
		
		this.name = name;
		return this;
	}
	
	public MethodBuilder returns( Class<?> returnType ) throws NotFoundException
	{
		logger.debug( "Setting return type to {}", returnType.getName() );
		
		this.returnType = getPool().get( returnType.getName() );
		return this;
	}
	
	public MethodBuilder takes( String name, Class<?> paramType ) throws NotFoundException
	{
		logger.debug( "Adding method parameter with name {} and type {}", name, paramType.getName() );
		
		this.parameters.add( getPool().get( paramType.getName() ) );
		
		int ordinal = paramOrderMap.size();
		
		logger.debug( "Method parameter {} has ordinal {}", name, ordinal );
		
		this.paramOrderMap.put( name, ordinal );
		this.paramInvOrderMap.put( ordinal, name );
		
		return this;
	}
	
	public MethodBuilder withSource( String src )
	{
		logger.debug( "Setting method soruce {}", src );
		
		this.src = src;
		return this;
	}
	
	public MethodAnnotationBuilder annotatedBy( Class<?> annotationClass )
	{
		logger.debug( "Adding method annotation {}", annotationClass.getName() );
		
		return new MethodAnnotationBuilder( this, annotationClass );
	}
	
	public MethodParamAnnotationBuilder paramAnnotatedBy( String paramName, Class<?> annotationClass )
	{
		logger.debug( "Adding annotation {} to parameter {}", annotationClass.getName(), paramName );
		
		return new MethodParamAnnotationBuilder( this, paramName, annotationClass );
	}
	
	public MethodBuilder build() throws CannotCompileException
	{
		logger.debug( "Building method" );
		
		CtClass[] params = parameters.toArray( new CtClass[]{} );
		method = new CtMethod( returnType, name, params, cClass );
		method.setModifiers( getModifier() );
		method.setBody( src );
		
		return this;
	}

	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{	
		logger.debug( "Adding method to class" );
		
		cClass.addMethod( method );
		
		return this.classBuilder;
	}
	
	public CtClass getCTClass()
	{
		return this.cClass;
	}
	
	public CtMethod getCTMethod()
	{
		return this.method;
	}
	
	public int getParamOrdinal( String name )
	{
		logger.debug( "Attempting to find ordinal for parameter {}", name );
		
		int ordinal = this.paramOrderMap.get( name );
		
		logger.debug( "Parameter {} has ordinal {}", name, ordinal );
		
		return ordinal;
	}
	
	public String getParamAtOrdinal( int ordinal )
	{
		logger.debug( "Attempting to find ordinal at {}", ordinal );
		
		String param = this.paramInvOrderMap.get( ordinal );
		
		logger.debug( "Parameter at ordinal {} is named {}", ordinal, name );
		
		return param;
	}
	
	public int getNumParams()
	{
		return this.paramOrderMap.size();
	}
}
