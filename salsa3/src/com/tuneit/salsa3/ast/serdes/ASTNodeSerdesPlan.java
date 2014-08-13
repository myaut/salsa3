package com.tuneit.salsa3.ast.serdes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.tuneit.salsa3.ast.ASTNode;
import com.tuneit.salsa3.ast.serdes.annotations.*;

public class ASTNodeSerdesPlan {
	public class Param {
		private int index;
		private String name;
		private boolean optional;
		private Object defaultValue;
		
		private String shortName;
		
		private String getterName;
		private Method getter; 
		
		public Param(int index, String name, boolean optional) {
			this.index = index;
			this.name = name;
			this.optional = optional;
			this.defaultValue = null;
		}
		
		public void findGetter(Class<?> nodeClass) {
			String firstLetter = name.substring(0, 1);
			String restOfName = name.substring(1);			
			getterName = "get" + firstLetter.toUpperCase() + restOfName;
			
			try {
				getter = nodeClass.getDeclaredMethod(getterName);
			}
			catch(NoSuchMethodException e) {
				try {
					getter = nodeClass.getSuperclass().getDeclaredMethod(getterName);
				} catch (NoSuchMethodException e1) {
					getter = null;
				} catch (SecurityException e1) {
					getter = null;
				}
			}
		}
		
		public String getGetterName() {
			return getterName;
		}
		
		public Method getGetter() {
			return getter;
		}
		
		public void setDefaultValue(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public boolean isOptionalAndDefault(Object o) {		
			if(!isOptional())
				return false;
			
			return ((this.defaultValue == null && o == null) ||
					 (this.defaultValue != null && o != null && this.defaultValue.equals(o)));
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			return o;
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
			return o;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getShortName() {
			return shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}

		public int getIndex() {
			return index;
		}

		public boolean isOptional() {
			return optional;
		}

		public Object getDefaultValue() {
			return defaultValue;
		} 
	}
	
	@SuppressWarnings("rawtypes")
	public class EnumParam extends Param {
		private Class enumClass;
		
		public EnumParam(int index, String name, boolean optional, Class enumClass) {
			super(index, name, optional);
			
			this.enumClass = enumClass;
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			return o.toString();
		}
		
		@SuppressWarnings("unchecked")
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) {
			return Enum.valueOf(enumClass, (String) o);
		}
	}
	
	
	public class NodeParam extends Param {
		public NodeParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			return ASTNodeSerdes.serializeNode(serializer, o);
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
			return ASTNodeSerdes.deserializeNode(deserializer, o);
		}
	}
	
	public class ListParam extends Param {
		private Param elementParam;
		
		public ListParam(int index, String name, boolean optional, Param elementParam) {
			super(index, name, optional);
			this.elementParam = elementParam;
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object ooList) throws ASTNodeSerdesException {
			Object list = serializer.createList();
			List<?> oList = (List<?>) ooList;
			
			for(Object item : oList) {
				Object o = elementParam.serialize(serializer, item);
				serializer.addToList(list, o);
			}
			
			return list;
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Object deserialize(ASTNodeDeserializer deserializer, Object list) throws ASTNodeSerdesException {
			List oList = new ArrayList();
			Iterator<?> iterator = deserializer.getListIterator(list); 
			
			while(iterator.hasNext()) {
				Object o = elementParam.deserialize(deserializer, iterator.next());
				
				oList.add(o);
			}
			
			return oList;
		}
	}	
	
	private Class<?> nodeClass;
	private String nodeClassName;
	private List<Param> params;
	private Set<String> shortParamNames;
	
	public ASTNodeSerdesPlan(Class<?> nodeClass) {
		this.nodeClass = nodeClass;
		this.params = new ArrayList<Param>();
		this.shortParamNames = new HashSet<String>();
		
		String className = nodeClass.getName();
		int beginIndex = className.lastIndexOf('.');
		nodeClassName = className.substring(beginIndex + 1);
		
		generatePlan();
	}
	
	@SuppressWarnings("rawtypes")
	private void generatePlan() {
		for(Field field : nodeClass.getDeclaredFields()) {			
			if(!field.isAnnotationPresent(Parameter.class)) {
				continue;
			}
			
			Param pampam;
			Parameter parameter = field.getAnnotation(Parameter.class);
			
			int offset = parameter.offset();
			String name = field.getName();
			boolean optional = parameter.optional();
			
			if(field.isAnnotationPresent(NodeParameter.class)) {
				pampam = new NodeParam(offset, name, optional);
			}
			else if(field.isAnnotationPresent(EnumParameter.class)) {
				EnumParameter enumParameter = field.getAnnotation(EnumParameter.class);
				Class enumClass = enumParameter.enumClass();
				pampam = new EnumParam(offset, name, optional, enumClass);
			}
			else {
				pampam = new Param(offset, name, optional);
			}
			
			if(field.isAnnotationPresent(DefaultIntegerValue.class)) {
				DefaultIntegerValue div = field.getAnnotation(DefaultIntegerValue.class);
				int defaultValue = div.value();
				
				pampam.setDefaultValue(defaultValue);
			}
			
			if(field.isAnnotationPresent(ListParameter.class)) {
				pampam = new ListParam(offset, name, optional, pampam);
			}
			
			addParam(pampam);
			
			/*
			System.out.println(nodeClass.getName() + "." + field.getName() + " [" + offset + 
								"] " + pampam.getClass().getName());
			for(Annotation a : field.getAnnotations()) {
				System.out.println(a.annotationType().getName());
			}
			*/
		}
	}
	
