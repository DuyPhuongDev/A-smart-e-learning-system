package com.hcmut.lms.notification.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class PreferencesUpsertRequest {

    @Valid
    @NotEmpty
    private List<PreferenceItemRequest> preferences;
}
