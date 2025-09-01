package com.ejemplo.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {

    @GetMapping("/holamundo")
    public String holaMundo() {
        return "Hola Mundo... Atentamente: Equipo Backend";
    }
}
