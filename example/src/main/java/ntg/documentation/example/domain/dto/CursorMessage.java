package ntg.documentation.example.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class CursorMessage {

    private String type;
    private UUID documentId;
    private String clientId;
    private String email;
    private int position;
    private Coordinates coords;

    @Getter
    @Setter
    public static class Coordinates {
        private double x;
        private double y;
    }

}