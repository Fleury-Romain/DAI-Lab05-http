package ch.heig.dai.lab.http;

import io.javalin.Javalin;
import java.util.ArrayList;
import java.util.List;

public class HTTPServer {
    private static List<String> items = new ArrayList<>();
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/", ctx -> ctx.result("Hello Java, from Javalin !"));

        // Create
        app.post("/items", ctx-> {
            String newItem = ctx.body();
            items.add(newItem);
            ctx.status(201).result("Item added");
        });

        // Read
        app.get("/items", ctx-> ctx.result(items.toString()));


        // Update an item by index
        app.put("/items/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));
            System.out.println("Receive index : " + index);
            if (index >= 0 && index < items.size()) {
                String updatedItem = ctx.body();
                items.set(index, updatedItem);
                ctx.result("Item updated");
            } else {
                ctx.status(404).result("Item not found");
            }
        });

        // Delete an item by index
        app.delete("/items/{index}", ctx -> {
            int index = Integer.parseInt(ctx.pathParam("index"));
            ctx.result(ctx.pathParam("index"));
            /*if (index >= 0 && index < items.size()) {
                items.remove(index);
                ctx.result("Item deleted");
            } else {
                ctx.status(404).result("Item not found");
            }*/
        });


        app.get("/Hello/{name}", ctx->{
            String name = ctx.pathParam("name");
            ctx.result("Hello my dear friend : " + name);
        });
    }
}