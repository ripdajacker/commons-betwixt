/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//betwixt/src/java/org/apache/commons/betwixt/io/read/ReadContext.java,v 1.4.2.4 2004/02/21 17:20:06 rdonkin Exp $
 * $Revision: 1.4.2.4 $
 * $Date: 2004/02/21 17:20:06 $
 *
 * ====================================================================
 * 
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgement:  
 *       "This product includes software developed by the 
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "Apache", "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache" nor may "Apache" appear in their names without prior 
 *    written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.betwixt.io.read;

import java.beans.IntrospectionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.betwixt.AttributeDescriptor;
import org.apache.commons.betwixt.BindingConfiguration;
import org.apache.commons.betwixt.ElementDescriptor;
import org.apache.commons.betwixt.XMLBeanInfo;
import org.apache.commons.betwixt.XMLIntrospector;
import org.apache.commons.betwixt.expression.Context;
import org.apache.commons.betwixt.expression.Updater;
import org.apache.commons.betwixt.strategy.ActionMappingStrategy;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;

/**  
  * Extends <code>Context</code> to provide read specific functionality. 
  *
  * @author Robert Burrell Donkin
  * @version $Revision: 1.4.2.4 $
  */
public class ReadContext extends Context {

	/** Beans indexed by ID strings */
	private HashMap beansById = new HashMap();
	/** Classloader to be used to load beans during reading */
	private ClassLoader classLoader;
	/** The read specific configuration */
	private ReadConfiguration readConfiguration;
	/** Records the element path together with the locations where classes were mapped*/
	private ArrayStack elementMappingStack = new ArrayStack();
	/** Contains actions for each element */
	private ArrayStack actionMappingStack = new ArrayStack();
	/** Stack contains all beans created */
	private ArrayStack objectStack = new ArrayStack();

	private Class rootClass;

	private XMLIntrospector xmlIntrospector;

	/** 
	  * Constructs a <code>ReadContext</code> with the same settings 
	  * as an existing <code>Context</code>.
	  * @param context not null
	  * @param readConfiguration not null
	  */
	public ReadContext(Context context, ReadConfiguration readConfiguration) {
		super(context);
		this.readConfiguration = readConfiguration;
	}

	/**
	  * Constructs a <code>ReadContext</code> with standard log.
	  * @param bindingConfiguration the dynamic configuration, not null
	  * @param readConfiguration the extra read configuration not null
	  */
	public ReadContext(
		BindingConfiguration bindingConfiguration,
		ReadConfiguration readConfiguration) {
		this(
			LogFactory.getLog(ReadContext.class),
			bindingConfiguration,
			readConfiguration);
	}

	/** 
	  * Base constructor
	  * @param log log to this Log
	  * @param bindingConfiguration the dynamic configuration, not null
	  * @param readConfiguration the extra read configuration not null
	  */
	public ReadContext(
		Log log,
		BindingConfiguration bindingConfiguration,
		ReadConfiguration readConfiguration) {
		super(null, log, bindingConfiguration);
		this.readConfiguration = readConfiguration;
	}

	/** 
	  * Constructs a <code>ReadContext</code> 
	  * with the same settings as an existing <code>Context</code>.
	  * @param readContext not null
	  */
	public ReadContext(ReadContext readContext) {
		super(readContext);
		beansById = readContext.beansById;
		classLoader = readContext.classLoader;
		readConfiguration = readContext.readConfiguration;
	}

	/**
	 * Puts a bean into storage indexed by an (xml) ID.
	 *
	 * @param id the ID string of the xml element associated with the bean
	 * @param bean the Object to store, not null
	 */
	public void putBean(String id, Object bean) {
		beansById.put(id, bean);
	}

	/**
	 * Gets a bean from storage by an (xml) ID.
	 *
	 * @param id the ID string of the xml element associated with the bean
	 * @return the Object that the ID references, otherwise null
	 */
	public Object getBean(String id) {
		return beansById.get(id);
	}

