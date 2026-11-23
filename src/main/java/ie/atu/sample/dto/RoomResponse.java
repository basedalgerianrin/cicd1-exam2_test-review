package ie.atu.sample.dto;

import lombok.Data;

@Data
public class RoomResponse {
    private Long id;
    private String name;
    private int capacity;
}

//DTO mirroring Room service JSON. Names must match their keys (id, name, capacity). Its not an entity, nothing is stored locally.