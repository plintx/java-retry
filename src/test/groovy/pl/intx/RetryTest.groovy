package pl.intx


import spock.lang.Specification

import java.util.function.Supplier

class RetryTest extends Specification {

    def "Should not throw exception on void method that not throws exception"() {
        when:
        Retry.times(1).run({ -> methodThatNotThrowException() })

        then:
        noExceptionThrown()
    }

    def "Should not retry on void method that not throws exception"() {
        given:
        def retry = Retry.times(1)

        when:

        retry.run({ -> methodThatNotThrowException() })

        then:
        retry.getRetryCounter() == 0

    }

    def "Should not throw exception on method that returns value not throws exception"() {
        when:
        def supplier = new Supplier() {
            @Override
            Object get() {
                return methodThatNotThrowExceptionAndReturnsValue("Good!");
            }
        }
        def value = Retry.times(1).run(supplier)

        then:
        noExceptionThrown()
        value == "Good!"
    }

    def "Should retry two times before throw exception"() {
        given:
        def retry = Retry.times(2)

        when:
        retry.run({ -> methodThatThrowsException() })

        then:
        thrown(RuntimeException)
        retry.getRetryCounter() == 2
    }

    def "Should retry two times then succeed"() {
        given:
        def testClass = new SucceedTestClass()
        def retry = Retry.times(5)
        def supplier = new Supplier() {
            @Override
            Object get() {
                testClass.someTestMethod("Good!")
            }
        }

        when:
        def value = retry.run(supplier)

        then:
        retry.getRetryCounter() == 2
        value == "Good!"
    }

    def methodThatNotThrowException() {

    }

    def methodThatNotThrowExceptionAndReturnsValue(String s) {
        s
    }

    void methodThatThrowsException() {
        throw new RuntimeException()
    }

    class SucceedTestClass {
        int counter = 0

        def someTestMethod(String value) {
            counter++
            if (counter < 3)
                throw new RuntimeException("Failed")
            return value
        }
    }
}