	/** 
	 * Clears the beans indexed by id.
	 */
	public void clearBeans() {
		beansById.clear();
	}

	/**
	  * Gets the classloader to be used.
	  * @return the classloader that should be used to load all classes, possibly null
	  */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	  * Sets the classloader to be used.
	  * @param classLoader the ClassLoader to be used, possibly null
	  */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/** 
	  * Gets the <code>BeanCreationChange</code> to be used to create beans 
	  * when an element is mapped.
	  * @return the BeanCreationChain not null
	  */
	public BeanCreationChain getBeanCreationChain() {
		return readConfiguration.getBeanCreationChain();
	}

    public ActionMappingStrategy getActionMappingStrategy() {
        return readConfiguration.getActionMappingStrategy();
    }

	/**
	  * Pops the top element from the element mapping stack.
	  * Also removes any mapped class marks below the top element.
	  *
	  * @return the name of the element popped 
	  * if there are any more elements on the stack, otherwise null.
	  * This is the local name if the parser is namespace aware, otherwise the name
	  */
	public String popElement() {

		Object top = null;
		if (!elementMappingStack.isEmpty()) {
			top = elementMappingStack.pop();
			if (top != null) {
				if (!(top instanceof String)) {
					return popElement();
				}
			}
		}

		return (String) top;
	}

	public String getCurrentElement() {
		return (String) elementMappingStack.peek();
	}

	/**
	  * Gets an iterator for the current relative path.
	  * This is not guarenteed to behave safely if the underlying array
	  * is modified during an interation.
	  * The current relative path is the sequence of element names
	  * starting with the element after the last mapped class marked.
	  *
	  * @return an Iterator over String's
	  */
	public Iterator getRelativeElementPathIterator() {
		return new RelativePathIterator();
	}

	public ElementDescriptor getRelativePathElementDescriptor()
		throws IntrospectionException {
		ElementDescriptor result = null;
		XMLBeanInfo lastMappedClazzInfo = getLastMappedClassXMLBeanInfo();
		if (lastMappedClazzInfo != null) {
			result =
				lastMappedClazzInfo
					.getElementDescriptor()
					.getElementDescriptor(
					getRelativeElementPathIterator());
		}
		return result;
	}

	/**
	  * Gets an iterator for the current relative path.
	  * This is not guarenteed to behave safely if the underlying array
	  * is modified during an interation.
	  * The current relative path is the sequence of element names
	  * starting with the element after the last mapped class marked.
	  *
	  * @return an Iterator over String's
	  */
	public Iterator getParentElementPathIterator() {
		Object top = elementMappingStack.peek();
		if (top instanceof Class) {
			return new RelativePathIterator(1);
		}
		return new RelativePathIterator();
	}

	public ElementDescriptor getParentPathElementDescriptor()
		throws IntrospectionException {
		ElementDescriptor result = null;
		XMLBeanInfo lastMappedClazzInfo = getLastMappedClassXMLBeanInfo();
		if (lastMappedClazzInfo != null) {
            Iterator it = getParentElementPathIterator();
            Object test = it.next();
			result =
				lastMappedClazzInfo
					.getElementDescriptor()
					.getElementDescriptor(it);
		}
		return result;
	}

	/**
	  * Gets the Class that was last mapped, if there is one.
	  * 
	  * @return the Class last marked as mapped or null if no class has been mapped
	  */
	public Class getLastMappedClass() {
		return getLastMappedClass(0);
	}

	public Class getLastMappedClass(int offset) {
		Class lastMapped = null;
		for (int i = offset, size = elementMappingStack.size();
			i < size;
			i++) {
			Object entry = elementMappingStack.peek(i);
			if (entry instanceof Class) {
				lastMapped = (Class) entry;
				break;
			}
		}
		return lastMapped;
	}
    
    
    private Class getParentClass()
    {
        Class result = null;
        boolean first = true;
        for (int i = 0, size = elementMappingStack.size();
            i < size;
            i++) {
            Object entry = elementMappingStack.peek(i);
            if (entry instanceof Class) {
                if (first) {
                    first = false;
                } else {
                
                    result = (Class) entry;
                    break;
                }
            }
        }
        return result;
    }
    
