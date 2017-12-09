package kr.co.mashup.feedgetapi.web;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ethan.kim on 2017. 12. 10..
 */
@RestController
public class TestController {

    @GetMapping("/")
    public ModelMap test() {
        ModelMap resultMap = new ModelMap();
        resultMap.addAttribute("test", true);

        return resultMap;
    }
}
