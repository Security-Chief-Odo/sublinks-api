package com.sublinksapp.sublinksappapi.api.lemmy.v3.models.requests;

import lombok.Builder;

@Builder
public record BlockPerson(
        Integer person_id,
        Boolean block
) {
}