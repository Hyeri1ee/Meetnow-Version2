package timetogeter.context.promise.application.dto.response;

import timetogeter.context.promise.application.dto.UserInfoDTO;

import java.util.List;

public record UserInfoResDTO (String promiseManager,
                              List<UserInfoDTO> users) {}
