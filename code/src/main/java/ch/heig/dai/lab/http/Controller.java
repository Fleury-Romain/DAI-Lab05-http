package ch.heig.dai.lab.http;

import io.javalin.http.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.*;

class Controller {
    private static List<Person> persons = new ArrayList<>();

    public String formatString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1).toLowerCase();
    }

    public void welcome(Context ctx) {
        ctx.result("Welcome to HTTPServer ! Supported commands: GET /, GET /person, GET /person/id, POST /person, PUT /person/id, DELETE /person/id, DELETE /person/all");
    }
    public void createOne(Context ctx) {
        String s = ctx.body();
        if (s.isEmpty()) {
            ctx.status(404).result("Empty body");
            return;
        }
        Pattern pattern = Pattern.compile("(lname|fname|birthyear|job)\\s*:\\s*([^,]+)");
        Matcher matcher = pattern.matcher(s);
        String lname = null, fname = null, birthyear = null, job = null;
        try {
            while (matcher.find()) {
                String champ = matcher.group(1);
                String valeur = matcher.group(2);
                switch (champ) {
                    case "lname"    : lname = formatString(valeur); break;
                    case "fname"    : fname = formatString(valeur); break;
                    case "birthyear": birthyear = valeur;           break;
                    case "job"      : job = formatString(valeur);   break;
                }
            }
            Person pnew = new Person(lname, fname, Integer.parseInt(birthyear), job);
            for (Person p : persons) {
                if (p.equals(pnew)) {
                    Person.deleteId();
                    ctx.status(404).result("Person already exists");
                    return;
                }
            }
            persons.add(pnew);
            ctx.result("Person created, id: " + persons.get(persons.size()-1).getId());
        } catch (Exception e) {
            ctx.status(404).result("Invalid body");
        }
    }
    public void getAll(Context ctx) {
        if (persons.isEmpty()) {
            ctx.result("Empty list");
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (persons.size() > 1) { sb.append("["); }
        for (int i = 0; i < persons.size(); i++) {
            sb.append(persons.get(i));
            if (i != persons.size() - 1)
                sb.append(", ");
        }
        if (persons.size() > 1) { sb.append("]"); }
        ctx.result(sb.toString());
    }
    public void getOne(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            int i;
            for (i = 0; i < persons.size(); ++i) {
                if (persons.get(i).getId() == id) {
                    ctx.result("[" + persons.get(i) + "]");
                    break;
                }
            }
            if (i == persons.size()) {
                ctx.status(404).result("Person not found");
            }
        } catch (Exception e) {
            ctx.status(404).result("Invalid id");
        }
    }
    public void updateOne(Context ctx) {
        String s = ctx.body();
        if (s.isEmpty()) {
            ctx.status(404).result("Empty body");
            return;
        }
        Pattern pattern = Pattern.compile("(job)\\s*:\\s*([^,]+)");
        Matcher matcher = pattern.matcher(s);
        String job = null;
        while (matcher.find()) {
            String champ = matcher.group(1);
            String valeur = matcher.group(2);
            if (champ.equals("job")) {
                job = formatString(valeur);
                break;
            }
        }
        if (job == null) {
            ctx.status(404).result("Invalid body");
            return;
        }
        int id = Integer.parseInt(ctx.pathParam("id"));
        int i;
        for (i = 0; i < persons.size(); ++i) {
            if (persons.get(i).getId() == id) {
                persons.get(i).setJob(job);
                break;
            }
        }
        if (i < persons.size()) {
            ctx.result("Person's job (id: " + id + ") updated");
        } else {
            ctx.status(404).result("Person not found");
        }
    }
    public void delete(Context ctx) {
        String s = ctx.pathParam("id").toLowerCase();
        if (s.equals("all")) {
            if (persons.isEmpty()) {
                ctx.result("List already empty");
                return;
            }
            persons.clear();
            Person.resetId();
            ctx.result("All persons deleted");
            return;
        }
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            int i;
            for (i = 0; i < persons.size(); i++) {
                if (persons.get(i).getId() == id) {
                    persons.remove(i);
                    ctx.result("Person (id: " + id + ") deleted");
                    return;
                }
            }
            if (i == persons.size()) {
                ctx.status(404).result("Person not found");
            }
        } catch (Exception e) {
            ctx.status(404).result("Invalid id");
        }
    }
}
