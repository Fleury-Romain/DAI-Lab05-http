package ch.heig.dai.lab.http;

import io.javalin.Javalin;
import java.util.ArrayList;
import java.util.List;

public class HTTPServer {
    private static List<Person> items = new ArrayList<>();
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/", ctx -> ctx.result("Hello Java, from Javalin !"));

        // Create
        app.post("/items", ctx-> {
            String newItem = ctx.body();
            String[] persons = newItem.split(",");
            items.add(new Person(persons[0], persons[1], Integer.parseInt(persons[2]), persons[3])); // arguments du constructeur
            ctx.status(201).result("Item added");
        });

        // Read
        app.get("/items", ctx-> ctx.result(items.toString()));


        // Update an item by index
        app.put("/items/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));
            System.out.println("Receive index : " + index);
            if (index >= 0 && index < items.size()) {
                Person person = items.get(index);
                String updatedItem = ctx.body();
                person.setJob(updatedItem);
                items.set(index, person);
                ctx.result("Item updated");
            } else {
                ctx.status(404).result("Item not found");
            }
        });

        // Delete an item by index
        app.delete("/items/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));
            ctx.result(ctx.pathParam("index"));
            if (index >= 0 && index < items.size()) {
                items.remove(index);
                ctx.result("Item deleted");
            } else {
                ctx.status(404).result("Item not found");
            }
        });


        app.get("/Hello/{name}", ctx->{
            String name = ctx.pathParam("name");
            ctx.result("Hello my dear friend : " + name);
        });
    }
}