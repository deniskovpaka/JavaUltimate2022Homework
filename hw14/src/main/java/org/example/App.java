package org.example;

import org.example.bean.People;
import org.example.bean.PeopleBean;
import org.example.context.ApplicationContext;
import org.example.context.ApplicationContextImpl;

import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        ApplicationContext simpleApplicationContext = new ApplicationContextImpl("org");
        PeopleBean bean = simpleApplicationContext.getBean(PeopleBean.class);
        Object coolBean = simpleApplicationContext.getBean("coolBean", PeopleBean.class);
        Map<String, Object> allBeans = simpleApplicationContext.getAllBeans(People.class);
    }
}
