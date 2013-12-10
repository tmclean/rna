package net.tmclean.nimble.core;

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
	private CtClass cClass = null;
	private ConstPool constPool = null;
	private Annotation annotation = null;
	
	public ClassAnnotationBuilder( ClassBuilder classBuilder, Class<?> annotationClass ) 
	{
		super( classBuilder.getPool() );
		
		this.classBuilder = classBuilder;
		this.cClass = classBuilder.getCTClass();
		this.constPool = cClass.getClassFile().getConstPool();
		
		this.annotation = new Annotation( annotationClass.getName(), this.cClass.getClassFile().getConstPool() );
	}
	
	public ClassAnnotationBuilder withStringsParam( String name, String ... values )
	{
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
		annotation.addMemberValue( name, new StringMemberValue( value, constPool ) );
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ClassBuilder apply() throws CannotCompileException, NotFoundException
	{
		String visibility = AnnotationsAttribute.visibleTag;
		
		if( cClass.getClassFile().getAttribute( visibility ) == null )
			cClass.getClassFile().getAttributes().add( (Object)new AnnotationsAttribute( constPool, visibility ) );
		
		((AnnotationsAttribute)cClass.getClassFile().getAttribute( visibility )).addAnnotation( annotation );
		
		return this.classBuilder;
	}
}
