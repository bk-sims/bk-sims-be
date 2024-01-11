package com.dalv.bksims.services.social_points_management;

import com.dalv.bksims.models.entities.social_points_management.Organization;
import com.dalv.bksims.models.repositories.social_points_management.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }
}
