package com.company.businessmetrics.app

import io.jmix.businesscalendar.model.BusinessCalendar
import io.jmix.businesscalendar.repository.BusinessCalendarRepository
import spock.lang.Specification

import java.time.LocalDate

import static java.time.LocalDate.parse

class BusinessDaysServiceTest extends Specification {

    public static final String SAMPLE_CALENDAR_CODE = "sampleCode"

    def "GetWorkingDaysOfWeek"() {
        given:
        BusinessCalendar calendar = Mock()
        calendar.isBusinessDay(parse("2025-11-24")) >> true
        calendar.isBusinessDay(parse("2025-11-25")) >> true
        calendar.isBusinessDay(parse("2025-11-26")) >> false
        calendar.isBusinessDay(parse("2025-11-27")) >> true
        calendar.isBusinessDay(parse("2025-11-28")) >> true
        calendar.isBusinessDay(parse("2025-11-29")) >> false
        calendar.isBusinessDay(parse("2025-11-30")) >> false

        and:
        BusinessCalendarRepository calendarRepository = Mock()
        calendarRepository.getBusinessCalendarByCode(SAMPLE_CALENDAR_CODE) >> calendar

        and:
        def service = new BusinessDaysService(calendarRepository)

        when:
        def week = service.getWorkingDaysOfWeek(SAMPLE_CALENDAR_CODE, parse("2025-11-26"))

        then:
        week == [parse("2025-11-24"), parse("2025-11-25"), parse("2025-11-27"), parse("2025-11-28")]
    }

    def "GetWorkingDaysOfMonth"() {

        given:
        BusinessCalendar calendar = Mock()

        def businessDays = [parse("2025-11-01"),
                            parse("2025-11-24"),
                            parse("2025-11-25"),
                            parse("2025-11-27"),
                            parse("2025-11-28"),
                            parse("2025-11-30")]
        calendar.isBusinessDay(_) >> {
            args ->
                LocalDate inputValue = args[0] as LocalDate // Получаем первый аргумент
                if (businessDays.contains(inputValue)) {
                    return true
                }
                return false
        }

        and:
        BusinessCalendarRepository calendarRepository = Mock()
        calendarRepository.getBusinessCalendarByCode(SAMPLE_CALENDAR_CODE) >> calendar

        and:
        def service = new BusinessDaysService(calendarRepository)

        when:
        def month = service.getWorkingDaysOfMonth(SAMPLE_CALENDAR_CODE, parse("2025-11-26"))

        then:
        month == businessDays
    }
}
