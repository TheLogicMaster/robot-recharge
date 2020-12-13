package com.thelogicmaster.robot_recharge.code;

public class Command {

    String name;
    String[] args;
    String description;
    String example;

    public Command(String name, String[] args, String description, String example) {
        this.name = name;
        this.args = args;
        this.description = description;
        this.example = example;
    }

    public Command() {
        this("", new String[]{}, "", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}
