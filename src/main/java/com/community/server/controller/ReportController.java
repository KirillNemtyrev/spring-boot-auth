package com.community.server.controller;

import com.community.server.body.ReportBody;
import com.community.server.entity.ReportEntity;
import com.community.server.repository.ReportRepository;
import com.community.server.repository.UserRepository;
import com.community.server.security.JwtAuthenticationFilter;
import com.community.server.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping()
    public Object sendReport(HttpServletRequest request, @Valid @RequestBody ReportBody reportBody) {
        String jwt = jwtAuthenticationFilter.getJwtFromRequest(request);
        Long userId = tokenProvider.getUserIdFromJWT(jwt);

        if(!userRepository.existsById(userId))
            return new UsernameNotFoundException("User is not found!");

        if(userId.equals(reportBody.getSuspectId()))
            return new ResponseEntity("Bad idea..", HttpStatus.BAD_REQUEST);

        if(!userRepository.existsById(reportBody.getSuspectId()))
            return new UsernameNotFoundException("Suspect user is not found!");

        ReportEntity reportEntity = new ReportEntity(userId, reportBody.getSuspectId(), reportBody.getReportType(), reportBody.getComment());

        reportRepository.save(reportEntity);
        return new ResponseEntity("Your report send!", HttpStatus.OK);
    }
}
