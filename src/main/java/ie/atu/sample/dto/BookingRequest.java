package ie.atu.sample.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookingRequest {
    @NotNull
    Long roomId;
    @NotBlank @Size(min = 2, max = 60) String organiserName;
    @Positive int attendeeCount;
}

/*
- roomId is Long (object), not long (primitive): a primitive can never be null,
  so @NotNull on it would never fail. With Long, a missing field -> null -> 400.
  A primitive holds a raw value. An object (a wrapper class) holds a reference to an object, and that reference can be null.

- @NotBlank rejects null, "", and "   ". @Size(2,60) alone would accepts the defined size (2, 60).
  Use both together.

- @Positive on int: 0 and negatives are rejected. A missing attendeeCount
  defaults to 0 (primitive) so it is rejected too, via @Positive.

- Validation only runs if the controller parameter has @Valid:
  create(@Valid @RequestBody BookingRequest req)
  Without @Valid these annotations are ignored. This is the easiest thing to forget.

- Field names must match the JSON keys exactly: roomId, organiserName, attendeeCount.

- @Data = getters + setters. Jackson needs them to fill the object from JSON.

 */