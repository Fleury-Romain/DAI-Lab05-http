package ch.heig.dai.lab.http;

public class Person {
    private String firstname;
    private String lastname;
    private int birthyear;
    private String job;
    private static int count;
    private final int id;
    public Person(String firstname, String lastname, int birthyear, String job) {
        this.firstname = firstname;
        this.lastname  = lastname;
        this.birthyear = birthyear;
        this.job       = job;
        this.id        = count++;
    }

    public String toString() {
        return "[id:" + id + ", fname:" + firstname + ", lname:" + lastname + ", birthyear:" + birthyear + ", job:" + job + "]";
    }

    public int getId() { return id; }

    public static void deleteId() { --count; }

    public static void resetId() { count = 0; }

    boolean equals(Person p) {
        return this == p || this.firstname.equals(p.firstname) && this.lastname.equals(p.lastname) && this.birthyear == p.birthyear && this.job.equals(p.job);
    }

    public void setJob(String job) { this.job = job; }
}
