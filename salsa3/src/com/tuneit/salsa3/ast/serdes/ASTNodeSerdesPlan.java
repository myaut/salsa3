package com.tuneit.salsa3.ast.serdes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.tuneit.salsa3.ast.ASTNode;

public class ASTNodeSerdesPlan {
	public abstract class Param {
		public int index;
		public String name;
		public boolean optional;
		public Object defaultValue;
		
		public Param(int index, String name, boolean optional) {
			this.index = index;
			this.name = name;
			this.optional = optional;
			this.defaultValue = null;
		}
		
		public String getGetterName() {
			String firstLetter = name.substring(0, 1);
			String restOfName = name.substring(1);
			
			return "get" + firstLetter.toUpperCase() + restOfName;
		}
		
		public void setDefaultValue(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public boolean isOptionalAndDefault(Object o) {			
			return this.optional && 
					((this.defaultValue == null && o == null) ||
					 (this.defaultValue != null && o != null && this.defaultValue.equals(o)));
		}
		
		public Object serialize(ASTNodeSerializer serializer, Object o) throws ASTNodeSerdesException {
			return o;
		}
		
		public Object deserialize(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
			return o;
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
	
	private Class<?> nodeClass;
	private List<Param> params;
	
	public ASTNodeSerdesPlan(Class<?> nodeClass) {
		this.nodeClass = nodeClass;
		this.params = new ArrayList<Param>();
	}
	
	public Param addStringParam(int index, String name, boolean optional) {
		Param param = new StringParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public Param addIntegerParam(int index, String name, boolean optional) {
		Param param = new IntegerParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public Param addBooleanParam(int index, String name, boolean optional) {
		Param param = new IntegerParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public Param addStringListParam(int index, String name, boolean optional) {
		Param param = new StringListParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public <T extends Enum<T>> Param addEnumParam(int index, String name, boolean optional, Class<T> enumClass) {
		Param param = new EnumParam<T>(index, name, optional, enumClass);
		params.add(param);
		
		return param;
	}
	
	public Param addNodeParam(int index, String name, boolean optional) {
		Param param = new NodeParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public Param addNodeListParam(int index, String name, boolean optional) {
		Param param = new NodeListParam(index, name, optional);
		params.add(param);
		
		return param;
	}
	
	public Object serializeNode(ASTNodeSerializer serializer, Object node) throws ASTNodeSerdesException {
		Object serializedNode = serializer.createNode(nodeClass.getName());
		
		for(Param param : params) {
			String getterName = param.getGetterName();
			Method getter;
			try {
				try {
					getter = nodeClass.getDeclaredMethod(getterName);
				}
				catch(NoSuchMethodException e) {
					getter = nodeClass.getSuperclass().getDeclaredMethod(getterName);
				}
				Object o = getter.invoke(node);
				
				if(param.isOptionalAndDefault(o)) {
					continue;
				}
				
				serializer.addToNode(serializedNode, param.name, param.serialize(serializer, o));
			} catch (NoSuchMethodException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								" is missing in class" + nodeClass.getName(), e);
			} catch (SecurityException e) {
				throw new ASTNodeSerdesException("Security exception for getter" + getterName + 
								" from class" + nodeClass.getName(), e);
			} catch (IllegalAccessException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"has invalid rights in class" + nodeClass.getName(), e);
			} catch (IllegalArgumentException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"got invalid argument in class" + nodeClass.getName(), e);
			} catch (InvocationTargetException e) {
				throw new ASTNodeSerdesException("Getter " + getterName + 
								"invokation error in class" + nodeClass.getName(), e);
			} catch (ClassCastException e) {
				throw new ASTNodeSerdesException("Unexpected class of parameter for class" + 
								nodeClass.getName(), e);
			}
		}
		
		return serializedNode;
	}
	
	public ASTNode deserializeNode(ASTNodeDeserializer deserializer, Object o) throws ASTNodeSerdesException {
		List<Object> paramValues = new ArrayList<Object>();
		List<Class<?>> paramClasses = new ArrayList<Class<?>>();
		
		int index = -1;
		
		for(Param param : params) {
			Object value = deserializer.getNodeParam(o, param.name);
			
			if(value == null) {
				if(!param.optional) {
					throw new ASTNodeSerdesException("Required parameter '" + param.name + 
											"' is missing  for class " + nodeClass.getName());
				}
				
				continue;
			}
			
			// System.out.println(param.name + " " + index + " " + param.index);
			
			if(index == param.index) {
				throw new ASTNodeSerdesException("Duplicate parameter '" + param.name + 
									"' at position " + Integer.toString(index) + 
									" for class " + nodeClass.getName());
			}
			
			index = param.index;			
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
