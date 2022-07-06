package org.example.bean;

import org.example.annotation.Bean;
import org.example.annotation.Inject;

@Bean("coolBean")
public class PeopleBean {
    @Inject
    Programmer programmer;
}
