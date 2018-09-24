package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by binlin on 2018/8/19.
 */
@Controller
@RequestMapping("/user/springsession/")
public class UserSpringSessionController {

    @Autowired
    private IUserService iUserService;


    @RequestMapping(value = "test.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> test() {
        return ServerResponse.createByErrorMessage("test");
    }

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest) {
        //service->mybatis->dao
        int i=0;
        int t=666/i;
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
//            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpSession session) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        CookieUtil.delLoginToken(httpServletRequest, httpServletResponse);
//        RedisShardedPoolUtil.del(loginToken);
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }




    @RequestMapping(value = "get_user_info.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session, HttpServletRequest httpServletRequest) {
//        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登陆，无法获取用户的信息");
//        }
//
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆，无法获取用户的信息");
    }
}
