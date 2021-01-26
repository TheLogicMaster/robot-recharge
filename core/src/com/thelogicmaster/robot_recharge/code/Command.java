package com.thelogicmaster.robot_recharge.code;

import lombok.*;

@Getter
@Setter
public class Command {
    String name;
    String[] args;
    String description;
    String example;
}
