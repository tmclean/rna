package net.tmclean.rna.core;

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
	private CtClass       cClass        = null;
	private ConstPool     constPool     = null;
	private Annotation    annotation    = null;
	private CtMethod      method        = null;
	private String        paramName     = null;
	
	public MethodParamAnnotationBuilder( MethodBuilder methodBuilder, String paramName, Class<?> annotationClass ) 
	{
		super( methodBuilder.getPool() );
		
		logger.debug( "Creating new MethodParamAnnotationBuilder to annotate parmeter {} with annotation {}", paramName, annotationClass.getName() );
		
		this.methodBuilder = methodBuilder;
		this.cClass = methodBuilder.getCTClass();
		this.constPool = cClass.getClassFile().getConstPool();
		this.method = this.methodBuilder.getCTMethod();
		this.paramName = paramName;
		
		this.annotation = new Annotation( annotationClass.getName(), this.cClass.getClassFile().getConstPool() );
	}
	
	public MethodParamAnnotationBuilder withStringsParam( String name, String ... values )
	{
		if( logger.isDebugEnabled() )
			logger.debug( "Setting annotation parameter {} with values {}", name, new Object[]{values} );
		
		ArrayMemberValue arrayVal = new ArrayMemberValue( constPool );
		
		MemberValue[] vals = new MemberValue[values == null ? 0 : values.length];
		
		for( int i=0; i<vals.length; i++ )
			vals[i] = new StringMemberValue( values[i], constPool );
		
		arrayVal.setValue( vals );

		logger.debug( "Adding annotation parameters" );
		
		annotation.addMemberValue( "value", arrayVal );
		
		return this;
	}
	
	public MethodParamAnnotationBuilder withStringParam( String name, String value )
	{
		logger.debug( "Adding annotation parameter {} with value {}", name, value );
		
		annotation.addMemberValue( name, new StringMemberValue( value, constPool ) );
		
		return this;
	}
	
	@Override
	public MethodBuilder apply() throws CannotCompileException, NotFoundException
	{
		logger.debug( "Applying method parameter annotation" );
		String visibility = ParameterAnnotationsAttribute.visibleTag;
		
		if( this.method.getMethodInfo().getAttribute( visibility ) == null )
		{
			logger.debug( "Method annotation info has not been initialized, initializing" );
			
			this.method.getMethodInfo().addAttribute( new ParameterAnnotationsAttribute( constPool, visibility ) );
		}
		
		logger.debug( "Getting ordinal for parameter {}", paramName );
		
		int paramIdx = methodBuilder.getParamOrdinal( paramName );
		
		logger.debug( "Parameter {} has ordinal {}", paramName, paramIdx );
		
	     AttributeInfo paramAtrributeInfo = this.method.getMethodInfo().getAttribute( visibility );
	     
	     logger.debug( "Getting method parameter annotation array" );
	     
	     ParameterAnnotationsAttribute parameterAtrribute = ((ParameterAnnotationsAttribute) paramAtrributeInfo);
	     Annotation[][] paramArrays = parameterAtrribute.getAnnotations();
	     
	     if( paramArrays == null || paramArrays.length == 0 )
	     {
	    	 logger.debug( "Method parameter annotation array has not been initalized, initializing array to fit this parameter" );
	    	 
	    	 paramArrays = new Annotation[methodBuilder.getNumParams()][];
	    	 
	    	 for( int i=0; i<paramArrays.length; i++ )
	    		 paramArrays[i] = new Annotation[]{};
	    		 
	     }

		Annotation[] addAnno = paramArrays[paramIdx];
		Annotation[] newAnno = null;
			     
		if( addAnno == null )
		{
			logger.debug( "Annotation array for this parameter has not been initialized, initializing for single annotation" );
			
			addAnno = new Annotation[1];
			newAnno = new Annotation[1];
		}
		else if( addAnno.length == 0 )
		{
			logger.debug( "Annotation array for this parameter has been initialized, but is empty. Initializing for single annotation" );
			newAnno = new Annotation[1];
		}
		else 
		{
			logger.debug( "Annotation array for this parameter has been initialized an other annotations exist. Expanding." );
			newAnno = Arrays.copyOf( addAnno, addAnno.length+1 );
		}
			     
		newAnno[newAnno.length-1] = annotation;
		paramArrays[paramIdx] = newAnno;
		
		logger.debug( "Assigning new annotation array to method parameter" );
		
		parameterAtrribute.setAnnotations( paramArrays );
		
		return this.methodBuilder;
	}
}
