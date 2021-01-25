package com.thelogicmaster.robot_recharge.code;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command {
    String name;
    String[] args;
    String description;
    String example;
}
