package com.example.email.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Fio {

    private String firstName;
    private String secondName;
    private String middleName;


    @Override
    public String toString() {
        return firstName + " " + middleName;
    }
}
