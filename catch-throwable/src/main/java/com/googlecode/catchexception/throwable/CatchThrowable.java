/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.catchexception.throwable;

import org.assertj.core.api.ThrowableAssert;

import com.googlecode.catchexception.throwable.internal.ThrowableHolder;

/**
 * @author rwoo
 * @since 1.2.0
 */
public class CatchThrowable {

    /**
     * Returns the throwable caught during the last call on the proxied object in the current thread.
     *
     * @param <E> This type parameter makes some type casts redundant.
     * @return Returns the throwable caught during the last call on the proxied object in the current thread - if the
     * call was made through a proxy that has been created via {@link #verifyThrowable(Object, Class)
     * verifyThrowable()} or {@link #catchThrowable(Object, Class) catchThrowable()}. Returns null the proxy has
     * not caught an throwable. Returns null if the caught throwable belongs to a class that is no longer
     * {@link ClassLoader loaded}.
     */
    public static Throwable caughtThrowable() {
        return ThrowableHolder.get();
    }

    /**
     * Use it to verify that an throwable is thrown and to get access to the thrown throwable (for further
     * verifications).
     * <p/>
     * The following example verifies that obj.doX() throws a Throwable:
     * <code><pre class="prettyprint lang-java">verifyThrowable(obj).doX(); // catch and verify
     * assert "foobar".equals(caughtThrowable().getMessage()); // further analysis
     * </pre></code>
     * <p/>
     * If <code>doX()</code> does not throw a <code>Throwable</code>, then a {@link ThrowableNotThrownAssertionError} is
     * thrown. Otherwise the thrown throwable can be retrieved via {@link #caughtThrowable()}.
     * <p/>
     *
     * @param <T> The type of the given <code>obj</code>.
     * @param obj The instance that shall be proxied. Must not be <code>null</code>.
     * @return Returns an object that verifies that each invocation on the underlying object throws an throwable.
     */
    public static void verifyThrowable(ThrowableAssert.ThrowingCallable actor) {
        verifyThrowable(actor, Throwable.class);
    }

    /**
     * Use it to verify that an throwable of specific type is thrown and to get access to the thrown throwable (for
     * further verifications).
     * <p/>
     * The following example verifies that obj.doX() throws a MyThrowable:
     * <code><pre class="prettyprint lang-java">verifyThrowable(obj, MyThrowable.class).doX(); // catch and verify
     * assert "foobar".equals(caughtThrowable().getMessage()); // further analysis
     * </pre></code>
     * <p/>
     * If <code>doX()</code> does not throw a <code>MyThrowable</code>, then a {@link ThrowableNotThrownAssertionError}
     * is thrown. Otherwise the thrown throwable can be retrieved via {@link #caughtThrowable()}.
     * <p/>
     *
     * @param <T>   The type of the given <code>obj</code>.
     * @param <E>   The type of the throwable that shall be caught.
     * @param obj   The instance that shall be proxied. Must not be <code>null</code>.
     * @param clazz The type of the throwable that shall be thrown by the underlying object. Must not be
     *              <code>null</code>
     *              .
     * @return Returns an object that verifies that each invocation on the underlying object throws an throwable of the
     * given type.
     */
    public static void verifyThrowable(ThrowableAssert.ThrowingCallable actor, Class<? extends Throwable> clazz) {
        validateArguments(actor, clazz);
        try {
            catchThrowable(actor, clazz, true);
        } catch (ThrowableNotThrownAssertionError e) {
            throw e;
        } catch (Throwable throwable) {
        }
    }

