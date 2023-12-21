package ch.heig.dai.lab.http;

public class Person {
    String firstname;
    String lastname;
    int age;
    String job;
    public Person(String firstname, String lastname, int age, String job) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.job = job;
    }

    public String toString() {
        String ageString = String.valueOf(age);
        if (age > 50) {
            ageString = "age canonique";
        }
        return "{" + firstname + ", " + lastname + ", " + ageString + ", " + job + "}";
    }

    public void setJob(String job) {
        this.job = job;
    }

}
