package com.example.spocktestdemo.service

import spock.lang.Specification

class MyServiceTest extends Specification {
    def myService = new MyService()
    def "myMethod1"() {
        expect:
        myService.myMethod1() == "myMethod1()"
    }

    def "myMethod2"() {
        expect:
        myService.myMethod2() == "myMethod2()"
    }
}
