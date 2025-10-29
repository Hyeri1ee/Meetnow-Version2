package timetogeter.context.group.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import timetogeter.context.auth.domain.adaptor.UserPrincipal;
import timetogeter.context.group.application.dto.request.*;
import timetogeter.context.group.application.dto.response.*;
import timetogeter.context.group.application.service.GroupManageDisplayService;
import timetogeter.context.group.application.service.GroupManageInfoService;
import timetogeter.context.group.application.service.GroupManageMemberService;
import timetogeter.global.interceptor.response.BaseResponse;
import timetogeter.global.interceptor.response.error.dto.ErrorResponse;

@RestController
@RequestMapping("/api/v1/group")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "그룹", description = "그룹 생성, 그룹 정보 수정, 그룹 멤버 초대/나가기 API")
public class GroupManageController {
    private final GroupManageInfoService groupManageInfoService;
    private final GroupManageMemberService groupManageMemberService;
    private final GroupManageDisplayService groupManageDisplayService;

//=====================
// 그룹 상세 - 그룹 초대하기(간소화.ver)
//=====================

    //1. 그룹 참가 url (http://localhost:8080/group/join/{groupId}) 을 공유후, 초대대상자(손님)이 들어가서 로그인후, 자신의 이메일 반환

    //2. 그후 가공된 정보들 가지고 서버에 아래 요청 http://localhost:8080/group/member/save/{groupId} post요청

//======================
// 그룹 상세 - 그룹 초대하기 (Step1,2,3)
//======================
/*
//    /*
//    그룹 상세 - 그룹 초대하기 - step1
//
//    [웹] 그룹원이 groupId, 개인키로 암호화한 그룹 아이디를 보냄 /api/v1/group/invite1 ->
//    [서버] GroupProxyUser테이블 내 encencGroupMemberId 반환 ->
//     */
//    @Operation(
//            summary = "그룹 초대 - Step1",
//            description = """
//        그룹원이 groupId와 개인키로 암호화한 그룹 아이디를 서버에 전송하면,
//        서버는 GroupProxyUser 테이블에서 encencGroupMemberId를 반환합니다.
//        """,
//            security = @SecurityRequirement(name = "BearerAuth")
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "성공",
//                    content = @Content(schema = @Schema(implementation = InviteGroup1Response.class))
//            ),
//            @ApiResponse(responseCode = "400", description = "요청 형식 오류 (필드 누락/유효성 실패)",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = {
//                                    @ExampleObject(name = "groupId 누락", value = """
//                    { "code": 400, "message": "groupId는 필수입니다." }
//                    """),
//                                    @ExampleObject(name = "encGroupId 누락", value = """
//                    { "code": 400, "message": "encGroupId는 필수입니다." }
//                    """)
//                            }
//                    )
//            ),
//            @ApiResponse(responseCode = "401", description = "인증 실패",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 401, "message": "인증이 필요합니다." }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "403", description = "권한 없음",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 403, "message": "권한이 없습니다." }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "422", description = "복호화/무결성 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 422, "message": "encGroupId 복호화 실패" }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 500, "message": "서버 내부 오류가 발생했습니다." }
//                """)
//                    )
//            )
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    @PostMapping(value = "/invite1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<InviteGroup1Response> inviteGroup1(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestBody InviteGroup1Request request) throws Exception{
//        String userId = userPrincipal.getId();
//        InviteGroup1Response response = groupManageMemberService.inviteGroup1(request,userId);
//        return new BaseResponse<>(response);
//    }
//
//    /*
//    그룹 상세 - 그룹 초대하기 - step2
//
//    [웹] 개인키로 encencGroupMemberId 복호화해서 encUserId 얻고,
//		encUserId, groupId 로 encGroupKey 요청 /api/v1/group/invite2 ->
//    [서버] GroupShareKey테이블 내 encGroupKey 반환->
//    */
//    @Operation(
//            summary = "그룹 초대 - Step2",
//            description = """
//        개인키로 encencGroupMemberId를 복호화하여 encUserId를 획득 후,
//        encUserId와 groupId로 encGroupKey를 요청합니다.
//        """,
//            security = @SecurityRequirement(name = "BearerAuth")
//    )
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "성공",
//                    content = @Content(schema = @Schema(implementation = InviteGroup2Response.class))
//            ),
//            @ApiResponse(responseCode = "400", description = "요청 형식 오류 (필드 누락/유효성 실패)",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = {
//                                    @ExampleObject(name = "encUserId 누락", value = """
//                    { "code": 400, "message": "encUserId는 필수입니다." }
//                    """),
//                                    @ExampleObject(name = "groupId 누락", value = """
//                    { "code": 400, "message": "groupId는 필수입니다." }
//                    """)
//                            }
//                    )
//            ),
//            @ApiResponse(responseCode = "401", description = "인증 실패",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 401, "message": "인증이 필요합니다." }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "403", description = "권한 없음",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 403, "message": "권한이 없습니다." }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "422", description = "복호화/무결성 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 422, "message": "encUserId 복호화 실패" }
//                """)
//                    )
//            ),
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                { "code": 500, "message": "서버 내부 오류가 발생했습니다." }
//                """)
//                    )
//            )
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    @PostMapping(value = "/invite2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<InviteGroup2Response> inviteGroup2(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestBody InviteGroup2Request request) throws Exception{
//        InviteGroup2Response response = groupManageMemberService.inviteGroup2(request);
//        return new BaseResponse<>(response);
//    }
//
//    /*
//    그룹 상세 - 그룹 초대하기 - step3
//
//    [웹] 개인키로 그룹키 획득,
//		enc ( 그룹키, 그룹아이디, 랜덤 UUID(만들때마다 다른 값이 나오도록), 초대하려는 유저의 이메일 ) by 랜덤 값 6자리(=초대 암호),
//
//		위의 값 보냄
//		/api/v1/group/invite3 ->
//    [서버] 받은 값을 redis에 INVITE_KEY:받은 값으로 저장 (해당 링크에 TTL 적용 유효한지를 확인)
//    */
//    @Operation(summary = "그룹 초대 - Step3", description = """
//        개인키로 그룹키를 획득 후,
//        그룹키, 그룹아이디, 랜덤 UUID, 초대할 유저 이메일을 랜덤 6자리 초대암호로 암호화하여 서버에 전달합니다.
//        서버는 Redis에 TTL 적용 후 저장하며, 초대 링크 유효성을 확인할 수 있습니다.
//        """)
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "초대코드 발급 성공",
//                    content = @Content(schema = @Schema(implementation = InviteGroup3Response.class))
//            ),
//
//            @ApiResponse(responseCode = "400", description = "요청 형식 오류 (필드 누락/유효성 실패)",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = {
//                                    @ExampleObject(name = "초대 암호 누락", value = """
//                                    { "code": 400, "message": "inviteCode는 필수입니다." }
//                                    """),
//                                    @ExampleObject(name = "그룹키 누락", value = """
//                                    { "code": 400, "message": "groupKey는 필수입니다." }
//                                    """)
//                            }
//                    )
//            ),
//
//            @ApiResponse(responseCode = "401", description = "인증 실패",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                            { "code": 401, "message": "인증이 필요합니다." }
//                            """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "403", description = "권한 없음",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                            { "code": 403, "message": "권한이 없습니다." }
//                            """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "422", description = "복호화/무결성 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                            { "code": 422, "message": "초대 암호 복호화 실패" }
//                            """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                            { "code": 500, "message": "서버 내부 오류가 발생했습니다." }
//                            """)
//                    )
//            )
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    @PostMapping(value = "/invite3", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<InviteGroup3Response> inviteGroup3(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestBody InviteGroup3Request request) throws Exception{
//        InviteGroup3Response response = groupManageMemberService.inviteGroup3(request);
//        return new BaseResponse<>(response);
//    }
//
////======================
//// 그룹 관리 - 초대받기 (Step1)
////======================
//
//    /*
//    그룹 상세 - 그룹 초대받기 - step1
//
//    [웹]
//    프론트에서 디코딩해서 얻은 encryptedValue와 함께
//    사용자의 정보들을 조합한후
//
//    값 보냄
//    /api/v1/group/join1->
//
//    [서버]
//    받은 encryptedValue와 값으로 초대코드 유효성 검증후,
//    통과시 저장
//    불통과시 시도 횟수 1증가 (5번이후는 해당 초대코드 유효x)
//     */
//    @Operation(summary = "그룹 초대받기 - Step1", description = """
//        초대코드를 사용하여 그룹에 참여합니다.
//        서버는 Redis에서 초대코드 유효성을 확인하고, 시도 횟수를 1 증가시킵니다.
//        검증 후 데이터베이스에 그룹 참여 정보를 저장합니다.
//        """)
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "그룹 참여 성공",
//                    content = @Content(schema = @Schema(implementation = JoinGroupResponse.class))
//            ),
//
//            @ApiResponse(responseCode = "400", description = "요청 형식 오류 (필드 누락/유효성 실패)",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = {
//                                    @ExampleObject(name = "초대코드 누락", value = """
//                                { "code": 400, "message": "encryptedValue는 필수입니다." }
//                                """),
//                                    @ExampleObject(name = "그룹 ID 누락", value = """
//                                { "code": 400, "message": "groupId는 필수입니다." }
//                                """)
//                            }
//                    )
//            ),
//
//            @ApiResponse(responseCode = "401", description = "인증 실패",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                        { "code": 401, "message": "인증이 필요합니다." }
//                        """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "403", description = "권한 없음",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                        { "code": 403, "message": "권한이 없습니다." }
//                        """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "422", description = "초대코드 검증 실패/만료",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                        { "code": 422, "message": "초대코드가 만료되었거나 유효하지 않습니다." }
//                        """)
//                    )
//            ),
//
//            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ErrorResponse.class),
//                            examples = @ExampleObject(value = """
//                        { "code": 500, "message": "서버 내부 오류가 발생했습니다." }
//                        """)
//                    )
//            )
//    })
//    @SecurityRequirement(name = "BearerAuth")
//    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public BaseResponse<JoinGroupResponse> joinGroup(
//            @AuthenticationPrincipal UserPrincipal userPrincipal,
//            @RequestBody JoinGroupRequest request) throws Exception{
//        String userId = userPrincipal.getId();
//        JoinGroupResponse response = groupManageMemberService.joinGroup(request,userId);
//        return new BaseResponse(response);
//    }

//======================
// 그룹 관리 - 나가기 (Step1,2,3)
//======================

    /*
    그룹 관리 - 그룹 나가기 - step1

    서버에서 그룹에서 나가겠냐는 메시지 반환
     */
    @Operation(summary = "그룹 나가기 - Step1", description = """
    서버에서 그룹에서 나가겠냐는 메시지를 반환합니다.
    사용자 확인 후, 나가기 전 메시지를 안내합니다.
""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퇴장 전 메시지 반환 성공",
                    content = @Content(schema = @Schema(implementation = LeaveGroup1Response.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "그룹 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/leave1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<LeaveGroup1Response> leaveGroup1(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody LeavGroup1Request request) throws Exception{
        String userId = userPrincipal.getId();
        LeaveGroup1Response response = groupManageMemberService.leaveGroup1(request, userId);
        return new BaseResponse(response);
    }

    /*
    그룹 관리 - 그룹 나가기 - step2

    서버에서 encencMemberId 반환
     */
    @Operation(summary = "그룹 나가기 - Step1", description = """
    서버에서 그룹에서 나가겠냐는 메시지를 반환합니다.
    사용자 확인 후, 나가기 전 메시지를 안내합니다.
""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퇴장 전 메시지 반환 성공",
                    content = @Content(schema = @Schema(implementation = LeaveGroup1Response.class))
            ),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "그룹 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/leave2", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<LeaveGroup2Response> leaveGroup2(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody LeaveGroup2Request request) throws Exception{
        LeaveGroup2Response response = groupManageMemberService.leaveGroup2(request);
        return new BaseResponse(response);
    }

    /*
    그룹 관리 - 그룹 나가기 - step3

    서버에서 사용자 퇴장 처리 혹은 방장인경우, 그룹 폭파
     */
    @Operation(summary = "그룹 나가기 - Step3", description = """
    서버에서 사용자의 퇴장 처리 혹은 방장인 경우 그룹 전체 삭제 처리합니다.
""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퇴장 처리/그룹 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LeaveGroup3Response.class),
                            examples = {
                                    @ExampleObject(name = "일반 그룹원 퇴장", value = """
                        { "message": "00그룹 그룹에서 나갔어요." }
                    """),
                                    @ExampleObject(name = "방장 그룹 삭제", value = """
                        { "message": "00그룹 그룹이 삭제되었어요." }
                    """)
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "그룹/회원 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping(value = "/leave3", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<LeaveGroup3Response> leaveGroup3(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody LeaveGroup3Request request) throws Exception{
        LeaveGroup3Response response = groupManageMemberService.leaveGroup3(request);
        return new BaseResponse(response);
    }

}
