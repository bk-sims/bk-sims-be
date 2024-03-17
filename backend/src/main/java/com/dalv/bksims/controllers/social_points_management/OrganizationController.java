package com.dalv.bksims.controllers.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.Organization;
import com.dalv.bksims.services.social_points_management.OrganizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Organization")
@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Slf4j
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    @Secured({"ROLE_STUDENT", "ROLE_LECTURER", "ROLE_ADMIN"})
    public List<Organization> findAll() {
        return organizationService.findAll();
    }
}
