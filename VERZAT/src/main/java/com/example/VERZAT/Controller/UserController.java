package com.example.VERZAT.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GaneshController {

    @GetMapping("/")
    public String ganesh(){
        return "Hello VERZAT";
    }
}
