package com.example.demo.controller;

import com.example.demo.model.Greeting;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FileWriteController {
    public static final Logger LOG = LoggerFactory.getLogger(FileWriteController.class);

    @Value("${input.dir}")
    private String inputDir;
    @Value("${output.dir}")
    private String outputDir;
    @Value("${file.patter}")
    private String filePattern;

    @RequestMapping("/checkStatus")
    public String checkstatus(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
        String retStr = "fileWrite source-dir:" + inputDir + " dest-dir:" + outputDir +
                        " filter pattern:" + filePattern;
        return retStr;
    }

    @GetMapping("/hello")
    @ResponseBody
    public Greeting sayHello(@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {
        String template = "Hello, %s!";
        AtomicLong counter = new AtomicLong();
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
