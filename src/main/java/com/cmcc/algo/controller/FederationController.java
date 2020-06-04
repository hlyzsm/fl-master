package com.cmcc.algo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmcc.algo.common.annotation.SysLog;
import com.cmcc.algo.common.utils.R;
import com.cmcc.algo.common.validator.ValidatorUtils;
import com.cmcc.algo.entity.FederationEntity;
import com.cmcc.algo.service.IFederationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 联邦信息表 前端控制器
 * </p>
 *
 * @author hjy
 * @since 2020-05-25
 */
@RestController
@RequestMapping("/federations")
public class FederationController {
    @Value("${requesturls.url}")
    private String url;

    @Autowired
    private IFederationService federationService;

    /**
     * list federations
     */
    @GetMapping("")
    public R federations(@RequestParam Map<String, Object> params) {

        List<Map<String, Object>> page = federationService.queryFederations(params);

        return R.ok().put("data", page);
    }

    @GetMapping("/{id}")
    public R get(@PathVariable("id") String id) {

        //FederationEntity data = federationService.queryFederationById(id);
        FederationEntity data = federationService.getById(id);

        return R.ok().put("data", data);
    }

    /**
      * create a new federation
      */
    @SysLog("save federation")
    @PostMapping("")
    public R save(@RequestBody FederationEntity federation){
        if (federation==null) {
            return R.error("bad request: entity is null");
        }
        //ValidatorUtils.validateEntity(federation, AddGroup.class);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        federation.setUuid(uuid);
        List<FederationEntity> list = federationService.list(new QueryWrapper<FederationEntity>()
                        .eq("name", federation.getName())
                        .or().eq("uuid", federation.getUuid()));
        if(CollectionUtils.isNotEmpty(list)){
            return R.error("名字重复，请重试。");
        }

        federationService.saveFederation(federation);
        return R.ok();
    }

    /**
     * delete a federation
     */
    @SysLog("delete federation")
    @DeleteMapping("/{id}")
    public R delete(@PathVariable(name = "id") long id){
        FederationEntity oldFederation = federationService.getById(id);
        if (oldFederation == null) {
            return R.error(String.format("bad request: federation with id %d not exits", id));
        }
        federationService.removeById(id);
        return R.ok();
    }

    /**
     * update a federation
     */
    @PutMapping("")
    public R update(@RequestBody FederationEntity federation) throws Exception {
        if (federation == null) {
            return R.error("bad request: entity is null");
        }
        long id = federation.getId();
        FederationEntity newFederation = federationService.getById(id);
        if (newFederation == null) {
            throw new Exception("get from database federation entity is null");
        }
	if (federation.getName() != null) {
	    newFederation.setName(federation.getName());
	}
        List<FederationEntity> list = federationService.list(new QueryWrapper<FederationEntity>()
                        .eq("name", federation.getName()));
        if(CollectionUtils.isNotEmpty(list)){
            //throw new Exception("名字重复，请重试。");
            return R.error("名字重复，请重试。");
        }
        federationService.saveFederation(newFederation);
	return R.ok();
    }
}
