package net.tmclean.rna.core;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class MethodAnnotationBuilder extends Builder<MethodAnnotationBuilder, MethodBuilder>
{
	private MethodBuilder methodBuilder = null;
	private CtClass       cClass        = null;
	private ConstPool     constPool     = null;
	private Annotation    annotation    = null;
	
	public MethodAnnotationBuilder( MethodBuilder methodBuilder, Class<?> annotationClass ) 
	{
		super( methodBuilder.getPool() );
		
		logger.debug( "Creating new MethodAnnotationBuilder for annotation {}", annotationClass.getName() );
		
		this.methodBuilder = methodBuilder;
		this.cClass = methodBuilder.getCTClass();
		this.constPool = cClass.getClassFile().getConstPool();
		
		this.annotation = new Annotation( annotationClass.getName(), this.cClass.getClassFile().getConstPool() );
	}
	
	public MethodAnnotationBuilder withStringsParam( String name, String ... values )
	{
		if( logger.isDebugEnabled() )
			logger.debug( "Setting method annotation parameter {} with values {}", name, new Object[]{ values } );
		
		ArrayMemberValue arrayVal = new ArrayMemberValue( constPool );
		
		MemberValue[] vals = new MemberValue[values == null ? 0 : values.length];
		
		for( int i=0; i<vals.length; i++ )
			vals[i] = new StringMemberValue( values[i], constPool );
		
		arrayVal.setValue( vals );

		annotation.addMemberValue( "value", arrayVal );
		
		return this;
	}
	
	public MethodAnnotationBuilder withStringParam( String name, String value )
	{
		logger.debug( "Adding method annotation parameter {} with value {}", name, value );
		
		annotation.addMemberValue( name, new StringMemberValue( value, constPool ) );
		
		return this;
	}
	
	@Override
	public MethodBuilder apply() throws CannotCompileException, NotFoundException
	{
		logger.debug( "Applying method annotation to method" );
		
		String visibility = AnnotationsAttribute.visibleTag;
		
		if( methodBuilder.getCTMethod().getMethodInfo().getAttribute( visibility ) == null )
		{
			logger.debug( "Method annotations info is uninitialized, initializing" );
			
			methodBuilder.getCTMethod().getMethodInfo().addAttribute( new AnnotationsAttribute( constPool, visibility ) );
		}
		
		logger.debug( "Adding method annotation to method" );
		
		((AnnotationsAttribute)methodBuilder.getCTMethod().getMethodInfo().getAttribute( visibility )).addAnnotation( annotation );
		
		return this.methodBuilder;
	}
}
