package io.github.fernandapcaetano.phastfin_backend.commons.infrastructure;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, LocalDateTime> {

    @Override
    public LocalDateTime convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return ZonedDateTime.of(localDateTime, ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("America/Sao_Paulo"));
    }
}
