package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuthReject;
import com.fulu.game.core.entity.UserTechAuthReject;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.entity.vo.searchVO.UserTechAuthSearchVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private UserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoFileService userInfoFileService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;
    @Autowired
    private UserTechAuthService userTechAuthService;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserTechAuthRejectService userTechAuthRejectService;

    /**
     * 用户认证信息列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/info-auth/list")
    public Result userInfoAuthList(@RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize,
                                   UserInfoAuthSearchVO userInfoAuthSearchVO) {
        PageInfo<UserInfoAuthVO> pageInfo = userInfoAuthService.list(pageNum, pageSize, userInfoAuthSearchVO);
        return Result.success().data(pageInfo);
    }


    /**
     * 认证信息创建
     *
     * @return
     */
    @PostMapping(value = "/info-auth/save")
    public Result userInfoAuthCreate(UserInfoAuthVO userInfoAuthVO) {
        userInfoAuthService.save(userInfoAuthVO);
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 信息认证说明
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/reason")
    public Result infoAuthRejectReason(@RequestParam(required = true) Integer id) {
        List<UserInfoAuthReject> authRejectList = userInfoAuthRejectService.findByUserInfoAuthId(id);
        List<UserInfoAuthRejectVO> userTechAuthRejectVOList = CollectionUtil.copyNewCollections(authRejectList, UserInfoAuthRejectVO.class);
        return Result.success().data(userTechAuthRejectVOList);
    }

    /**
     * 认证信息驳回
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/reject")
    public Result userInfoAuthReject(@RequestParam(required = true) Integer id,
                                     @RequestParam(required = true) String reason) {
        userInfoAuthService.reject(id, reason);
        return Result.success().msg("认证信息驳回成功!");
    }

    /**
     * 认证信息冻结
     *
     * @param id
     * @param reason
     * @return
     */
    @PostMapping(value = "/info-auth/freeze")
    public Result userInfoAuthFreeze(@RequestParam(required = true) Integer id,
                                     @RequestParam(required = true) String reason) {
        userInfoAuthService.freeze(id, reason);
        return Result.success().msg("认证信息冻结成功!");
    }

    /**
     * 认证信息解冻
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/unfreeze")
    public Result userInfoAuthUnFreeze(@RequestParam(required = true) Integer id) {
        userInfoAuthService.unFreeze(id);
        return Result.success().msg("认证信息解冻成功!");
    }

    /**
     * 清除认证信息驳回状态
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/unreject")
    public Result userInfoAuthUnReject(@RequestParam(required = true) Integer id) {
        userInfoAuthService.unReject(id);
        return Result.success().msg("认证信息认证通过!");
    }

    /**
     * 删除身份证照片
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/idcard/delete")
    public Result deleteIdCard(@RequestParam(required = true) Integer id) {
        userInfoFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }

    /**
     * 删除认证信息(写真和声音)
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/info-auth/file/delete")
    public Result deleteAuthFile(@RequestParam(required = true) Integer id) {
        userInfoAuthFileService.deleteById(id);
        return Result.success().msg("删除成功!");
    }

    /**
     * 查询用户个人认证信息
     *
     * @param userId
     * @return
     */
    @PostMapping(value = "/info-auth/query")
    public Result userAuthInfo(@RequestParam(required = false, name = "userId") Integer userId) {
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserAuthInfoByUserId(userId);
        return Result.success().data(userInfoAuthVO);
    }


    /**
     * 通过用户手机查询用户信息
     *
     * @param mobile
     * @return
     */
    @PostMapping(value = "/get")
    public Result findByMobile(@RequestParam(required = true) String mobile) {
        User user = userService.findByMobile(mobile);
        if (user == null) {
            return Result.error().msg("手机号查询错误!");
        }
        return Result.success().data(user);
    }

    /**
     * 用户技能认证信息添加和修改
     *
     * @param userTechAuthVO
     * @return
     */
    @PostMapping(value = "/tech-auth/save")
    public Result techAuthSave(UserTechAuthVO userTechAuthVO) {
        userTechAuthService.save(userTechAuthVO);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 技能审核通过
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/pass")
    public Result techAuthPass(Integer id) {
        userTechAuthService.pass(id);
        return Result.success().msg("技能审核通过!");
    }

    /**
     * 技能不通过
     *
     * @param id
     * @param reason
     * @return
     */
    @PostMapping(value = "/tech-auth/reject")
    public Result techAuthReject(Integer id,
                                 String reason) {
        userTechAuthService.reject(id, reason);
        return Result.success().msg("技能驳回成功!");
    }

    /**
     * 信息认证说明
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/reason")
    public Result techAuthRejectReason(Integer id) {
        List<UserTechAuthReject> techAuthRejectList = userTechAuthRejectService.findByTechAuth(id);
        List<UserTechAuthRejectVO> userTechAuthRejectVOList = CollectionUtil.copyNewCollections(techAuthRejectList, UserTechAuthRejectVO.class);
        return Result.success().data(userTechAuthRejectVOList);
    }

    /**
     * 技能冻结
     *
     * @param id
     * @param reason
     * @return
     */
    @PostMapping(value = "/tech-auth/freeze")
    public Result techAuthFreeze(Integer id,
                                 String reason) {
        userTechAuthService.freeze(id, reason);
        return Result.success().msg("技能冻结成功!");
    }

    /**
     * 技能解冻
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/unfreeze")
    public Result techAuthUnFreeze(Integer id) {
        userTechAuthService.unFreeze(id);
        return Result.success().msg("技能解冻成功!");
    }


    /**
     * 用户技能认证信息查询
     *
     * @return
     */
    @PostMapping(value = "/tech-auth/list")
    public Result techAuthList(@RequestParam("pageNum") Integer pageNum,
                               @RequestParam("pageSize") Integer pageSize,
                               UserTechAuthSearchVO userTechAuthSearchVO) {
        PageInfo<UserTechAuthVO> page = userTechAuthService.list(pageNum, pageSize, null, userTechAuthSearchVO);
        return Result.success().data(page);
    }

    /**
     * 用户技能认证信息查询
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/query")
    public Result techAuthInfo(@RequestParam(required = true) Integer id) {
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id);
        return Result.success().data(userTechAuthVO);
    }

    /**
     * 单个用户-封禁
     *
     * @param id
     * @return
     */
    @PostMapping("/lock")
    public Result lock(@RequestParam("id") Integer id) {
        userService.lock(id);
        return Result.success().msg("操作成功！");
    }

    /**
     * 单个用户-解封
     *
     * @param id
     * @return
     */
    @PostMapping("/unlock")
    public Result unlock(@RequestParam("id") Integer id) {
        userService.unlock(id);
        return Result.success().msg("操作成功！");
    }

    /**
     * 查询-用户-列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/list")
    public Result list(UserVO userVO,
                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageInfo<UserVO> userList = userService.list(userVO, pageNum, pageSize);
        return Result.success().data(userList).msg("查询用户列表成功！");
    }

    /**
     * 添加用户
     *
     * @param userVO
     * @return
     */
    @PostMapping("/save")
    public Result save(UserVO userVO) {
//        if (StringUtils.isEmpty(userVO.getMobile())) {
//            throw new UserException(UserException.ExceptionCode.IllEGAL_MOBILE_EXCEPTION);
//        }
//        //判断手机号是否已注册成用户
//        User user = userService.findByMobile(userVO.getMobile());
//        if (user != null) {
//            return Result.error(ResultStatus.MOBILE_DUPLICATE).msg("手机号已注册");
//        } else {
//            User newUser = userService.createNewUser(userVO);
//            return Result.success().data(newUser).msg("新用户添加成功！");
//        }
        return Result.error().msg("后台添加用户功能暂时关闭");
    }

    /**
     * 用户/陪玩师列表导出
     *
     * @param response
     * @param type
     * @throws Exception
     */
    @RequestMapping("/export/{type}")
    public void userExport(HttpServletResponse response,
                           @PathVariable(name = "type", required = true) Integer type,
                           @RequestParam(value = "startTime", required = false) Date startTime,
                           @RequestParam(value = "endTime", required = false) Date endTime) throws Exception {
        Integer userType = null;
        String title;
        List<User> userList;
        switch (type) {
            case 0:
                title = "所有用户列表";
                break;
            case 1:
                title = "普通玩家列表";
                userType = UserTypeEnum.GENERAL_USER.getType();
                break;
            case 2:
                title = "陪玩师列表";
                userType = UserTypeEnum.ACCOMPANY_PLAYER.getType();
                break;
            default:
                title = "所有用户列表";
        }
        userList = userService.findByLoginTime(userType, startTime, endTime);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, User.class, userList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
    }

}