	private Param addParam(Param param) {
		String longName = param.getName();
		String baseShortName = longName.substring(0, 1) + longName.substring(1).replaceAll("[a-z]", "");
		String shortName = baseShortName;
		int counter = 1;
		
		while(shortParamNames.contains(shortName)) {
			++counter;
			shortName = baseShortName + counter;
		}
		
		shortParamNames.add(shortName);
		param.setShortName(shortName);
		
		params.add(param);
		param.findGetter(nodeClass);
		
		return param;
	}
	
	public String getClassName() {
		return nodeClassName;
	}
	
	public Object serializeNode(ASTNodeSerializer serializer, Object node) throws ASTNodeSerdesException {
		Object serializedNode = serializer.createNode(nodeClassName);
		
		for(Param param : params) {
			String getterName = param.getGetterName();
			Method getter = param.getGetter();
			
			if(getter == null) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
						" is missing in class" + nodeClassName);
			}
			
			try {				
				Object o = getter.invoke(node);
				
				if(param.isOptionalAndDefault(o)) {
					continue;
				}
				
				if(o == null) {
					throw new ASTNodeSerdesException("Getter " + getterName + 
							" returned null for class" + nodeClassName);
				}
				
				serializer.addToNode(serializedNode, param.getName(), param.getShortName(), param.serialize(serializer, o));
			} catch (SecurityException e) {
				throw new ASTNodeSerdesException("Security exception for getter" + getterName + 
								" from class" + nodeClassName, e);
			} catch (IllegalAccessException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"has invalid rights in class" + nodeClassName, e);
			} catch (IllegalArgumentException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"got invalid argument in class" + nodeClassName, e);
			} catch (InvocationTargetException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"invokation error in class" + nodeClassName, e);
			} catch (ClassCastException e) {
				throw new ASTNodeSerdesException("Unexpected class of parameter for class" + 
						nodeClassName, e);
			}
		}
		
		return serializedNode;
	}
	
	public ASTNode deserializeNode(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
		Map<Integer, Object> paramValues = new TreeMap<Integer, Object>();
		Map<Integer, Class<?>> paramClasses = new TreeMap<Integer, Class<?>>();
		
		for(Param param : params) {
			Object value = deserializer.getNodeParam(o, param.getName(), param.getShortName());
			
			if(value == null) {
				if(!param.isOptional()) {
					throw new ASTNodeSerdesException("Required parameter '" + param.getName() + 
											"' is missing  for class " + nodeClass.getName());
				}
				
				continue;
			}
			
			if(paramValues.containsKey(param.getIndex())) {
				throw new ASTNodeSerdesException("Duplicate parameter '" + param.getName() + 
									"' at position " + Integer.toString(param.getIndex()) + 
									" for class " + nodeClass.getName());
			}
			
			value = param.deserialize(deserializer, value);
			
			paramValues.put(param.getIndex(), value);
			paramClasses.put(param.getIndex(), value.getClass());
						
			// System.out.println(nodeClass.getName() + "." + param.getName() + " " +value.getClass().getName());
		}
		
		try {
			/* pure toArray() breaks type hints (?) and causes compilation error,
			 * so convert list to array _manually_ */
			Class<?>[] paramClassesArray = new Class<?>[paramClasses.size()]; 
			int i = 0;
			
			for(Object paramClassObject : paramClasses.values().toArray()) {
				Class<?> paramClass = (Class<?>) paramClassObject;
				
				if(List.class.isAssignableFrom(paramClass)) {
					/* While ASTNodes use ArrayList<?> for lists, they declare getters/constructors
					 * with interfaced List<?>. getDeclaredConstructor() doesn't recognize that,
					 * so give him a clue. */
					paramClassesArray[i] = List.class;
				}
				else if(ASTNode.class.isAssignableFrom(paramClass)) {
					/* Same as ArrayList<?>, downgrade to most common class */
					paramClassesArray[i] = ASTNode.class;
				}
				else {
					paramClassesArray[i] = paramClass;
				}
				
				++i;
			}
			
			Constructor<?> ctor = nodeClass.getDeclaredConstructor(paramClassesArray);
			return (ASTNode) ctor.newInstance(paramValues.values().toArray());
		} catch (NoSuchMethodException e) {
			throw new ASTNodeSerdesException("Constructor is missing for class " + nodeClass.getName(), e);
		} catch (SecurityException e) {
			throw new ASTNodeSerdesException("Security exception for class " + nodeClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ASTNodeSerdesException("Constructor has invalid rights for class " + nodeClass.getName(), e);
		} catch (IllegalArgumentException e) {
			throw new ASTNodeSerdesException("Constructor got invalid argument for class " + nodeClass.getName(), e);
		} catch (InvocationTargetException e) {
			throw new ASTNodeSerdesException("Constructor invokation target error for class " + nodeClass.getName(), e);
		} catch (InstantiationException e) {
			throw new ASTNodeSerdesException("Instantiation exception for class " + nodeClass.getName(), e);
		} catch (ClassCastException e) {
			throw new ASTNodeSerdesException("Unexpected class of parameter", e);
		}
		
	}
}
