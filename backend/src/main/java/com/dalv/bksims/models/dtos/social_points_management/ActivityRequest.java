package com.dalv.bksims.models.dtos.social_points_management;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record ActivityRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(min = 2, max = 50, message = "Title length must be from 2 to 50")
        String title,
        MultipartFile bannerFile,
        @NotBlank(message = "Description cannot be blank")
        @Size(min = 2, max = 1000, message = "Description length must be from 2 to 1000")
        String description,
        @NotBlank(message = "Location cannot be blank")
        String location,
        @NotBlank(message = "Start date cannot be blank")
        String startDate,
        @NotBlank(message = "End date cannot be blank")
        String endDate,
        @NotNull(message = "Number of participants cannot be null")
        @Min(value = 1, message = "Number of participants must be greater than or equal to 1")
        @Max(value = 1000, message = "Number of participants must be less than or equal to 1000")
        Integer numberOfParticipants,
        @NotNull(message = "Can participants invite field cannot be null")
        Boolean canParticipantsInvite,
        @NotNull(message = "Points cannot be null")
        @Min(value = 1, message = "Points must be greater than or equal to 1")
        @Max(value = 500, message = "Points must be less than or equal to 500")
        Integer points,
        @NotNull(message = "Owner id cannot be null")
        UUID ownerId,
        MultipartFile regulationsFile,
        String registrationStartDate,
        String registrationEndDate,
        String activityType,
        String organization
) {
}
