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
	private Map<String, Integer> paramOrderMap = new HashMap<>();
	private Map<Integer, String> paramInvOrderMap = new HashMap<>();
	
	private ClassBuilder classBuilder = null;
	private CtMethod method = null;
	
	public MethodBuilder( ClassBuilder classBuilder )
	{
		super( classBuilder.getPool() );

		this.classBuilder = classBuilder;
		this.cClass = this.classBuilder.getCTClass();
	}
	
	public MethodBuilder named( String name )
	{
		this.name = name;
		return this;
	}
	
	public MethodBuilder returns( Class<?> returnType ) throws NotFoundException
	{
		this.returnType = getPool().get( returnType.getName() );
		return this;
	}
	
	public MethodBuilder takes( String name, Class<?> paramType ) throws NotFoundException
	{
		this.parameters.add( getPool().get( paramType.getName() ) );
		
		int ordinal = paramOrderMap.size();
		
		this.paramOrderMap.put( name, ordinal );
		this.paramInvOrderMap.put( ordinal, name );
		
		return this;
	}
	
	public MethodBuilder withSource( String src )
	{
		this.src = src;
		return this;
	}
	
	public MethodAnnotationBuilder annotatedBy( Class<?> annotationClass )
	{
		return new MethodAnnotationBuilder( this, annotationClass );
	}
	
	public MethodParamAnnotationBuilder paramAnnotatedBy( String paramName, Class<?> annotationClass )
	{
		return new MethodParamAnnotationBuilder( this, paramName, annotationClass );
	}
	
	public MethodBuilder build() throws CannotCompileException
	{
		CtClass[] params = parameters.toArray( new CtClass[]{} );
		method = new CtMethod( returnType, name, params, cClass );
		method.setModifiers( getModifier() );
		method.setBody( src );
		
		return this;
	}

	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{	
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
		return this.paramOrderMap.get( name );
	}
	
	public int getNumParams()
	{
		return this.paramOrderMap.size();
	}
	
	public String getParamAtOrdinal( int ordinal )
	{
		return this.paramInvOrderMap.get( ordinal );
	}
}
