
package ie.atu.sample.service;

import feign.FeignException;
import ie.atu.sample.client.RoomClient;
import ie.atu.sample.dto.BookingRequest;
import ie.atu.sample.dto.BookingResponse;
import ie.atu.sample.dto.RoomResponse;
import ie.atu.sample.model.Booking;
import ie.atu.sample.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomClient roomClient;

    public BookingResponse create(BookingRequest request) {

        RoomResponse room;
        try {
            room = roomClient.getRoom(request.getRoomId());
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Room " + request.getRoomId() + " not found");
        }

        if (request.getAttendeeCount() > room.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Attendee count " + request.getAttendeeCount()
                            + " exceeds room capacity " + room.getCapacity());
        }

        Booking booking = new Booking();
        booking.setOrganiserName(request.getOrganiserName());
        booking.setAttendeeCount(request.getAttendeeCount());
        booking.setRoomId(request.getRoomId());

        Booking saved = bookingRepository.save(booking);

        return toResponse(saved);
    }

    public BookingResponse getById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking " + id + " not found"));
        return toResponse(booking);
    }


    private BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setOrganiserName(booking.getOrganiserName());
        response.setAttendeeCount(booking.getAttendeeCount());
        response.setRoomId(booking.getRoomId());
        response.setStatus(booking.getStatus());
        return response;
    }
}

/*
- Layers: the controller handles HTTP, the service holds the rules, the repository
  talks to the database. My original code mixed these up, which is why Task 2 never worked.

- Constructor injection: Spring supplies BookingRepository and RoomClient because they are
  final fields set through the constructor (@RequiredArgsConstructor writes it).
  I never call "new" on them myself.

- OpenFeign: RoomClient is only an interface. Spring generates the HTTP call to
  GET /api/rooms/{id} on the Room Service, and Jackson fills RoomResponse from the JSON.
  Without @Data on RoomResponse, capacity would stay 0 and every booking would be rejected.

- Order matters: check capacity BEFORE save(), so a rejected booking never reaches the database.

- save() returns the stored entity with its generated id. Build the response from that
  returned object, not the one I passed in, otherwise id is null.

- Optional: findById may find nothing. orElseThrow turns "empty" into an HTTP 404
  with a clear message, instead of a NullPointerException.

- ResponseStatusException lets the service choose the HTTP status (400 over capacity,
  404 missing booking) without writing a custom exception class.

- Entity vs DTO: Booking is what the database stores, BookingResponse is what the client sees.
  toResponse() is the single place where one is converted to the other.
*/
