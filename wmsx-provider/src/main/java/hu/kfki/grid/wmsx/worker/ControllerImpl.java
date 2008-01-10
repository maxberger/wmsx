package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.worker.Controller;

public class ControllerImpl implements Controller {

    public String sayHello() {
        System.out.println("Hello is called.");
        return "Hello, World!";
    }

}
