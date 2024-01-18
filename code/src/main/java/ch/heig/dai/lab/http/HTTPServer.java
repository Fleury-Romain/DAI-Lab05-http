package ch.heig.dai.lab.http;

import io.javalin.Javalin;

public class HTTPServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        Controller controller = new Controller();

        // Read welcome message
        app.get("/", controller::welcome);

        // Create
        app.post("/person", controller::createOne);

        // Read all persons
        app.get("/person", controller::getAll);

        // Read a person by id
        app.get("/person/{id}", controller::getOne);

        // Update a person by id
        app.put("/person/{id}", controller::updateOne);

        // Delete a person by id
        app.delete("/person/{id}", controller::delete);
    }
}