package net.tmclean.nimble.core;

import java.util.Arrays;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class MethodParamAnnotationBuilder extends Builder<MethodParamAnnotationBuilder, MethodBuilder>
{
	private MethodBuilder methodBuilder = null;
	private CtClass cClass = null;
	private ConstPool constPool = null;
	private Annotation annotation = null;
	private CtMethod method = null;
	private String paramName = null;
	
	public MethodParamAnnotationBuilder( MethodBuilder methodBuilder, String paramName, Class<?> annotationClass ) 
	{
		super( methodBuilder.getPool() );
		
		this.methodBuilder = methodBuilder;
		this.cClass = methodBuilder.getCTClass();
		this.constPool = cClass.getClassFile().getConstPool();
		this.method = this.methodBuilder.getCTMethod();
		this.paramName = paramName;
		
		this.annotation = new Annotation( annotationClass.getName(), this.cClass.getClassFile().getConstPool() );
	}
	
	public MethodParamAnnotationBuilder withStringsParam( String name, String ... values )
	{
		ArrayMemberValue arrayVal = new ArrayMemberValue( constPool );
		
		MemberValue[] vals = new MemberValue[values == null ? 0 : values.length];
		
		for( int i=0; i<vals.length; i++ )
			vals[i] = new StringMemberValue( values[i], constPool );
		
		arrayVal.setValue( vals );

		annotation.addMemberValue( "value", arrayVal );
		
		return this;
	}
	
	public MethodParamAnnotationBuilder withStringParam( String name, String value )
	{
		annotation.addMemberValue( name, new StringMemberValue( value, constPool ) );
		
		return this;
	}
	
	@Override
	public MethodBuilder apply() throws CannotCompileException, NotFoundException
	{
		String visibility = ParameterAnnotationsAttribute.visibleTag;
		
		if( this.method.getMethodInfo().getAttribute( visibility ) == null )
			this.method.getMethodInfo().addAttribute( new ParameterAnnotationsAttribute( constPool, visibility ) );
		
		int paramIdx = methodBuilder.getParamOrdinal( paramName );
		
	     AttributeInfo paramAtrributeInfo = this.method.getMethodInfo().getAttribute( visibility );
	     
	     ParameterAnnotationsAttribute parameterAtrribute = ((ParameterAnnotationsAttribute) paramAtrributeInfo);
	     Annotation[][] paramArrays = parameterAtrribute.getAnnotations();
	     
	     if( paramArrays.length == 0 )
	     {
	    	 paramArrays = new Annotation[paramIdx+1][];
	     }
	     else if( paramArrays.length <= paramIdx )
	     {
	    	 Annotation[][] newParamArrays = new Annotation[paramArrays.length+1][];
	    	 
	    	 for( int i=0; i<paramArrays.length; i++ )
	    	 {
	    		 newParamArrays[i] = paramArrays[i];
	    	 }
	    	 
	    	 paramArrays = newParamArrays;
	     }

		Annotation[] addAnno = paramArrays[paramIdx];
		Annotation[] newAnno = null;
			     
		if( addAnno == null )
		{
			addAnno = new Annotation[1];
			newAnno = new Annotation[1];
		}
		else if( addAnno.length == 0 )
		{
			newAnno = new Annotation[1];
		}
		else 
		{
			newAnno = Arrays.copyOf( addAnno, addAnno.length+1 );
		}
			     
		newAnno[newAnno.length-1] = annotation;
		paramArrays[paramIdx] = newAnno;
		parameterAtrribute.setAnnotations( paramArrays );
		
		return this.methodBuilder;
	}
}
