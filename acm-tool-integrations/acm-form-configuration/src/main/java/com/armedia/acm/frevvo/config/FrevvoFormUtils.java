/**
 * 
 */
package com.armedia.acm.frevvo.config;

import java.lang.reflect.Field;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormUtils {

	/**
	 * The method will return value for given field name of the object.
	 * It uses reflection.
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E get(Object object, String fieldName) 
	{
	    Class<?> clazz = object.getClass();
	    while (clazz != null) 
	    {
	        try 
	        {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            return (E) field.get(object);
	        } 
	        catch (NoSuchFieldException e) 
	        {
	            clazz = clazz.getSuperclass();
	        } 
	        catch (Exception e) 
	        {
	            throw new IllegalStateException(e);
	        }
	    }
	    
	    return null;
	}
	
	/**
	 * This method will set the value for the property in the object for given name of the property.
	 * It uses reflection.
	 * 
	 * @param object
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	public static Object set(Object object, String fieldName, Object fieldValue) 
	{
	    Class<?> clazz = object.getClass();
	    while (clazz != null) 
	    {
	        try 
	        {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            field.set(object, fieldValue);
	            
	            return object;
	        } 
	        catch (NoSuchFieldException e) 
	        {
	            clazz = clazz.getSuperclass();
	        } 
	        catch (Exception e) 
	        {
	            throw new IllegalStateException(e);
	        }
	    }
	    
	    return object;
	}
	
}
