package timetogeter.context.schedule.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import timetogeter.context.auth.domain.adaptor.UserPrincipal;
import timetogeter.context.schedule.application.dto.request.CalendarCreateRequest1;
import timetogeter.context.schedule.application.dto.request.CalendarRewriteRequest1;
import timetogeter.context.schedule.application.dto.request.CalendarViewRequest1;
import timetogeter.context.schedule.application.dto.request.CalendarViewRequest2;
import timetogeter.context.schedule.application.dto.response.CalendarCreateResponse1;
import timetogeter.context.schedule.application.dto.response.CalendarRewriteResponse1;
import timetogeter.context.schedule.application.dto.response.CalendarViewResponse1;
import timetogeter.context.schedule.application.dto.response.CalendarViewResponse2;
import timetogeter.context.schedule.application.service.CalendarDetailService;
import timetogeter.context.schedule.application.service.CalendarViewService;
import timetogeter.global.interceptor.response.BaseResponse;
import timetogeter.global.interceptor.response.error.dto.SuccessResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarManageController {

    private final CalendarViewService calendarViewService;
    private final CalendarDetailService calendarDetailService;


//======================
// 캘린더 - 캘린더 메인, 캘린더 일정 확인 (Step1,2)
//======================

    /*
    캘린더 - 캘린더 메인, 일정 확인 - step1

    [웹] 자신이 가진 개인 일정은 PromiseShareKey 테이블 내에서
        encUserId : 개인키로 암호화된 사용자 아이디
        encPromiseKey : 개인키로 암호화된 개인키
        scheduleId : 스케줄 아이디
        이렇게 저장됨. 따라서 개인키로 개인키를 암호화한 encPromiseKey, 약속키를 개인키로 암호화한 리스트를
        요청으로 보냄
        /api/v1/calendar/month1

    [서버] encPromiseKey에 해당하는 scheduleId를 리스트로 반환
     */
    @PostMapping(value = "/view1", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<CalendarViewResponse1> viewCalendar1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CalendarViewRequest1 requests) throws Exception{
        String userId = userPrincipal.getId();
        CalendarViewResponse1 response = calendarViewService.viewCalendar1(requests);
        return new BaseResponse<>(response);
    }

    /*
    캘린더 - 캘린더 메인, 일정 확인 - step2

    [웹] scheduleId를 리스트 형태로 요청
        /api/v1/calendar/month2

    [서버] scheduleId에 해당하는 Schedule 테이블 내 레코드를 리스트 형태로 반환
     */
    @PostMapping(value = "/view2", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<List<CalendarViewResponse2>> viewCalendar2(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CalendarViewRequest2 requests) throws Exception{
        long apiStart = System.currentTimeMillis(); // 전체 API 처리 시작
        List<CalendarViewResponse2> response = calendarViewService.viewCalendar2(requests);
        long apiEnd = System.currentTimeMillis(); // 전체 API 처리 종료
        log.info("Total /view2 API processing time {} ms", apiEnd - apiStart);

        return new BaseResponse<>(response);
    }



//======================
// 캘린더 - 일정등록 (Step1)
//======================

    /*
    [웹] Schedule 내 저장할
        title, content, type, place, placeUrl, startDateTime, endDateTime,
        encPromiseKey(개인키를 개인키로 암호화), encUserId(유저아이디를 개인키로 암호화)
        내용 담아 요청
    /api/v1/calendar/create1

    [서버] Schedule 테이블, PromiseShareKey 테이블 내에 저장
     */
    @PostMapping(value = "/create1", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<CalendarCreateResponse1> createCalendar1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CalendarCreateRequest1 requests) throws Exception{
        String userId = userPrincipal.getId();
        CalendarCreateResponse1 response = calendarDetailService.createCalendar1(requests);
        return new BaseResponse<>(response);
    }

//======================
// 캘린더 - 일정 수정 (Step1)
//======================

    /*
    [웹] encPromiseKey와 수정하는 부분 함께 요청
        /api/v1/calendar/rewrite1

    [서버] scheduleId에 해당하는 것에 수정해서 반환
     */
    @PostMapping(value = "/rewrite1", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<CalendarRewriteResponse1> rewriteCalendar1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CalendarRewriteRequest1 requests) throws Exception{
        String userId = userPrincipal.getId();
        CalendarRewriteResponse1 response = calendarDetailService.rewriteCalendar1(requests);
        return new BaseResponse<>(response);
    }


}
