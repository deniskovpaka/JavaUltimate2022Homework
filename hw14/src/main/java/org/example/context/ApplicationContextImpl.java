package org.example.context;

import lombok.SneakyThrows;
import org.example.annotation.Bean;
import org.example.annotation.Inject;
import org.example.exception.NoSuchBeanException;
import org.example.exception.NoUniqueBeanException;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContextImpl implements ApplicationContext {final Map<String, Object> map = new HashMap<>();

    @SneakyThrows
    public ApplicationContextImpl(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Bean.class);

        for (Class<?> aClass : typesAnnotatedWith) {
            Constructor<?> constructor = aClass.getConstructor();
            Object o = constructor.newInstance();

            String beanName = aClass.getAnnotation(Bean.class).value().equals("")
                    ? basicName(aClass)
                    : aClass.getAnnotation(Bean.class).value();
            map.put(beanName, o);
        }

        map.entrySet().stream()
                .map(bean -> Map.entry(bean.getValue(), Arrays.asList(bean.getValue().getClass().getDeclaredFields())))
                .filter(fields -> fields.getValue().stream()
                        .anyMatch(field -> field.isAnnotationPresent(Inject.class)))
                .forEach(field -> {
                    field.getValue().forEach(fieldWithInjection -> {
                        if (fieldWithInjection.isAnnotationPresent(Inject.class)) {
                            fieldWithInjection.setAccessible(true);
                            Object findBeanForInjection = getBean(fieldWithInjection.getName());
                            Object objectForSetField = field.getKey();
                            try {
                                fieldWithInjection.set(objectForSetField, findBeanForInjection);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                });

    }

    private String basicName(Class<?> elem) {
        return elem.getSimpleName().substring(0, 1).toLowerCase() + elem.getSimpleName().substring(1);
    }


    public <T> Object getBean(String beanName) {
        List<Object> beanEntity = map.entrySet().stream()
                .filter(type -> type.getKey().equals(beanName))
                .map(Map.Entry::getValue).toList();

        if (beanEntity.size() == 0) {
            throw new NoSuchBeanException();
        }

        if (beanEntity.size() > 1) {
            throw new NoUniqueBeanException();
        }

        return beanEntity.get(0);
    }


    @Override
    public <T> T getBean(Class<T> beanType) {
        List<Object> beanEntity = map.values().stream().filter(type -> checkClass(type, beanType)).toList();

        if (beanEntity.size() == 0) {
            throw new NoSuchBeanException();
        }

        if (beanEntity.size() > 1) {
            throw new NoUniqueBeanException();
        }

        return beanType.cast(beanEntity.get(0));
    }

    private <T> boolean checkClass(Object type, Class<T> beanType) {
        return type.getClass().isAssignableFrom(beanType) || beanType.isAssignableFrom(type.getClass()) ||
                Arrays.stream(type.getClass().getInterfaces()).anyMatch(beanType::isAssignableFrom);
    }


    @Override
    public <T> Object getBean(String name, Class<T> beanType) {
        return Optional.ofNullable(map.get(name)).orElseThrow(NoSuchBeanException::new);
    }

    @Override
    public <T> Map<String, Object> getAllBeans(Class<T> beanType) {
        return map.entrySet().stream()
                .filter(val -> checkClass(val.getValue(), beanType))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
