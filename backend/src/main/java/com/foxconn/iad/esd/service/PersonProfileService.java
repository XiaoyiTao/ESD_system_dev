package com.foxconn.iad.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.foxconn.iad.esd.common.PageResponse;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileCreateReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfilePageReq;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileResp;
import com.foxconn.iad.esd.controller.dto.person.PersonProfileUpdateReq;
import com.foxconn.iad.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.esd.dal.dataobject.PersonProfileDO;
import com.foxconn.iad.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.esd.dal.mapper.PersonProfileMapper;
import com.foxconn.iad.esd.exception.BusinessException;
import com.foxconn.iad.esd.platform.PlatformUserGateway;
import com.foxconn.iad.esd.platform.PlatformUserResp;
import com.foxconn.iad.esd.platform.PlatformDeptGateway;
import com.foxconn.iad.esd.platform.PlatformDeptResp;
import com.foxconn.iad.esd.security.LoginUserContext;
import com.foxconn.iad.esd.security.SiteAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonProfileService {

    private final PersonProfileMapper personProfileMapper;
    private final AssetMapper assetMapper;
    private final PlatformUserGateway platformUserGateway;
    private final PlatformDeptGateway platformDeptGateway;
    private final SiteAccessService siteAccessService;

    @Transactional
    public Long create(PersonProfileCreateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        PlatformUserResp user = platformUserGateway.requireEnabledUser(
                request.getPlatformUserId(), request.getSiteCode());
        PlatformDeptResp dept = platformDeptGateway.requireEnabledDept(user.getDeptId());
        PlatformUserResp supervisor = request.getSupervisorUserId() == null ? null
                : platformUserGateway.requireEnabledUser(request.getSupervisorUserId(), request.getSiteCode());

        PersonProfileDO profile = new PersonProfileDO();
        profile.setSiteCode(request.getSiteCode());
        profile.setPlatformUserId(user.getId());
        profile.setEmployeeNo(user.getUsername());
        profile.setEmployeeName(user.getNickname());
        profile.setDeptId(user.getDeptId());
        profile.setDeptName(dept == null ? null : dept.getName());
        profile.setSupervisorUserId(supervisor == null ? null : supervisor.getId());
        profile.setSupervisorName(supervisor == null ? null : supervisor.getNickname());
        profile.setFloorCode(normalize(request.getFloorCode()));
        profile.setShiftCode(normalize(request.getShiftCode()));
        profile.setEsdStatus(1);
        profile.setCreator(String.valueOf(operator.getUserId()));
        profile.setUpdater(String.valueOf(operator.getUserId()));
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        try {
            personProfileMapper.insert(profile);
        } catch (DuplicateKeyException exception) {
            throw new BusinessException(400, "该员工已绑定当前厂区");
        }
        return profile.getId();
    }

    @Transactional
    public void update(Long id, PersonProfileUpdateReq request) {
        LoginUserContext operator = siteAccessService.requireSite(request.getSiteCode());
        PersonProfileDO profile = requireProfile(id, request.getSiteCode());
        if (Integer.valueOf(0).equals(request.getEsdStatus())) {
            long issuedCount = countHeldAssets(profile.getPlatformUserId(), request.getSiteCode(), null);
            if (issuedCount > 0) {
                throw new BusinessException(400, "员工仍持有资产，不能停用 ESD 人员档");
            }
        }
        PlatformUserResp supervisor = request.getSupervisorUserId() == null ? null
                : platformUserGateway.requireEnabledUser(request.getSupervisorUserId(), request.getSiteCode());
        profile.setSupervisorUserId(supervisor == null ? null : supervisor.getId());
        profile.setSupervisorName(supervisor == null ? null : supervisor.getNickname());
        profile.setFloorCode(normalize(request.getFloorCode()));
        profile.setShiftCode(normalize(request.getShiftCode()));
        if (request.getEsdStatus() != null) {
            profile.setEsdStatus(request.getEsdStatus());
        }
        profile.setUpdater(String.valueOf(operator.getUserId()));
        profile.setUpdateTime(LocalDateTime.now());
        personProfileMapper.updateById(profile);
    }

    public PageResponse<PersonProfileResp> page(PersonProfilePageReq request) {
        siteAccessService.requireSite(request.getSiteCode());
        LambdaQueryWrapper<PersonProfileDO> query = new LambdaQueryWrapper<PersonProfileDO>()
                .eq(PersonProfileDO::getSiteCode, request.getSiteCode())
                .eq(request.getDeptId() != null, PersonProfileDO::getDeptId, request.getDeptId())
                .eq(request.getEsdStatus() != null, PersonProfileDO::getEsdStatus, request.getEsdStatus())
                .and(StringUtils.hasText(request.getKeyword()), wrapper -> wrapper
                        .like(PersonProfileDO::getEmployeeNo, request.getKeyword())
                        .or().like(PersonProfileDO::getEmployeeName, request.getKeyword()))
                .orderByDesc(PersonProfileDO::getCreateTime);
        IPage<PersonProfileDO> page = personProfileMapper.selectPage(
                new Page<>(request.getPageNo(), request.getPageSize()), query);
        List<PersonProfileResp> list = new ArrayList<>();
        for (PersonProfileDO profile : page.getRecords()) {
            list.add(toResponse(profile));
        }
        return new PageResponse<>(list, page.getTotal());
    }

    public PersonProfileResp get(Long id, String siteCode) {
        siteAccessService.requireSite(siteCode);
        return toResponse(requireProfile(id, siteCode));
    }

    private PersonProfileDO requireProfile(Long id, String siteCode) {
        PersonProfileDO profile = personProfileMapper.selectOne(
                new LambdaQueryWrapper<PersonProfileDO>()
                        .eq(PersonProfileDO::getId, id)
                        .eq(PersonProfileDO::getSiteCode, siteCode));
        if (profile == null) {
            throw new BusinessException(404, "人员档不存在");
        }
        return profile;
    }

    private PersonProfileResp toResponse(PersonProfileDO profile) {
        PersonProfileResp response = new PersonProfileResp();
        response.setId(profile.getId());
        response.setSiteCode(profile.getSiteCode());
        response.setPlatformUserId(profile.getPlatformUserId());
        response.setEmployeeNo(profile.getEmployeeNo());
        response.setEmployeeName(profile.getEmployeeName());
        response.setDeptId(profile.getDeptId());
        response.setDeptName(profile.getDeptName());
        response.setSupervisorUserId(profile.getSupervisorUserId());
        response.setSupervisorName(profile.getSupervisorName());
        response.setFloorCode(profile.getFloorCode());
        response.setShiftCode(profile.getShiftCode());
        response.setEsdStatus(profile.getEsdStatus());
        response.setGarmentCount(countHeldAssets(profile.getPlatformUserId(), profile.getSiteCode(), 1));
        response.setShoesCount(countHeldAssets(profile.getPlatformUserId(), profile.getSiteCode(), 2));
        response.setCreateTime(profile.getCreateTime());
        response.setUpdateTime(profile.getUpdateTime());
        return response;
    }

    private long countHeldAssets(Long userId, String siteCode, Integer assetType) {
        return assetMapper.selectCount(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getSiteCode, siteCode)
                .eq(AssetDO::getCurrentHolderUserId, userId)
                .eq(AssetDO::getLifecycleStatus, 20)
                .eq(assetType != null, AssetDO::getAssetType, assetType));
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