    /**
     * Use it to catch an throwable and to get access to the thrown throwable (for further verifications).
     * <p/>
     * In the following example you catch throwables that are thrown by obj.doX():
     * <code><pre class="prettyprint lang-java">catchThrowable(obj).doX(); // catch
     * if (caughtThrowable() != null) {
     * assert "foobar".equals(caughtThrowable().getMessage()); // further analysis
     * }</pre></code> If <code>doX()</code>
     * throws a throwable, then {@link #caughtThrowable()} will return the caught throwable. If <code>doX()</code> does
     * not throw a throwable, then {@link #caughtThrowable()} will return <code>null</code>.
     * <p/>
     *
     * @param <T> The type of the given <code>obj</code>.
     * @param obj The instance that shall be proxied. Must not be <code>null</code>.
     * @return Returns a proxy for the given object. The proxy catches throwables of the given type when a method on the
     * proxy is called.
     */
    public static void catchThrowable(ThrowableAssert.ThrowingCallable actor) {
        validateArguments(actor, Throwable.class);
        try {
            catchThrowable(actor, Throwable.class, false);
        } catch (Throwable throwable) {
        }
    }

    /**
     * Use it to catch an throwable of a specific type and to get access to the thrown throwable (for further
     * verifications).
     * <p/>
     * In the following example you catch throwables of type MyThrowable that are thrown by obj.doX():
     * <code><pre class="prettyprint lang-java">catchThrowable(obj, MyThrowable.class).doX(); // catch
     * if (caughtThrowable() != null) {
     * assert "foobar".equals(caughtThrowable().getMessage()); // further analysis
     * }</pre></code> If <code>doX()</code>
     * throws a <code>MyThrowable</code>, then {@link #caughtThrowable()} will return the caught throwable. If
     * <code>doX()</code> does not throw a <code>MyThrowable</code>, then {@link #caughtThrowable()} will return
     * <code>null</code>. If <code>doX()</code> throws an throwable of another type, i.e. not a subclass but another
     * class, then this throwable is not thrown and {@link #caughtThrowable()} will return <code>null</code>.
     * <p/>
     *
     * @param <T>   The type of the given <code>obj</code>.
     * @param <E>   The type of the throwable that shall be caught.
     * @param obj   The instance that shall be proxied. Must not be <code>null</code>.
     * @param clazz The type of the throwable that shall be caught. Must not be <code>null</code>.
     * @return Returns a proxy for the given object. The proxy catches throwables of the given type when a method on the
     * proxy is called.
     */
    public static void catchThrowable(ThrowableAssert.ThrowingCallable actor,
                                      Class<? extends Throwable> clazz) throws Throwable {
        validateArguments(actor, clazz);
        catchThrowable(actor, clazz, false);
    }

    private static void catchThrowable(ThrowableAssert.ThrowingCallable actor,
                                       Class<? extends Throwable> clazz, boolean assertException) throws Throwable {
        resetCaughtThrowable();
        Throwable e = ThrowableCaptor.captureThrowable(actor);
        if (e == null) {
            if (!assertException) {
                return;
            } else {
                throw new ThrowableNotThrownAssertionError(clazz);
            }
        }
        // is the thrown exception of the expected type?
        if (clazz.isAssignableFrom(e.getClass())) {
            ThrowableHolder.set(e);
        } else {
            if (assertException) {
                throw new ThrowableNotThrownAssertionError(clazz, e);
            } else {
                throw e;
            }
        }
    }

    private static void validateArguments(ThrowableAssert.ThrowingCallable actor, Class<? extends Throwable> clazz) {
        if (actor == null) throw new IllegalArgumentException("obj must not be null");
        if (clazz == null) throw new IllegalArgumentException("throwableClazz must not be null");
    }

    /**
     * Sets the {@link #caughtThrowable() caught throwable} to null. This does not affect throwables saved at threads
     * other than the current one.
     * <p/>
     * Actually you probably never need to call this method because each method call on a proxied object in the current
     * thread resets the caught throwable. But if you want to improve test isolation or if you want to 'clean up' after
     * testing (to avoid memory leaks), call the method before or after testing.
     */
    public static void resetCaughtThrowable() {
        ThrowableHolder.set(null);
    }

}
