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

public class ClassAnnotationBuilder extends Builder<ClassAnnotationBuilder, ClassBuilder>
{
	private ClassBuilder classBuilder = null;
	private CtClass      cClass       = null;
	private ConstPool    constPool    = null;
	private Annotation   annotation   = null;
	
	public ClassAnnotationBuilder( ClassBuilder classBuilder, Class<?> annotationClass ) 
	{
		super( classBuilder.getPool() );
	
		logger.debug( "Creating new ClassAnnotationBuilder for " + annotationClass.getName() );
		
		this.classBuilder = classBuilder;
		this.cClass = classBuilder.getCTClass();
		this.constPool = cClass.getClassFile().getConstPool();
		
		this.annotation = new Annotation( annotationClass.getName(), this.cClass.getClassFile().getConstPool() );
	}
	
	public ClassAnnotationBuilder withStringsParam( String name, String ... values )
	{
		if( logger.isDebugEnabled() )
			logger.debug( "Setting class annotation String param '{}' with values {}", name, new Object[]{values} );
		
		ArrayMemberValue arrayVal = new ArrayMemberValue( constPool );
		
		MemberValue[] vals = new MemberValue[values == null ? 0 : values.length];
		
		for( int i=0; i<vals.length; i++ )
			vals[i] = new StringMemberValue( values[i], constPool );
		
		arrayVal.setValue( vals );

		annotation.addMemberValue( "value", arrayVal );
		
		return this;
	}
	
	public ClassAnnotationBuilder withStringParam( String name, String value )
	{
		logger.debug( "Setting class annotation String param '{}' with value {}", name, value );
		
		annotation.addMemberValue( name, new StringMemberValue( value, constPool ) );
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{
		logger.debug( "Applying class annotation" );
		
		String visibility = AnnotationsAttribute.visibleTag;
		
		if( cClass.getClassFile().getAttribute( visibility ) == null )
		{
			logger.debug( "Class AnnotationInfo has not been initialized for this class, initializing" );
			
			cClass.getClassFile().getAttributes().add( (Object)new AnnotationsAttribute( constPool, visibility ) );
		}
		
		logger.debug( "Adding annotation to class" );
		((AnnotationsAttribute)cClass.getClassFile().getAttribute( visibility )).addAnnotation( annotation );
		
		return this.classBuilder;
	}
}
