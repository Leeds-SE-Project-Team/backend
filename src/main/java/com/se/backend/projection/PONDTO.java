package com.se.backend.projection;

import com.se.backend.models.PON;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PONDTO {
    Long id;
    Long tourID;
    String name;
    String location;
    Integer sequence;

    public PONDTO(PON PON) {
        id = PON.getId();
        tourID = PON.getTour().getId();
        name = PON.getName();
        location = PON.getLocation();
        sequence = PON.getSequence();
    }

    public static List<PONDTO> toListDTO(List<PON> PONList) {
        if (Objects.isNull(PONList)) {
            return new ArrayList<>(0);
        }
        return PONList.stream().map(PON::toDTO).toList();
    }


}

