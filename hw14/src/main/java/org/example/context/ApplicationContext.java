package org.example.context;

import java.util.Map;

public interface ApplicationContext {
    <T> T getBean(Class<T> beanType);
    <T> Object getBean(String name, Class<T> beanType);
    <T> Map<String,Object> getAllBeans(Class<T> beanType);
}
