package com.company.businessmetrics.app;

import io.jmix.businesscalendar.model.BusinessCalendar;
import io.jmix.businesscalendar.repository.BusinessCalendarRepository;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BusinessDaysService {

    private final BusinessCalendarRepository businessCalendarRepository;

    public BusinessDaysService(BusinessCalendarRepository businessCalendarRepository) {
        this.businessCalendarRepository = businessCalendarRepository;
    }

    public List<LocalDate> getWorkingDaysOfWeek(String calendarCode, LocalDate anyDayInWeek) {
        BusinessCalendar calendar = businessCalendarRepository.getBusinessCalendarByCode(calendarCode);
        LocalDate startOfWeek = anyDayInWeek.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = anyDayInWeek.with(DayOfWeek.SUNDAY);
        return getBusinessDaysInPeriod(calendar, startOfWeek, endOfWeek);
    }

    public List<LocalDate> getWorkingDaysOfMonth(String calendarCode, LocalDate anyDayInMonth) {
        BusinessCalendar calendar = businessCalendarRepository.getBusinessCalendarByCode(calendarCode);

        LocalDate firstOfMonth = anyDayInMonth.withDayOfMonth(1);
        LocalDate lastOfMonth = anyDayInMonth.withDayOfMonth(anyDayInMonth.lengthOfMonth());
        return getBusinessDaysInPeriod(calendar, firstOfMonth, lastOfMonth);
    }

    private List<LocalDate> getBusinessDaysInPeriod(BusinessCalendar calendar, LocalDate startOfPeriod, LocalDate endOfPeriod) {
        List<LocalDate> workingDays = new ArrayList<>();

        for (LocalDate current = startOfPeriod; !current.isAfter(endOfPeriod); current = current.plusDays(1)) {
            if (calendar.isBusinessDay(current)) {
                workingDays.add(current);
            }
        }
        return workingDays;
    }
}