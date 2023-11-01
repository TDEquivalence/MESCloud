package com.alcegory.mescloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class RequestKpiDto {

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Timestamp startDate;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        private Timestamp endDate;

        public RequestKpiDto(LocalDate startDate, LocalDate endDate) {
                this.startDate = Timestamp.valueOf(startDate.atStartOfDay());
                this.endDate = Timestamp.valueOf(endDate.atStartOfDay().plusDays(1).minusNanos(1));
        }

        public RequestKpiDto(Timestamp startDate, Timestamp endDate) {
                this.startDate = startDate;
                this.endDate = endDate;
        }

        public LocalDate getStartDateAsLocalDate() {
                return startDate.toLocalDateTime().toLocalDate();
        }

        public LocalDate getEndDateAsLocalDate() {
                return endDate.toLocalDateTime().toLocalDate();
        }

        public static RequestKpiDto createRequestKpiForDay(LocalDate day) {
                LocalDateTime dayStart = day.atStartOfDay();
                LocalDateTime dayEnd = day.atTime(23, 59, 59, 999_999_999); // nanoseconds

                return new RequestKpiDto(
                        Timestamp.valueOf(dayStart),
                        Timestamp.valueOf(dayEnd)
                );
        }
}

