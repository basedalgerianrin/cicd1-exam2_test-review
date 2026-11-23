package ie.atu.sample.controller;

import ie.atu.sample.dto.BookingRequest;
import ie.atu.sample.dto.BookingResponse;
import ie.atu.sample.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }
}

/*

- The controller only handles HTTP: it reads the request, calls the service, and
  chooses the status code. No business rules live here.

- @Valid on the @RequestBody parameter is what triggers the annotations in
  BookingRequest. Without it, @NotBlank/@Size/@Positive are silently ignored and
  bad input would reach the service. Invalid input -> Spring returns HTTP 400.

- @RequestBody tells Spring to turn the JSON body into a BookingRequest (Jackson,
  using the setters from @Data and matching field names).

- POST returns 201 Created via ResponseEntity, not the default 200. ResponseEntity
  lets me set the status and the body together.

- GET /{id} with @PathVariable reads the id from the URL. A missing booking is
  handled in the service, which throws ResponseStatusException -> HTTP 404.
  A plain return type is enough here because the default status is 200 OK.

- Constructor injection again: @RequiredArgsConstructor injects BookingService.
*/