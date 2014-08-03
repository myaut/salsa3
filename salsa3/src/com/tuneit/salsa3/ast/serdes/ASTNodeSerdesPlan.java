package com.tuneit.salsa3.ast.serdes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import com.tuneit.salsa3.ast.ASTNode;

public class ASTNodeSerdesPlan {
	public abstract class Param {
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
	
	public class StringParam extends Param {
		public StringParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
	}
	
	public class IntegerParam extends Param {
		public IntegerParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
	}
	
	public class BooleanParam extends Param {
		public BooleanParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
	}
	
	public class EnumParam<T extends Enum<T>> extends Param {
		private Class<T> enumClass;
		
		public EnumParam(int index, String name, boolean optional, Class<T> enumClass) {
			super(index, name, optional);
			
			this.enumClass = enumClass;
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			return o.toString();
		}
		
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
	
	public class NodeListParam extends Param {
		public NodeListParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			Object list = serializer.createList();
			List<?> oList = (List<?>) o;
			
			for(Object item : oList) {
				Object node = ASTNodeSerdes.serializeNode(serializer, item);
				serializer.addToList(list, node);
			}
			
			return list;
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
			List<ASTNode> nodeList = new ArrayList<ASTNode>();
			Iterator<?> iterator = deserializer.getListIterator(o); 
			
			while(iterator.hasNext()) {
				Object node = iterator.next();
				ASTNode astNode = ASTNodeSerdes.deserializeNode(deserializer, node);
				
				nodeList.add(astNode);
			}
			
			return nodeList;
		}
	}
	
	public class StringListParam extends Param {
		public StringListParam(int index, String name, boolean optional) {
			super(index, name, optional);
		}
		
		@SuppressWarnings("unchecked")
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			Object list = serializer.createList();
			List<String> oList = (List<String>) o;
			
			for(String str : oList) {
				serializer.addToList(list, str);
			}
			
			return list;
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException  {
			List<String> stringList = new ArrayList<String>();
			Iterator<?> iterator = deserializer.getListIterator(o); 
			
			while(iterator.hasNext()) {
				String string = (String) iterator.next();
				
				stringList.add(string);
			}
			
			return stringList;
		}
	}
	
	public class EnumListParam<T extends Enum<T>> extends Param {
		private Class<T> enumClass;
		
		public EnumListParam(int index, String name, boolean optional, Class<T> enumClass) {
			super(index, name, optional);
			
			this.enumClass = enumClass;
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			Object list = serializer.createList();
			List<?> eList = (List<?>) o;
			
			for(Object e : eList) {
				serializer.addToList(list, e.toString());
			}
			
			return list;
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
			List<Object> enumList = new ArrayList<Object>();
			Iterator<?> iterator = deserializer.getListIterator(o); 
			
			while(iterator.hasNext()) {
				String string = (String) iterator.next();
				
				enumList.add(Enum.valueOf(enumClass, string));
			}
			
			return enumList;
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
	}
	

	public String getClassName() {
		return nodeClassName;
	}
	
	public Param addStringParam(int index, String name, boolean optional) {
		return addParam(new StringParam(index, name, optional));
	}
	
	public Param addIntegerParam(int index, String name, boolean optional) {
		return addParam(new IntegerParam(index, name, optional));
	}
	
	public Param addBooleanParam(int index, String name, boolean optional) {
		return addParam(new IntegerParam(index, name, optional));
	}
	
	public Param addStringListParam(int index, String name, boolean optional) {
		return addParam(new StringListParam(index, name, optional));
	}
	
	public <T extends Enum<T>> Param addEnumParam(int index, String name, boolean optional, Class<T> enumClass) {
		return addParam(new EnumParam<T>(index, name, optional, enumClass));
	}
	
	public Param addNodeParam(int index, String name, boolean optional) {
		return addParam(new NodeParam(index, name, optional));
	}
	
	public Param addNodeListParam(int index, String name, boolean optional) {
		return addParam(new NodeListParam(index, name, optional));
	}
	
	public <T extends Enum<T>> Param addEnumListParam(int index, String name, boolean optional, Class<T> enumClass) {
		return addParam(new EnumListParam<T>(index, name, optional, enumClass));
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
		List<Object> paramValues = new ArrayList<Object>();
		List<Class<?>> paramClasses = new ArrayList<Class<?>>();
		
		int index = -1;
		
		for(Param param : params) {
			Object value = deserializer.getNodeParam(o, param.getName(), param.getShortName());
			
			if(value == null) {
				if(!param.isOptional()) {
					throw new ASTNodeSerdesException("Required parameter '" + param.getName() + 
											"' is missing  for class " + nodeClass.getName());
				}
				
				continue;
			}
			
			if(index == param.getIndex()) {
				throw new ASTNodeSerdesException("Duplicate parameter '" + param.getName() + 
									"' at position " + Integer.toString(index) + 
									" for class " + nodeClass.getName());
			}
			
			index = param.getIndex();			
			value = param.deserialize(deserializer, value);
			
			paramValues.add(value);
			paramClasses.add(value.getClass());
		}
		
		try {
			/* pure toArray() breaks type hints (?) and causes compilation error,
			 * so convert list to array _manually_ */
			Class<?>[] paramClassesArray = new Class<?>[paramClasses.size()]; 
			int i = 0;
			
			for(Object paramClassObject : paramClasses.toArray()) {
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
			return (ASTNode) ctor.newInstance(paramValues.toArray());
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
