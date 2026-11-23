package ie.atu.sample.client;

import ie.atu.sample.dto.RoomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name ="room-service", url = "${room.service.url}")
public interface RoomClient {
    @GetMapping("/api/rooms/{id}")
    RoomResponse getRoom(@PathVariable("id") Long id);
}

/*
Declarative HTTP client. Spring generates the implementation at runtime. {id} in the path is filled from the @PathVariable.
The base URL comes from application.properties, not hard-coded.
 */