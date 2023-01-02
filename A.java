package org.cis1200.checkers;

public class A {
    public void m1() {

    }
}

class B {

}

class C {
    public void m3() {
        throw new IllegalArgumentException();

    }
}

class D {
    public static void main(String[] args) {
        C c = new C();
        B b = new B();
        System.out.println((new B()).equals(new B()));
    }
}
