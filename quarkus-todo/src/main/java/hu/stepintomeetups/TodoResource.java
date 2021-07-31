package hu.stepintomeetups;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
public class TodoResource {

    private final TodoRepository todoRepository;

    public TodoResource(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GET
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Todo postTodo(Todo todo) {
        //todo.setId(UUID.randomUUID().toString());
        return todoRepository.save(todo);
    }

}