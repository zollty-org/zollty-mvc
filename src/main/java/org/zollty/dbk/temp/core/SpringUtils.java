/*
 * @(#)SpringUtils.java
 * Create by Zollty_Tsow on 2013-11-30 
 * you may find ZollTy at csdn, github, oschina, stackoverflow...
 * e.g. https://github.com/zollty  http://www.cnblogs.com/zollty 
 * 
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 */
package org.zollty.dbk.temp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.zollty.framework.util.Assert;
import org.zollty.dbk.temp.beans.BeanInstantiationException;
import org.zollty.dbk.util.ReflectionUtils;


/**
 * @author zollty 
 * @since 2013-11-30
 */
public class SpringUtils {
	
	
	/**  org.springframework.beans.BeanUtils
	 * Convenience method to instantiate a class using its no-arg constructor.
	 * As this method doesn't try to load classes by name, it should avoid
	 * class-loading issues.
	 * @param clazz class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 */
	public static <T> T instantiate(Class<T> clazz) throws BeanInstantiationException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new BeanInstantiationException(clazz, "Specified class is an interface");
		}
		try {
			return clazz.newInstance();
		}
		catch (InstantiationException ex) {
			throw new BeanInstantiationException(clazz, "Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new BeanInstantiationException(clazz, "Is the constructor accessible?", ex);
		}
	}
	
	/**
	 * Instantiate a class using its no-arg constructor.
	 * As this method doesn't try to load classes by name, it should avoid
	 * class-loading issues.
	 * <p>Note that this method tries to set the constructor accessible
	 * if given a non-accessible (that is, non-public) constructor.
	 * @param clazz class to instantiate
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 */
	public static <T> T instantiateClass(Class<T> clazz) throws BeanInstantiationException {
		Assert.notNull(clazz, "Class must not be null");
		if (clazz.isInterface()) {
			throw new BeanInstantiationException(clazz, "Specified class is an interface");
		}
		try {
			return instantiateClass(clazz.getDeclaredConstructor());
		}
		catch (NoSuchMethodException ex) {
			throw new BeanInstantiationException(clazz, "No default constructor found", ex);
		}
	}
	
	/**
	 * Convenience method to instantiate a class using the given constructor.
	 * As this method doesn't try to load classes by name, it should avoid
	 * class-loading issues.
	 * <p>Note that this method tries to set the constructor accessible
	 * if given a non-accessible (that is, non-public) constructor.
	 * @param ctor the constructor to instantiate
	 * @param args the constructor arguments to apply
	 * @return the new instance
	 * @throws BeanInstantiationException if the bean cannot be instantiated
	 */
	public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
		Assert.notNull(ctor, "Constructor must not be null");
		try {
			ReflectionUtils.makeAccessible(ctor);
			return ctor.newInstance(args);
		}
		catch (InstantiationException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Is it an abstract class?", ex);
		}
		catch (IllegalAccessException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Is the constructor accessible?", ex);
		}
		catch (IllegalArgumentException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Illegal arguments for constructor", ex);
		}
		catch (InvocationTargetException ex) {
			throw new BeanInstantiationException(ctor.getDeclaringClass(),
					"Constructor threw exception", ex.getTargetException());
		}
	}
	
	
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Trim <i>all</i> whitespace from the given String:
	 * leading, trailing, and inbetween characters.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			}
			else {
				index++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Determine whether the given array is empty:
	 * i.e. {@code null} or of zero length.
	 * @param array the array to check
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}
	
	/**
	 * Turn given source String array into sorted array.
	 * @param array the source array
	 * @return the sorted array (never {@code null})
	 */
	public static String[] sortStringArray(String[] array) {
		if (isEmpty(array)) {
			return new String[0];
		}
		Arrays.sort(array);
		return array;
	}
	
	
	public static final int BUFFER_SIZE = 4096;
	/** StreamUtils
	 * Copy the contents of the given InputStream to the given OutputStream.
	 * Leaves both streams open when done.
	 * @param in the InputStream to copy from
	 * @param out the OutputStream to copy to
	 * @return the number of bytes copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(InputStream in, OutputStream out) throws IOException {
		Assert.notNull(in, "No InputStream specified");
		Assert.notNull(out, "No OutputStream specified");
		int byteCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}
	

	/**
	 * Copy the contents of the given byte array to the given OutputStream.
	 * Closes the stream when done.
	 * @param in the byte array to copy from
	 * @param out the OutputStream to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(byte[] in, OutputStream out) throws IOException {
		Assert.notNull(in, "No input byte array specified");
		Assert.notNull(out, "No OutputStream specified");
		try {
			out.write(in);
		}
		finally {
			try {
				out.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	/**
	 * Copy the contents of the given String to the given output Writer.
	 * Closes the writer when done.
	 * @param in the String to copy from
	 * @param out the Writer to copy to
	 * @throws IOException in case of I/O errors
	 */
	public static void copy(String in, Writer out) throws IOException {
		Assert.notNull(in, "No input String specified");
		Assert.notNull(out, "No Writer specified");
		try {
			out.write(in);
		}
		finally {
			try {
				out.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	/**
	 * Copy the contents of the given Reader to the given Writer.
	 * Closes both when done.
	 * @param in the Reader to copy from
	 * @param out the Writer to copy to
	 * @return the number of characters copied
	 * @throws IOException in case of I/O errors
	 */
	public static int copy(Reader in, Writer out) throws IOException {
		Assert.notNull(in, "No Reader specified");
		Assert.notNull(out, "No Writer specified");
		try {
			int byteCount = 0;
			char[] buffer = new char[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				byteCount += bytesRead;
			}
			out.flush();
			return byteCount;
		}
		finally {
			try {
				in.close();
			}
			catch (IOException ex) {
			}
			try {
				out.close();
			}
			catch (IOException ex) {
			}
		}
	}
	
	/**
	 * Copy the contents of the given Reader into a String.
	 * Closes the reader when done.
	 * @param in the reader to copy from
	 * @return the String that has been copied to
	 * @throws IOException in case of I/O errors
	 */
	public static String copyToString(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}
	
	/** Assert
	 * Assert a boolean expression, throwing {@code IllegalArgumentException}
	 * if the test result is {@code false}.
	 * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
	 * @param expression a boolean expression
	 * @param message the exception message to use if the assertion fails
	 * @throws IllegalArgumentException if expression is {@code false}
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}
	
	//---------------------------------------------------------------------
	// Convenience methods for content-based equality/hash-code handling
	//---------------------------------------------------------------------

	/** ObjectUtils
	 * Determine if the given objects are equal, returning {@code true}
	 * if both are {@code null} or {@code false} if only one is
	 * {@code null}.
	 * <p>Compares arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	/** PatternMatchUtils
	 * Match a String against the given pattern, supporting the following simple
	 * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy" matches (with an
	 * arbitrary number of pattern parts), as well as direct equality.
	 * @param pattern the pattern to match against
	 * @param str the String to match
	 * @return whether the String matches the given pattern
	 */
	public static boolean simpleMatch(String pattern, String str) {
		if (pattern == null || str == null) {
			return false;
		}
		int firstIndex = pattern.indexOf('*');
		if (firstIndex == -1) {
			return pattern.equals(str);
		}
		if (firstIndex == 0) {
			if (pattern.length() == 1) {
				return true;
			}
			int nextIndex = pattern.indexOf('*', firstIndex + 1);
			if (nextIndex == -1) {
				return str.endsWith(pattern.substring(1));
			}
			String part = pattern.substring(1, nextIndex);
			int partIndex = str.indexOf(part);
			while (partIndex != -1) {
				if (simpleMatch(pattern.substring(nextIndex), str.substring(partIndex + part.length()))) {
					return true;
				}
				partIndex = str.indexOf(part, partIndex + 1);
			}
			return false;
		}
		return (str.length() >= firstIndex &&
				pattern.substring(0, firstIndex).equals(str.substring(0, firstIndex)) &&
				simpleMatch(pattern.substring(firstIndex), str.substring(firstIndex)));
	}
	
	
	/** StringUtils
	 * Take a String which is a delimited list and convert it to a String array.
	 * <p>A single delimiter can consists of more than one character: It will still
	 * be considered as single delimiter string, rather than as bunch of potential
	 * delimiter characters - in contrast to {@code tokenizeToStringArray}.
	 * @param str the input String
	 * @param delimiter the delimiter between elements (this is a single delimiter,
	 * rather than a bunch individual delimiter characters)
	 * @param charsToDelete a set of characters to delete. Useful for deleting unwanted
	 * line breaks: e.g. "\r\n\f" will delete all new lines and line feeds in a String.
	 * @return an array of the tokens in the list
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] {str};
		}
		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		}
		else {
			int pos = 0;
			int delPos;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}
	
	/** StringUtils
	 * Copy the given Collection into a String array.
	 * The Collection must contain String elements only.
	 * @param collection the Collection to copy
	 * @return the String array ({@code null} if the passed-in
	 * Collection was {@code null})
	 */
	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return collection.toArray(new String[collection.size()]);
	}
	
	/** StringUtils
	 * Delete any character in a given String.
	 * @param inString the original String
	 * @param charsToDelete a set of characters to delete.
	 * E.g. "az\n" will delete 'a's, 'z's and new lines.
	 * @return the resulting String
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}
	
	/** StringUtils
	 * Trim leading and trailing whitespace from the given String.
	 * @param str the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/** StringUtils
	 * Check whether the given CharSequence contains any whitespace characters.
	 * @param str the CharSequence to check (may be {@code null})
	 * @return {@code true} if the CharSequence is not empty and
	 * contains at least 1 whitespace character
	 * @see Character#isWhitespace
	 */
	public static boolean containsWhitespace(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	
	/** CollectionUtils
	 * Determine whether the given Collection only contains a single unique object.
	 * @param collection the Collection to check
	 * @return {@code true} if the collection contains a single reference or
	 * multiple references to the same instance, {@code false} else
	 */
	public static boolean hasUniqueObject(Collection collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		boolean hasCandidate = false;
		Object candidate = null;
		for (Object elem : collection) {
			if (!hasCandidate) {
				hasCandidate = true;
				candidate = elem;
			}
			else if (candidate != elem) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Convert the given number into an instance of the given target class.
	 * @param number the number to convert
	 * @param targetClass the target class to convert to
	 * @return the converted number
	 * @throws IllegalArgumentException if the target class is not supported
	 * (i.e. not a standard Number subclass as included in the JDK)
	 * @see java.lang.Byte
	 * @see java.lang.Short
	 * @see java.lang.Integer
	 * @see java.lang.Long
	 * @see java.math.BigInteger
	 * @see java.lang.Float
	 * @see java.lang.Double
	 * @see java.math.BigDecimal
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number> T convertNumberToTargetClass(Number number, Class<T> targetClass)
			throws IllegalArgumentException {

		Assert.notNull(number, "Number must not be null");
		Assert.notNull(targetClass, "Target class must not be null");

		if (targetClass.isInstance(number)) {
			return (T) number;
		}
		else if (targetClass.equals(Byte.class)) {
			long value = number.longValue();
			if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Byte(number.byteValue());
		}
		else if (targetClass.equals(Short.class)) {
			long value = number.longValue();
			if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Short(number.shortValue());
		}
		else if (targetClass.equals(Integer.class)) {
			long value = number.longValue();
			if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
				raiseOverflowException(number, targetClass);
			}
			return (T) new Integer(number.intValue());
		}
		else if (targetClass.equals(Long.class)) {
			return (T) new Long(number.longValue());
		}
		else if (targetClass.equals(BigInteger.class)) {
			if (number instanceof BigDecimal) {
				// do not lose precision - use BigDecimal's own conversion
				return (T) ((BigDecimal) number).toBigInteger();
			}
			else {
				// original value is not a Big* number - use standard long conversion
				return (T) BigInteger.valueOf(number.longValue());
			}
		}
		else if (targetClass.equals(Float.class)) {
			return (T) new Float(number.floatValue());
		}
		else if (targetClass.equals(Double.class)) {
			return (T) new Double(number.doubleValue());
		}
		else if (targetClass.equals(BigDecimal.class)) {
			// always use BigDecimal(String) here to avoid unpredictability of BigDecimal(double)
			// (see BigDecimal javadoc for details)
			return (T) new BigDecimal(number.toString());
		}
		else {
			throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
					number.getClass().getName() + "] to unknown target class [" + targetClass.getName() + "]");
		}
	}

	/**
	 * Raise an overflow exception for the given number and target class.
	 * @param number the number we tried to convert
	 * @param targetClass the target class we tried to convert to
	 */
	private static void raiseOverflowException(Number number, Class targetClass) {
		throw new IllegalArgumentException("Could not convert number [" + number + "] of type [" +
				number.getClass().getName() + "] to target class [" + targetClass.getName() + "]: overflow");
	}
	
//	/** ReflectionUtils
//	 * Determine whether the given field is a "public static final" constant.
//	 * @param field the field to check
//	 */
//	public static boolean isPublicStaticFinal(Field field) {
//		int modifiers = field.getModifiers();
//		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
//	}
//	
//	/** ReflectionUtils
//	 * Attempt to find a {@link Method} on the supplied class with the supplied name
//	 * and no parameters. Searches all superclasses up to {@code Object}.
//	 * <p>Returns {@code null} if no {@link Method} can be found.
//	 * @param clazz the class to introspect
//	 * @param name the name of the method
//	 * @return the Method object, or {@code null} if none found
//	 */
//	public static Method findMethod(Class<?> clazz, String name) {
//		return findMethod(clazz, name, new Class[0]);
//	}
//
//	/** ReflectionUtils
//	 * Attempt to find a {@link Method} on the supplied class with the supplied name
//	 * and parameter types. Searches all superclasses up to {@code Object}.
//	 * <p>Returns {@code null} if no {@link Method} can be found.
//	 * @param clazz the class to introspect
//	 * @param name the name of the method
//	 * @param paramTypes the parameter types of the method
//	 * (may be {@code null} to indicate any signature)
//	 * @return the Method object, or {@code null} if none found
//	 */
//	public static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
//		Assert.notNull(clazz, "Class must not be null");
//		Assert.notNull(name, "Method name must not be null");
//		Class<?> searchType = clazz;
//		while (searchType != null) {
//			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
//			for (Method method : methods) {
//				if (name.equals(method.getName()) &&
//						(paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
//					return method;
//				}
//			}
//			searchType = searchType.getSuperclass();
//		}
//		return null;
//	}
	
//	/** ClassUtils
//	 * Given a method, which may come from an interface, and a target class used
//	 * in the current reflective invocation, find the corresponding target method
//	 * if there is one. E.g. the method may be {@code IFoo.bar()} and the
//	 * target class may be {@code DefaultFoo}. In this case, the method may be
//	 * {@code DefaultFoo.bar()}. This enables attributes on that method to be found.
//	 * <p><b>NOTE:</b> In contrast to {@link org.springframework.aop.support.AopUtils#getMostSpecificMethod},
//	 * this method does <i>not</i> resolve Java 5 bridge methods automatically.
//	 * Call {@link org.springframework.core.BridgeMethodResolver#findBridgedMethod}
//	 * if bridge method resolution is desirable (e.g. for obtaining metadata from
//	 * the original method definition).
//	 * <p><b>NOTE:</b> Since Spring 3.1.1, if Java security settings disallow reflective
//	 * access (e.g. calls to {@code Class#getDeclaredMethods} etc, this implementation
//	 * will fall back to returning the originally provided method.
//	 * @param method the method to be invoked, which may come from an interface
//	 * @param targetClass the target class for the current invocation.
//	 * May be {@code null} or may not even implement the method.
//	 * @return the specific target method, or the original method if the
//	 * {@code targetClass} doesn't implement it or is {@code null}
//	 */
//	public static Method getMostSpecificMethod(Method method, Class<?> targetClass) {
//		if (method != null && isOverridable(method, targetClass) &&
//				targetClass != null && !targetClass.equals(method.getDeclaringClass())) {
//			try {
//				if (Modifier.isPublic(method.getModifiers())) {
//					try {
//						return targetClass.getMethod(method.getName(), method.getParameterTypes());
//					}
//					catch (NoSuchMethodException ex) {
//						return method;
//					}
//				}
//				else {
//					Method specificMethod =
//							findMethod(targetClass, method.getName(), method.getParameterTypes());
//					return (specificMethod != null ? specificMethod : method);
//				}
//			}
//			catch (AccessControlException ex) {
//				// Security settings are disallowing reflective access; fall back to 'method' below.
//			}
//		}
//		return method;
//	}
//	
//	/** ClassUtils
//	 * Determine whether the given method is overridable in the given target class.
//	 * @param method the method to check
//	 * @param targetClass the target class to check against
//	 */
//	private static boolean isOverridable(Method method, Class<?> targetClass) {
//		if (Modifier.isPrivate(method.getModifiers())) {
//			return false;
//		}
//		if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
//			return true;
//		}
//		return getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass));
//	}
//	
//	/** ClassUtils
//	 * Determine the name of the package of the given class,
//	 * e.g. "java.lang" for the {@code java.lang.String} class.
//	 * @param clazz the class
//	 * @return the package name, or the empty String if the class
//	 * is defined in the default package
//	 */
//	public static String getPackageName(Class<?> clazz) {
//		Assert.notNull(clazz, "Class must not be null");
//		return getPackageName(clazz.getName());
//	}
//
//	/** ClassUtils The package separator character '.' */
//	private static final char PACKAGE_SEPARATOR = '.';
//	
//	/** ClassUtils
//	 * Determine the name of the package of the given fully-qualified class name,
//	 * e.g. "java.lang" for the {@code java.lang.String} class name.
//	 * @param fqClassName the fully-qualified class name
//	 * @return the package name, or the empty String if the class
//	 * is defined in the default package
//	 */
//	public static String getPackageName(String fqClassName) {
//		Assert.notNull(fqClassName, "Class name must not be null");
//		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
//		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
//	}
//
//	
//	/** ClassUtils
//	 * Return the user-defined class for the given instance: usually simply
//	 * the class of the given instance, but the original class in case of a
//	 * CGLIB-generated subclass.
//	 * @param instance the instance to check
//	 * @return the user-defined class
//	 */
//	public static Class<?> getUserClass(Object instance) {
//		Assert.notNull(instance, "Instance must not be null");
//		return getUserClass(instance.getClass());
//	}
//
//	/** ClassUtils The CGLIB class separator character "$$" */
//	public static final String CGLIB_CLASS_SEPARATOR = "$$";
//	
//	/** ClassUtils
//	 * Return the user-defined class for the given class: usually simply the given
//	 * class, but the original class in case of a CGLIB-generated subclass.
//	 * @param clazz the class to check
//	 * @return the user-defined class
//	 */
//	public static Class<?> getUserClass(Class<?> clazz) {
//		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
//			Class<?> superClass = clazz.getSuperclass();
//			if (superClass != null && !Object.class.equals(superClass)) {
//				return superClass;
//			}
//		}
//		return clazz;
//	}
	
}
