//package com.dalv.bksims.controllers.helper;
//
//import com.dalv.bksims.models.entities.social_points_management.Activity;
//import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.Optional;
//import java.util.UUID;
//
//public class HelperFunction {
//    private Specification<Activity> buildFinalActivitySpec(Specification<Activity> activitySpecification, Specification<Activity> statusSpecification, Optional<UUID> userId) {
//        Specification<Activity> finalActivitySpec;
//        if (activitySpecification == null || userId.isEmpty()) {
//            return null;
//        }
//
//        Specification<Activity> activityByUserIdSpec = SpecificationBuilder.specification(ActivityByUserIdSpec.class).withParam("userId", userId.toString()).build();
//
//    }
//}