    private XMLBeanInfo getParentXMLBeanInfo() throws IntrospectionException {
        XMLBeanInfo result = null;
        Class parentClass = getParentClass();
        if ( parentClass != null ) {
            result = getXMLIntrospector().introspect(parentClass);
        }
        return result;
    }
    

    /**
     * @return
     */
    public ElementDescriptor getParentElementDescriptor() throws IntrospectionException {
        ElementDescriptor parentDescriptor = null;
        XMLBeanInfo parentInfo = getParentXMLBeanInfo();
        if ( parentInfo != null ) {
            Iterator it = getParentElementPathIterator();
            parentDescriptor =
                parentInfo
                    .getElementDescriptor()
                    .getElementDescriptor(it);            
        }
        
        return parentDescriptor;
    }
    

	public XMLBeanInfo getLastMappedClassXMLBeanInfo()
		throws IntrospectionException {
		XMLBeanInfo lastMappedXMLBeanInfo = null;
		Class lastMappedClass = getLastMappedClass();
		if (lastMappedClass != null) {
			lastMappedXMLBeanInfo =
				getXMLIntrospector().introspect(lastMappedClass);
		}
		return lastMappedXMLBeanInfo;
	}

	public Iterator getRelativeElementPathIterator(int offset) {
		return new RelativePathIterator(1);
	}

	/** 
	  * Pushes the given element onto the element mapping stack.
	  *
	  * @param elementName the local name if the parser is namespace aware,
	  * otherwise the full element name. Not null
	  */
	public void pushElement(String elementName) {

		elementMappingStack.push(elementName);
		// special case to ensure that root class is appropriately marked
		//TODO: is this really necessary?
		if (elementMappingStack.size() == 1 && rootClass != null) {
			markClassMap(rootClass);
		}
	}

	public boolean isAtRootElement() {
		return (elementMappingStack.size() == 1);
	}

    public boolean isStackEmpty() {
        return (elementMappingStack.size() == 0);
    }


	/**
	  * Marks the element name stack with a class mapping.
	  * Relative paths and last mapped class are calculated using these marks.
	  * 
	  * @param mappedClazz the Class which has been mapped at the current path, not null
	  */
	public void markClassMap(Class mappedClazz) {
		elementMappingStack.push(mappedClazz);
	}

	/** Used to return relative path */
	private class RelativePathIterator implements Iterator {

		/** The stack position that this iterator is at */
		private int at;
		private int offset;

		public RelativePathIterator() {
			this(0);
		}

		public RelativePathIterator(int offset) {
			this.offset = offset;
			at = elementMappingStack.size() - 1;
			for (int i = offset, size = elementMappingStack.size();
				i < size;
				i++) {
				Object entry = elementMappingStack.peek(i);
				if (entry instanceof Class) {
					at = i - 1;
					break;
				}
			}
		}

		public boolean hasNext() {
			return (at >= offset);
		}

