package timetogeter.context.schedule.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timetogeter.context.schedule.application.dto.request.CalendarViewRequest1;
import timetogeter.context.schedule.application.dto.request.CalendarViewRequest2;
import timetogeter.context.schedule.application.dto.response.CalendarViewResponse1;
import timetogeter.context.schedule.application.dto.response.CalendarViewResponse2;
import timetogeter.context.promise.domain.entity.PromiseShareKey;
import timetogeter.context.schedule.domain.entity.Schedule;
import timetogeter.context.promise.domain.repository.PromiseShareKeyRepository;
import timetogeter.context.schedule.domain.repository.ScheduleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CalendarViewService {

    private final PromiseShareKeyRepository promiseShareKeyRepository;
    private final ScheduleRepository scheduleRepository;

//======================
// 캘린더 - 캘린더 메인, 캘린더 일정 확인 (Step1,2)
//======================

    //캘린더 - 캘린더 메인, 캘린더 일정 확인 - Step1 - 메인 서비스 메소드
    public CalendarViewResponse1 viewCalendar1(CalendarViewRequest1 request) {
        List<String> encPromiseKeyList = request.encPromiseKeyList();

        // encPromiseKeyList에 해당하는 PromiseShareKey만 조회
        List<PromiseShareKey> matchedKeys = promiseShareKeyRepository.findByEncPromiseKeyIn(encPromiseKeyList);

        // scheduleId만 추출
        List<String> scheduleIdList = matchedKeys.stream()
                .map(PromiseShareKey::getScheduleId)
                .distinct() // 중복 제거
                .collect(Collectors.toList());

        return new CalendarViewResponse1(scheduleIdList);
    }

    //캘린더 - 캘린더 메인, 캘린더 일정 확인 - Step2 - 메인 서비스 메소드
    public List<CalendarViewResponse2> viewCalendar2(CalendarViewRequest2 request) {
        List<String> scheduleIdList = request.scheduleIdList();

        // scheduleId에 해당하는 Schedule 객체들 조회
        // ----------------------------
        // 1. 쿼리 시간 측정
        // ----------------------------
        long queryStart = System.currentTimeMillis();
        List<Schedule> schedules = scheduleRepository.findByIdIn(scheduleIdList);
        long end = System.currentTimeMillis();
        long queryEnd = System.currentTimeMillis();
        log.info("scheduleList find query time {} ms", queryEnd - queryStart);
        // Schedule -> CalendarViewResponse2 로 매핑
        // ----------------------------
        // 2. DTO 매핑 시간 측정
        // ----------------------------
        long mappingStart = System.currentTimeMillis();
        List<CalendarViewResponse2> response = schedules.stream()
                .map(schedule -> new CalendarViewResponse2(
                        schedule.getTitle(),
                        schedule.getContent(),
                        schedule.getScheduleId()
                ))
                .collect(Collectors.toList());
        long mappingEnd = System.currentTimeMillis();
        log.info("DTO mapping time {} ms", mappingEnd - mappingStart);

        return response;
    }

}
