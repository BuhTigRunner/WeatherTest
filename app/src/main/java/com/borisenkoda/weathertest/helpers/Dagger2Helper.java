package com.borisenkoda.weathertest.helpers;

import java.lang.reflect.Method;
import java.util.HashMap;

public class Dagger2Helper {

    private static HashMap<Class<?>, HashMap<Class<?>, Method>> cache = new HashMap<>();

    public static <T, S extends T> void inject(Class<T> componentClass, S component, Object target) {

        HashMap<Class<?>, Method> methods = cache.get(componentClass);
        if (methods == null) {
            methods = getInjectors(componentClass);
            cache.put(componentClass, methods);
        }

        Class targetClass = target.getClass();
        Method method = methods.get(targetClass);
        if (method == null)
            throw new RuntimeException("No " + targetClass + " injector exists in " + componentClass + " component");

        try {
            method.invoke(component, target);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<Class<?>, Method> getInjectors(Class componentClass) {
        HashMap<Class<?>, Method> methods = new HashMap<>();
        for (Method method : componentClass.getMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (params.length == 1)
                methods.put(params[0], method);
        }
        return methods;
    }
}
