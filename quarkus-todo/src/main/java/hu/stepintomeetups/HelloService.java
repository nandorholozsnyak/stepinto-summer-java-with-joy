package hu.stepintomeetups;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HelloService {

    public String hello() {
        return "Hello Step Into Meetup";
    }

}
