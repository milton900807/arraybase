package com.arraybase;

import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

public class JavaSourceFromString extends SimpleJavaFileObject  {

	private String qualifiedName = null;
	private String sourceCode = null;
	
	
	public JavaSourceFromString(String _className, String _source) {
        super(URI.create("string:///" +_className.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        qualifiedName = _className;
        sourceCode = _source;
	}

	 
    
    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return sourceCode ;
    }
 
    public String getQualifiedName() {
        return qualifiedName;
    }
 
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
 
    public String getSourceCode() {
        return sourceCode;
    }
 
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}