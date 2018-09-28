package com.fulu.game.admin.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.date.DateUtil;
import com.fulu.game.admin.service.AdminUserInfoAuthService;
import com.fulu.game.admin.service.AdminUserTechAuthService;
import com.fulu.game.admin.service.impl.AdminUserTechAuthServiceImpl;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.UserTypeEnum;
import com.fulu.game.common.utils.CollectionUtil;
import com.fulu.game.common.utils.OssUtil;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.to.UserInfoAuthTO;
import com.fulu.game.core.entity.to.UserTechAuthTO;
import com.fulu.game.core.entity.vo.*;
import com.fulu.game.core.entity.vo.searchVO.UserInfoAuthSearchVO;
import com.fulu.game.core.entity.vo.searchVO.UserTechAuthSearchVO;
import com.fulu.game.core.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 用户Controller
 *
 * @author wangbin
 * @date
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {

    @Autowired
    private AdminUserInfoAuthService userInfoAuthService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInfoFileService userInfoFileService;
    @Autowired
    private UserInfoAuthFileService userInfoAuthFileService;
    @Autowired
    private AdminUserTechAuthService userTechAuthService;
    @Autowired
    private UserInfoAuthRejectService userInfoAuthRejectService;
    @Autowired
    private UserTechAuthRejectService userTechAuthRejectService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserBodyAuthService userBodyAuthService;
    @Autowired
    private VirtualDetailsService virtualDetailsService;
    @Autowired
    private UserNightInfoService userNightInfoService;
    @Qualifier(value = "adminUserTechAuthServiceImpl")
    @Autowired
    private AdminUserTechAuthServiceImpl adminUserTechAuthService;
    @Autowired
    private OssUtil ossUtil;

    /**
     * 陪玩师认证信息列表
     *
     * @param pageNum              页码
     * @param pageSize             每页显示数据条数
     * @param userInfoAuthSearchVO 查询条件VO
     * @return 封装结果集
     */
    @PostMapping(value = "/info-auth/list")
    public Result userInfoAuthList(@RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize,
                                   UserInfoAuthSearchVO userInfoAuthSearchVO) {
        PageInfo<UserInfoAuthVO> pageInfo = userInfoAuthService.list(pageNum, pageSize, userInfoAuthSearchVO);
        return Result.success().data(pageInfo);
    }

    /**
     * 设置是否马甲
     *
     * @param id 认证id
     * @return 封装结果集
     */
    @PostMapping(value = "/info-auth/vest/set")
    public Result setVest(@RequestParam Integer id) {
        UserInfoAuth userInfoAuth = userInfoAuthService.setVest(id);
        return Result.success().data(userInfoAuth).msg("操作成功！");
    }

    /**
     * 认证信息创建
     *
     * @return
     */
    @PostMapping(value = "/info-auth/save")
    public Result userInfoAuthCreate(UserInfoAuthTO userInfoAuthTO) {
        userInfoAuthService.save(userInfoAuthTO);

        //更新一些特定为空的字段
        userInfoAuthService.saveOtherInfo(userInfoAuthTO);

        return Result.success().data(userInfoAuthTO);
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
        userInfoAuthService.pass(id);
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
        UserInfoAuthVO userInfoAuthVO = userInfoAuthService.findUserInfoAuthByUserId(userId);
        //获取用户所有认证技能信息
        List<UserTechAuthVO> list = adminUserTechAuthService.findUserTechAuthList(userId);
        userInfoAuthVO.setUserTechAuthVOList(list);
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
     * 实名认证审核--通过
     *
     * @param userId 用户id
     * @return 封装结果集
     */
    @PostMapping(value = "/body-auth/pass")
    public Result bodyAuthPass(@RequestParam Integer userId) {
        userBodyAuthService.pass(userId);
        return Result.success().msg("通过成功！");
    }

    /**
     * 实名认证审核--拒绝或驳回
     *
     * @param userId 用户id
     * @param remark 备注
     * @return 封装结果集
     */
    @PostMapping(value = "/body-auth/reject")
    public Result bodyAuthReject(@RequestParam Integer userId, String remark) {
        userBodyAuthService.reject(userId, remark);
        return Result.success().msg("操作成功！");
    }


    /**
     * 用户身份认证信息
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @return 封装结果集
     */
    @PostMapping(value = "/body-auth/list")
    public Result userBodyAuthList(@RequestParam("pageNum") Integer pageNum,
                                   @RequestParam("pageSize") Integer pageSize,
                                   UserBodyAuthVO userBodyAuthVO) {
        PageInfo<UserBodyAuthVO> pageInfo = userBodyAuthService.findByVO(pageNum, pageSize, userBodyAuthVO);
        return Result.success().data(pageInfo);
    }

    /**
     * 用户身份认证信息列表导出
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping("/body-auth/export")
    public void orderExport(HttpServletResponse response,
                            UserBodyAuthVO userBodyAuthVO) throws Exception {
        String title = "用户身份认证信息列表";
        List<UserBodyAuthVO> voList = userBodyAuthService.list(userBodyAuthVO);
        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, UserBodyAuthVO.class, voList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();

//        FileOutputStream fos = new FileOutputStream(new File("E:\\test.xls"));
//        workbook.write(fos);
//        workbook.close();
//        fos.close();
    }


    /**
     * 用户技能认证信息添加和修改
     *
     * @param userTechAuthTO
     * @return
     */
    @PostMapping(value = "/tech-auth/save")
    public Result techAuthSave(UserTechAuthTO userTechAuthTO) {
        userTechAuthService.save(userTechAuthTO);
        return Result.success().data(userTechAuthTO);
    }

    /**
     * 技能审核通过
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/pass")
    public Result techAuthPass(Integer id, Integer techLevelId) {
        userTechAuthService.pass(id, techLevelId);
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
        PageInfo<UserTechAuthVO> page = userTechAuthService.list(pageNum, pageSize, userTechAuthSearchVO);
        return Result.success().data(page);
    }

    /**
     * 用户技能认证信息查询
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/tech-auth/query")
    public Result techAuthInfo(Integer id) {
        UserTechAuthVO userTechAuthVO = userTechAuthService.findTechAuthVOById(id, null);
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
        Boolean flag = userService.lock(id);
        if (flag) {
            return Result.success().msg("封禁用户并且剔除用户登录状态成功！");
        } else {
            return Result.success().msg("封禁用户未找到用户登录状态！");
        }
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
     * 设置用户代聊客服ID
     *
     * @param id
     * @return
     */
    @PostMapping("/set-substitute")
    public Result setSubstitute(@RequestParam("id") Integer id, Integer substituteId) {
        userInfoAuthService.setSubstitute(id, substituteId);
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
     * 查询-用户信息
     * 管理后台使用
     *
     * @param id
     * @return
     */
    @PostMapping("/query")
    public Result list(@RequestParam Integer id) {
        UserVO userVO = userService.findUserVOById(id);
        return Result.success().data(userVO).msg("查询用户成功！");
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
                           @RequestParam(value = "endTime", required = false) Date endTime,
                           UserInfoAuthSearchVO userInfoAuthSearchVO,
                           UserVO userVO) throws Exception {
        Integer userType = null;
        String title;
        switch (type) {
            case 0:
                title = "所有用户列表";
                userType = null;
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

        userVO.setType(userType);
        List<UserVO> voList;
        if (UserTypeEnum.ACCOMPANY_PLAYER.getType().equals(userType)) {
            voList = userService.list(userInfoAuthSearchVO);
        } else {
            voList = userService.list(userVO, null, null).getList();
        }

        ExportParams exportParams = new ExportParams(title, "sheet1", ExcelType.XSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams, UserVO.class, voList);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(title, "UTF-8"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }


    /**
     * 聊天对象信息获取
     *
     * @param id
     * @return
     */
    @PostMapping("/chatwith/get")
    public Result chatWithGet(@RequestParam("id") Integer id) {
        UserInfoVO userInfoVO = userInfoAuthService.findUserCardByUserId(id, false, true, true, true);
        List<Product> productList = productService.findByUserId(id);
        userInfoVO.setProductList(productList);
        return Result.success().data(userInfoVO).msg("查询聊天对象信息成功！");
    }


    /**
     * 获取钻石/魅力值明细
     *
     * @return
     */
    @RequestMapping("/virtual-detail/list")
    public Result virtualDetailList(Integer type, Integer userId,
                                    @RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("pageNum") Integer pageNum) {

        VirtualDetailsVO vd = new VirtualDetailsVO();
        vd.setUserId(userId);
        vd.setType(type);

        PageInfo<VirtualDetails> list = virtualDetailsService.findByParameterWithPage(vd, pageSize, pageNum, " create_time desc");

        return Result.success().data(list).msg("查询列表成功！");
    }

    /**
     * 获取午夜场陪玩师设置
     *
     * @param userId 用户id
     * @return 封装结果集
     */
    @RequestMapping("/night-config/get")
    public Result getNightConfig(@RequestParam Integer userId) {
        UserNightInfoVO info = userNightInfoService.getNightConfig(userId);
        return Result.success().data(info).msg("查询成功！");
    }

    /**
     * 设置午夜场陪玩师信息
     *
     * @param userId      陪玩师id
     * @param sort        排序权重
     * @param categoryId  游戏分类id
     * @param salesModeId 单位id
     * @return 封装结果集
     */
    @RequestMapping("/night-config/set")
    public Result setNightConfig(@RequestParam Integer userId,
                                 @RequestParam Integer sort,
                                 @RequestParam Integer categoryId,
                                 @RequestParam Integer salesModeId) {
        UserNightInfo info = userNightInfoService.setNightConfig(userId, sort, categoryId, salesModeId);
        return Result.success().data(info).msg("设置成功！");
    }


    /**
     * 修改/填写资料
     *
     * @param userVO
     * @return
     */
    @RequestMapping("update")
    public Result update(UserVO userVO) {
        User user = new User();
        user.setId(userVO.getId());
        if (userVO.getBirth() != null) {
            user.setAge(DateUtil.ageOfNow(userVO.getBirth()));
        }
        user.setGender(userVO.getGender());
        user.setCity(userVO.getCity());
        user.setProvince(userVO.getProvince());
        user.setCountry(userVO.getCountry());
        user.setBirth(userVO.getBirth());
        user.setConstellation(userVO.getConstellation());
        user.setNickname(userVO.getNickname());
        user.setHeadPortraitsUrl(ossUtil.activateOssFile(userVO.getHeadPortraitsUrl()));
        userService.update(user);
        user = userService.findById(userVO.getId());
        userService.updateRedisUser(user);
        // 保存用户认证信息
        saveUserInfoAuth(userVO);
        user.setIdcard(null);
        user.setRealname(null);
        return Result.success().data(user).msg("个人信息设置成功！");
    }

    /**
     * 保存用户认证信息
     *
     * @param userVO
     */
    private void saveUserInfoAuth(UserVO userVO) {
        // 当存在用户认证信息时取修改
        if (userVO != null && (userVO.getInterests() != null ||
                userVO.getProfession() != null || userVO.getAbout() != null || userVO.getMainPicUrl() != null)) {
            UserInfoAuth userInfoAuth = new UserInfoAuth();
            userInfoAuth.setUserId(userVO.getId());
            if (userVO.getInterests() != null) {
                userInfoAuth.setInterests(userVO.getInterests());
            }
            if (userVO.getProfession() != null) {
                userInfoAuth.setProfession(userVO.getProfession());
            }
            if (userVO.getAbout() != null) {
                userInfoAuth.setAbout(userVO.getAbout());
            }
            if (userVO.getMainPicUrl() != null) {
                userInfoAuth.setMainPicUrl(userVO.getMainPicUrl());
            }
            // 判断认证信息是否存在，不存在就新增
            UserInfoAuth tmp = userInfoAuthService.findByUserId(userVO.getId());
            userInfoAuth.setUpdateTime(new Date());
            if (tmp == null) {
                userInfoAuth.setCreateTime(new Date());
                userInfoAuthService.create(userInfoAuth);
            } else {
                userInfoAuthService.updateByUserId(userInfoAuth);
            }
        }
        saveUserInfoAuthFile(userVO);
    }

    /**
     * 保存用户认证文件信息（语音）
     *
     * @param userVO
     */
    private void saveUserInfoAuthFile(UserVO userVO) {
        UserInfoAuth userInfoAuth = userInfoAuthService.findByUserId(userVO.getId());
        if (userInfoAuth != null) {
            // 先删除语音
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), 2);
            if (userVO != null && (userVO.getVoiceUrl() != null || userVO.getDuration() != null)) {
                UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                userInfoAuthFile.setUrl(ossUtil.activateOssFile(userVO.getVoiceUrl()));
                userInfoAuthFile.setDuration(userVO.getDuration());
                userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                userInfoAuthFile.setType(2);
                userInfoAuthFile.setName("语音");
                userInfoAuthFile.setCreateTime(new Date());
                userInfoAuthFileService.create(userInfoAuthFile);
            }
            userInfoAuthFileService.deleteByUserAuthIdAndType(userInfoAuth.getId(), 1);
            if (userVO != null && userVO.getPicUrls() != null){
                int index = 1;
                UserInfoAuthFile userInfoAuthFile = new UserInfoAuthFile();
                for(String url: userVO.getPicUrls()){
                    userInfoAuthFile.setUrl(ossUtil.activateOssFile(url));
                    userInfoAuthFile.setDuration(null);
                    userInfoAuthFile.setInfoAuthId(userInfoAuth.getId());
                    userInfoAuthFile.setType(1);
                    userInfoAuthFile.setName("私照" + index++);
                    userInfoAuthFile.setCreateTime(new Date());
                    userInfoAuthFileService.create(userInfoAuthFile);
                }
            }
        }
    }

}