		public Object next() {
			if (hasNext()) {
				return elementMappingStack.peek(at--);
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * Pops an action mapping from the stack
	 * @return
	 */
	public MappingAction popMappingAction() {
		return (MappingAction) actionMappingStack.pop();
	}

	/**
	 * Pushs an action mapping onto the stack
	 * @param mappingAction
	 */
	public void pushMappingAction(MappingAction mappingAction) {
		actionMappingStack.push(mappingAction);
	}

	/**
	 * Gets the current mapping action
	 * @return MappingAction 
	 */
	public MappingAction currentMappingAction() {
		if (actionMappingStack.size() == 0)
		{
			return null;	
		}
		return (MappingAction) actionMappingStack.peek();
	}

	public Object getBean() {
		return objectStack.peek();
	}

	public void setBean(Object bean) {
		// TODO: maybe need to deprecate the set bean method
		// and push into subclass
		// for now, do nothing		
	}

	public Object popBean() {
		return objectStack.pop();
	}

	public void pushBean(Object bean) {
		objectStack.push(bean);
	}

	public XMLIntrospector getXMLIntrospector() {
		return xmlIntrospector;
	}

	public void setXMLIntrospector(XMLIntrospector xmlIntrospector) {
		this.xmlIntrospector = xmlIntrospector;
	}

	public Class getRootClass() {
		return rootClass;
	}

	public void setRootClass(Class rootClass) {
		this.rootClass = rootClass;
	}

	public ElementDescriptor getCurrentDescriptor() throws Exception {
		ElementDescriptor result = null;
		Iterator relativePathIterator = getRelativeElementPathIterator();
		if (relativePathIterator.hasNext()) {
			Class lastMappedClazz = getLastMappedClass();
			if (lastMappedClazz != null) {
				XMLBeanInfo lastMappedClazzInfo =
					getXMLIntrospector().introspect(lastMappedClazz);
				ElementDescriptor baseDescriptor =
					lastMappedClazzInfo.getElementDescriptor();
				result =
					baseDescriptor.getElementDescriptor(relativePathIterator);
			}
		} else {
			// this means that we're updating the root
			Class lastMappedClazz = getLastMappedClass();
			if (lastMappedClazz != null) {
				XMLBeanInfo lastMappedClazzInfo =
					getXMLIntrospector().introspect(lastMappedClazz);
				ElementDescriptor baseDescriptor =
					lastMappedClazzInfo.getElementDescriptor();
				result =
					baseDescriptor.getElementDescriptor(relativePathIterator);
			}
		}
		return result;
	}

	public ElementDescriptor getActiveDescriptor() throws Exception {

		ElementDescriptor computedDescriptor =
			getRelativePathElementDescriptor();
		if (computedDescriptor == null) {
			computedDescriptor = getParentPathElementDescriptor();
		}
		ElementDescriptor parentDescriptor = null;
		if (computedDescriptor != null
			&& computedDescriptor.getPropertyType() == null) {
			XMLBeanInfo childXMLBeanInfo = getLastMappedClassXMLBeanInfo();
			if (childXMLBeanInfo == null) {
				getLog().trace("No XMLBeanInfo");

			} else {

				parentDescriptor =
					childXMLBeanInfo.getElementDescriptor().findParent(
						computedDescriptor);
			}
		}

		if (computedDescriptor == null
			|| computedDescriptor.getSingularPropertyType() == null) {
			computedDescriptor = parentDescriptor;
		}
		return computedDescriptor;
	}
	
	public void populateAttributes(
		AttributeDescriptor[] attributeDescriptors,
		Attributes attributes) {

		Log log = getLog();
		if (attributeDescriptors != null) {
			for (int i = 0, size = attributeDescriptors.length;
				i < size;
				i++) {
				AttributeDescriptor attributeDescriptor =
					attributeDescriptors[i];

				// The following isn't really the right way to find the attribute
				// but it's quite robust.
				// The idea is that you try both namespace and local name first
				// and if this returns null try the qName.
				String value =
					attributes.getValue(
						attributeDescriptor.getURI(),
						attributeDescriptor.getLocalName());

				if (value == null) {
					value =
						attributes.getValue(
							attributeDescriptor.getQualifiedName());
				}

				if (log.isTraceEnabled()) {
					log.trace("Attr URL:" + attributeDescriptor.getURI());
					log.trace(
						"Attr LocalName:" + attributeDescriptor.getLocalName());
					log.trace(value);
				}

				Updater updater = attributeDescriptor.getUpdater();
				log.trace(updater);
				if (updater != null && value != null) {
					updater.update(this, value);
				}
			}
		}
	}

}
