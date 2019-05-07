package com.zg.admin.service;

import com.zg.admin.entity.Role;

import java.util.List;

public interface RoleServiceInte {
    List getRoleList(Role roel);
    boolean insertRole(Role role);
    boolean editRole(Role role);
    boolean deleteRoles(String ids);
}
