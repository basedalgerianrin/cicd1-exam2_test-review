package ie.atu.sample.dto;

import lombok.Data;

@Data
public class BookingResponse {
    private Long id;
    private String organiserName;
    private Long roomId;
    private String status;
    private int attendeeCount;
}

/*
- Output-only DTO: what the client sees after POST/GET. Kept separate from
  BookingRequest because the server, not the client, controls id and status.
- No validation here: validation applies to input, not output.
- @Data supplies the getters Jackson uses to write the JSON.
- Built in BookingService from the saved Booking; id is only set after save().
*/