package com.nisipeanu.containerprotect.reflection;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;
import java.util.stream.Collectors;

public class ClassReflection {

    /**
     * Get all classes in a package
     *
     * @param packageName package as a string to get classes from
     * @return A set of Classes found in the package
     */
    public static Set<Class<?>> getClassesInPackage(String packageName) {
        ConfigurationBuilder config = new ConfigurationBuilder()
                .setScanners(new ResourcesScanner(), new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName))
                .filterInputsBy(new FilterBuilder().includePackage(packageName));

        Reflections reflections = new Reflections(config);
        return reflections.getSubTypesOf(Object.class);
    }

    /**
     * Filters superclasses out of the Set, leaving only top classes
     *
     * @param classes
     * @return A new filtered Set
     */
    public static Set<Class<?>> filterSuperClasses(Set<Class<?>> classes) {
        var filteredClasses = classes.stream().filter(c -> isTopExtender(c, classes));
        return filteredClasses.collect(Collectors.toSet());
    }

    /**
     * Checks if it no other classes from classList extends classToCheck
     *
     * @param classToCheck Class to check if it's the top extender
     * @param classList    List of Classes to check against
     * @return True if no class extends classToCheck, false otherwise
     */
    private static boolean isTopExtender(Class<?> classToCheck, Set<Class<?>> classList) {
        return classList.stream().noneMatch(c -> !c.equals(classToCheck) && classToCheck.isAssignableFrom(c));
    }

}
