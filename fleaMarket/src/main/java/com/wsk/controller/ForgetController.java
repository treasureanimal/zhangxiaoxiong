package com.wsk.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.wsk.pojo.UserInformation;
import com.wsk.pojo.UserPassword;
import com.wsk.response.BaseResponse;
import com.wsk.service.UserInformationService;
import com.wsk.service.UserPasswordService;
import com.wsk.tool.StringUtils;

/**
 * Created by wsk1103 on 2017/5/9.
 */
@RestController
public class ForgetController {

    @Resource
    private UserPasswordService userPasswordService;
    @Resource
    private UserInformationService userInformationService;

    @RequestMapping(value = "checkCode.do", method = {RequestMethod.POST, RequestMethod.GET})
    public Map checkPhone(HttpServletRequest request, Model model,
                          @RequestParam String code, @RequestParam String phone, @RequestParam String token) {
        Map<String, Integer> map = new HashMap<>();
        String name = request.getParameter("name");
        if (!StringUtils.getInstance().isNullOrEmpty(name)) {
            request.getSession().setAttribute("name", name);
        }
        String checkCodeToken = (String) request.getSession().getAttribute("token");
        if(!StringUtils.getInstance().isNullOrEmpty(phone)){
        	request.getSession().setAttribute("phone", phone);
        }
        if (StringUtils.getInstance().isNullOrEmpty(checkCodeToken) || !checkCodeToken.equals(token)) {
            map.put("result", 0);
            return map;
        }
        //验证码错误
        if (!checkCodePhone(code, request)) {
            map.put("result", 0);
            return map;
        }
        map.put("result", 1);
        return map;
    }

    //更新密码
    @RequestMapping("updatePassword.do")
    public String updatePassword(HttpServletRequest request, Model model,
                                       @RequestParam String password, @RequestParam String token) {
        //防止重复提交
        String updatePasswordToken = (String) request.getSession().getAttribute("token");
        if (StringUtils.getInstance().isNullOrEmpty(updatePasswordToken) || !updatePasswordToken.equals(token)) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        String realPhone = (String) request.getSession().getAttribute("phone");
        UserPassword userPassword = new UserPassword();
        String newPassword = StringUtils.getInstance().getMD5(password);
        int uid;
        try {
            uid = userInformationService.selectIdByPhone(realPhone);
            if (uid == 0) {
                return JSON.toJSONString(new BaseResponse(0, ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        int id = userPasswordService.selectByUid(uid).getId();
        userPassword.setId(id);
        userPassword.setUid(uid);
        userPassword.setModified(new Date());
        userPassword.setPassword(newPassword);
        int result;
        try {
            result = userPasswordService.updateByPrimaryKeySelective(userPassword);
        } catch (Exception e) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        //更新失败
        if (result != 1) {
            return JSON.toJSONString(new BaseResponse(0, ""));
        }
        UserInformation userInformation = userInformationService.selectByPrimaryKey(uid);
        request.getSession().setAttribute("userInformation", userInformation);
        return JSON.toJSONString(new BaseResponse(1, ""));
    }

    //check the phone`s code
    private boolean checkCodePhone(String codePhone, HttpServletRequest request) {
        String trueCodePhone = "12251103";
        return codePhone.equals(trueCodePhone);
    }
}
