package com.fulu.game.h5.controller.thunder.excel;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fulu.game.common.Result;
import com.fulu.game.common.enums.ThunderCodeEnum;
import com.fulu.game.core.entity.ThunderCode;
import com.fulu.game.core.service.ThunderCodeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * todo：描述文字
 *
 * @author Gong ZeChun
 * @date 2018/10/11 16:37
 */
@RestController
@Slf4j
@RequestMapping("/excel")
public class ThunderExcelHander {

    @Autowired
    private ThunderCodeService thunderCodeService;

    @RequestMapping("/import")
    public Result excelImport() {
        String filePath = "D:\\aaa.xls";
//        List<RedeemCode> redeemCodeList = GenIdUtil.importExcel(filePath, 0, 0, RedeemCode.class);
        ImportParams params = new ImportParams();
        List<RedeemCode> redeemCodeList = ExcelImportUtil.importExcel(new File(filePath), RedeemCode.class, params);

        if (CollectionUtils.isEmpty(redeemCodeList)) {
            return Result.success().msg("无数据！");
        }

        for (RedeemCode code : redeemCodeList) {
            String sohu = code.getSohu();
            String ximalaya = code.getXimalaya();
            String steamSdk = code.getSteamSdk();
            String steamAccount = code.getSteamAccount();
            String steamPassword = code.getSteamPassword();
            String cibn = code.getCibn();

            if (StrUtil.isNotBlank(sohu)) {
                ThunderCode result = thunderCodeService.findByCode(sohu, ThunderCodeEnum.SOHU.getType());
                if (result == null) {
                    ThunderCode thunderCode = new ThunderCode();
                    thunderCode.setIsUse(false);
                    thunderCode.setCode(sohu);
                    thunderCode.setType(ThunderCodeEnum.SOHU.getType());
                    thunderCode.setUpdateTime(DateUtil.date());
                    thunderCode.setCreateTime(DateUtil.date());
                    thunderCode.setDelFlag(false);
                    thunderCodeService.create(thunderCode);
                }
            }

            if (StrUtil.isNotBlank(ximalaya)) {
                ThunderCode result = thunderCodeService.findByCode(ximalaya, ThunderCodeEnum.XIMALAYA.getType());
                if (result == null) {
                    ThunderCode thunderCode = new ThunderCode();
                    thunderCode.setIsUse(false);
                    thunderCode.setCode(ximalaya);
                    thunderCode.setType(ThunderCodeEnum.XIMALAYA.getType());
                    thunderCode.setUpdateTime(DateUtil.date());
                    thunderCode.setCreateTime(DateUtil.date());
                    thunderCode.setDelFlag(false);
                    thunderCodeService.create(thunderCode);
                }
            }

            if (StrUtil.isNotBlank(steamSdk)) {
                ThunderCode result = thunderCodeService.findByCode(steamSdk, ThunderCodeEnum.STEAM_SDK.getType());
                if (result == null) {
                    ThunderCode thunderCode = new ThunderCode();
                    thunderCode.setIsUse(false);
                    thunderCode.setCode(steamSdk);
                    thunderCode.setType(ThunderCodeEnum.STEAM_SDK.getType());
                    thunderCode.setUpdateTime(DateUtil.date());
                    thunderCode.setCreateTime(DateUtil.date());
                    thunderCode.setDelFlag(false);
                    thunderCodeService.create(thunderCode);
                }
            }

            if (StrUtil.isNotBlank(steamAccount)) {
                ThunderCode result = thunderCodeService.findByCode(steamAccount, ThunderCodeEnum.STEAM_ACCOUNT_PSW.getType());
                if (result == null) {
                    ThunderCode thunderCode = new ThunderCode();
                    thunderCode.setIsUse(false);
                    thunderCode.setAccount(steamAccount);
                    thunderCode.setPassword(steamPassword);
                    thunderCode.setType(ThunderCodeEnum.STEAM_ACCOUNT_PSW.getType());
                    thunderCode.setUpdateTime(DateUtil.date());
                    thunderCode.setCreateTime(DateUtil.date());
                    thunderCode.setDelFlag(false);
                    thunderCodeService.create(thunderCode);
                }
            }

            if (StrUtil.isNotBlank(cibn)) {
                ThunderCode result = thunderCodeService.findByCode(cibn, ThunderCodeEnum.CIBN.getType());
                if (result == null) {
                    ThunderCode thunderCode = new ThunderCode();
                    thunderCode.setIsUse(false);
                    thunderCode.setCode(cibn);
                    thunderCode.setType(ThunderCodeEnum.CIBN.getType());
                    thunderCode.setUpdateTime(DateUtil.date());
                    thunderCode.setCreateTime(DateUtil.date());
                    thunderCode.setDelFlag(false);
                    thunderCodeService.create(thunderCode);
                }
            }
        }

        return Result.success().msg("导入成功！");
    }
}
