package com.java.softwarestore.web;

import com.java.softwarestore.model.dto.ProgramDetailsDto;
import com.java.softwarestore.service.HibernateMostPopularManager;
import com.java.softwarestore.service.QueryResultsOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/top")
public class MostPopularController {

    @Value("${top.programs.limit.to.query}")
    private int topProgramsLimitToQuery;
    @Autowired
    private HibernateMostPopularManager mostPopularManager;

    @RequestMapping(value = "get", method = RequestMethod.GET)
    private List<ProgramDetailsDto> getTopPrograms() {
        return mostPopularManager.getTopPrograms(topProgramsLimitToQuery, QueryResultsOrder
                .DESCENDING, QueryResultsOrder.DESCENDING);

    }

}