package com.sparta.todo.jwt;

import com.sparta.todo.entity.UserRoleEnum;
import jakarta.servlet.http.HttpServletRequest;

public class AuthUtil {

    // 관리자 권한이 있는지 확인
    public static boolean hasRole(HttpServletRequest request, UserRoleEnum userRoleEnum) {
        String role = (String) request.getAttribute("role");
        return role != null && role.equals(userRoleEnum.ADMIN.getAuthority());
    }

    // 사용자 권한 또는 관리자 권한이 있는지 확인
    public static boolean hasAnyRole(HttpServletRequest request, UserRoleEnum... userRoleEnums) {
        String role = (String) request.getAttribute("role");
        for (UserRoleEnum userRoleEnum : userRoleEnums) {
            if (role != null && role.equals(userRoleEnum.getAuthority())) {
                return true;
            }
        }
         return false;
    }
}
